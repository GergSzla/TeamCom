package ie.wit.teamcom.models


import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Comment (
    var id:String = "" /*UUID*/,
    var comment_date:String = "",
    var comment_time:String ="",
    var comment_content:String ="",
    var comment_date_id:Long = 0,
    var liked_by: ArrayList<String> = ArrayList<String>(),
    var comment_author:Member = Member(),
    var comment_likes:Int = 0
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "comment_date" to comment_date,
            "comment_time" to comment_time,
            "comment_content" to comment_content,
            "comment_date_id" to comment_date_id,
            "liked_by" to liked_by,
            "comment_author" to comment_author,
            "comment_likes" to comment_likes
        )
    }
}