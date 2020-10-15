package ie.wit.teamcom.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.activity_channel_join.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class ChannelJoin : AppCompatActivity() , AnkoLogger {

    var user = Account()
    lateinit var eventListener : ValueEventListener
    lateinit var app: MainApp
    var currentDateId: Long = 0
    var channelList = ArrayList<Channel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_join)

        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        btnJoinNew.setOnClickListener {
            checkInvite(txtChannelCode.text.toString())
        }
    }

    private fun checkInvite(invite_code: String) {
        val uid = app.auth.currentUser!!.uid

        app.database.child("invites")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(invite_code)){
                        generateDateID()
                        if(snapshot.child(invite_code).child("valid_to").value.toString().toLong() > currentDateId){

                            val childUpdates = HashMap<String, Any>()
                            app.database.child("invites").child(invite_code).child("belongs_to")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {
                                        info("Firebase error : ${error.message}")
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val children = snapshot.children
                                        children.forEach {
                                            val channel = it.
                                            getValue<Channel>(Channel::class.java)

                                            channelList.add(channel!!)
                                            val channelUpd = HashMap<String, Any>()
                                            childUpdates["/users/$uid/channels/${channelList[0].id}"] = channelList[0]
                                            channelUpd["/users/$uid/channels/${channelList[0].id}/orderDateId"] = currentDateId

                                            app.database.updateChildren(childUpdates)
                                            app.database.updateChildren(channelUpd)


                                            app.database.child("invites").child(invite_code).child("belongs_to")
                                                .removeEventListener(this)
                                        }
                                    }
                                })
                        } else {
                            Toast.makeText(this@ChannelJoin, "This Invite Has Expired", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@ChannelJoin, "This Invite Does Not Exist", Toast.LENGTH_LONG).show()
                    }
                    app.database.child("user-stats")
                        .removeEventListener(this)
                }
            })
    }

    fun generateDateID() {
        var currentEndDateTime= LocalDateTime.now()
        var year = Calendar.getInstance().get(Calendar.YEAR).toString()
        var month = ""
        if (Calendar.getInstance().get(Calendar.MONTH)+1 < 10){
            month = "0"+(Calendar.getInstance().get(Calendar.MONTH)+1).toString()
        }else{
            month = (Calendar.getInstance().get(Calendar.MONTH)+1).toString()
        }
        var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        var hour = currentEndDateTime.format(DateTimeFormatter.ofPattern("HH")).toString()
        var minutes = currentEndDateTime.format(DateTimeFormatter.ofPattern("mm")).toString()
        var seconds = currentEndDateTime.format(DateTimeFormatter.ofPattern("ss")).toString()


        var dateId = year+month+day+hour+minutes+seconds
        currentDateId = 100000000000000 - dateId.toLong()
    }
}