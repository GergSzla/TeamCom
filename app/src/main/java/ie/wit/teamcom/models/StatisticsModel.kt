package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class Stats (
    var user_id: String = "",
    var tasks_overdue: Int = 0,
    var tasks_on_time: Int = 0,
    var ongoing_tasks: Int = 0,
    var tasks_due_24hrs: Int = 0,
    var tasks_due_7days: Int = 0,
    var tasks_due_14days: Int = 0
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user_id" to user_id,
            "tasks_overdue" to tasks_overdue,
            "tasks_on_time" to tasks_on_time,
            "ongoing_tasks" to ongoing_tasks,
            "tasks_due_24hrs" to tasks_due_24hrs,
            "tasks_due_7days" to tasks_due_7days,
            "tasks_due_14days" to tasks_due_14days
            )
    }
}