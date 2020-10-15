package ie.wit.teamcom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.card_channel.view.*


interface ChannelListener {
    fun onChannelClick(channel: Channel)
}

class ChannelsAdapter constructor(var channels: ArrayList<Channel>,
                                    private val listener: ChannelListener): RecyclerView.Adapter<ChannelsAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_channel, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val channel = channels[holder.adapterPosition]
        holder.bind(channel, listener)
    }

    override fun getItemCount(): Int = channels.size

    fun removeAt(position: Int) {
        channels.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(channel: Channel, listener: ChannelListener) {
            itemView.tag = channel

            itemView.txtChannelNameCard.text = channel.channelName
            itemView.txtChannelDescCard.text = channel.channelDescription
        }
    }
}