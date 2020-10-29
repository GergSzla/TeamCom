package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class Reminder (
    var id:String = "" /*UUID*/,
    var rem_msg:String ="",
    var rem_date: String ="",
    var rem_date_id:Long = 0,
    var rem_status: String ="",
    var rem_date_as_string: String ="",
    var rem_reminder_date_it:Long = 0,
    var rem_time_as_string: String =""
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "rem_msg" to rem_msg,
            "rem_date" to rem_date,
            "rem_date_id" to rem_date_id,
            "rem_status" to rem_status,
            "rem_date_as_string" to rem_date_as_string,
            "rem_time_as_string" to rem_time_as_string

        )
    }
}