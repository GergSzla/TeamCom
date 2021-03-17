package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.AppNotification
import kotlinx.android.synthetic.main.card_notification.view.*


interface NotificationsListener {
    fun onNotificationClicked(notification: AppNotification)
}

class NotificationsAdapter constructor(
    var notifications: ArrayList<AppNotification>,
    private val listener: NotificationsListener
) : RecyclerView.Adapter<NotificationsAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_notification, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val notification = notifications[holder.adapterPosition]
        holder.bind(notification, listener)
    }

    override fun getItemCount(): Int = notifications.size

    fun removeAt(position: Int) {
        notifications.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(notification: AppNotification, listener: NotificationsListener) {
            itemView.tag = notification

            itemView.txtNotifType.text = notification.type
            itemView.txtNotifContent.text = notification.msg
            itemView.txtNotifDandT.text = notification.date_and_time

            if (!notification.seen){
                itemView.sqrSeenStatus.setTextColor(Color.parseColor("#a60000"))
            } else {
                itemView.sqrSeenStatus.setTextColor(Color.parseColor("#bbbcbb"))
            }

            itemView.setOnClickListener {
                listener.onNotificationClicked(notification)
            }
        }
    }
}