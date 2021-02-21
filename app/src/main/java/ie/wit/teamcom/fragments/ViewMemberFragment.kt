package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.TasksAdapter
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_member.view.*
import kotlinx.android.synthetic.main.fragment_tasks.view.*
import kotlinx.android.synthetic.main.fragment_view_member.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList


class ViewMemberFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp

    //    lateinit var eventListener: ValueEventListener
    lateinit var root: View

    var completed = ArrayList<Task>()
    var overdue = ArrayList<Task>()
    var completed_overdue = ArrayList<Task>()
    var ongoing = ArrayList<Task>()

    var projects = ArrayList<Project>()
    var project = Project()
    var user = Member()
    var currentChannel = Channel()
    var selected_member = Member()
    var user_stats = Stats()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            selected_member = it.getParcelable("member_key")!!
            currentChannel = it.getParcelable("channel_key")!!
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_view_member, container, false)
        activity?.title = selected_member.firstName + " " + selected_member.surname



        root.txt_mem_name.text = selected_member.firstName + " " + selected_member.surname
        root.txtViewEmail.text = selected_member.email
        //TODO root.txtViewWhatIDo.text = selected_member.whatIDo
        root.txtViewRole.text = selected_member.role.role_name

        user_stats.user_id = selected_member.id

        var ref = FirebaseStorage.getInstance().getReference("photos/${selected_member.id}.jpg")
        ref.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it)
                .resize(260, 260)
                .transform(CropCircleTransformation())
                .into(root.img_mem)
        }

        root.btn_change_role.isVisible = app.currentActiveMember.role.role_name == "Admin"


        root.btn_change_role.setOnClickListener {
            navigateTo(AssignRolesFragment.newInstance(selected_member, currentChannel))
        }

        get_all_projects()

        return root
    }

    fun display_details() {
//TODO ONLY SHOW STATS + MENTAL HEALTH IF ADMIN {
        if (app.currentActiveMember.role.role_name == "Admin") {

            val aaChartView = root.findViewById<AAChartView>(R.id.aa_chart_view)
            val aaChartModel: AAChartModel = AAChartModel()
                .chartType(AAChartType.Area)
                .title("title")
                .subtitle("subtitle")
                .backgroundColor("#4b2b7f")
                .dataLabelsEnabled(true)
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Tokyo")
                            .data(
                                arrayOf(
                                    7.0,
                                    6.9,
                                    9.5,
                                    14.5,
                                    18.2,
                                    21.5,
                                    25.2,
                                    26.5,
                                    23.3,
                                    18.3,
                                    13.9,
                                    9.6
                                )
                            ),
                        AASeriesElement()
                            .name("NewYork")
                            .data(
                                arrayOf(
                                    0.2,
                                    0.8,
                                    5.7,
                                    11.3,
                                    17.0,
                                    22.0,
                                    24.8,
                                    24.1,
                                    20.1,
                                    14.1,
                                    8.6,
                                    2.5
                                )
                            ),
                        AASeriesElement()
                            .name("London")
                            .data(
                                arrayOf(
                                    0.9,
                                    0.6,
                                    3.5,
                                    8.4,
                                    13.5,
                                    17.0,
                                    18.6,
                                    17.9,
                                    14.3,
                                    9.0,
                                    3.9,
                                    1.0
                                )
                            ),
                        AASeriesElement()
                            .name("Berlin")
                            .data(
                                arrayOf(
                                    3.9,
                                    4.2,
                                    5.7,
                                    8.5,
                                    11.9,
                                    15.2,
                                    17.0,
                                    16.6,
                                    14.2,
                                    10.3,
                                    6.6,
                                    4.8
                                )
                            )
                    )
                )

            aaChartView.aa_drawChartWithChartModel(aaChartModel)
            root.txtOngoingTasks.text = ongoing.size.toString()
            root.txtCompletedTasks.text = completed.size.toString()
            root.txtOverdueTasks.text = overdue.size.toString()
            root.txtCompletedOverdueTasks.text = completed_overdue.size.toString()

            app.generateDateID("1")
            var current = app.valid_from_cal

            app.generateDateID("24")
            var plus_24hr = app.valid_to_cal

            app.generateDateID("168")
            var plus_7d = app.valid_to_cal

            app.generateDateID("336")
            var plus_14d = app.valid_to_cal

            var due_in_24_hrs = 0
            var due_in_7_days = 0
            var due_in_14_days = 0
            ongoing.forEach {
                if (it.task_due_date_id > plus_24hr) {
                    due_in_24_hrs += 1
                }
                if (it.task_due_date_id > plus_7d) {
                    due_in_7_days += 1
                }
                if (it.task_due_date_id > plus_14d) {
                    due_in_14_days += 1
                }
            }
            overdue.forEach {
                if (it.task_due_date_id > plus_24hr) {
                    due_in_24_hrs += 1
                }
                if (it.task_due_date_id > plus_7d) {
                    due_in_7_days += 1
                }
                if (it.task_due_date_id > plus_14d) {
                    due_in_14_days += 1
                }
            }

            root.txtDue24HrsTasks.text = due_in_24_hrs.toString()
            root.txtDue7daysTasks.text = due_in_7_days.toString()
            root.txtDue14daysTasks.text = due_in_14_days.toString()
            //TODO MENTAL HEALTH
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(member: Member, channel: Channel) =
            ViewMemberFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                    putParcelable("member_key", member)
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

    fun get_all_projects() {
        app.database.child("channels").child(ie.wit.teamcom.fragments.currentChannel!!.id)
            .child("projects")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase projects error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val proj = it.getValue<Project>(Project::class.java)
                        projects.add(proj!!)

                        app.database.child("channels")
                            .child(ie.wit.teamcom.fragments.currentChannel!!.id).child("projects")
                            .removeEventListener(this)
                    }
                    get_all_tasks()
                }
            })
    }

    fun get_all_tasks() {
        projects.forEach {
            project = it
            app.database.child("channels").child(ie.wit.teamcom.fragments.currentChannel!!.id)
                .child("projects")
                .child(project.proj_id).child("proj_task_stages").child("0")
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

                            if (task!!.task_assignee.id == app.auth.currentUser!!.uid) {
                                if (task.task_status == "Ongoing") {
                                    ongoing.add(task)
                                } else if (task.task_status == "Completed") {
                                    completed.add(task)
                                } else if (task.task_status == "Completed Overdue") {
                                    completed_overdue.add(task)
                                } else if (task.task_status == "Overdue") {
                                    overdue.add(task)
                                }
                            }

                            app.database.child("channels")
                                .child(ie.wit.teamcom.fragments.currentChannel!!.id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("0")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }
                })

            app.database.child("channels").child(ie.wit.teamcom.fragments.currentChannel!!.id)
                .child("projects")
                .child(project.proj_id).child("proj_task_stages").child("1")
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

                            if (task!!.task_assignee.id == app.auth.currentUser!!.uid) {
                                if (task.task_status == "Ongoing") {
                                    ongoing.add(task)
                                } else if (task.task_status == "Completed") {
                                    completed.add(task)
                                } else if (task.task_status == "Completed Overdue") {
                                    completed_overdue.add(task)
                                } else if (task.task_status == "Overdue") {
                                    overdue.add(task)
                                }
                            }

                            app.database.child("channels")
                                .child(ie.wit.teamcom.fragments.currentChannel!!.id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("1")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }

                })


            app.database.child("channels").child(ie.wit.teamcom.fragments.currentChannel!!.id)
                .child("projects")
                .child(project.proj_id).child("proj_task_stages").child("2")
                .child("stage_tasks").orderByChild("task_due_date_id")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase tasks/stages error : ${error.message}")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.children
                        children.forEach {
                            val task = it.getValue<Task>(Task::class.java)

                            if (task!!.task_assignee.id == app.auth.currentUser!!.uid) {
                                if (task.task_status == "Ongoing") {
                                    ongoing.add(task)
                                } else if (task.task_status == "Completed") {
                                    completed.add(task)
                                } else if (task.task_status == "Completed Overdue") {
                                    completed_overdue.add(task)
                                } else if (task.task_status == "Overdue") {
                                    overdue.add(task)
                                }
                            }
                            app.database.child("channels")
                                .child(ie.wit.teamcom.fragments.currentChannel!!.id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("2")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }
                })


            app.database.child("channels").child(ie.wit.teamcom.fragments.currentChannel!!.id)
                .child("projects")
                .child(project.proj_id).child("proj_task_stages").child("3")
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

                            if (task!!.task_assignee.id == app.auth.currentUser!!.uid) {
                                if (task.task_status == "Ongoing") {
                                    ongoing.add(task)
                                } else if (task.task_status == "Completed") {
                                    completed.add(task)
                                } else if (task.task_status == "Completed Overdue") {
                                    completed_overdue.add(task)
                                } else if (task.task_status == "Overdue") {
                                    overdue.add(task)
                                }
                            }

                            app.database.child("channels")
                                .child(ie.wit.teamcom.fragments.currentChannel!!.id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("3")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }
                })

            app.database.child("channels").child(ie.wit.teamcom.fragments.currentChannel!!.id)
                .child("projects")
                .child(project.proj_id).child("proj_task_stages").child("4")
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

                            if (task!!.task_assignee.id == app.auth.currentUser!!.uid) {
                                if (task.task_status == "Ongoing") {
                                    ongoing.add(task)
                                } else if (task.task_status == "Completed") {
                                    completed.add(task)
                                } else if (task.task_status == "Completed Overdue") {
                                    completed_overdue.add(task)
                                } else if (task.task_status == "Overdue") {
                                    overdue.add(task)
                                }
                            }
                            app.database.child("channels")
                                .child(ie.wit.teamcom.fragments.currentChannel!!.id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("4")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }
                })

            app.database.child("channels").child(ie.wit.teamcom.fragments.currentChannel!!.id)
                .child("projects")
                .child(project.proj_id).child("proj_task_stages").child("5")
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

                            if (task!!.task_assignee.id == app.auth.currentUser!!.uid) {
                                if (task.task_status == "Ongoing") {
                                    ongoing.add(task)
                                } else if (task.task_status == "Completed") {
                                    completed.add(task)
                                } else if (task.task_status == "Completed Overdue") {
                                    completed_overdue.add(task)
                                } else if (task.task_status == "Overdue") {
                                    overdue.add(task)
                                }
                            }
                            app.database.child("channels")
                                .child(ie.wit.teamcom.fragments.currentChannel!!.id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("5")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                        display_details()
                    }
                })
        }
    }
}