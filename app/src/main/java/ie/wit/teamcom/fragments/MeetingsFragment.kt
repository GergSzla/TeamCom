package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.MeetingListener
import ie.wit.teamcom.adapters.MeetingsAdapter
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Meeting
import kotlinx.android.synthetic.main.fragment_meetings.view.*
import kotlinx.android.synthetic.main.fragment_reminders.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList
import java.util.HashMap

var meetingList = ArrayList<Meeting>()

class MeetingsFragment : Fragment(), AnkoLogger, MeetingListener {

    lateinit var app: MainApp
    lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
        }
        app.getAllMembers(currentChannel.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_meetings, container, false)
        activity?.title = getString(R.string.title_meetings)
        root.meetingsRecyclerView.layoutManager = LinearLayoutManager(activity)

        root.btnAddNewMeeting.setOnClickListener {
            if (app.currentActiveMember.role.perm_admin || app.currentActiveMember.role.perm_create_meetings) {
                navigateTo(CreateMeetingFragment.newInstance(currentChannel, false))
            } else {
                Toast.makeText(
                    context, "You do not have the permissions to do this!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        root.btnRequestMeeting.setOnClickListener {
            if (app.currentActiveMember.role.perm_admin || app.currentActiveMember.role.perm_request_meetings) {
                navigateTo(CreateMeetingFragment.newInstance(currentChannel, true))
            } else {
                Toast.makeText(
                    context, "You do not have the permissions to do this!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        setSwipeRefresh()
        return root
    }


    fun getAllMeetings(db: DatabaseReference, pa: Boolean) {
        meetingList = ArrayList<Meeting>()
        db.child("channels").child(currentChannel.id).child("meetings")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase nf error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val meeting = it.getValue<Meeting>(Meeting::class.java)
                        meetingList.add(meeting!!)

                        if (!pa) {
                            root.meetingsRecyclerView.adapter = MeetingsAdapter(
                                meetingList,
                                this@MeetingsFragment
                            )
                            root.meetingsRecyclerView.adapter?.notifyDataSetChanged()
                            if (meetingList.size > 0) {
                                root.txtEmpty_meetings.isVisible = false
                            }
                            checkSwipeRefresh()
                        }

                        db.child("channels").child(currentChannel.id).child("meetings")
                            .removeEventListener(this)
                    }
                }
            })
    }

    fun setSwipeRefresh() {
        root.swiperefreshMeetings.setOnRefreshListener {
            root.swiperefreshMeetings.isRefreshing = true
            getAllMeetings(app.database, false)
        }
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllMeetings(app.database, false)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }


    fun checkSwipeRefresh() {
        if (root.swiperefreshMeetings.isRefreshing) root.swiperefreshMeetings.isRefreshing = false
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            MeetingsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

    override fun onMeetingClicked(meeting: Meeting) {
        if (!meeting.requested_meeting) {
            navigateTo(ViewMeetingFragment.newInstance(meeting, currentChannel))
        } else if (meeting.requested_meeting && (app.currentActiveMember.role.perm_admin || app.currentActiveMember.role.perm_approve_meetings)) {
            createApproveDialog(meeting)
        } else {
            Toast.makeText(
                context, "This meeting is still pending approval.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun createApproveDialog(meeting : Meeting) {
        AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_approve_meeting)
            .setNegativeButton("Reject") { dialog, _ ->
                dialog.dismiss()
                rejectMeeting(meeting)
            }
            .setPositiveButton("Approve") { dialog, _ ->
                dialog.dismiss()
                approveMeeting(meeting)
            }.show()
    }

    fun approveMeeting(meeting : Meeting){
        meeting.requested_meeting = false

        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/meetings/${meeting.meeting_uuid}"] =
                        meeting
                    app.database.updateChildren(childUpdates)

                    app.generateDateID("1")
                    val logUpdates = HashMap<String, Any>()
                    var new_log = ie.wit.teamcom.models.Log(
                        log_id = app.valid_from_cal,
                        log_triggerer = app.currentActiveMember,
                        log_date = app.dateAsString,
                        log_time = app.timeAsString,
                        log_content = "${app.currentActiveMember.firstName} ${app.currentActiveMember.surname} approved Meeting : ${meeting.meeting_title} on ${meeting.meeting_date_as_string} @ ${meeting.meeting_time_as_string}]."
                    )
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)

                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                }
            })
    }

    fun rejectMeeting(meeting : Meeting){
        app.database.child("channels").child(currentChannel!!.id).child("meetings")
            .child(meeting.meeting_uuid)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()

                        app.generateDateID("1")
                        val logUpdates = HashMap<String, Any>()
                        var new_log = ie.wit.teamcom.models.Log(
                            log_id = app.valid_from_cal,
                            log_triggerer = app.currentActiveMember,
                            log_date = app.dateAsString,
                            log_time = app.timeAsString,
                            log_content = "${app.currentActiveMember.firstName} ${app.currentActiveMember.surname} rejected Meeting : ${meeting.meeting_title} on ${meeting.meeting_date_as_string} @ ${meeting.meeting_time_as_string}]."
                        )
                        logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                        app.database.updateChildren(logUpdates)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }
}