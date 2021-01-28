package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Event (
    var event_id: String = "" /*UUID*/,
    var event_name: String = "",
    var event_desc: String = "",
    var event_participants: Member = Member(), //TODO: CHANGE TO ARRAY OF MEMBERS !!
    var event_date: String = "",
    var event_type: String = "",
    var event_color: String = "#000000"

) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "event_id" to event_id,
            "event_name" to event_name,
            "event_participants" to event_participants,
            "event_date" to event_date,
            "event_type" to event_type,
            "event_color" to event_color
        )
    }
}