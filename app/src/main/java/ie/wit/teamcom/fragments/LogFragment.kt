package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.LogAdapter
import ie.wit.teamcom.adapters.LogListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Log
import kotlinx.android.synthetic.main.fragment_log.view.*
import kotlinx.android.synthetic.main.fragment_role_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList


class LogFragment : Fragment(), AnkoLogger, LogListener {
    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var logList = ArrayList<Log>()

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
        root = inflater.inflate(R.layout.fragment_log, container, false)
        activity?.title = getString(R.string.title_logs)
        root.logsRecyclerView.layoutManager = LinearLayoutManager(activity)

        setSwipeRefresh()
        return root
    }

    fun getAllChannelLogs() {
        logList = ArrayList<Log>()
        app.database.child("channels").child(currentChannel!!.id).child("logs")
            .orderByChild("log_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase roles error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val log = it.getValue<Log>(Log::class.java)

                        logList.add(log!!)
                        root.logsRecyclerView.adapter = LogAdapter(logList, this@LogFragment)
                        root.logsRecyclerView.adapter?.notifyDataSetChanged()
                    }
                    checkSwipeRefresh()
                    app.database.child("channels").child(currentChannel!!.id).child("logs")
                        .orderByChild("log_id")
                        .removeEventListener(this)

                }
            })
    }

    fun setSwipeRefresh() {
        root.logsrefreshLogs.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.logsrefreshLogs.isRefreshing = true
                getAllChannelLogs()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllChannelLogs()
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    fun checkSwipeRefresh() {
        if (root.logsrefreshLogs.isRefreshing) root.logsrefreshLogs.isRefreshing = false
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            LogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

}