package ie.wit.teamcom.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.activity_channel_create.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import java.util.*

class ChannelCreate : AppCompatActivity(), AnkoLogger {

    var user = Account()
    lateinit var eventListener : ValueEventListener
    var ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_create)

        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()

        btnCreateNew.setOnClickListener {
            createChannel(
                Channel(
                    id = UUID.randomUUID().toString(),
                    channelName = txtChannelName.text.toString(),
                    description = txtChannelDesc.text.toString(),
                    image = 0
                )
            )
        }
    }
    private fun createChannel(channel: Channel){
        val uid = app.auth.currentUser!!.uid
        val channelValues = channel.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/users/$uid/channels/${channel.id}"] = channelValues
        childUpdates["/channels/${channel.id}"] = channelValues

        app.database.updateChildren(childUpdates)

    }
}