package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Role (
    var id:String = "" /*UUID*/,
    var role_name:String = "",
    var permission_code: String = "",
    var color_code: String ="#000000"
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "role_name" to role_name,
            "permission_code" to permission_code,
            "color_code" to color_code
        )
    }
}