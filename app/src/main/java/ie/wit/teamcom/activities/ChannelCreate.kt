package ie.wit.teamcom.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ie.wit.adventurio.helpers.showImagePicker
import ie.wit.adventurio.helpers.uploadChannelImageView
import ie.wit.adventurio.helpers.uploadProfileImageView
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_channel_create.*
import kotlinx.android.synthetic.main.activity_channels_list.*
import org.jetbrains.anko.AnkoLogger
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChannelCreate : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp
    val IMAGE_REQUEST = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_create)

        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        app.getUser()

        Picasso.get().load(R.mipmap.socialnetwork)
            .resize(300, 300)
            .transform(CropCircleTransformation())
            .into(imageView2)
        //Icons made by <a href="https://www.flaticon.com/authors/iconixar" title="iconixar">iconixar</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>


        //user = intent.getParcelableExtra("user_key")

        btnChannelImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        btnCreateNew.setOnClickListener {
            validateForm()
            if (!(txtChannelName.text.toString() == "" || txtChannelDesc.text.toString() == "")){
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

    private fun createChannel(channel: Channel){
        val uid = app.auth.currentUser!!.uid
        val userValues = app.user
        var role = Role(id = UUID.randomUUID().toString(), role_name = "Admin", permission_code = "10000000000000", color_code = "b20202", isDefault = true)
//        var task_stage_1 = TaskStage(id = UUID.randomUUID().toString(), stage_name = "To Do",stage_color_code =  "d00000", stage_no = 1, stage_active = true)
//        var task_stage_2 = TaskStage(id = UUID.randomUUID().toString(), stage_name = "In Progress",stage_color_code =  "d08800", stage_no = 2, stage_active = true)
//        var task_stage_3 = TaskStage(id = UUID.randomUUID().toString(), stage_name = "Completed",stage_color_code =  "00a500", stage_no = 3, stage_active = true)
//        var task_stage_4 = TaskStage(id = UUID.randomUUID().toString(), stage_name = "",stage_color_code =  "6e087a", stage_no = 4, stage_active = false)
//        var task_stage_5 = TaskStage(id = UUID.randomUUID().toString(), stage_name = "",stage_color_code =  "1c087a", stage_no = 5, stage_active = false)
//        var task_stage_6 = TaskStage(id = UUID.randomUUID().toString(), stage_name = "",stage_color_code =  "7a0832", stage_no = 6, stage_active = false)
//
//        val task_stage_arr = arrayListOf<TaskStage>(task_stage_1,task_stage_2,task_stage_3,task_stage_4,task_stage_5,task_stage_6)


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

//        val taskStageChildUpdate = HashMap<String, Any>()
//        taskStageChildUpdate["/channels/${channel.id}/task_stages/"] = task_stage_arr
//        app.database.updateChildren(taskStageChildUpdate)

        var new_member = Member(id = uid, firstName = userValues.firstName, surname = userValues.surname, email = userValues.email, image = 0, login_used = userValues.login_used, role = roleValues)
        var new_log = Log(log_id = app.valid_from_cal, log_triggerer = new_member, log_date = app.dateAsString, log_time = app.timeAsString, log_content = "The channel, ${channel.channelName} has been successfully created.")
        val logChildUpdate = HashMap<String, Any>()
        logChildUpdate["/channels/${channel.id}/logs/${new_log.log_id}"] = new_log
        app.database.updateChildren(logChildUpdate)


        finish()
    }
}