package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Meeting (
    var meeting_uuid:String = "",
    var meeting_title:String ="",
    var meeting_date_id:Long = 0,
    var meeting_date_end_id:Long = 0,
    var meeting_date_as_string: String = "",
    var meeting_time_as_string: String = "",
    var meeting_date_as_string_end: String = "",
    var meeting_time_as_string_end: String = "",
    var meeting_desc: String = "",
    var participants: ArrayList<Member> = ArrayList<Member>(),
    var online:Boolean = true,
    var meeting_creator: Member = Member(),
////IF ONLINE
    var meeting_id: String = "",
    var meeting_passcode: String = "",
    var meeting_platform: String = "",
////IF OFFLINE/PHYSICAL
    var meeting_location: String = ""
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "meeting_uuid" to meeting_uuid,
            "meeting_title" to meeting_title,
            "meeting_date_id" to meeting_date_id,
            "meeting_date_as_string" to meeting_date_as_string,
            "meeting_time_as_string" to meeting_time_as_string,
            "meeting_passcode" to meeting_passcode,
            "meeting_creator" to meeting_creator,

            "meeting_desc" to meeting_desc,
            "participants" to participants,
            "meeting_platform" to meeting_platform,
            "meeting_location" to meeting_location,
            "online" to online

        )
    }
}