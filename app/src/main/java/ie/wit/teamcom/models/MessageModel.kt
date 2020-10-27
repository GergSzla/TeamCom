package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
data class Message (
    var id:String = "" /*UUID*/,
    var author: Member = Member(),
    var content: String = "",
    var mes_date_order: Long = 0,
    var msg_date:String = "",
    var msg_time:String ="",
    var read_by: ArrayList<Member> = ArrayList<Member>()

) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "author" to author,
            "content" to content,
            "mes_date_order" to mes_date_order,
            "read_by" to read_by
        )
    }
}