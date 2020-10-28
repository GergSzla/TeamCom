package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Post
import ie.wit.teamcom.models.Reminder
import kotlinx.android.synthetic.main.card_post.view.*
import kotlinx.android.synthetic.main.card_reminder.view.*


interface ReminderListener {
}

class RemindersAdapter constructor(
    var reminders: ArrayList<Reminder>,
    private val listener: ReminderListener
): RecyclerView.Adapter<RemindersAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_reminder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val reminder = reminders[holder.adapterPosition]
        holder.bind(reminder, listener)
    }

    override fun getItemCount(): Int = reminders.size

    fun removeAt(position: Int) {
        reminders.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(reminder: Reminder, listener: ReminderListener) {
            itemView.tag = reminder

            itemView.txtRemMsg.text = reminder.rem_msg
            itemView.txtRemDandT.text = reminder.rem_date_as_string +" - "+ reminder.rem_time_as_string
        }
    }
}