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
import ie.wit.teamcom.adapters.InviteAdapter
import ie.wit.teamcom.adapters.MessageAdapter
import ie.wit.teamcom.adapters.MessageListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import kotlinx.android.synthetic.main.fragment_conversation.view.*
import kotlinx.android.synthetic.main.fragment_conversation.view.conversationsRecyclerView
import kotlinx.android.synthetic.main.fragment_invites_list.view.*
import kotlinx.android.synthetic.main.fragment_messaging.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

class MessagingFragment : Fragment(), AnkoLogger, MessageListener {

    lateinit var app: MainApp
    lateinit var eventListener: ValueEventListener
    lateinit var root: View
    var current_conversation = Conversation()
    var new_message = Message()
    var messageList = ArrayList<Message>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            current_conversation = it.getParcelable("channel_key")!!
        }
        app.getAllMembers(currentChannel.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_messaging, container, false)
        activity?.title = getString(R.string.title_conversations)
        root.messagesRecyclerView.layoutManager = LinearLayoutManager(activity)


        getAllConvoMessages()

        root.imgBtnSendMsg.setOnClickListener {
            new_message.content = root.txtMessage.text.toString()
            new_message.author = app.currentActiveMember
            new_message.id = UUID.randomUUID().toString()
            app.generateDateID("1")
            new_message.mes_date_order = app.valid_from_cal
            new_message.read_by.add(app.currentActiveMember)
            new_message.msg_date = app.dateAsString
            new_message.msg_time = app.timeAsString
            current_conversation.messages.add(new_message)
            sendMsg()
            //TODO: Push notification to other participant about new message
            //pushNotification()
        }

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

    fun sendMsg() {
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()

                    childUpdates["/channels/${currentChannel.id}/conversations/${current_conversation.id}"] =
                        current_conversation
                    app.database.updateChildren(childUpdates)

                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                    navigateTo(MessagingFragment.newInstance(current_conversation))
                    root.txtMessage.setText(" ")
                }
            })
    }


    fun getAllConvoMessages() {
        messageList = ArrayList<Message>()
        app.database.child("channels").child(currentChannel!!.id).child("conversations")
            .child(current_conversation.id).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Msg error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val msg = it.getValue<Message>(Message::class.java)
                        messageList.add(msg!!)
                        root.messagesRecyclerView.adapter = MessageAdapter(
                            messageList,
                            this@MessagingFragment
                        )
                        root.messagesRecyclerView.adapter?.notifyDataSetChanged()

                        if (messageList.size > 0 ) {
                            root.txtEmpty_msgs.isVisible = false
                        }
                        app.database.child("channels").child(currentChannel!!.id)
                            .child("conversations").child(current_conversation.id).child("messages")
                            .removeEventListener(this)
                    }
                    var size = messageList.size
                    root.messagesRecyclerView.scrollToPosition(size - 1)
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(conversation: Conversation) =
            MessagingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", conversation)
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
}