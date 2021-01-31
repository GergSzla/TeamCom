package ie.wit.teamcom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Invite
import ie.wit.teamcom.models.Meeting
import kotlinx.android.synthetic.main.card_invite.view.*


interface InviteListener {
    fun onInviteClicked(invite: Invite)
}

class InviteAdapter constructor(var invites: ArrayList<Invite>,
                                private val listener: InviteListener): RecyclerView.Adapter<InviteAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_invite, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val invite = invites[holder.adapterPosition]
        holder.bind(invite, listener)
    }

    override fun getItemCount(): Int = invites.size

    fun removeAt(position: Int) {
        invites.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(invite: Invite, listener: InviteListener) {
            itemView.tag = invite

            itemView.txtCode.text = invite.invite_code
            itemView.txtValidFrom_Card.text = invite.valid_from_as_string
            itemView.txtValidTo_Card.text = invite.valid_to_as_string
            itemView.txtUses_Card.text = invite.invite_uses.toString() + "/" + invite.invite_use_limit.toString()

            itemView.setOnClickListener {
                listener.onInviteClicked(invite)
            }

            /*itemView.txtRoleNameCard.text = role.role_name
            itemView.txtRoleNameCard.setTextColor(Color.parseColor(role.color_code))
            */
        }
    }
}