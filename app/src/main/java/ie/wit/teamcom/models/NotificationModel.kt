package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class AppNotification(
    var id: String = "",
    var type: String = "",
    var msg: String = "",
    var date_and_time: String = "",
    var seen: Boolean = false,
    var date_id: Long = 0
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "type" to type,
            "msg" to msg,
            "date_and_time" to date_and_time,
            "seen" to seen,
            "date_id" to date_id
        )
    }
}