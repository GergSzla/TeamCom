package ie.wit.teamcom.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import kotlinx.android.synthetic.main.fragment_create_conversation.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.*


class CreateConversationFragment : Fragment(),AnkoLogger {

    var members_as_string = ArrayList<String>()
    var members_list =  ArrayList<Member>()
    lateinit var app: MainApp
    lateinit var eventListener : ValueEventListener
    lateinit var root: View
    var new_conversation = Conversation()
    var convo_participant = Member()

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

        getMemberNames()

        root.btnCreateConvo.setOnClickListener {
            app.generateDateID("1")
            new_conversation.conv_date_order = app.valid_from_cal
            if (root.editTxtGroupChatName.text.toString() != ""){
                new_conversation.gc_name = root.editTxtGroupChatName.text.toString()
            }
            new_conversation.id = UUID.randomUUID().toString()
            var i = 0
            members_list.forEach{
                if (root.spinnerMembers.selectedItem == (members_list[i].firstName+" "+members_list[i].surname )){
                    convo_participant = members_list[i]
                } else {
                    i++
                }
            }
            new_conversation.participants.add(app.currentActiveMember)
            new_conversation.participants.add(convo_participant)
            createNewConvo(new_conversation)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel,app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel,app.currentActiveMember)
    }

    private fun createNewConvo(conversation: Conversation) {
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel!!.id}/conversations/${conversation.id}"] = conversation
                    app.database.updateChildren(childUpdates)

                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                }
            })
    }

    fun getMemberNames(){
        members_as_string.add("")
        app.database.child("channels").child(currentChannel!!.id).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val member = it.getValue<Member>(Member::class.java)
                        if (member!!.id != app.currentActiveMember.id) {
                            members_as_string.add(member!!.firstName + " " + member!!.surname)
                            members_list.add(member)
                        }

                        app.database.child("channel").child(currentChannel!!.id).child("members")
                            .removeEventListener(this)
                    }
                    val adapter1 = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item, // Layout
                        members_as_string
                    )
                    adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                    root.spinnerMembers.adapter = adapter1
                    root.spinnerMembers.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            parentView: AdapterView<*>?,
                            selectedItemView: View,
                            position: Int,
                            id: Long
                        ) {
                            root.txtMembers.text = ""
                            root.txtMembers.append("[${root.spinnerMembers.selectedItem}] ")
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>?) {
                        }
                    }
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