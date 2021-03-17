package ie.wit.teamcom.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ie.wit.adventurio.helpers.*
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.*
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_channel_create.*
import org.jetbrains.anko.AnkoLogger
import java.io.IOException
import java.util.*

class ChannelCreate : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp
    val IMAGE_REQUEST = 1
    lateinit var loader: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_create)

        app = application as MainApp
        auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        loader = createLoader(this)

        showLoader(loader, "Loading . . .", "Loading User Details . . .")
        app.getUser()

        Picasso.get().load(R.mipmap.socialnetwork)
            .resize(300, 300)
            .transform(CropCircleTransformation())
            .into(imageView2)
        hideLoader(loader)
        //Icons made by <a href="https://www.flaticon.com/authors/iconixar" title="iconixar">iconixar</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>

        btnChannelImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        btnCreateNew.setOnClickListener {
            showLoader(loader, "Loading . . .", "Validating . . . ")
            validateForm()
            hideLoader(loader)
            if (!(txtChannelName.text.toString() == "" || txtChannelDesc.text.toString() == "")) {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!(requestCode !== IMAGE_REQUEST || resultCode !== Activity.RESULT_OK || data == null || data.data == null)) {
            val uri: Uri = data.data!!
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                Picasso.get().load(uri)
                    .resize(300, 300)
                    .transform(CropCircleTransformation())
                    .into(imageView2)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val channel_name = txtChannelName.text.toString()
        if (TextUtils.isEmpty(channel_name)) {
            txtChannelName.error = "Channel Name Required."
            valid = false
        } else {
            txtChannelName.error = null
        }

        val channel_desc = txtChannelDesc.text.toString()
        if (TextUtils.isEmpty(channel_desc)) {
            txtChannelDesc.error = "Channel Description Required."
            valid = false
        } else {
            txtChannelDesc.error = null
        }

        return valid
    }

    private fun createChannel(channel: Channel) {
        showLoader(loader, "Loading . . .", "Creating Channel ${channel.id} . . .")
        val uid = auth.currentUser!!.uid
        val userValues = app.user
        var role = Role(
            id = UUID.randomUUID().toString(),
            role_name = "Admin",
            permission_code = "10000000000000",
            color_code = "b20202",
            isDefault = true
        )

        val roleValues = role

        uploadChannelImageView(app, imageView2, channel.id)

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

        var new_member = Member(
            id = uid,
            firstName = userValues.firstName,
            surname = userValues.surname,
            email = userValues.email,
            image = 0,
            login_used = userValues.login_used,
            role = roleValues
        )
        var new_log = Log(
            log_id = app.valid_from_cal,
            log_triggerer = new_member,
            log_date = app.dateAsString,
            log_time = app.timeAsString,
            log_content = "The channel, ${channel.channelName} has been successfully created."
        )
        val logChildUpdate = HashMap<String, Any>()
        logChildUpdate["/channels/${channel.id}/logs/${new_log.log_id}"] = new_log
        app.database.updateChildren(logChildUpdate)

        hideLoader(loader)
        finish()
    }
}