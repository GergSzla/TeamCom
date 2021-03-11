package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Log
import kotlinx.android.synthetic.main.card_log.view.*


interface LogListener {
}

class LogAdapter constructor(var logs: ArrayList<Log>,
                             private val listener: LogListener): RecyclerView.Adapter<LogAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_log, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val log = logs[holder.adapterPosition]
        holder.bind(log, listener)
    }

    override fun getItemCount(): Int = logs.size

    fun removeAt(position: Int) {
        logs.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(log: Log, listener: LogListener) {
            itemView.tag = log

            itemView.txtLogTriggerer.text = "${log.log_triggerer.firstName} ${log.log_triggerer.surname}"

            if(log.log_triggerer.role.color_code.take(1) != "#"){
                itemView.txtLogTriggerer.setTextColor(Color.parseColor("#"+log.log_triggerer.role.color_code))
            } else {
                itemView.txtLogTriggerer.setTextColor(Color.parseColor(log.log_triggerer.role.color_code))
            }

            itemView.txtLogDandT.text = "${log.log_date} @ ${log.log_time}"
            itemView.txtLogContent.text = "${log.log_content}"
        }
    }
}