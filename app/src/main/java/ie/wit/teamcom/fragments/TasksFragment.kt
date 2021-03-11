package ie.wit.teamcom.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.*
import ie.wit.teamcom.R
import ie.wit.teamcom.activities.LoginRegActivity
import ie.wit.teamcom.adapters.*
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import kotlinx.android.synthetic.main.fragment_role_list.view.*
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
    var selected_project = Project()
    var completed_tasks = ArrayList<Task>()
    var active_tasks = ArrayList<Task>()
    lateinit var loader: androidx.appcompat.app.AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
            selected_project = it.getParcelable("proj_key")!!
        }
        app.getAllMembers(currentChannel.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_tasks, container, false)
        activity?.title = getString(R.string.proj_tasks)
        root.tasks1RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks2RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks3RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks4RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks5RecyclerView.layoutManager = LinearLayoutManager(activity)
        root.tasks6RecyclerView.layoutManager = LinearLayoutManager(activity)

        loader = createLoader(requireActivity())

        root.btnCreateTask.setOnClickListener {
            navigateTo(CreateTaskFragment.newInstance(currentChannel, selected_project))
        }

        root.btnEditProject.setOnClickListener {
            navigateTo(TaskStagesFragment.newInstance(currentChannel, selected_project, true))
        }

        setSwipeRefresh()

        return root
    }

    fun setSwipeRefresh() {
        root.swiperefreshTasks.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshTasks.isRefreshing = true
                getAllTasks()
                check_completed()
            }
        })
    }


    fun checkSwipeRefresh() {
        if (root.swiperefreshTasks.isRefreshing) root.swiperefreshTasks.isRefreshing = false
    }

    fun update_project_stats() {
        //count active tasks (excl. complete)
        active_tasks = ArrayList<Task>()
        for (i in 1..5) {
            app.database.child("channels").child(currentChannel!!.id).child("projects")
                .child(selected_project.proj_id).child("proj_task_stages").child("$i")
                .child("stage_tasks")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase tasks/stages error : ${error.message}")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.children
                        children.forEach {
                            val task = it.getValue<Task>(Task::class.java)
                            active_tasks.add(task!!)
                            app.database.child("channels").child(currentChannel!!.id)
                                .child("projects").child(selected_project.proj_id)
                                .child("proj_task_stages").child("$i")
                                .removeEventListener(this)
                        }
                        var active = active_tasks.size
                        app.database.child("channels").child(currentChannel.id)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val childUpdates_ = HashMap<String, Any>()
                                    childUpdates_["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_active_tasks/"] =
                                        active
                                    app.database.updateChildren(childUpdates_)

                                    app.database.child("channels").child(currentChannel.id)
                                        .removeEventListener(this)
                                }
                            })
                    }
                })
        }

        //count complete tasks
        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .child(selected_project.proj_id).child("proj_task_stages").child("0")
            .child("stage_tasks")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val task = it.getValue<Task>(Task::class.java)
                        completed_tasks.add(task!!)
                        app.database.child("channels").child(currentChannel!!.id).child("projects")
                            .child(selected_project.proj_id).child("proj_task_stages").child("0")
                            .child("stage_tasks")
                            .removeEventListener(this)
                    }
                    var completed = completed_tasks.size
                    app.database.child("channels").child(currentChannel.id)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val childUpdates = HashMap<String, Any>()
                                childUpdates["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_completed_tasks/"] =
                                    completed
                                app.database.updateChildren(childUpdates)

                                app.database.child("channels").child(currentChannel.id)
                                    .removeEventListener(this)
                            }
                        })
                }
            })

        //save new number to db

    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllTasks()
        update_project_stats()

    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    fun check_completed() {

        app.generateDateID("1")
        task_list_1.forEach {
            if (app.valid_from_cal > it.task_due_date_id) {
                var check_comp_task = it
                check_comp_task.passed_due_date = true

                var task_index = task_list_1.indexOf(it)
                val task_update = HashMap<String, Any>()

                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/0/stage_tasks/$task_index/passed_due_date"] =
                    true

                app.database.updateChildren(task_update)
            }
            //check if completed before due
            var task_index = task_list_1.indexOf(it)
            if (it.task_completed_date_id > it.task_due_date_id){
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/0/stage_tasks/$task_index/task_status"] =
                    "Completed"
                app.database.updateChildren(task_update)
            }
            //check if overdue but completed
            if (it.task_completed_date_id <= it.task_due_date_id){
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/0/stage_tasks/$task_index/task_status"] =
                    "Completed Overdue"
                app.database.updateChildren(task_update)

            }
        }
        task_list_2.forEach {
            if (app.valid_from_cal > it.task_due_date_id) {
                var check_comp_task = it
                check_comp_task.passed_due_date = true

                var task_index = task_list_2.indexOf(it)
                val task_update = HashMap<String, Any>()

                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/1/stage_tasks/$task_index/passed_due_date"] =
                    true


                app.database.updateChildren(task_update)

            }
            var task_index = task_list_2.indexOf(it)
            if (app.valid_from_cal < it.task_due_date_id){
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/1/stage_tasks/$task_index/task_status"] =
                    "Overdue"
                app.database.updateChildren(task_update)
            } else {
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/1/stage_tasks/$task_index/task_status"] =
                    "Ongoing"
                app.database.updateChildren(task_update)
            }
        }
        task_list_3.forEach {
            if (app.valid_from_cal > it.task_due_date_id) {
                var check_comp_task = it
                check_comp_task.passed_due_date = true

                var task_index = task_list_3.indexOf(it)
                val task_update = HashMap<String, Any>()

                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/2/stage_tasks/$task_index/passed_due_date"] =
                    true

                app.database.updateChildren(task_update)
            }

            var task_index = task_list_3.indexOf(it)
            if (app.valid_from_cal < it.task_due_date_id){
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/2/stage_tasks/$task_index/task_status"] =
                    "Overdue"
                app.database.updateChildren(task_update)
            } else {
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/2/stage_tasks/$task_index/task_status"] =
                    "Ongoing"
                app.database.updateChildren(task_update)
            }

        }
        task_list_4.forEach {
            if (app.valid_from_cal > it.task_due_date_id) {
                var check_comp_task = it
                check_comp_task.passed_due_date = true

                var task_index = task_list_2.indexOf(it)
                val task_update = HashMap<String, Any>()

                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/3/stage_tasks/$task_index/passed_due_date"] =
                    true

                app.database.updateChildren(task_update)
            }

            var task_index = task_list_4.indexOf(it)
            if (app.valid_from_cal < it.task_due_date_id){
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/3/stage_tasks/$task_index/task_status"] =
                    "Overdue"
                app.database.updateChildren(task_update)
            } else {
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/3/stage_tasks/$task_index/task_status"] =
                    "Ongoing"
                app.database.updateChildren(task_update)
            }
        }
        task_list_5.forEach {
            if (app.valid_from_cal > it.task_due_date_id) {
                var check_comp_task = it
                check_comp_task.passed_due_date = true

                var task_index = task_list_2.indexOf(it)
                val task_update = HashMap<String, Any>()

                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/4/stage_tasks/$task_index/passed_due_date"] =
                    true

                app.database.updateChildren(task_update)
            }

            var task_index = task_list_5.indexOf(it)
            if (app.valid_from_cal < it.task_due_date_id){
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/4/stage_tasks/$task_index/task_status"] =
                    "Overdue"
                app.database.updateChildren(task_update)
            } else {
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/4/stage_tasks/$task_index/task_status"] =
                    "Ongoing"
                app.database.updateChildren(task_update)
            }
        }
        task_list_6.forEach {
            if (app.valid_from_cal > it.task_due_date_id) {
                var check_comp_task = it
                check_comp_task.passed_due_date = true

                var task_index = task_list_2.indexOf(it)
                val task_update = HashMap<String, Any>()

                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/5/stage_tasks/$task_index/passed_due_date"] =
                    true

                app.database.updateChildren(task_update)
            }

            var task_index = task_list_6.indexOf(it)
            if (app.valid_from_cal < it.task_due_date_id){
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/5/stage_tasks/$task_index/task_status"] =
                    "Overdue"
                app.database.updateChildren(task_update)
            } else {
                val task_update = HashMap<String, Any>()
                task_update["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/5/stage_tasks/$task_index/task_status"] =
                    "Ongoing"
                app.database.updateChildren(task_update)
            }
        }
    }

    fun getAllTasks() {
        task_list_1 = ArrayList<Task>()
        root.tasks1RecyclerView.adapter = TasksAdapter(task_list_1, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .child(selected_project.proj_id).child("proj_task_stages").child("0")
            .child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.getValue<Task>(Task::class.java)
                        task_list_1.add(task!!)
                        root.tasks1RecyclerView.adapter =
                            TasksAdapter(task_list_1, this@TasksFragment)
                        root.tasks1RecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("channels").child(currentChannel!!.id).child("projects")
                            .child(selected_project.proj_id).child("proj_task_stages").child("0")
                            .child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                    if (task_list_1.size <= 0) {
                        root.txtStage1Title.isVisible = false
                        root.rec_1_titles.isVisible = false
                    } else {
                        root.txtStage1Title.text = task_list_1[0].task_current_stage
                    }
                }
            })

        task_list_2 = ArrayList<Task>()
        root.tasks2RecyclerView.adapter = TasksAdapter(task_list_2, this@TasksFragment)

        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .child(selected_project.proj_id).child("proj_task_stages").child("1")
            .child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.getValue<Task>(Task::class.java)
                        task_list_2.add(task!!)
                        root.tasks2RecyclerView.adapter =
                            TasksAdapter(task_list_2, this@TasksFragment)
                        root.tasks2RecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("channels").child(currentChannel!!.id).child("projects")
                            .child(selected_project.proj_id).child("proj_task_stages").child("1")
                            .child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                    if (task_list_2.size <= 0) {
                        root.txtStage2Title.isVisible = false
                        root.rec_2_titles.isVisible = false
                    } else {
                        root.txtStage2Title.text = task_list_2[0].task_current_stage
                    }
                }

            })


        task_list_3 = ArrayList<Task>()
        root.tasks3RecyclerView.adapter = TasksAdapter(task_list_3, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .child(selected_project.proj_id).child("proj_task_stages").child("2")
            .child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.getValue<Task>(Task::class.java)
                        task_list_3.add(task!!)
                        root.tasks3RecyclerView.adapter =
                            TasksAdapter(task_list_3, this@TasksFragment)
                        root.tasks3RecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("channels").child(currentChannel!!.id).child("projects")
                            .child(selected_project.proj_id).child("proj_task_stages").child("2")
                            .child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                    if (task_list_3.size <= 0) {
                        root.txtStage3Title.isVisible = false
                        root.rec_3_titles.isVisible = false
                    } else {
                        root.txtStage3Title.text = task_list_3[0].task_current_stage
                    }
                }
            })


        task_list_4 = ArrayList<Task>()
        root.tasks4RecyclerView.adapter = TasksAdapter(task_list_4, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .child(selected_project.proj_id).child("proj_task_stages").child("3")
            .child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.getValue<Task>(Task::class.java)
                        task_list_4.add(task!!)
                        root.tasks4RecyclerView.adapter =
                            TasksAdapter(task_list_4, this@TasksFragment)
                        root.tasks4RecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("channels").child(currentChannel!!.id).child("projects")
                            .child(selected_project.proj_id).child("proj_task_stages").child("3")
                            .child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                    if (task_list_4.size <= 0) {
                        root.txtStage4Title.isVisible = false
                        root.rec_4_titles.isVisible = false
                    } else {
                        root.txtStage4Title.text = task_list_4[0].task_current_stage
                    }
                }
            })

        task_list_5 = ArrayList<Task>()
        root.tasks5RecyclerView.adapter = TasksAdapter(task_list_5, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .child(selected_project.proj_id).child("proj_task_stages").child("4")
            .child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.getValue<Task>(Task::class.java)
                        task_list_5.add(task!!)
                        root.tasks5RecyclerView.adapter =
                            TasksAdapter(task_list_5, this@TasksFragment)
                        root.tasks5RecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("channels").child(currentChannel!!.id).child("projects")
                            .child(selected_project.proj_id).child("proj_task_stages").child("4")
                            .child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                    if (task_list_5.size <= 0) {
                        root.txtStage5Title.isVisible = false
                        root.rec_5_titles.isVisible = false
                    } else {
                        root.txtStage5Title.text = task_list_5[0].task_current_stage
                    }
                }
            })

        task_list_6 = ArrayList<Task>()
        root.tasks6RecyclerView.adapter = TasksAdapter(task_list_6, this@TasksFragment)
        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .child(selected_project.proj_id).child("proj_task_stages").child("5")
            .child("stage_tasks").orderByChild("task_due_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val task = it.getValue<Task>(Task::class.java)
                        task_list_6.add(task!!)
                        root.tasks6RecyclerView.adapter =
                            TasksAdapter(task_list_6, this@TasksFragment)
                        root.tasks6RecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("channels").child(currentChannel!!.id).child("projects")
                            .child(selected_project.proj_id).child("proj_task_stages").child("5")
                            .child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                    if (task_list_6.size <= 0) {
                        root.txtStage6Title.isVisible = false
                        root.rec_6_titles.isVisible = false
                    } else {
                        root.txtStage6Title.text = task_list_6[0].task_current_stage
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
        fun newInstance(channel: Channel, project: Project) =
            TasksFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                    putParcelable("proj_key", project)
                }
            }
    }

    fun change_state_dialog(stage: TaskStage, task: Task) {
        task_stage_list = ArrayList<TaskStage>()
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_change_stage)

        val cancel = dialog.findViewById(R.id.btn_cancel_stage) as ImageButton
        val stageRecycler = dialog.findViewById(R.id.stage_rec) as RecyclerView
        stageRecycler.layoutManager = LinearLayoutManager(activity)

        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .child(selected_project.proj_id).child("proj_task_stages")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val stage = it.getValue<TaskStage>(TaskStage::class.java)
                        if (stage!!.stage_active) {
                            task_stage_list.add(stage)
                        }
                        stageRecycler.adapter = StageAdapter(task_stage_list, this@TasksFragment)
                        stageRecycler.adapter?.notifyDataSetChanged()
                        app.database.child("channels").child(currentChannel!!.id)
                            .child("projects").child(selected_project.proj_id)
                            .child("proj_task_stages")
                            .removeEventListener(this)
                    }
                }
            })
        cancel.setOnClickListener {
            dialog.dismiss()
            check_completed()
        }
        dialog.show()
    }


    fun getCurrentTaskStage(task: Task) {
        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .child(selected_project.proj_id).child("proj_task_stages")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase tasks/stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val stage = it.getValue<TaskStage>(TaskStage::class.java)
                        if (stage!!.stage_name == task.task_current_stage) {
                            selected_stage = stage
                        }

                        app.database.child("channels").child(currentChannel!!.id)
                            .child("projects").child(selected_project.proj_id)
                            .child("proj_task_stages")
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
        taskCreator.text = task.task_creator.firstName + " " + task.task_creator.surname
        taskAssignee.text = task.task_assignee.firstName + " " + task.task_assignee.surname
        taskDueDate.text = task.task_due_date_as_string + ", " + task.task_due_time_as_string

        if (task.task_current_stage_color.take(1) != "#") {
            taskStage.setBackgroundColor(Color.parseColor("#" + task.task_current_stage_color))
        } else {
            taskStage.setBackgroundColor(Color.parseColor(task.task_current_stage_color))
        }

        taskStage.setOnClickListener {
            dialog.dismiss()
            change_state_dialog(selected_stage, task)
        }

        progressBar.progress = task.task_importance
        taskProgress.text = task.task_importance.toString() + "/5"
        taskStage.text = task.task_current_stage
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun onStageClick(stage: TaskStage) {
        var index_of_task = selected_stage.stage_tasks.indexOf(selected_task)
        selected_stage.stage_tasks.removeAt(index_of_task)
        selected_task.task_current_stage = stage.stage_name
        selected_task.task_current_stage_color = stage.stage_color_code
        if (stage.stage_name == "Completed"){
            app.generateDateID("1")
            selected_task.task_completed_date_id = app.valid_to_cal
        }
        stage.stage_tasks.add(selected_task)

        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/${stage.stage_no - 1}/"] =
                        stage
                    app.database.updateChildren(childUpdates)

                    val childUpdates_ = HashMap<String, Any>()
                    childUpdates_["/channels/${currentChannel.id}/projects/${selected_project.proj_id}/proj_task_stages/${selected_stage.stage_no - 1}/"] =
                        selected_stage
                    app.database.updateChildren(childUpdates_)

                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                    check_completed()
                    navigateTo(TasksFragment.newInstance(currentChannel, selected_project))

                }
            })

//        app.database.child("channels").child(currentChannel!!.id).child("projects")
//            .child(selected_project.proj_id).child("proj_task_stages").child("${stage.stage_no - 1}").child("stage_tasks")
//            .addListenerForSingleValueEvent(
//                object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        snapshot.ref.removeValue()
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                    }
//                })

//        var deleted = false
//        var i = 0
//        while (!deleted) {
//            app.database.child("channels").child("projects").child(selected_project.proj_id).child("proj_task_stages")
//                .child("${selected_stage.stage_no - 1}").child("stage_tasks").child("$i")
//                .addListenerForSingleValueEvent(
//                    object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            snapshot.ref.removeValue()
//                            deleted = true
//                            return
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                        }
//                    })
//            if (i <= 5 && !deleted) {
//                i++
//            } else if (i > 5 && !deleted) {
//                //Toast.makeText(requireContext(), "Error: Task Not Found In Any Stages!", Toast.LENGTH_SHORT).show()
//                return
//            }
//        }
    }


}