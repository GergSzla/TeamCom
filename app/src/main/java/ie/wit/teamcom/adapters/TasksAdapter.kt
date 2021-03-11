package ie.wit.teamcom.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Task
import ie.wit.teamcom.models.TaskStage
import kotlinx.android.synthetic.main.card_channel.view.*
import kotlinx.android.synthetic.main.item_task.view.*


interface TaskListener {
    fun onTaskClicked(task: Task)
}

class TasksAdapter constructor(var tasks: ArrayList<Task>,
                                  private val listener: TaskListener): RecyclerView.Adapter<TasksAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.item_task, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val task = tasks[holder.adapterPosition]
        holder.bind(task, listener)
    }

    override fun getItemCount(): Int = tasks.size

    fun removeAt(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }


    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: Task/*, task_stage: TaskStage*/, listener: TaskListener) {
            itemView.tag = task

            if(task.task_current_stage_color.take(1) != "#"){
                itemView.sqrStage.setTextColor(Color.parseColor("#"+task.task_current_stage_color))
            } else {
                itemView.sqrStage.setTextColor(Color.parseColor(task.task_current_stage_color))
            }
            itemView.txtTaskName.text = task.task_msg
            itemView.txtTaskAssignee.text ="${task.task_assignee.firstName}  ${task.task_assignee.surname}"
            itemView.txtTaskImportance.text = task.task_importance.toString() +"/5"
            itemView.txtTaskStage.text = task.task_current_stage

            if(task.task_current_stage_color.take(1) != "#"){
                itemView.txtTaskStage.setBackgroundColor(Color.parseColor("#"+task.task_current_stage_color))
            } else {
                itemView.txtTaskStage.setBackgroundColor(Color.parseColor(task.task_current_stage_color))
            }

            itemView.txtTaskCreator.text = task.task_creator.firstName + " " + task.task_creator.surname
            itemView.txtTaskDueDate.text = task.task_due_date_as_string+", " + task.task_due_time_as_string

            itemView.setOnClickListener {
                listener.onTaskClicked(task)
            }
        }
    }
}