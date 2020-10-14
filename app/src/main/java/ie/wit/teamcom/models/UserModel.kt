package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
data class Account (
    var id:String = "" /*UUID*/,
    var firstName:String = "",
    var surname:String = "",
    var email: String = "",
    var image: Int = 0,
    var loginUsed: String ="",
    var channels:  ArrayList<Channel> = ArrayList<Channel>()) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "surname" to surname,
            "email" to email,
            "image" to image,
            "loginUsed" to loginUsed,
            "channels" to channels
        )
    }
}