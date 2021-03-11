package ie.wit.teamcom.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Event
import kotlinx.android.synthetic.main.card_event.view.*


interface EventListener_ {
}

class EventAdapter constructor(var events: ArrayList<Event>,
                               private val listener: EventListener_): RecyclerView.Adapter<EventAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_event, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val event = events[holder.adapterPosition]
        holder.bind(event, listener)
    }

    override fun getItemCount(): Int = events.size

    fun removeAt(position: Int) {
        events.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(event: Event, listener: EventListener_) {
            itemView.tag = event

            itemView.txt_event_title.text = event.event_name
            itemView.txt_num_participants.text = "1" //TODO: WHEN CHANGED TO ARRAYLIST OF MEMBERS, ADD COUNT HERE
            itemView.event_card.setCardBackgroundColor(Color.parseColor(event.event_color))
        }
    }
}