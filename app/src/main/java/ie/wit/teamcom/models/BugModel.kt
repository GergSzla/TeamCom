package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
data class Bug (
    var id:String = "" ,
    var bug_no:Int = 0,
    var page:String = "",
    var issue:String = "",
    var issue_desc: String = "",
    var channel: String = "",
    var reported_by: Member = Member(),
    var date_reported: String ="",
    var date_resolved:String ="",
    var fixed:Boolean = false
) : Parcelable


{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "bug_no" to bug_no,
            "page" to page,
            "issue" to issue,
            "issue_desc" to issue_desc,
            "channel" to channel,
            "reported_by" to reported_by,
            "date_reported" to date_reported,
            "date_resolved" to date_resolved,
            "fixed" to fixed
        )
    }
}