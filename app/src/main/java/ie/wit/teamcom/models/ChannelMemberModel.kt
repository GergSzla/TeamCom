package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
data class Member (
    var id:String = "" /*UUID*/,
    var firstName:String = "",
    var surname:String = "",
    var email: String = "",
    var image: Int = 0,
    var login_used: String ="",
    var department: Department = Department(),
    var online:Boolean = false,
    var role: Role = Role()) : Parcelable



{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "surname" to surname,
            "email" to email,
            "image" to image,
            "login_used" to login_used,
            "department" to department,
            "online" to online,
            "role" to role
        )
    }
}