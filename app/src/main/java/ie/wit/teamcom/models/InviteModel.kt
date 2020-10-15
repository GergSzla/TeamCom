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
    var invite_uses: Int = 0
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "invite_code" to invite_code,
            "valid_from" to valid_from,
            "valid_to" to valid_to
        )
    }
}