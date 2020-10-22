package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Post (
    var id:String = "" /*UUID*/,
    var post_date:String = "",
    var post_time:String ="",
    var post_content:String ="",
    var post_date_id:Long = 0,
    var post_author:Member = Member(),
    var post_likes:Int = 0,
    var liked_by: ArrayList<String> = ArrayList<String>(),
    var post_comments: ArrayList<Comment> = ArrayList<Comment>(),
    var post_comments_total:Int = 0
    ) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "post_date" to post_date,
            "post_time" to post_time,
            "post_content" to post_content,
            "post_date_id" to post_date_id,
            "post_author" to post_author,
            "post_likes" to post_likes,
            "post_comments" to post_comments,
            "post_comments_total" to post_comments_total
        )
    }
}