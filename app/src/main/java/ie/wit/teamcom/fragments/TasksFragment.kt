package ie.wit.teamcom.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.*
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Log
import ie.wit.teamcom.models.Task
import ie.wit.teamcom.models.TaskStage
import kotlinx.android.synthetic.main.fragment_tasks.view.*
import kotlinx.android.synthetic.main.fragment_tasks.view.tasks6RecyclerView
import kotlinx.android.synthetic.main.popup_change_stage.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList
import java.util.HashMap

class TasksFragment : Fragment(), AnkoLogger, TaskListener, StagesListener {

    lateinit var app: MainApp
    lateinit var root: View
    var task_list_1 = ArrayList<Task>()
    var task_list_2 = ArrayList<Task>()
    var task_list_3 = ArrayList<Task>()
    var task_list_4 = ArrayList<Task>()
    var task_list_5 = ArrayList<Task>()
    var task_list_6 = ArrayList<Task>()
    var task_stage_list = ArrayList<TaskStage>()
    var selected_stage = TaskStage()
    var selected_task = Task()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
        }
        app.getAllMembers(currentChannel.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_tasks, container, false)
        activity?.title = getString(R.string.menu_tasks)
        root.tasks1RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks2RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks3RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks4RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks5RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks6RecyclerView.layoutManager = LinearLayoutManager(activity)


        root.btnCreateTask.setOnClickListener {
            navigateTo(CreateTaskFragment.newInstance(currentChannel))
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel,app.currentActiveMember)
        getAllTasks()
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel,app.currentActiveMember)
    }

    fun getAllTasks(){
        task_list_1 = ArrayList<Task>()
        root.tasks1RecyclerView.adapter = TasksAdapter(task_list_1, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("0").child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.
                        getValue<Task>(Task::class.java)
                        task_list_1.add(task!!)
                        root.tasks1RecyclerView.adapter = TasksAdapter(task_list_1, this@TasksFragment)
                        root.tasks1RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("0").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })

        task_list_2 = ArrayList<Task>()
        root.tasks2RecyclerView.adapter = TasksAdapter(task_list_2, this@TasksFragment)

        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("1").child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.
                        getValue<Task>(Task::class.java)
                        task_list_2.add(task!!)
                        root.tasks2RecyclerView.adapter = TasksAdapter(task_list_2, this@TasksFragment)
                        root.tasks2RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("1").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })

        task_list_3 = ArrayList<Task>()
        root.tasks3RecyclerView.adapter = TasksAdapter(task_list_3, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("2").child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.
                        getValue<Task>(Task::class.java)
                        task_list_3.add(task!!)
                        root.tasks3RecyclerView.adapter = TasksAdapter(task_list_3, this@TasksFragment)
                        root.tasks3RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("2").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })

        task_list_4 = ArrayList<Task>()
        root.tasks4RecyclerView.adapter = TasksAdapter(task_list_4, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("3").child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.
                        getValue<Task>(Task::class.java)
                        task_list_4.add(task!!)
                        root.tasks4RecyclerView.adapter = TasksAdapter(task_list_4, this@TasksFragment)
                        root.tasks4RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("3").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })

        task_list_5 = ArrayList<Task>()
        root.tasks5RecyclerView.adapter = TasksAdapter(task_list_5, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("4").child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.
                        getValue<Task>(Task::class.java)
                        task_list_5.add(task!!)
                        root.tasks5RecyclerView.adapter = TasksAdapter(task_list_5, this@TasksFragment)
                        root.tasks5RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("4").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })

        task_list_6 = ArrayList<Task>()
        root.tasks6RecyclerView.adapter = TasksAdapter(task_list_6, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("5").child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.
                        getValue<Task>(Task::class.java)
                        task_list_6.add(task!!)
                        root.tasks6RecyclerView.adapter = TasksAdapter(task_list_6, this@TasksFragment)
                        root.tasks6RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("5").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            TasksFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

    fun change_state_dialog(stage : TaskStage, task : Task){
        task_stage_list = ArrayList<TaskStage>()
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_change_stage)
        //root.stage_rec.layoutManager = LinearLayoutManager(activity)

        val cancel = dialog.findViewById(R.id.btn_cancel_stage) as ImageButton
        val stageRecycler = dialog.findViewById(R.id.stage_rec) as RecyclerView
        stageRecycler.layoutManager = LinearLayoutManager(activity)

        app.database.child("channels").child(currentChannel!!.id).child("task_stages")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val stage = it.
                        getValue<TaskStage>(TaskStage::class.java)
                        if(stage!!.stage_active){
                            task_stage_list.add(stage)

                        }
                        stageRecycler.adapter = StageAdapter(task_stage_list, this@TasksFragment)
                        stageRecycler.adapter?.notifyDataSetChanged()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages")
                            .removeEventListener(this)
                    }
                }
            })
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    fun getCurrentTaskStage(task : Task){
        app.database.child("channels").child(currentChannel!!.id).child("task_stages")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val stage = it.
                        getValue<TaskStage>(TaskStage::class.java)
                        if(stage!!.stage_name == task.task_current_stage){
                            selected_stage = stage
                        }

                        app.database.child("channels").child(currentChannel!!.id).child("task_stages")
                            .removeEventListener(this)
                    }
                }
            })
    }

    override fun onTaskClicked(task: Task) {
        getCurrentTaskStage(task)
        selected_task = task

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_view_task)
        val cancel = dialog.findViewById(R.id.btn_cancel) as ImageButton
        val progressBar = dialog.findViewById(R.id.progressImportance) as ProgressBar
        val taskName = dialog.findViewById(R.id.txtTaskName) as TextView
        val taskDesc = dialog.findViewById(R.id.txtTaskDesc) as TextView
        val taskAssignee = dialog.findViewById(R.id.txtTaskAssignee) as TextView
        val taskCreator = dialog.findViewById(R.id.txtTaskCreator) as TextView
        val taskDueDate = dialog.findViewById(R.id.txtTaskDueDate) as TextView
        val taskStage = dialog.findViewById(R.id.txtTaskStage) as TextView
        val taskProgress = dialog.findViewById(R.id.txtTaskProgress) as TextView

        taskName.text = task.task_msg
        taskDesc.text = task.task_desc
        taskCreator.text = task.task_creator.firstName+ " " + task.task_creator.surname
        taskAssignee.text = task.task_assignee.firstName+ " " + task.task_assignee.surname
        taskDueDate.text = task.task_due_date_as_string+", "+task.task_due_time_as_string

        if(task.task_current_stage_color.take(1) != "#"){
            taskStage.setBackgroundColor(Color.parseColor("#"+task.task_current_stage_color))
        } else {
            taskStage.setBackgroundColor(Color.parseColor(task.task_current_stage_color))
        }

        taskStage.setOnClickListener {
            change_state_dialog(selected_stage, task)
        }

        progressBar.progress = task.task_importance
        taskProgress.text = task.task_importance.toString()+"/5"
        taskStage.text = task.task_current_stage
        /*proceed.setOnClickListener {
            app.database.child("channels").child(currentChannel!!.id).child("meetings").child(selected_meeting.meeting_uuid)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.ref.removeValue()
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            dialog.dismiss()
            navigateTo(MeetingsFragment.newInstance(currentChannel))
        }*/
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun onStageClick(stage: TaskStage) {
        selected_task.task_current_stage = stage.stage_name
        selected_task.task_current_stage_color = stage.stage_color_code
        stage.stage_tasks.add(selected_task)
        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/task_stages/${stage.stage_no-1}/"] = stage
                    app.database.updateChildren(childUpdates)

                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                }
            })

        var deleted = false
        var i = 0
        while (!deleted){
            app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("${selected_stage.stage_no-1}").child("stage_tasks").child("$i")
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.ref.removeValue()
                            deleted = true
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            if(i <= 5 && !deleted){
                i++
            } else if (i > 5 && !deleted){
                //Toast.makeText(requireContext(), "Error: Task Not Found In Any Stages!", Toast.LENGTH_SHORT).show()
                return
            }
        }
    }


}