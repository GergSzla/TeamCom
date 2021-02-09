package ie.wit.teamcom.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.showImagePicker
import ie.wit.adventurio.helpers.uploadProfileImageView
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_channel_create.*
import kotlinx.android.synthetic.main.activity_channels_list.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.AnkoLogger
import java.io.IOException

class ProfileActivity : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp
    var user = Account()
    lateinit var loader: androidx.appcompat.app.AlertDialog
    val IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
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
                    .into(profImage_edit_view, object : Callback {
                        override fun onSuccess() {
                            // Drawable is ready
                            uploadProfileImageView(app, profImage_edit_view)
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
                    .into(profImage_edit_view, object : Callback {
                        override fun onSuccess() {
                            // Drawable is ready
                            uploadProfileImageView(app, profImage_edit_view)
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
                    .into(profImage_edit_view)
            }
        }

        txtClickChangeImg.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        txtDisplayFullName.setText(user.firstName + " " + user.surname)
        txtDisplayEmail.setText(user.email)
//        txtDisplayWhatIDo.setText()
        chkAutoLogin.isChecked = user.auto_login

        btnProfileSave.setOnClickListener{
            user.firstName = txtDisplayFullName.text.toString().substringBefore(" ")
            user.surname = txtDisplayFullName.text.toString().substringAfter(" ")
            user.email = txtDisplayEmail.text.toString()
            user.auto_login = chkAutoLogin.isChecked
            //TODO: user.what_i_do = editTxtDisplayWhatIDo.text.toString()

//
//            txtDisplayFullName.isVisible = false
//            txtDisplayEmail.isVisible = false
//            txtSaveProfile.isVisible = false
//            txtClickChangeImg.isVisible = false
//
//
//            txtDisplayFullName.isVisible = true
//            txtDisplayEmail.isVisible = true
//            txtEditProfile.isVisible = true
            uploadProfileImageView(app, profImage_edit_view)

            app.database.child("users").child(user.id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/users/${user.id}/firstName"] = user.firstName
                        val childUpdates_ = HashMap<String, Any>()
                        childUpdates_["/users/${user.id}/surname"] = user.surname
                        val childUpdates__ = HashMap<String, Any>()
                        childUpdates__["/users/${user.id}/auto_login"] = user.auto_login

                        app.database.updateChildren(childUpdates)
                        app.database.updateChildren(childUpdates_)
                        app.database.updateChildren(childUpdates__)


                        finish()
//                        change_user_details_in_channels()
                        app.database.child("users").child(user.id)
                            .removeEventListener(this)
                    }
                })
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
                    .into(profImage_edit_view)

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

    override fun onBackPressed() {
        finish()
    }
}