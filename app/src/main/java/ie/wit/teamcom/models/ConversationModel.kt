package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
data class Conversation (
    var id:String = "" /*UUID*/,
    var participants: ArrayList<Member> = ArrayList<Member>(),
    var gc_name:String = "" /*UUID*/,
    var messages:ArrayList<Message> = ArrayList<Message>(),
    var conv_date_order: Long = 0
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "participants" to participants,
            "gc_name" to gc_name,
            "messages" to messages,
            "conv_date_order" to conv_date_order
        )
    }
}