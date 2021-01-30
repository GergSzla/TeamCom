package ie.wit.teamcom.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.ProjectListener
import ie.wit.teamcom.adapters.ProjectsAdapter
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Project
import kotlinx.android.synthetic.main.fragment_projects_list.view.*
import kotlinx.android.synthetic.main.fragment_role_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList

class ProjectListFragment : Fragment(), AnkoLogger, ProjectListener {

    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var projectList = ArrayList<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_projects_list, container, false)
        activity?.title = getString(R.string.title_project_settings)

        root.btn_add_project.setOnClickListener {
            navigateTo(TaskStagesFragment.newInstance(currentChannel))
        }

        root.projectsRecyclerView.layoutManager = LinearLayoutManager(activity)

        setSwipeRefresh()

        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllProjects()
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    override fun onProjClick(project: Project) {
        navigateTo(TasksFragment.newInstance(currentChannel,project))
    }

    fun getAllProjects() {
        projectList = ArrayList<Project>()
        app.database.child("channels").child(currentChannel!!.id).child("projects")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase projects error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val project = it.getValue<Project>(Project::class.java)
                        projectList.add(project!!)
                        root.projectsRecyclerView.adapter =
                            ProjectsAdapter(projectList, this@ProjectListFragment)
                        root.projectsRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("projects")
                            .removeEventListener(this)
                    }
                }
            })
    }


    fun setSwipeRefresh() {
        root.swiperefreshProjects.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshProjects.isRefreshing = true
                getAllProjects()
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefreshProjects.isRefreshing) root.swiperefreshProjects.isRefreshing = false
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            ProjectListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

}