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
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.ConversationAdapter
import ie.wit.teamcom.adapters.ConversationListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Conversation
import kotlinx.android.synthetic.main.activity_channels_list.*
import kotlinx.android.synthetic.main.fragment_conversation.view.*
import kotlinx.android.synthetic.main.fragment_members.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList

class ConversationFragment : Fragment(), AnkoLogger, ConversationListener {

    lateinit var app: MainApp
    lateinit var root: View
    var conversationList = ArrayList<Conversation>()


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
        root = inflater.inflate(R.layout.fragment_conversation, container, false)
        activity?.title = getString(R.string.title_conversations)
        root.conversationsRecyclerView.layoutManager = LinearLayoutManager(activity)

        root.btnAddNewConvo.setOnClickListener {
            navigateTo(CreateConversationFragment.newInstance(currentChannel))
        }

        setSwipeRefresh()
        return root
    }

    fun setSwipeRefresh() {
        root.swiperefreshConversations.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshConversations.isRefreshing = true
                getAllConversations()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllConversations()
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }


    fun checkSwipeRefresh() {
        if (root.swiperefreshConversations.isRefreshing) root.swiperefreshConversations.isRefreshing =
            false
    }

    fun getAllConversations() {
        conversationList = ArrayList<Conversation>()
        app.database.child("channels").child(currentChannel!!.id).child("conversations")
            .orderByChild("conv_date_order")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Conversations error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val convo = it.getValue<Conversation>(Conversation::class.java)
                        convo!!.participants.forEach { par_it ->
                            if (par_it.id == app.currentActiveMember.id) {
                                conversationList.add(convo!!)
                                root.conversationsRecyclerView.adapter = ConversationAdapter(
                                    conversationList,
                                    this@ConversationFragment
                                )
                                root.conversationsRecyclerView.adapter?.notifyDataSetChanged()
                            }
                        }
                        if (conversationList.size > 0) {
                            root.txtEmpty_convo.isVisible = false
                        }
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id)
                            .child("conversations").orderByChild("conv_date_order")
                            .removeEventListener(this)
                    }
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            ConversationFragment().apply {
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

    override fun onConversationClick(convo: Conversation) {
        navigateTo(MessagingFragment.newInstance(convo))
    }
}