package ie.wit.teamcom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Department
import kotlinx.android.synthetic.main.card_dept.view.*
import kotlinx.android.synthetic.main.card_invite.view.*


interface DepartmentListener {
    fun onDeptClick(dept: Department)
}

class DepartmentAdapter constructor(var depts: ArrayList<Department>,
                                private val listener: DepartmentListener): RecyclerView.Adapter<DepartmentAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_dept, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val dept = depts[holder.adapterPosition]
        holder.bind(dept, listener)
    }

    override fun getItemCount(): Int = depts.size

    fun removeAt(position: Int) {
        depts.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(dept: Department, listener: DepartmentListener) {
            itemView.tag = dept

            itemView.txtDeptNameCard.text = dept.dept_name

            itemView.setOnClickListener {
                listener.onDeptClick(dept)
            }
        }
    }
}