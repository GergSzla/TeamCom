package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.NotificationsAdapter
import ie.wit.teamcom.adapters.NotificationsListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.AppNotification
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.fragment_news_feed.view.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList
import java.util.HashMap

var notification_list = ArrayList<AppNotification>()

class NotificationsFragment : Fragment(), AnkoLogger, NotificationsListener {

    lateinit var root: View
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_notifications, container, false)
        activity?.title = getString(R.string.title_notif)
        root.notificationsRecyclerView.layoutManager = LinearLayoutManager(activity)

        setSwipeRefresh()


        return root
    }

    fun setSwipeRefresh() {
        root.refreshNotifications.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.refreshNotifications.isRefreshing = true
                getAllNotifications(app.database,false)
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.refreshNotifications.isRefreshing) root.refreshNotifications.isRefreshing = false
    }

    fun getAllNotifications(db : DatabaseReference,notif_check : Boolean) {
        notification_list = ArrayList<AppNotification>()
        notification_list.clear()

        db.child("channels").child(currentChannel.id).child("notifications")
            .child(auth.currentUser!!.uid).orderByChild(
                "date_id"
            )
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Notifications error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val notification = it.getValue<AppNotification>(AppNotification::class.java)
                        notification_list.add(notification!!)

                        if(!notif_check){
                            root.notificationsRecyclerView.adapter = NotificationsAdapter(
                                notification_list,
                                this@NotificationsFragment
                            )
                            root.notificationsRecyclerView.adapter?.notifyDataSetChanged()
                            if (notification_list.size > 0) {
                                root.txtEmpty_Notif.isVisible = false
                            }
                            checkSwipeRefresh()
                        }

                        db.child("channels").child(currentChannel.id)
                            .child("notifications").child(auth.currentUser!!.uid)
                            .orderByChild(
                                "date_id"
                            )
                            .removeEventListener(this)
                    }
                }
            })
    }

    fun pushNotification(db : DatabaseReference,new_notification: AppNotification) {
        db.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()

                    childUpdates["/channels/${currentChannel.id}/notifications/${auth.currentUser!!.uid}/${new_notification.id}"] =
                        new_notification
                    db.updateChildren(childUpdates)

                    getAllNotifications(db, true)

                    db.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                }
            })
    }


    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            NotificationsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllNotifications(app.database, false)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    override fun onNotificationClicked(notification: AppNotification) {
        when (notification.type) {
            "Conversation" -> {
                navigateTo(ConversationFragment.newInstance(currentChannel))
            }
            "Task" -> {
                navigateTo(ProjectListFragment.newInstance(currentChannel))
            }
            "Reminder" -> {
                navigateTo(RemindersFragment.newInstance(currentChannel))
            }
            "Meeting" -> {
                navigateTo(MeetingsFragment.newInstance(currentChannel))
            }
            "Event" -> {
                navigateTo(CalendarFragment.newInstance(currentChannel))
            }
        }
        notification.seen = true

        val childUpdates = HashMap<String, Any>()
        childUpdates["/channels/${currentChannel.id}/notifications/${auth.currentUser!!.uid}/${notification.id}"] =
            notification
        app.database.updateChildren(childUpdates)

    }
}