package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@Parcelize
data class Channel (
    var id:String = "" /*UUID*/,
    var channelName:String = "",
    var channelDescription:String = "",
    var members: ArrayList<Account> = ArrayList<Account>(),
    var image: Int = 0,
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "channelName" to channelName,
            "description" to channelDescription,
            "members" to members,
            "image" to image
        )
    }
}