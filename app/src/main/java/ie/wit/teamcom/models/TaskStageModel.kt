package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class TaskStage (
    var id:String = "" /*UUID*/,
    var stage_no:Int = 0,
    var stage_name:String = "",
    var stage_color_code:String = "",
    var stage_tasks: ArrayList<Task> = ArrayList<Task>(),
    var stage_active:Boolean = false
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "stage_no" to stage_no,
            "stage_name" to stage_name,
            "stage_color_code" to stage_color_code,
            "stage_tasks" to stage_tasks,
            "stage_active" to stage_active,
        )
    }
}