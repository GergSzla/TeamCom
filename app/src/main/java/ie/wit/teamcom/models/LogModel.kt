package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Log (
    var log_id:Long = 0,
    var log_triggerer:Member = Member(),
    var log_date:String = "",
    var log_time:String ="",
    var log_content:String =""
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "log_id" to log_id,
            "log_triggerer" to log_triggerer,
            "log_date" to log_date,
            "log_time" to log_time,
            "log_content" to log_content
        )
    }
}