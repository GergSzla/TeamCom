package ie.wit.teamcom.fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.activities.CreateZoomMeetingActivity
import ie.wit.teamcom.adapters.MembersAdapter
import ie.wit.teamcom.adapters.MembersListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Meeting
import ie.wit.teamcom.models.Member
import kotlinx.android.synthetic.main.fragment_create_meeting.view.*
import kotlinx.android.synthetic.main.fragment_view_meeting.view.*
import kotlinx.android.synthetic.main.fragment_view_meeting.view.textViewDateAndTime
import kotlinx.android.synthetic.main.fragment_view_meeting.view.textViewDesc
import kotlinx.android.synthetic.main.fragment_view_meeting.view.textViewID
import kotlinx.android.synthetic.main.fragment_view_meeting.view.textViewLoc
import kotlinx.android.synthetic.main.fragment_view_meeting.view.textViewPasscode
import kotlinx.android.synthetic.main.fragment_view_meeting.view.textViewPlatform
import kotlinx.android.synthetic.main.fragment_view_meeting.view.textViewTitle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList

class ViewMeetingFragment : Fragment(), AnkoLogger, MembersListener {

    lateinit var app: MainApp
    lateinit var root: View
    var selected_meeting = Meeting()
    var memberList = ArrayList<Member>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            selected_meeting = it.getParcelable("meeting_key")!!
            currentChannel = it.getParcelable("channel_key")!!

        }
        app.getAllMembers(currentChannel.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_view_meeting, container, false)
        activity?.title = selected_meeting.meeting_title

        root.meetingMembersRecyclerView.layoutManager = LinearLayoutManager(activity)

        (selected_meeting.meeting_title + " @ " + selected_meeting.meeting_date_as_string).also {
            root.txtChannelNameSettings.text = it
        }


        root.linearNotOnline.isVisible = false
        root.linearOnline.isVisible = false

        if (selected_meeting.meeting_creator.id == app.currentActiveMember.id || app.currentActiveMember.role.perm_admin) {
            root.btnCancelMeeting.isVisible = true
            root.btnUpdateMeeting.isVisible = true
        } else {
            root.btnCancelMeeting.isVisible = false
            root.btnUpdateMeeting.isVisible = false
        }

        root.textViewTitle.text = selected_meeting.meeting_title
        root.textViewDesc.text = selected_meeting.meeting_desc
        "${selected_meeting.meeting_date_as_string} @ ${selected_meeting.meeting_time_as_string}".also {
            root.textViewDateAndTime.text = it
        }

        if (selected_meeting.online) {
            root.linearOnline.isVisible = true
            root.linearNotOnline.isVisible = false

            root.textViewPlatform.text = selected_meeting.meeting_platform
            root.textViewID.text = selected_meeting.meeting_id
            root.textViewPasscode.text = selected_meeting.meeting_passcode
        } else {
            root.linearOnline.isVisible = false
            root.linearNotOnline.isVisible = true

            root.textViewLoc.text = selected_meeting.meeting_location
        }

        root.btnUpdateMeeting.setOnClickListener {
            navigateTo(EditMeetingFragment.newInstance(selected_meeting, currentChannel))
        }

        root.btnCancelMeeting.setOnClickListener {
            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.popup_warning)
            val proceed = dialog.findViewById(R.id.btnProceed) as Button
            val cancel = dialog.findViewById(R.id.btnCancel) as Button
            proceed.setOnClickListener {
                app.database.child("channels").child(currentChannel!!.id).child("meetings")
                    .child(selected_meeting.meeting_uuid)
                    .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.ref.removeValue()
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                dialog.dismiss()
                navigateTo(MeetingsFragment.newInstance(currentChannel))
            }
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        if (selected_meeting.meeting_creator.id !== auth.currentUser!!.uid) {
            root.linearCreator.isVisible = false
        }


//        Join Zoom Meeting
//        https://us04web.zoom.us/j/71894461029?pwd=N3kzcklxOGFhL3VUbXoweDRYU3RWUT09
//
//        Meeting ID: 718 9446 1029
//        Passcode: gV27ZD


        root.btnJoinMeeting.setOnClickListener {
            if (selected_meeting.online) {
                if (selected_meeting.meeting_platform == "Zoom") {
                    val intent = Intent(activity, CreateZoomMeetingActivity::class.java)
                    startActivityForResult(intent.putExtra("meeting_key", selected_meeting), 0)
                } else if (selected_meeting.meeting_platform == "GoToMeeting") {
                    val callIntent: Intent =
                        Uri.parse("https://global.gotomeeting.com/").let { uri ->
                            Intent(Intent.ACTION_VIEW, uri)
                        }
                    startActivity(callIntent)
                } else if (selected_meeting.meeting_platform == "Google Hangouts") {
                    val callIntent: Intent = Uri.parse("https://hangouts.google.com/").let { uri ->
                        Intent(Intent.ACTION_VIEW, uri)
                    }
                    startActivity(callIntent)
                } else if (selected_meeting.meeting_platform == "Skype") {
                    val callIntent: Intent = Uri.parse("https://www.skype.com/en/").let { uri ->
                        Intent(Intent.ACTION_VIEW, uri)
                    }
                    startActivity(callIntent)
                } else if (selected_meeting.meeting_platform == "Microsoft Teams") {
                    val callIntent: Intent =
                        Uri.parse("https://www.microsoft.com/en-ie/microsoft-teams/group-chat-software")
                            .let { uri ->
                                Intent(Intent.ACTION_VIEW, uri)
                            }
                    startActivity(callIntent)
                } else if (selected_meeting.meeting_platform == "Join.me") {
                    val callIntent: Intent = Uri.parse("https://www.join.me/").let { uri ->
                        Intent(Intent.ACTION_VIEW, uri)
                    }
                    startActivity(callIntent)
                }
            } else {
                root.btnJoinMeeting.isVisible = false
            }
        }

        getAllMeetingMembers()

        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getAllMeetingMembers() {
        memberList = ArrayList<Member>()
        app.database.child("channels").child(currentChannel!!.id).child("meetings")
            .child(selected_meeting.meeting_uuid).child("participants")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase roles error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val member = it.getValue<Member>(Member::class.java)
                        memberList.add(member!!)
                        root.meetingMembersRecyclerView.adapter = MembersAdapter(
                            memberList,
                            this@ViewMeetingFragment
                        )
                        root.meetingMembersRecyclerView.adapter?.notifyDataSetChanged()
                        app.database.child("channels").child(currentChannel!!.id).child("meetings")
                            .child(selected_meeting.meeting_uuid).child("participants")
                            .removeEventListener(this)
                    }
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(meeting: Meeting, channel: Channel) =
            ViewMeetingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("meeting_key", meeting)
                    putParcelable("channel_key", channel)
                }
            }
    }

    override fun onMemberClick(member: Member) {
        //TODO: GO TO MEMBER PROFILE
    }
}