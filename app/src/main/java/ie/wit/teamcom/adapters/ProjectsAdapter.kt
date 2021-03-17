package ie.wit.teamcom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Project
import kotlinx.android.synthetic.main.card_project.view.*

interface ProjectListener {
    fun onProjClick(project: Project)
}

class ProjectsAdapter constructor(
    var projects: ArrayList<Project>,
    private val listener: ProjectListener
) : RecyclerView.Adapter<ProjectsAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_project, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val project = projects[holder.adapterPosition]
        holder.bind(project, listener)
    }

    override fun getItemCount(): Int = projects.size

    fun removeAt(position: Int) {
        projects.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(project: Project, listener: ProjectListener) {
            itemView.tag = project

            itemView.txt_proj_name.text = project.proj_name
            itemView.txt_date_and_time.text = "${project.proj_due_date} @ ${project.proj_due_time}"

            itemView.txt_completed_tasks.text = project.proj_completed_tasks.toString()


            itemView.txt_pending_tasks.text = (project.proj_task_stages[1].stage_tasks.size
                    + project.proj_task_stages[2].stage_tasks.size
                    + project.proj_task_stages[3].stage_tasks.size
                    + project.proj_task_stages[4].stage_tasks.size
                    + project.proj_task_stages[5].stage_tasks.size).toString()

            itemView.txt_completed_tasks.text = project.proj_task_stages[0].stage_tasks.size.toString()

            itemView.setOnClickListener {
                listener.onProjClick(project)
            }
        }
    }
}