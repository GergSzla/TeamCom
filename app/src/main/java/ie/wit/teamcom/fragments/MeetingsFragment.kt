package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.MeetingListener
import ie.wit.teamcom.adapters.MeetingsAdapter
import ie.wit.teamcom.adapters.RemindersAdapter
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Comment
import ie.wit.teamcom.models.Meeting
import ie.wit.teamcom.models.Reminder
import kotlinx.android.synthetic.main.fragment_meetings.view.*
import kotlinx.android.synthetic.main.fragment_reminders.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList

class MeetingsFragment : Fragment(), AnkoLogger, MeetingListener {

    lateinit var app: MainApp
    lateinit var root: View
    var meetingList = ArrayList<Meeting>()

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
            navigateTo(CreateMeetingFragment.newInstance(currentChannel))
        }
        setSwipeRefresh()
        return root
    }

    fun getAllMeetings(){
        meetingList = ArrayList<Meeting>()
        app.database.child("channels").child(currentChannel!!.id).child("meetings")
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
                        root.meetingsRecyclerView.adapter = MeetingsAdapter(
                            meetingList,
                            this@MeetingsFragment
                        )
                        root.meetingsRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("meetings").removeEventListener(this)
                    }
                }
            })
    }

    fun setSwipeRefresh() {
        root.swiperefreshMeetings.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshMeetings.isRefreshing = true
                getAllMeetings()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel,app.currentActiveMember)
        getAllMeetings()
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel,app.currentActiveMember)
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
        navigateTo(ViewMeetingFragment.newInstance(meeting, currentChannel))
    }
}