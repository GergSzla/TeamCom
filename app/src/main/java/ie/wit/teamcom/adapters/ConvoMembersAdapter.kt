package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Member
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_member_meeting.view.*


interface ConvoMembersListener {

}

class ConvoMembersAdapter constructor(var members: ArrayList<Member>,
                                 private val listener: ConvoMembersListener): RecyclerView.Adapter<ConvoMembersAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_member_meeting, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val member = members[holder.adapterPosition]
        holder.bind(member, listener)
    }

    override fun getItemCount(): Int = members.size

    fun removeAt(position: Int) {
        members.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(member: Member, listener: ConvoMembersListener) {
            itemView.tag = member

            var ref = FirebaseStorage.getInstance().getReference("photos/${member.id}.jpg")
            ref.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it)
                    .resize(260, 260)
                    .transform(CropCircleTransformation())
                    .into(itemView.imgMeetingMem)
            }
            itemView.txtMembermeetingName_card.text = member.firstName + " " + member.surname

            if(member.role.color_code.take(1) != "#"){
                itemView.txtMembermeetingName_card.setTextColor(Color.parseColor("#"+member.role.color_code))
            } else {
                itemView.txtMembermeetingName_card.setTextColor(Color.parseColor(member.role.color_code))
            }
        }
    }
}