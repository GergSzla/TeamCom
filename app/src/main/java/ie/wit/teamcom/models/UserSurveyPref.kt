package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
data class SurveyPref(
    var user_id: String = "",
    var enabled: Boolean = true,
    var visible_to_admin:Boolean = false,
    var frequency: String = "",
    var next_date_id: Long = 0

) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user_id" to user_id,
            "enabled" to enabled,
            "visible_to_admin" to visible_to_admin,
            "frequency" to frequency,
            "next_date_id" to next_date_id
        )
    }
}