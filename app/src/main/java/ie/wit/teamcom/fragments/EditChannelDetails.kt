package ie.wit.teamcom.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ie.wit.adventurio.helpers.showImagePicker
import ie.wit.adventurio.helpers.uploadChannelImageView
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Member
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_edit_channel_details.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.IOException
import java.util.ArrayList

class EditChannelDetails : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var member_list = ArrayList<Member>()
    val IMAGE_REQUEST = 1

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
        root = inflater.inflate(R.layout.fragment_edit_channel_details, container, false)
        activity?.title = getString(R.string.title_settings)

        var ref = FirebaseStorage.getInstance()
            .getReference("channel_photos/profile/${currentChannel.id}.jpg")
        ref.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it)
                .resize(150, 150)
                .transform(CropCircleTransformation())
                .into(root.img_channel_edit)
        }

        root.channel_name_edit.setText(currentChannel.channelName)
        root.channel_desc_edit.setText(currentChannel.channelDescription)

        root.btn_edit_channel.setOnClickListener {
            save_channel()
        }

        root.btnChangeChannelImg.setOnClickListener {
            showImagePicker(requireActivity(), IMAGE_REQUEST)
        }

        return root
    }

    fun save_channel() {
        currentChannel.channelName = root.channel_name_edit.text.toString()
        currentChannel.channelDescription = root.channel_desc_edit.text.toString()
        uploadChannelImageView(app, root.img_channel_edit, currentChannel.id)

        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/channelName"] =
                        currentChannel.channelName
                    val childUpdates_ = HashMap<String, Any>()
                    childUpdates_["/channels/${currentChannel.id}/channelDescription"] =
                        currentChannel.channelDescription

                    app.database.updateChildren(childUpdates)
                    app.database.updateChildren(childUpdates_)

                    save_user_channel()

                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!(requestCode !== IMAGE_REQUEST || resultCode !== Activity.RESULT_OK || data == null || data.data == null)) {
            val uri: Uri = data.data!!
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                Picasso.get().load(uri)
                    .resize(300, 300)
                    .transform(CropCircleTransformation())
                    .into(root.img_channel_edit)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun save_user_channel() {
        member_list = ArrayList<Member>()
        app.database.child("channels").child(currentChannel.id).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase member error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val member = it.getValue<Member>(Member::class.java)
                        member!!.id.forEach { par_it ->
                            val childUpdates = HashMap<String, Any>()
                            childUpdates["/users/${auth.currentUser!!.uid}/channels/${currentChannel.id}/channelName"] =
                                currentChannel.channelName
                            val childUpdates_ = HashMap<String, Any>()
                            childUpdates_["/users/${auth.currentUser!!.uid}/channels/${currentChannel.id}/channelDescription"] =
                                currentChannel.channelDescription

                            app.database.updateChildren(childUpdates)
                            app.database.updateChildren(childUpdates_)
                        }
                    }
                    app.database.child("channels").child(currentChannel.id).child("members")
                        .removeEventListener(this)
                    navigateTo(NewsFeedFragment.newInstance(currentChannel))
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            EditChannelDetails().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }
}