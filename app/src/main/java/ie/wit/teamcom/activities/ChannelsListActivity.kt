package ie.wit.teamcom.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.ChannelListener
import ie.wit.teamcom.adapters.ChannelsAdapter
import ie.wit.teamcom.fragments.currentChannel
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.activity_channels_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import java.util.*


class ChannelsListActivity : AppCompatActivity(), AnkoLogger, ChannelListener {

    lateinit var app: MainApp
    var channelsList = ArrayList<Channel>()
    val layoutManager = LinearLayoutManager(this)
    var user = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channels_list)
        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference
        //app.getAllMembers(currentChannel.id)
        user = intent.extras!!.getParcelable<Account>("user_key")!!


        app.getUser()

        txtClickChangeImg.isVisible = false
        editTxtDisplayFullName.isVisible = false
        editTxtDisplayEmail.isEnabled = false //TODO: ALLOW USER TO SWITCH FROM GOOGLE ACC TO NORMAL
        editTxtDisplayEmail.isVisible = false
        txtSaveProfile.isVisible = false
        editTxtDisplayWhatIDo.isVisible = false

        //user = intent.getParcelableExtra("user_key")
        setSwipeRefresh()
        getAllUserChannels(app.auth.currentUser!!.uid)

        txtDisplayFullName.text = user.firstName + " " + user.surname
        txtDisplayEmail.text = user.email
        //TODO: txtDisplayWhatIDo.text = user.what_i_do

        txtEditProfile.setOnClickListener {
            editTxtDisplayFullName.isVisible = true
            editTxtDisplayEmail.isVisible = true
            txtSaveProfile.isVisible = true
            txtClickChangeImg.isVisible = true
            editTxtDisplayFullName.setText(user.firstName+" "+user.surname)
            editTxtDisplayEmail.setText(user.email)



            txtDisplayFullName.isVisible = false
            txtDisplayEmail.isVisible = false
            txtEditProfile.isVisible = false
            txtDisplayWhatIDo.isVisible = false
        }

        txtClickChangeImg.setOnClickListener {
            //TODO: CHANGE PICTURE
        }

        txtSaveProfile.setOnClickListener {
            user.firstName = editTxtDisplayFullName.text.toString().substringBefore(" ")
            user.surname = editTxtDisplayFullName.text.toString().substringAfter(" ")
            user.email = editTxtDisplayEmail.text.toString()
            //TODO: user.what_i_do = editTxtDisplayWhatIDo.text.toString()


            editTxtDisplayFullName.isVisible = false
            editTxtDisplayEmail.isVisible = false
            txtSaveProfile.isVisible = false
            txtClickChangeImg.isVisible = false


            txtDisplayFullName.isVisible = true
            txtDisplayEmail.isVisible = true
            txtEditProfile.isVisible = true

            app.database.child("users").child(user.id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/users/${user.id}/firstName"] = user.firstName
                        childUpdates["/users/${user.id}/surname"] = user.surname

                        app.database.updateChildren(childUpdates)

                        app.database.child("channels").child(currentChannel!!.id)
                            .removeEventListener(this)
                    }
                })
        }

        val builder: AlertDialog.Builder? = this.let {
            AlertDialog.Builder(it)
        }

        //TODO: txtDisplayWhatIDo.text = app.currentActiveMember.what_i_do

        builder?.setTitle("Add New Channel")?.setItems(
            arrayOf("Create", "Join")
        ) { _, which ->
            when (which) {
                0 -> {
                    startActivity(intentFor<ChannelCreate>()/*.putExtra("user_key",user)*/)
                }
                1 -> {
                    startActivity(intentFor<ChannelJoin>()/*.putExtra("user_key",user)*/)
                }
            }
        }


        val dialog: AlertDialog? = builder?.create()


        btnAddNew.setOnClickListener {
            dialog?.show()
        }
    }

    fun getAllUserChannels(userId: String?) {
        //loader = createLoader(activity!!)
        channelsList = ArrayList<Channel>()
        app.database.child("users").child(userId!!).child("channels").orderByChild("orderDateId")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase channel error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children

                    children.forEach {
                        val channel = it.
                        getValue<Channel>(Channel::class.java)
                        channelsRecyclerView.layoutManager = layoutManager
                        channelsList.add(channel!!)
                        channelsRecyclerView.adapter = ChannelsAdapter(channelsList, this@ChannelsListActivity)
                        channelsRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("users").child(userId).child("channels")
                            .removeEventListener(this)
                    }
                }
            })
    }


    fun setSwipeRefresh() {
        swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                swiperefresh.isRefreshing = true
                getAllUserChannels(app.auth.currentUser!!.uid)
            }
        })
    }

    fun checkSwipeRefresh() {
        if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
    }



    override fun onChannelClick(channel: Channel) {
        startActivity(intentFor<Home>().putExtra("channel_key",channel))
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

}

