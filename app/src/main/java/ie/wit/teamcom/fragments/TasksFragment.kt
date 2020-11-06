package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.RoleAdapter
import ie.wit.teamcom.adapters.TaskListener
import ie.wit.teamcom.adapters.TasksAdapter
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Role
import ie.wit.teamcom.models.Task
import kotlinx.android.synthetic.main.fragment_role_list.view.*
import kotlinx.android.synthetic.main.fragment_tasks.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList

class TasksFragment : Fragment(), AnkoLogger, TaskListener {

    lateinit var app: MainApp
    lateinit var root: View
    var task_list = ArrayList<Task>()

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

        getAllTasks()

        root.btnCreateTask.setOnClickListener {
            navigateTo(CreateTaskFragment.newInstance(currentChannel))
        }

        return root
    }

    fun getAllTasks(){
        task_list = ArrayList<Task>()
        //TODO: Get from all Stages
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
                        task_list.add(task!!)
                        root.tasks1RecyclerView.adapter = TasksAdapter(task_list, this@TasksFragment)
                        root.tasks1RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("0").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })


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
                        task_list.add(task!!)
                        root.tasks2RecyclerView.adapter = TasksAdapter(task_list, this@TasksFragment)
                        root.tasks2RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("1").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })

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
                        task_list.add(task!!)
                        root.tasks3RecyclerView.adapter = TasksAdapter(task_list, this@TasksFragment)
                        root.tasks3RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("2").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })

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
                        task_list.add(task!!)
                        root.tasks4RecyclerView.adapter = TasksAdapter(task_list, this@TasksFragment)
                        root.tasks4RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("3").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })

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
                        task_list.add(task!!)
                        root.tasks5RecyclerView.adapter = TasksAdapter(task_list, this@TasksFragment)
                        root.tasks5RecyclerView.adapter?.notifyDataSetChanged()
                        //checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("task_stages").child("4").child("stage_tasks").orderByChild("task_due_date_id")
                            .removeEventListener(this)
                    }
                }
            })

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
                        task_list.add(task!!)
                        root.tasks6RecyclerView.adapter = TasksAdapter(task_list, this@TasksFragment)
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

    override fun onTaskClicked(task: Task) {
        TODO("Not yet implemented")
    }
}