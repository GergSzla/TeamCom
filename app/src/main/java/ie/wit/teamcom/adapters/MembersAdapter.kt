package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Member
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_channels_list.*
import kotlinx.android.synthetic.main.card_member.view.*


interface MembersListener {
    fun onMemberClick(member: Member)
}

class MembersAdapter constructor(var members: ArrayList<Member>,
                                 private val listener: MembersListener): RecyclerView.Adapter<MembersAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_member, parent, false)
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

        fun bind(member: Member, listener: MembersListener) {
            itemView.tag = member

            //itemView.imageView3 TODO: ADD SHOW USER_IMAGE
            itemView.txtMemberName_card.text = member.firstName + " " + member.surname

            if (member.department.dept_name == ""){
                itemView.txtMemberDept_card.text = "[Department Not Assigned]"
            } else {
                itemView.txtMemberDept_card.text = member.department.dept_name
            }

            if (member.role.role_name == ""){
                itemView.txtMemberRole_card.text = "[Role Not Assigned]"
            } else {
                itemView.txtMemberRole_card.text = member.role.role_name
            }

            var ref = FirebaseStorage.getInstance().getReference("photos/${member.id}.jpg")
            ref.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it)
                    .resize(260, 260)
                    .transform(CropCircleTransformation())
                    .into(itemView.imageView3)
            }
            itemView.txtMemberRole_card.text = member.role.role_name

            if(member.role.color_code.take(1) != "#"){
                itemView.txtMemberRole_card.setTextColor(Color.parseColor("#"+member.role.color_code))
            } else {
                itemView.txtMemberRole_card.setTextColor(Color.parseColor(member.role.color_code))
            }
            itemView.setOnClickListener {
                listener.onMemberClick(member)
            }
        }
    }
}