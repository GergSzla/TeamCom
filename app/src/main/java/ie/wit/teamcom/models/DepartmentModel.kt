package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Department (
    var id:String = "" /*UUID*/,
    var dept_name:String ="",
    var dept_members: ArrayList<Member> = ArrayList<Member>(),
    var date_order_id:Long = 0
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "dept_name" to dept_name,
            "dept_members" to dept_members,
            "date_order_id" to date_order_id
        )
    }
}