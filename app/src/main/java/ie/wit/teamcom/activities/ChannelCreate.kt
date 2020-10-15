package ie.wit.teamcom.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_channel_create.*
import org.jetbrains.anko.AnkoLogger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChannelCreate : AppCompatActivity(), AnkoLogger {

    var user = Account()
    lateinit var eventListener : ValueEventListener
    var ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
    lateinit var app: MainApp
    var orderDateId :Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_create)

        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        user = intent.getParcelableExtra("user_key")
        btnCreateNew.setOnClickListener {
            createChannel(
                Channel(
                    id = UUID.randomUUID().toString(),
                    channelName = txtChannelName.text.toString(),
                    channelDescription = txtChannelDesc.text.toString(),
                    image = 0
                )
            )
        }
    }

    private fun getUser(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("users").child(uid)
        eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(Account::class.java)!!

                uidRef.removeEventListener(this)

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        uidRef.addListenerForSingleValueEvent(eventListener)

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
        orderDateId = 100000000000000 - dateId.toLong()
    }

    private fun createChannel(channel: Channel){
        val uid = app.auth.currentUser!!.uid
        val userValues = user

        generateDateID()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/users/$uid/channels/${channel.id}"] = channel
        childUpdates["/channels/${channel.id}"] = channel
        app.database.updateChildren(childUpdates)

        val userChildUpdates = HashMap<String, Any>()
        userChildUpdates["/channels/${channel.id}/members/$uid"] = userValues
        userChildUpdates["/channels/${channel.id}/admin/$uid"] = userValues
        userChildUpdates["/users/$uid/channels/${channel.id}/orderDateId"] = orderDateId

        //userChildUpdates["/users/$uid/channels/${channel.id}/members/$uid"] = userValues
        app.database.updateChildren(userChildUpdates)

        finish()
    }
}