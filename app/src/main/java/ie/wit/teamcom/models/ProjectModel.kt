package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Project(
    var proj_id: String = "" /*UUID*/,
    var proj_name: String = "",
    var proj_description: String = "",
    var proj_due_date_id: Long = 0,
    var proj_due_date: String = "",
    var proj_due_time: String = "",
    var proj_active_tasks: Int = 0,
    var proj_completed_tasks: Int = 0,
    var proj_task_stages: ArrayList<TaskStage> = ArrayList<TaskStage>()
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "proj_id" to proj_id,
            "proj_name" to proj_name,
            "proj_description" to proj_description,
            "proj_due_date_id" to proj_due_date_id,
            "proj_task_stages" to proj_task_stages
        )
    }
}