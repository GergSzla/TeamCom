package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.MeetingListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Meeting
import kotlinx.android.synthetic.main.fragment_meetings.view.*
import org.jetbrains.anko.AnkoLogger

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
        activity?.title = getString(R.string.title_conversations)
        root.meetingsRecyclerView.layoutManager = LinearLayoutManager(activity)

        root.btnAddNewMeeting.setOnClickListener {

        }

        return root
    }

    fun getAllMeetings(){

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
        getAllMeetings()
    }

    fun checkSwipeRefresh() {
        if (root.swiperefreshMeetings.isRefreshing) root.swiperefreshMeetings.isRefreshing = false
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
        TODO("Not yet implemented")
    }
}