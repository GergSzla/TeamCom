package ie.wit.adventurio.helpers

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import kotlinx.android.synthetic.main.loading.*
import kotlinx.android.synthetic.main.loading_progress.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.roundToInt

fun createLoader(activity: FragmentActivity): AlertDialog {
    val loaderBuilder = AlertDialog.Builder(activity)
        .setCancelable(true) // 'false' if you want user to wait
        .setView(R.layout.loading)
    var loader = loaderBuilder.create()
    loader.setTitle(R.string.app_name)

    return loader
}

fun createProgressLoader(activity: FragmentActivity): AlertDialog {
    val loaderBuilder = AlertDialog.Builder(activity)
        .setCancelable(true) // 'false' if you want user to wait
        .setView(R.layout.loading_progress)
    var loader = loaderBuilder.create()
    loader.setTitle(R.string.app_name)

    return loader
}

fun uploadProfileImageView(app: MainApp, imageView: ImageView) {
    // Get the data from an ImageView as bytes
    val uid = auth.currentUser!!.uid
    val imageRef = app.storage.child("photos").child("${uid}.jpg")
    val bitmap = (imageView.drawable as BitmapDrawable).bitmap
    val baos = ByteArrayOutputStream()

    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    var uploadTask = imageRef.putBytes(data)
}

fun uploadChannelImageView(app: MainApp, imageView: ImageView, channel_id: String) {
    // Get the data from an ImageView as bytes
    val uid = auth.currentUser!!.uid
    val imageRef = app.storage.child("channel_photos").child("profile").child("${channel_id}.jpg")
    val bitmap = (imageView.drawable as BitmapDrawable).bitmap
    val baos = ByteArrayOutputStream()

    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    var uploadTask = imageRef.putBytes(data)
}

fun readImageUri(resultCode: Int, data: Intent?): Uri? {
    var uri: Uri? = null
    if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
        try {
            uri = data.data
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return uri
}

fun showLoader(loader: AlertDialog, title: String, message: String) {
    if (!loader.isShowing()) {
        loader.setTitle(title)
        loader.show()
        loader.loaderTV.text = message
    }
}

fun showProgressLoader(loader: AlertDialog, title: String, message: String, progress: Double) {
    if (!loader.isShowing()) {
        loader.setTitle(title)
        loader.show()
        loader.progressBar_loader.max = 100
        loader.progressBar_loader.progress = progress.roundToInt()
        loader.percentage.text = "$progress%"
        loader.loaderProgressTitle.text = message
        Thread.sleep(3_000)
    }
}

fun hideLoader(loader: AlertDialog) {
    if (loader.isShowing())
        loader.dismiss()
}


