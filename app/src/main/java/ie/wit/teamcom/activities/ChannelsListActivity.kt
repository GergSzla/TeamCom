package ie.wit.teamcom.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.activity_channels_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import java.util.*


class ChannelsListActivity : AppCompatActivity(), AnkoLogger{

    var user = Account()
    lateinit var eventListener : ValueEventListener
    var ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channels_list)
        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()

        getUser()

        val builder: AlertDialog.Builder? = this.let {
            AlertDialog.Builder(it)
        }

        builder?.setTitle("Add New Channel")?.setItems(
            arrayOf("Create", "Join")
        ) { _, which ->
            when (which) {
                0 -> {
                    startActivity(intentFor<ChannelCreate>())
                    /*val newChannelBuilder: AlertDialog.Builder? = this.let {
                        AlertDialog.Builder(it)
                    }
                    // Get the layout inflater
                    val inflater = this.layoutInflater
                    newChannelBuilder?.setTitle("Add New Channel")?.setView(
                        inflater.inflate(
                            R.layout.dialog_create_channel,
                            null
                        )
                    )
                        ?.setPositiveButton("Create",
                            DialogInterface.OnClickListener { dialog, id ->
                                createChannel(
                                    Channel(
                                        id = UUID.randomUUID().toString(),
                                        channelName = txtChannelName.text.toString(),
                                        description = txtChannelDesc.text.toString(),
                                        image = 0
                                    )
                                )
                            })
                        ?.setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialog, id ->
                            })
                    val dialog: AlertDialog? = newChannelBuilder?.create()
                    dialog?.show()
                    val textView: TextView = dialog?.findViewById(R.id.txtChannelName)!!
                    textView.text = "gsdgs"
                    val textView2: TextView = dialog?.findViewById(R.id.txtChannelDesc)!!
                    textView2.text = "gdsgs"*/
                }
                1 -> {
                    Toast.makeText(this, "Join Selected", Toast.LENGTH_SHORT).show()
                }
            }
        }


        val dialog: AlertDialog? = builder?.create()


        btnAddNew.setOnClickListener {
            dialog?.show()
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

}

