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
import ie.wit.teamcom.models.*
import kotlinx.android.synthetic.main.activity_channel_create.*
import org.jetbrains.anko.AnkoLogger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChannelCreate : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_create)

        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        app.getUser()
        //user = intent.getParcelableExtra("user_key")
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


    private fun createChannel(channel: Channel){
        val uid = app.auth.currentUser!!.uid
        val userValues = app.user
        var role = Role(id = UUID.randomUUID().toString(), role_name = "Admin", permission_code = "10000000000000", color_code = "b20202", isDefault = true)
        val roleValues = role

        app.generateDateID("1")

        val childUpdates = HashMap<String, Any>()
        childUpdates["/users/$uid/channels/${channel.id}"] = channel
        childUpdates["/channels/${channel.id}"] = channel
        app.database.updateChildren(childUpdates)

        val userChildUpdates = HashMap<String, Any>()
        userChildUpdates["/channels/${channel.id}/members/$uid"] = userValues
        userChildUpdates["/channels/${channel.id}/admin/$uid"] = userValues
        userChildUpdates["/users/$uid/channels/${channel.id}/orderDateId"] = app.valid_from_cal
        app.database.updateChildren(userChildUpdates)

        val roleChildUpdates = HashMap<String, Any>()
        roleChildUpdates["/channels/${channel.id}/roles/${role.id}"] = roleValues
        roleChildUpdates["/channels/${channel.id}/members/$uid/role"] = roleValues
        app.database.updateChildren(roleChildUpdates)

        var new_member = Member(id = uid, firstName = userValues.firstName, surname = userValues.surname, email = userValues.email, image = 0, login_used = userValues.login_used, role = roleValues)
        var new_log = Log(log_id = app.valid_from_cal, log_triggerer = new_member, log_date = app.dateAsString, log_time = app.timeAsString, log_content = "The channel, ${channel.channelName} has been successfully created.")
        val logChildUpdate = HashMap<String, Any>()
        logChildUpdate["/channels/${channel.id}/logs/${new_log.log_id}"] = new_log
        app.database.updateChildren(logChildUpdate)


        finish()
    }
}