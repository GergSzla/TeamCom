package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.TaskStage
import kotlinx.android.synthetic.main.card_stages.view.txtTaskName

interface StagesListener {
    fun onStageClick(stage: TaskStage)
}

class StageAdapter constructor(var stages: ArrayList<TaskStage>,
                              private val listener: StagesListener): RecyclerView.Adapter<StageAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_stages, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val stage = stages[holder.adapterPosition]
        holder.bind(stage, listener)
    }

    override fun getItemCount(): Int = stages.size

    fun removeAt(position: Int) {
        stages.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(stage: TaskStage, listener: StagesListener) {
            itemView.tag = stage
            itemView.txtTaskName.text = stage.stage_name

            if(stage.stage_color_code.take(1) != "#"){
                itemView.txtTaskName.setBackgroundColor(Color.parseColor("#"+stage.stage_color_code))
            } else {
                itemView.txtTaskName.setBackgroundColor(Color.parseColor(stage.stage_color_code))
            }



            itemView.setOnClickListener {
                listener.onStageClick(stage)
            }
        }
    }

}