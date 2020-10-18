package ie.wit.teamcom.fragments

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
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
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Invite
import kotlinx.android.synthetic.main.fragment_invites_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.time.LocalDateTime
import java.time.Month
import java.time.MonthDay
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.*


class InvitesListFragment : Fragment(), AnkoLogger, InviteListener {
    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var invitesList = ArrayList<Invite>()
    var valid_from_cal: Long = 0
    var valid_to_cal: Long = 0
    var auto_delete_cal: Long = 0
    var invite = Invite()
    var key_set : String = ""

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
        root = inflater.inflate(R.layout.fragment_invites_list, container, false)
        activity?.title = getString(R.string.title_invites_settings)

        root.btnAddNewInvite.setOnClickListener {
            //navigateTo(InviteCreateFragment.newInstance(currentChannel))
            showDialog("Fuck Sake")
        }

        root.invitesRecyclerView.layoutManager = LinearLayoutManager(activity)

        setSwipeRefresh()

        return root
    }

    fun getAllChannelInvites() {
        invitesList = ArrayList<Invite>()
        app.database.child("channels").child(currentChannel!!.id).child("invites").orderByChild("valid_from")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase roles error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
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

    override fun onResume() {
        super.onResume()
        getAllChannelInvites()
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
            createInvite(hrs_active.text.toString(), uses.text.toString())
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun createInvite(hrs_active: String, uses: String){
        generateDateID(hrs_active)
        invite.valid_from = valid_from_cal
        invite.valid_to = valid_to_cal
        invite.auto_deletion = auto_delete_cal
        invite.id = UUID.randomUUID().toString()
        invite.invite_uses = 0
        invite.invite_use_limit = uses.toInt()
        keyGen()
        invite.invite_code = "tc-" + currentChannel.channelName.take(3).toLowerCase() + "@" + key_set
        writeNewInvite()
    }

    fun generateDateID(hrs : String) {
        /*
        GETS VALID FROM
         */
        var currentEndDateTime= LocalDateTime.now()
        var year = Calendar.getInstance().get(Calendar.YEAR).toString()
        var month = ""
        month = if (Calendar.getInstance().get(Calendar.MONTH)+1 < 10){
            "0"+(Calendar.getInstance().get(Calendar.MONTH)+1).toString()
        }else{
            (Calendar.getInstance().get(Calendar.MONTH)+1).toString()
        }

        var day = ""
        day = if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10){
            "0"+(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).toString()
        }else{
            (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).toString()
        }
        var hour = currentEndDateTime.format(DateTimeFormatter.ofPattern("HH")).toString()
        var minutes = currentEndDateTime.format(DateTimeFormatter.ofPattern("mm")).toString()
        var seconds = currentEndDateTime.format(DateTimeFormatter.ofPattern("ss")).toString()

        /*
        NOW GET FOR VALID TO (+HRS VALID)
         */
        invite.valid_from_as_string = day+"/"+month+"/"+year+", "+hour+":"+minutes+":"+seconds

        var dateId = year+month+day+hour+minutes+seconds

        var cal = LocalDateTime.now().plusHours(hrs.toLong())

        var year1 = cal.year.toString()
        var month1  = ""
        month1 = if (cal.monthValue < 10) {
            "0" + (cal.monthValue).toString()
        } else{
            (cal.monthValue).toString()
        }

        var day1 =""
        day1 = if (cal.dayOfMonth < 10) {
            "0" + (cal.dayOfMonth).toString()
        } else{
            (cal.dayOfMonth).toString()
        }

        var hour1 = ""
        hour1 = if (cal.hour < 10) {
            "0" + (cal.hour).toString()
        } else{
            (cal.hour).toString()
        }

        var minutes1 = ""
        minutes1 = if (cal.minute < 10) {
            "0" + (cal.minute).toString()
        } else{
            (cal.minute).toString()
        }

        var seconds1= ""
        seconds1 = if (cal.second < 10) {
            "0" + (cal.second).toString()
        } else{
            (cal.second).toString()
        }

        invite.valid_to_as_string = day1+"/"+month1+"/"+year1+", "+hour1+":"+minutes1+":"+seconds1
        var dateId_end = year1+month1+day1+hour1+minutes1+seconds1

        /*
        NOW GET AUTO DELETION
         */

        var cal1 = LocalDateTime.now().plusHours(hrs.toLong() + 36)

        var year2 = cal1.year.toString()
        var month2  = ""
        month2 = if (cal1.monthValue+1 < 10) {
            "0" + (cal1.monthValue + 1).toString()
        } else{
            (cal1.monthValue+1).toString()
        }

        var day2 =""
        day2 = if (cal1.dayOfMonth < 10) {
            "0" + (cal1.dayOfMonth).toString()
        } else{
            (cal1.dayOfMonth).toString()
        }

        var hour2 = ""
        hour2 = if (cal1.hour < 10) {
            "0" + (cal1.hour).toString()
        } else{
            (cal1.hour).toString()
        }

        var minutes2 = ""
        minutes2 = if (cal1.minute < 10) {
            "0" + (cal1.minute).toString()
        } else{
            (cal1.minute).toString()
        }

        var seconds2= ""
        seconds2 = if (cal1.second < 10) {
            "0" + (cal1.second).toString()
        } else{
            (cal1.second).toString()
        }

        var dateId_delete = year2+month2+day2+hour2+minutes2+seconds2


        auto_delete_cal = 100000000000000 - dateId_delete.toLong()
        valid_to_cal = 100000000000000 - dateId_end.toLong()
        valid_from_cal = 100000000000000 - dateId.toLong()
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
                    childUpdates["/channels/${currentChannel.id}/invites/${invite.invite_code}"] = invite
                    childUpdates["/invites/${invite.invite_code}/belongs_to/${currentChannel.id}"] = currentChannel

                    app.database.updateChildren(childUpdates)

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
}