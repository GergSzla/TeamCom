package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Member
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


            if(member.online){
                itemView.txtOnlineStatus.setTextColor(Color.parseColor("#008000"))
            } else {
                itemView.txtOnlineStatus.setTextColor(Color.parseColor("#A9A9A9"))
            }
            /*itemView.txtRoleNameCard.text = role.role_name
            itemView.txtRoleNameCard.setTextColor(Color.parseColor(role.color_code))
            */
            itemView.setOnClickListener {
                listener.onMemberClick(member)
            }
        }
    }
}