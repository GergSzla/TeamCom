package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class Task (
    var id:String = "" /*UUID*/,
    var task_msg: String = "",
    var task_desc: String = "",
    var task_assignee: Member = Member(),
    var task_creator: Member = Member(),
    var task_due_date_as_string:String = "",
    var task_due_time_as_string:String = "",
    var task_current_stage:String ="",
    var task_current_stage_color:String ="",
    var task_importance:Int = 1,
    var task_due_date_id:Long = 0
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "task_msg" to task_msg,
            "task_desc" to task_desc,
            "task_assignee" to task_assignee,
            "task_creator" to task_creator,
            "task_due_date_as_string" to task_due_date_as_string,
            "task_due_time_as_string" to task_due_time_as_string,
            "task_current_stage" to task_current_stage,
            "task_current_stage_color" to task_current_stage_color,
            "task_importance" to task_importance,
            "task_due_date_id" to task_due_date_id
        )
    }
}