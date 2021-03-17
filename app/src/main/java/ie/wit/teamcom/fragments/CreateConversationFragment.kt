package ie.wit.teamcom.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.ConvoMembersAdapter
import ie.wit.teamcom.adapters.ConvoMembersListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import ie.wit.utils.SwipeToRemoveSelectedMemberCallback
import ie.wit.utils.SwipeToSelectMemberCallback
import kotlinx.android.synthetic.main.fragment_create_conversation.view.*
import kotlinx.android.synthetic.main.fragment_create_conversation.view.btnRefr
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*


class CreateConversationFragment : Fragment(), AnkoLogger, ConvoMembersListener {

    lateinit var app: MainApp
    lateinit var eventListener: ValueEventListener
    lateinit var root: View
    var new_conversation = Conversation()
    var channel_members = ArrayList<Member>()
    var selected_members = ArrayList<Member>()

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
        root = inflater.inflate(R.layout.fragment_create_conversation, container, false)
        activity?.title = getString(R.string.title_create_conversations)

        root.selectMemsConvoRecyclerView.layoutManager = LinearLayoutManager(activity)
        root.selectedMemsConvoRecyclerView.layoutManager = LinearLayoutManager(activity)

        root.btnCreateConvo.setOnClickListener {
            if (selected_members.size > 2 && root.editTxtGroupChatName.text.toString() != "") {
                app.generateDateID("1")
                new_conversation.conv_date_order = app.valid_from_cal
                new_conversation.gc_name = root.editTxtGroupChatName.text.toString()

                new_conversation.id = UUID.randomUUID().toString()
                new_conversation.participants = selected_members
                new_conversation.participants.add(app.currentActiveMember)
                createNewConvo(new_conversation)
                navigateTo(ConversationFragment.newInstance(currentChannel))
            } else if (selected_members.size > 2 && root.editTxtGroupChatName.text.toString() == "") {
                Toast.makeText(context, "Group Chat Name Is Required!", Toast.LENGTH_LONG)
            } else if(selected_members.size == 1){
                app.generateDateID("1")
                new_conversation.conv_date_order = app.valid_from_cal
                new_conversation.gc_name = ""

                new_conversation.id = UUID.randomUUID().toString()
                new_conversation.participants = selected_members
                new_conversation.participants.add(app.currentActiveMember)
                createNewConvo(new_conversation)
                navigateTo(ConversationFragment.newInstance(currentChannel))
            }
        }

        root.btnRefr.setOnClickListener {
            root.swiperefreshCreateConvo_1.isRefreshing = true
            root.swiperefreshCreateConvo_2.isRefreshing = true
            channelMembersToRecycler()
        }

        getAllChannelMembers()


        val swipeMoveToSelectedHandler = object : SwipeToSelectMemberCallback(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter1 = root.selectMemsConvoRecyclerView.adapter as ConvoMembersAdapter
                adapter1.removeAt(viewHolder.adapterPosition)
                move_to_selected((viewHolder.itemView.tag as Member))
            }
        }
        val itemTouchMoveHelper = ItemTouchHelper(swipeMoveToSelectedHandler)
        itemTouchMoveHelper.attachToRecyclerView(root.selectMemsConvoRecyclerView)

        val swipeRemoveFromSelectedHandler =
            object : SwipeToRemoveSelectedMemberCallback(requireActivity()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter2 = root.selectedMemsConvoRecyclerView.adapter as ConvoMembersAdapter
                    adapter2.removeAt(viewHolder.adapterPosition)
                    remove_from_selected((viewHolder.itemView.tag as Member))
                }
            }
        val itemTouchMoveBackHelper = ItemTouchHelper(swipeRemoveFromSelectedHandler)
        itemTouchMoveBackHelper.attachToRecyclerView(root.selectedMemsConvoRecyclerView)

        return root
    }

    fun move_to_selected(member: Member) {
        channel_members.remove(member)
        selected_members.add(member)

        if (selected_members.size > 1) {
            root.txtGCName.isVisible = true
            root.editTxtGroupChatName.isVisible = true
        } else {
            root.txtGCName.isVisible = false
            root.editTxtGroupChatName.isVisible = false
        }

        channelMembersToRecycler()
    }

    fun remove_from_selected(member: Member) {
        selected_members.remove(member)
        channel_members.add(member)

        channelMembersToRecycler()
    }

    fun setSwipeRefresh() {
        root.swiperefreshCreateConvo_1.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshCreateConvo_1.isRefreshing = true
                root.swiperefreshCreateConvo_2.isRefreshing = true
                channelMembersToRecycler()
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefreshCreateConvo_1.isRefreshing) root.swiperefreshCreateConvo_1.isRefreshing =
            false
        if (root.swiperefreshCreateConvo_2.isRefreshing) root.swiperefreshCreateConvo_2.isRefreshing =
            false
    }

    fun getAllChannelMembers() {
        channel_members = ArrayList<Member>()
        app.database.child("channels").child(currentChannel.id).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase members error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val member = it.getValue<Member>(Member::class.java)
                        channel_members.add(member!!)

                        app.database.child("channels").child(currentChannel!!.id).child("members")
                            .removeEventListener(this)
                    }
                    channel_members.remove(app.currentActiveMember)
                    channelMembersToRecycler()
                }
            })
    }

    fun channelMembersToRecycler() {
        channel_members.forEach {
            root.selectMemsConvoRecyclerView.adapter = ConvoMembersAdapter(
                channel_members,
                this@CreateConversationFragment
            )
            root.selectMemsConvoRecyclerView.adapter?.notifyDataSetChanged()
        }
        selected_members.forEach {
            root.selectedMemsConvoRecyclerView.adapter = ConvoMembersAdapter(
                selected_members,
                this@CreateConversationFragment
            )
            root.selectedMemsConvoRecyclerView.adapter?.notifyDataSetChanged()
        }
        checkSwipeRefresh()
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

    private fun createNewConvo(conversation: Conversation) {
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel!!.id}/conversations/${conversation.id}"] =
                        conversation
                    app.database.updateChildren(childUpdates)

                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                }
            })
    }


    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            CreateConversationFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }
}