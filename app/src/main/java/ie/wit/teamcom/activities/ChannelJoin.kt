package ie.wit.teamcom.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Log
import kotlinx.android.synthetic.main.activity_channel_create.*
import kotlinx.android.synthetic.main.activity_channel_join.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class ChannelJoin : AppCompatActivity(), AnkoLogger {


    lateinit var app: MainApp
    var channelList = ArrayList<Channel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_join)

        app = application as MainApp
        auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        app.getUser()
        btnJoinNew.setOnClickListener {
            validateForm()
            if (txtChannelCode.text.toString() !== "") {
                app.generateDateID("1")
                checkInvite(txtChannelCode.text.toString())
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val inv_code = txtChannelCode.text.toString()
        if (TextUtils.isEmpty(inv_code)) {
            txtChannelCode.error = "Channel Invite Code Required."
            valid = false
        } else {
            txtChannelCode.error = null
        }

        return valid
    }

    private fun checkInvite(invite_code: String) {
        val uid = auth.currentUser!!.uid

        app.database.child("invites")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(invite_code)) {
                        app.generateDateID("1")
                        if (snapshot.child(invite_code).child("valid_to").value.toString()
                                .toLong() < app.valid_from_cal && snapshot.child(invite_code)
                                .child("available").value == true
                        ) {
                            val childUpdates = HashMap<String, Any>()
                            app.database.child("invites").child(invite_code).child("belongs_to")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {
                                        info("Firebase error : ${error.message}")
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val children = snapshot.children
                                        children.forEach {
                                            val channel = it.getValue<Channel>(Channel::class.java)

                                            channelList.add(channel!!)
                                            val channelUpd = HashMap<String, Any>()
                                            childUpdates["/users/$uid/channels/${channelList[0].id}"] =
                                                channelList[0]
                                            childUpdates["/channels/${channelList[0].id}/members/$uid"] =
                                                app.user
                                            channelUpd["/users/$uid/channels/${channelList[0].id}/orderDateId"] =
                                                app.valid_from_cal

                                            app.database.updateChildren(childUpdates)
                                            app.database.updateChildren(channelUpd)

                                            app.getAllMembers(channelList[0].id)
                                            val logUpdates = HashMap<String, Any>()
                                            var new_log = Log(
                                                log_id = app.valid_from_cal,
                                                log_triggerer = app.currentActiveMember,
                                                log_date = app.dateAsString,
                                                log_time = app.timeAsString,
                                                log_content = "${app.user.firstName} ${app.user.surname} has joined the channel."
                                            )
                                            logUpdates["/channels/${channelList[0].id}/logs/${new_log.log_id}"] =
                                                new_log
                                            app.database.updateChildren(logUpdates)

                                            app.database.child("invites").child(invite_code)
                                                .child("belongs_to")
                                                .removeEventListener(this)
                                        }
                                    }
                                })
                            app.database.child("invites").child(invite_code)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {
                                        info("Firebase error : ${error.message}")
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {


                                        var invite_uses =
                                            snapshot.child("invite_uses").value.toString()
                                                .toInt()
                                        var invite_use_limit =
                                            snapshot.child("invite_use_limit").value.toString()
                                                .toInt()
                                        invite_uses += 1

                                        val inv_update_uses = HashMap<String, Any>()
                                        val inv_update_uses_ = HashMap<String, Any>()
                                        inv_update_uses["/channels/${channelList[0].id}/invites/$invite_code/invite_uses/"] =
                                            invite_uses
                                        inv_update_uses_["/invites/$invite_code/invite_uses/"] =
                                            invite_uses
                                        app.database.updateChildren(inv_update_uses)
                                        app.database.updateChildren(inv_update_uses_)

                                        if (invite_uses == invite_use_limit) {
                                            val inv_update_availability = HashMap<String, Any>()
                                            val inv_update_availability_ = HashMap<String, Any>()

                                            inv_update_availability["/channels/${channelList[0].id}/invites/$invite_code/available/"] =
                                                false
                                            inv_update_availability_["/invites/$invite_code/available/"] =
                                                false
                                            app.database.updateChildren(inv_update_availability)
                                            app.database.updateChildren(inv_update_availability_)
                                        }

                                        app.database.child("invites").child(invite_code)
                                            .removeEventListener(this)

                                    }
                                })
                        } else {
                            Toast.makeText(
                                this@ChannelJoin,
                                "This Invite Has Expired",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ChannelJoin,
                            "This Invite Does Not Exist",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    app.database.child("invites")
                        .removeEventListener(this)
                }
            })
    }


}