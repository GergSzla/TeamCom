package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Role
import kotlinx.android.synthetic.main.card_role.view.*

interface RoleListener {
    fun onRoleClick(role: Role)
}

class RoleAdapter constructor(var roles: ArrayList<Role>,
                                  private val listener: RoleListener): RecyclerView.Adapter<RoleAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_role, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val role = roles[holder.adapterPosition]
        holder.bind(role, listener)
    }

    override fun getItemCount(): Int = roles.size

    fun removeAt(position: Int) {
        roles.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(role: Role, listener: RoleListener) {
            itemView.tag = role

            itemView.txtRoleNameCard.text = role.role_name
            itemView.txtRoleNameCard.setTextColor(Color.parseColor(role.color_code))

            itemView.setOnClickListener {
                listener.onRoleClick(role)
            }
        }
    }
}