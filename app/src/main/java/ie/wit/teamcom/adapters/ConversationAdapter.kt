package ie.wit.teamcom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Conversation
import kotlinx.android.synthetic.main.card_channel.view.*
import kotlinx.android.synthetic.main.card_conversation.view.*


interface ConversationListener {
    fun onConversationClick(convo: Conversation)
}

class ConversationAdapter constructor(
    var conversations: ArrayList<Conversation>,
    private val listener: ConversationListener
) : RecyclerView.Adapter<ConversationAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_conversation, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val conversation = conversations[holder.adapterPosition]
        holder.bind(conversation, listener)
    }

    override fun getItemCount(): Int = conversations.size

    fun removeAt(position: Int) {
        conversations.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(conversation: Conversation, listener: ConversationListener) {
            itemView.tag = conversation

            var auth: FirebaseAuth = FirebaseAuth.getInstance()


            if (conversation.gc_name != "") {
                itemView.txtParticipants.text = conversation.gc_name
            } else {
                conversation.participants.forEach{
                    if(it.id !== auth.currentUser!!.uid){
                        itemView.txtParticipants.text = it.firstName + " " + it.surname
                    }
                }
            }


            //var last_message: String =""
            if (conversation.messages.isEmpty()) {
                itemView.txtLastMessage.text = "Start Chatting!"
            } else {
                var size = conversation.messages.size
                itemView.txtLastMessage.text = conversation.messages[size - 1].content
            }

            itemView.setOnClickListener {
                listener.onConversationClick(conversation)
            }
        }
    }
}