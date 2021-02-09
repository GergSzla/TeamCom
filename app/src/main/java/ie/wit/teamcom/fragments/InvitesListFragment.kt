package ie.wit.teamcom.fragments

import android.R.attr.label
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.InviteAdapter
import ie.wit.teamcom.adapters.InviteListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import kotlinx.android.synthetic.main.dialog_create_invite.view.*
import kotlinx.android.synthetic.main.floating_popup.*
import kotlinx.android.synthetic.main.fragment_invites_list.view.*
import kotlinx.android.synthetic.main.fragment_members.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*


class InvitesListFragment : Fragment(), AnkoLogger, InviteListener {
    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var invitesList = ArrayList<Invite>()
    var invite = Invite()
    var key_set : String = ""
    var myClipboard: ClipboardManager? = null

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
        root = inflater.inflate(R.layout.fragment_invites_list, container, false)
        activity?.title = getString(R.string.title_invites_settings)

        root.btnAddNewInvite.setOnClickListener {
            showDialog("")
        }

        root.invitesRecyclerView.layoutManager = LinearLayoutManager(activity)

        setSwipeRefresh()

        return root
    }

//    private fun validateForm(): Boolean{
//        var valid = true
//
//        val exp = root.txtExpires.text.toString()
//        if (TextUtils.isEmpty(exp)) {
//            root.txtExpires.error = "Expires In (Hrs) Required."
//            valid = false
//        } else {
//            root.txtExpires.error = null
//        }
//
//        if (root.txtUses.text.toString().toInt() >= 1){
//            val uses = root.txtUses.text.toString()
//            if (TextUtils.isEmpty(uses)) {
//                root.txtUses.error = "Uses (Minimum 1) Required."
//                valid = false
//            } else {
//                root.txtUses.error = null
//            }
//        }
//
//
//        return valid
//    }

    override fun onInviteClicked(invite: Invite) {
        app.copy_invitation(invite.invite_code)
        Toast.makeText(context,"Invite Code Copied to Clipboard!",Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllChannelInvites()
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    fun getAllChannelInvites() {
        invitesList = ArrayList<Invite>()
        app.database.child("channels").child(currentChannel!!.id).child("invites").orderByChild("valid_from")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase invites error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val invite = it.getValue<Invite>(Invite::class.java)
                        invitesList.add(invite!!)
                        root.invitesRecyclerView.adapter = InviteAdapter(
                            invitesList,
                            this@InvitesListFragment
                        )
                        root.invitesRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("invites")
                            .orderByChild(
                                "valid_from"
                            )
                            .removeEventListener(this)
                    }
                }
            })
    }

    fun setSwipeRefresh() {
        root.swiperefreshInvites.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshInvites.isRefreshing = true
                getAllChannelInvites()
            }
        })
    }



    fun checkSwipeRefresh() {
        if (root.swiperefreshInvites.isRefreshing) root.swiperefreshInvites.isRefreshing = false
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            InvitesListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

    private fun showDialog(title: String) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_create_invite)
        val create = dialog.findViewById(R.id.buttonSubmit) as Button
        val cancel = dialog.findViewById(R.id.buttonCancel) as Button
        val hrs_active = dialog.findViewById(R.id.txtExpires) as EditText
        val uses = dialog.findViewById(R.id.txtUses) as EditText
        create.setOnClickListener {
//            validateForm()
//            if (validateForm()){
                createInvite(hrs_active.text.toString(), uses.text.toString())
                dialog.dismiss()
//            }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun createInvite(hrs_active: String, uses: String){
        app.generateDateID(hrs_active)
        invite.valid_to_as_string = app.valid_to_String
        invite.valid_from_as_string = app.valid_from_String
        invite.valid_from = app.valid_from_cal
        invite.valid_to = app.valid_to_cal
        invite.auto_deletion = app.auto_delete_cal
        invite.id = UUID.randomUUID().toString()
        invite.invite_uses = 0
        invite.invite_use_limit = uses.toInt()
        keyGen()
        invite.invite_code = "tc-" + currentChannel.channelName.take(3).toLowerCase() + "@" + key_set
        writeNewInvite()

    }


    fun keyGen(){
        var res: Resources = resources
        var key_1 = res.getStringArray(R.array.randomLetters).random()
        var key_2 = res.getStringArray(R.array.randomLetters).random()
        var key_3 = res.getStringArray(R.array.randomLetters).random()
        var key_4 = res.getStringArray(R.array.randomLetters).random()
        var key_5 = res.getStringArray(R.array.randomLetters).random()
        var key_6 = res.getStringArray(R.array.randomLetters).random()
        var key_7 = res.getStringArray(R.array.randomLetters).random()

        key_set = key_1 + key_2 + "-" + key_3 + key_4 + key_5 + key_6 + "_" + key_7
    }

    fun writeNewInvite(){
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    val child1Updates = HashMap<String, Any>()
                    val logUpdates = HashMap<String, Any>()


                    child1Updates["/invites/${invite.invite_code}/"] = invite
                    app.database.updateChildren(child1Updates)
                    childUpdates["/channels/${currentChannel.id}/invites/${invite.invite_code}"] =
                        invite
                    childUpdates["/invites/${invite.invite_code}/belongs_to/${currentChannel.id}"] =
                        currentChannel
                    app.database.updateChildren(childUpdates)

                    var new_log = Log(
                        log_id = app.valid_from_cal,
                        log_triggerer = app.currentActiveMember,
                        log_date = app.dateAsString,
                        log_time = app.timeAsString,
                        log_content = "Invite was created [${invite.invite_code}] with ${invite.invite_use_limit} uses and expires on ${invite.valid_to_as_string}."
                    )
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)


                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                    //startActivity(intentFor<ChannelsListActivity>().putExtra("user_key", user))
                }
            })
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    ///Log Nightmare Functions Below
    //Get user as member (MUST HAVE ROLE!!!)

}