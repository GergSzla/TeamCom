package ie.wit.teamcom.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ie.wit.adventurio.helpers.*
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.ChannelListener
import ie.wit.teamcom.adapters.ChannelsAdapter
import ie.wit.teamcom.fragments.currentChannel
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_channels_list.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import java.io.IOException
import java.util.*


class ChannelsListActivity : AppCompatActivity(), AnkoLogger, ChannelListener {

    lateinit var app: MainApp
    var channelsList = ArrayList<Channel>()
    val layoutManager = LinearLayoutManager(this)
    var user = Account()
    var is_edited = false
    val IMAGE_REQUEST = 1
    lateinit var loader : androidx.appcompat.app.AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channels_list)
        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        loader = createLoader(this)

        user = intent.extras!!.getParcelable<Account>("user_key")!!

        if (user.image == 0) {
            if (app.auth.currentUser?.photoUrl != null) {
                Picasso.get().load(app.auth.currentUser?.photoUrl)
                    .resize(260, 260)
                    .transform(CropCircleTransformation())
                    .into(profImage, object : Callback {
                        override fun onSuccess() {
                            // Drawable is ready
                            uploadProfileImageView(app, profImage)
                            user.image = 1
                            updateUserProfile(app.auth.currentUser!!.uid, user.image)
                        }

                        override fun onError(e: Exception) {
                        }

                    })
            } else {
                Picasso.get().load(R.mipmap.user)
                    .resize(260, 260)
                    .transform(CropCircleTransformation())
                    .into(profImage, object : Callback {
                        override fun onSuccess() {
                            // Drawable is ready
                            uploadProfileImageView(app, profImage)
                            user.image = 1
                            updateUserProfile(app.auth.currentUser!!.uid, user.image)
                        }

                        override fun onError(e: Exception) {}
                    })
            }
        } else if (user.image == 1) {
            var ref = FirebaseStorage.getInstance()
                .getReference("photos/${app.auth.currentUser!!.uid}.jpg")
            ref.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it)
                    .resize(260, 260)
                    .transform(CropCircleTransformation())
                    .into(profImage)
            }
        }

        showLoader(loader,"Loading . . .", "Loading User Data . . .")
        app.getUser()
        hideLoader(loader)

        setSwipeRefresh()
        getAllUserChannels(app.auth.currentUser!!.uid)

        card_full_name.text = user.firstName + " " + user.surname
        card_email.text = user.email
        //TODO: txtDisplayWhatIDo.text = user.what_i_do

        btn_edit_prof.setOnClickListener {
            startActivity(intentFor<ProfileActivity>().putExtra("user_key",user))
//            editTxtDisplayFullName.isVisible = true
//            editTxtDisplayEmail.isVisible = true
//            txtSaveProfile.isVisible = true
//            txtClickChangeImg.isVisible = true
//            editTxtDisplayFullName.setText(user.firstName + " " + user.surname)
//            editTxtDisplayEmail.setText(user.email)



//            txtDisplayFullName.isVisible = false
//            txtDisplayEmail.isVisible = false
//            txtEditProfile.isVisible = false
//            txtDisplayWhatIDo.isVisible = false
        }

//        txtClickChangeImg.setOnClickListener {
//            showImagePicker(this, IMAGE_REQUEST)
//        }

//        txtSaveProfile.setOnClickListener {
//            user.firstName = editTxtDisplayFullName.text.toString().substringBefore(" ")
//            user.surname = editTxtDisplayFullName.text.toString().substringAfter(" ")
//            user.email = editTxtDisplayEmail.text.toString()
//            //TODO: user.what_i_do = editTxtDisplayWhatIDo.text.toString()
//
//
//            editTxtDisplayFullName.isVisible = false
//            editTxtDisplayEmail.isVisible = false
//            txtSaveProfile.isVisible = false
//            txtClickChangeImg.isVisible = false
//
//
//            txtDisplayFullName.isVisible = true
//            txtDisplayEmail.isVisible = true
//            txtEditProfile.isVisible = true
//            uploadProfileImageView(app, profImage)
//
//            app.database.child("users").child(user.id)
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onCancelled(error: DatabaseError) {
//                    }
//
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val childUpdates = HashMap<String, Any>()
//                        childUpdates["/users/${user.id}/firstName"] = user.firstName
//                        val childUpdates_ = HashMap<String, Any>()
//
//                        childUpdates_["/users/${user.id}/surname"] = user.surname
//
//                        app.database.updateChildren(childUpdates)
//                        app.database.updateChildren(childUpdates_)
//
//                        change_user_details_in_channels()
//                        app.database.child("users").child(user.id)
//                            .removeEventListener(this)
//                    }
//                })


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

    fun change_user_details_in_channels() {
        is_edited = true
        getAllUserChannels(app.auth.currentUser!!.uid)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!(requestCode !== IMAGE_REQUEST || resultCode !== Activity.RESULT_OK || data == null || data.data == null)) {
            val uri: Uri = data.data!!
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                Picasso.get().load(uri)
                    .resize(260, 260)
                    .transform(CropCircleTransformation())
                    .into(profImage)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun updateUserProfile(uid: String?, image: Int) {
        app.database.child("users").child(uid!!).child("image")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(image)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }

    fun getAllUserChannels(userId: String?) {
        showLoader(loader, "Loading . . .", "Loading Channels . . . ")
        channelsList = ArrayList<Channel>()
        app.database.child("users").child(userId!!).child("channels").orderByChild("orderDateId")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase channel error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children

                    children.forEach { it ->
                        val channel = it.getValue<Channel>(Channel::class.java)
                        channelsRecyclerView.layoutManager = layoutManager
                        channelsList.add(channel!!)
                        channelsRecyclerView.adapter =
                            ChannelsAdapter(channelsList, this@ChannelsListActivity)
                        channelsRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        if (is_edited) {
                            channelsList.forEach {
                                app.database.child("channels").child(it.id).child("members").child(userId)
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val childUpdates = HashMap<String, Any>()
                                            childUpdates["/channels/${it.id}/members/$userId/firstName"] =
                                                user.firstName
                                            val childUpdates_ = HashMap<String, Any>()

                                            childUpdates_["/channels/${it.id}/members/$userId/surname"] =
                                                user.surname

                                            app.database.updateChildren(childUpdates)
                                            app.database.updateChildren(childUpdates_)

                                            change_user_details_in_channels()
                                            app.database.child("channels").child(it.id).child("members").child(userId)
                                                .child(currentChannel!!.id)
                                                .removeEventListener(this)
                                        }
                                    })
                            }
                        }
                        hideLoader(loader)
                        app.database.child("users").child(userId).child("channels").orderByChild("orderDateId")
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
        startActivity(intentFor<Home>().putExtra("channel_key", channel))
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

}

