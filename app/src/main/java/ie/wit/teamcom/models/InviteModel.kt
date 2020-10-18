package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Invite (
    var id:String = "" /*UUID*/,
    var invite_code: String ="",
    var valid_from: Long = 0,
    var valid_to: Long = 0,
    var valid_from_as_string : String = "",
    var valid_to_as_string : String = "",
    var auto_deletion: Long = 0,
    var invite_uses: Int = 0,
    var invite_use_limit: Int = 0

) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "invite_code" to invite_code,
            "valid_from" to valid_from,
            "valid_to" to valid_to,
            "auto_deletion" to auto_deletion,
            "invite_uses" to invite_uses,
            "invite_use_limit" to invite_use_limit
        )
    }
}