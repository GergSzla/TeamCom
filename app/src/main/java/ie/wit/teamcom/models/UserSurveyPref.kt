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
    var frequency: String = ""
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user_id" to user_id,
            "enabled" to enabled,
            "frequency" to frequency
        )
    }
}