package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
data class UserMHModel(
    var user_id: String = "",
    var set_of_ans_1_per: Double = 0.0,
    var set_of_ans_2_per: Double = 0.0,

) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user_id" to user_id,
            "set_of_ans_1_per" to set_of_ans_1_per,
            "set_of_ans_2_per" to set_of_ans_2_per
        )
    }
}