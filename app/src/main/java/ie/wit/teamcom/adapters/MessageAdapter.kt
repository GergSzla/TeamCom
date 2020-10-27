package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Member
import ie.wit.teamcom.models.Message
import kotlinx.android.synthetic.main.card_log.view.*
import kotlinx.android.synthetic.main.card_member.view.*
import kotlinx.android.synthetic.main.card_message.view.*


interface MessageListener {
}

class MessageAdapter constructor(var messages: ArrayList<Message>,
                                 private val listener: MessageListener): RecyclerView.Adapter<MessageAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_message, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val msg = messages[holder.adapterPosition]
        holder.bind(msg, listener)
    }

    override fun getItemCount(): Int = messages.size

    fun removeAt(position: Int) {
        messages.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(msg: Message, listener: MessageListener) {
            itemView.tag = msg

            itemView.msgAuthor.text = msg.author.firstName + " " + msg.author.surname

            if(msg.author.role.color_code.take(1) != "#"){
                itemView.msgAuthor.setTextColor(Color.parseColor("#"+msg.author.role.color_code))
            } else {
                itemView.txtLogTriggerer.setTextColor(Color.parseColor(msg.author.role.color_code))
            }

            itemView.msgDandT.text = "${msg.msg_date} @ ${msg.msg_time}"
            itemView.msgContent.text = msg.content
        }
    }
}