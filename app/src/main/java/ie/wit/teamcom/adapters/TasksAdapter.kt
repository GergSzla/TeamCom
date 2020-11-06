package ie.wit.teamcom.adapters


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
                                  private val listener: TaskListener): RecyclerView.Adapter<TasksAdapter.RowViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        return RowViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.item_task, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val task = tasks[holder.adapterPosition]
        holder.bind(task, listener)
    }

    override fun getItemCount(): Int = tasks.size

    fun removeAt(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }


    class RowViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: Task/*, task_stage: TaskStage*/, listener: TaskListener) {
            itemView.tag = task

            //if(task.)
            itemView.txtTaskName.text = task.task_msg
            itemView.txtTaskAssignee.text ="${task.task_assignee.firstName}  ${task.task_assignee.surname}"
            itemView.txtTaskImportance.text = "0/5"
            itemView.txtTaskStage.text = " "
            itemView.txtTaskCreator.text = task.task_creator.firstName + " " + task.task_creator.surname
            itemView.txtTaskDueDate.text = task.task_due_date_as_string+", " + task.task_due_time_as_string

            itemView.setOnClickListener {
                listener.onTaskClicked(task)
            }
        }
    }
}