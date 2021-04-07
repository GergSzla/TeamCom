package ie.wit.teamcom.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.*
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_view_member.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

var user_mh = UserMHModel()
var string_range_1 = ""
var string_range_2 = ""
var string_overall = ""
var string_mh_desc = ""
var allow_admin = false
var survey_enabled = false
var survey_freq = ""
var completed = ArrayList<Task>()
var overdue = ArrayList<Task>()
var completed_overdue = ArrayList<Task>()
var ongoing = ArrayList<Task>()
var due_in_24_hrs = 0
var due_in_7_days = 0
var due_in_14_days = 0

class ViewMemberFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp

    lateinit var eventListener: ValueEventListener
    lateinit var root: View


    var projects = ArrayList<Project>()
    var project = Project()
    var user = Member()
    var currentChannel_ = Channel()
    var selected_member = Member()
    var user_stats = Stats()
    lateinit var loader: androidx.appcompat.app.AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            selected_member = it.getParcelable("member_key")!!
            currentChannel_ = it.getParcelable("channel_key")!!
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_view_member, container, false)
        activity?.title = selected_member.firstName + " " + selected_member.surname

        val chart = root.findViewById(R.id.pie_tasks) as PieChart

        (selected_member.firstName + " " + selected_member.surname).also { root.txt_mem_name.text = it }
        (selected_member.firstName + " " + selected_member.surname).also { root.txtViewName.text = it }
        root.txtViewEmail.text = selected_member.email
        root.txtViewRole.text = selected_member.role.role_name

        user_stats.user_id = selected_member.id

        val ref = FirebaseStorage.getInstance().getReference("photos/${selected_member.id}.jpg")
        ref.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it)
                .resize(260, 260)
                .transform(CropCircleTransformation())
                .into(root.img_mem)
        }

        root.btn_change_role.isVisible = app.currentActiveMember.role.role_name == "Admin"
        root.btn_kick_user.isVisible = app.currentActiveMember.role.role_name == "Admin"


        root.btn_change_role.setOnClickListener {
            navigateTo(AssignRolesFragment.newInstance(selected_member, currentChannel))
        }

        root.btn_kick_user.setOnClickListener {
            show_warning_dialog()
        }
        loader = createLoader(requireActivity())

        showLoader(loader, "Loading . . . ", "Loading Page . . . ")
        get_all_projects(app.database, selected_member.id)

        Handler().postDelayed(
            {
                check_pref(app.database, false, selected_member.id)
            },
            2000 // value in milliseconds
        )
        return root
    }

    private fun show_warning_dialog() {
        AlertDialog.Builder(requireContext())
            .setView(R.layout.warning_dialog_kick)
            .setTitle("Warning!")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Proceed") { dialog, _ ->
                kick_selected_user()
                dialog.dismiss()
            }.show()

//        root.textView20.text = "Are You Sure You Wish To Proceed and Kick This User?"
    }

    fun get_mh_entry(db: DatabaseReference, pa: Boolean, userID : String) {
//        TODO: CHANGE TO SELECTED USER ID NOT CURRENT
        db.child("channels").child(currentChannel.id).child("surveys")
            .child(userID).child("entry")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    user_mh.set_of_ans_1_per =
                        snapshot.child("set_of_ans_1_per").value.toString().toDouble()
                    user_mh.set_of_ans_2_per =
                        snapshot.child("set_of_ans_2_per").value.toString().toDouble()
                    user_mh.user_id = snapshot.child("user_id").value.toString()

                    db.child("channels").child(currentChannel.id).child("surveys")
                        .child(userID).child("entry")
                        .removeEventListener(this)
                    analyse_data(db, pa)
                }
            })
    }

    private fun kick_selected_user() {

        //TODO: CHECK IF ADMIN
        if (selected_member.id !== auth.currentUser!!.uid) {

            delete_user_from_channel()

        } else {
            makeText(context, "You Cannot Kick Yourself", Toast.LENGTH_LONG).show()
        }
    }

    private fun delete_user_from_channel() {
        app.database.child("channels").child(currentChannel.id).child("members")
            .child(selected_member.id)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        app.database.child("users").child(selected_member.id).child("channels")
            .child(currentChannel.id)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
    }


    fun display_details() {
        if (app.currentActiveMember.role.role_name == "Admin") {


            app.generateDateID("1")
            var current = app.valid_from_cal

            app.generateDateID("24")
            var plus_24hr = app.valid_to_cal

            app.generateDateID("168")
            var plus_7d = app.valid_to_cal

            app.generateDateID("336")
            var plus_14d = app.valid_to_cal

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

            val entries = ArrayList<PieEntry>()

            entries.add(PieEntry(ongoing.size.toFloat(), "Ongoing"))
            entries.add(PieEntry(completed.size.toFloat(), "Completed On Time"))
            entries.add(PieEntry(overdue.size.toFloat(), "Overdue"))
            entries.add(PieEntry(completed_overdue.size.toFloat(), "Completed Overdue"))

            val set = PieDataSet(entries, "Tasks Statistics")

            val data = PieData(set)

            root.pie_tasks.data = data
            set.setColors(
                Color.parseColor("#4300bf"),
                Color.parseColor("#5910e1"),
                Color.parseColor("#5c2eb2"),
                Color.parseColor("#400ee8")
            )
            data.setValueTextSize(16f)
            root.pie_tasks.holeRadius = 6f
            root.pie_tasks.transparentCircleRadius = 7f
            root.pie_tasks.description.isEnabled = false
            root.pie_tasks.legend.isEnabled = false
            root.pie_tasks.isEnabled = false
            root.pie_tasks.setEntryLabelTextSize(10f)
            root.pie_tasks.invalidate() // refresh

            root.txtOngoingTasks.text = ongoing.size.toString()
            root.txtCompletedTasks.text = completed.size.toString()
            root.txtOverdueTasks.text = overdue.size.toString()
            root.txtCompletedOverdueTasks.text = completed_overdue.size.toString()

            hideLoader(loader)
            if (allow_admin) {
                root.mem_mental_health.isVisible = true
                get_mh_entry(app.database, false, selected_member.id)
            } else {
                root.mem_mental_health.isVisible = false
            }

        }

    }


    fun analyse_data(db: DatabaseReference, pa: Boolean) {
        if(user_mh.set_of_ans_2_per == 0.0 || user_mh.set_of_ans_1_per == 0.0){
            root.mem_mental_health.isVisible = false
        }


        if (!pa) {
            root.progressBar_mh.progress = user_mh.set_of_ans_2_per.toInt()
            root.txt_percentage.text = String.format("%.1f", user_mh.set_of_ans_2_per)
        }

        string_mh_desc = ""
        if (user_mh.set_of_ans_2_per > 85) {
            string_range_2 = "range_1"
        } else if (user_mh.set_of_ans_2_per > 70 && user_mh.set_of_ans_2_per <= 85) {
            string_range_2 = "range_2"
        } else if (user_mh.set_of_ans_2_per > 55 && user_mh.set_of_ans_2_per <= 70) {
            string_range_2 = "range_3"
        } else if (user_mh.set_of_ans_2_per > 40 && user_mh.set_of_ans_2_per <= 55) {
            string_range_2 = "range_4"
        } else if (user_mh.set_of_ans_2_per <= 40) {
            string_range_2 = "range_5"
        }

        if (user_mh.set_of_ans_1_per > 80) {
            string_range_1 = "range_1"
        } else if (user_mh.set_of_ans_1_per > 60.5 && user_mh.set_of_ans_1_per <= 80) {
            string_range_1 = "range_2"
        } else if (user_mh.set_of_ans_1_per > 45.5 && user_mh.set_of_ans_1_per <= 60.5) {
            string_range_1 = "range_3"
        } else if (user_mh.set_of_ans_1_per > 37 && user_mh.set_of_ans_1_per <= 45.5) {
            string_range_1 = "range_4"
        } else if (user_mh.set_of_ans_1_per <= 37) {
            string_range_1 = "range_5"
        }

        if (string_range_1 == "range_1") {
            string_mh_desc += "-Feels Supported, Valued"
        } else if (string_range_1 == "range_2") {

            if (due_in_24_hrs >= 3) {
                string_mh_desc += "-With $due_in_24_hrs, user is potentially stressed."
            } else {
                string_mh_desc += "\nPotentially Stressed."
            }

        } else if (string_range_1 == "range_3") {
            if (due_in_24_hrs >= 3) {
                string_mh_desc += "-With $due_in_24_hrs, user is potentially overwhelmed."
            } else {
                string_mh_desc += "\nPotentially Overwhelmed."
            }

        } else if (string_range_1 == "range_4") {
            string_mh_desc += "-Perhaps does not feel valued or feels their work is not valued."
        } else if (string_range_1 == "range_5") {
            string_mh_desc += "-Perhaps does not feel valued or feels their work is not valued \nand feels overwhelmed and unsupported."
        }

        if (string_range_2 == "range_1") {
            string_overall = "Perfectly Well"
            string_mh_desc += "\n-Feels Perfectly Well"

        } else if (string_range_2 == "range_2") {
            string_overall = "Potentially Stressed"
            string_mh_desc += "\n-Could be feeling quite stressed recently."

        } else if (string_range_2 == "range_3") {
            string_overall = "Quite Concerning"
            string_mh_desc += "\n-Could be feeling a little down recently."

        } else if (string_range_2 == "range_4") {
            string_overall = "Unwell"
            string_mh_desc += "\n-Could be feeling a little down or unwell recently."

        } else if (string_range_2 == "range_5") {
            string_overall = "Could Use Some Friendly Words"
            string_mh_desc += "\n-Seems like this user needs some support. Please approach with care and support! \n-Please refer to the following website!\nhttps://www.aware.ie/support/support-line/"

        }

        if (!pa) {
            display_mh_stats()
        }
    }

    private fun display_mh_stats() {
        root.txt_overall_standing.text = string_overall
        root.txt_mh_desc.text = string_mh_desc
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

    fun get_all_projects(db: DatabaseReference, userID : String) {

        //clear global vars
        completed.clear()
        overdue.clear()
        completed_overdue.clear()
        ongoing.clear()

        due_in_24_hrs = 0
        due_in_7_days = 0
        due_in_14_days = 0

        //
        projects = ArrayList<Project>()
        db.child("channels").child(currentChannel.id)
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

                        db.child("channels")
                            .child(currentChannel.id)
                            .child("projects")
                            .removeEventListener(this)
                    }
                    get_all_tasks(db, currentChannel.id, userID)
                }
            })
    }

    fun get_all_tasks(db: DatabaseReference, channel_id: String, userID : String) {
        projects.forEach {
            project = it
            db.child("channels").child(channel_id)
                .child("projects")
                .child(project.proj_id)
                .child("proj_task_stages")
                .child("0")
                .child("stage_tasks")
                .orderByChild("task_due_date_id")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase tasks/stages error : ${error.message}")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        //hideLoader(loader)
                        val children = snapshot.children
                        children.forEach {
                            val task = it.getValue<Task>(Task::class.java)

                            if (task!!.task_assignee.id == userID) {
                                when (task.task_status) {
                                    "Ongoing" -> {
                                        ongoing.add(task)
                                    }
                                    "Completed" -> {
                                        completed.add(task)
                                    }
                                    "Completed Overdue" -> {
                                        completed_overdue.add(task)
                                    }
                                    "Overdue" -> {
                                        overdue.add(task)
                                    }
                                }
                            }

                            db.child("channels")
                                .child(channel_id)
                                .child("projects")
                                .child(project.proj_id)
                                .child("proj_task_stages")
                                .child("0")
                                .child("stage_tasks")
                                .orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }
                })

            db.child("channels")
                .child(channel_id)
                .child("projects")
                .child(project.proj_id)
                .child("proj_task_stages")
                .child("1")
                .child("stage_tasks")
                .orderByChild("task_due_date_id")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase tasks/stages error : ${error.message}")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        //hideLoader(loader)
                        val children = snapshot.children
                        children.forEach {
                            val task = it.getValue<Task>(Task::class.java)

                            if (task!!.task_assignee.id == userID) {
                                when (task.task_status) {
                                    "Ongoing" -> {
                                        ongoing.add(task)
                                    }
                                    "Completed" -> {
                                        completed.add(task)
                                    }
                                    "Completed Overdue" -> {
                                        completed_overdue.add(task)
                                    }
                                    "Overdue" -> {
                                        overdue.add(task)
                                    }
                                }
                            }

                            db.child("channels")
                                .child(channel_id)
                                .child("projects")
                                .child(project.proj_id)
                                .child("proj_task_stages")
                                .child("1")
                                .child("stage_tasks")
                                .orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }

                })


            db.child("channels").child(channel_id)
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

                            if (task!!.task_assignee.id == userID) {
                                when (task.task_status) {
                                    "Ongoing" -> {
                                        ongoing.add(task)
                                    }
                                    "Completed" -> {
                                        completed.add(task)
                                    }
                                    "Completed Overdue" -> {
                                        completed_overdue.add(task)
                                    }
                                    "Overdue" -> {
                                        overdue.add(task)
                                    }
                                }
                            }
                            db.child("channels")
                                .child(channel_id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("2")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }
                })


            db.child("channels").child(channel_id)
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

                            if (task!!.task_assignee.id == userID) {
                                when (task.task_status) {
                                    "Ongoing" -> {
                                        ongoing.add(task)
                                    }
                                    "Completed" -> {
                                        completed.add(task)
                                    }
                                    "Completed Overdue" -> {
                                        completed_overdue.add(task)
                                    }
                                    "Overdue" -> {
                                        overdue.add(task)
                                    }
                                }
                            }

                            db.child("channels")
                                .child(channel_id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("3")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }
                })

            db.child("channels").child(channel_id)
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

                            if (task!!.task_assignee.id == userID) {
                                when (task.task_status) {
                                    "Ongoing" -> {
                                        ongoing.add(task)
                                    }
                                    "Completed" -> {
                                        completed.add(task)
                                    }
                                    "Completed Overdue" -> {
                                        completed_overdue.add(task)
                                    }
                                    "Overdue" -> {
                                        overdue.add(task)
                                    }
                                }
                            }
                            db.child("channels")
                                .child(channel_id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("4")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }
                })

            db.child("channels").child(channel_id)
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

                            if (task!!.task_assignee.id == userID) {
                                when (task.task_status) {
                                    "Ongoing" -> {
                                        ongoing.add(task)
                                    }
                                    "Completed" -> {
                                        completed.add(task)
                                    }
                                    "Completed Overdue" -> {
                                        completed_overdue.add(task)
                                    }
                                    "Overdue" -> {
                                        overdue.add(task)
                                    }
                                }
                            }
                            db.child("channels")
                                .child(channel_id)
                                .child("projects")
                                .child(project.proj_id).child("proj_task_stages").child("5")
                                .child("stage_tasks").orderByChild("task_due_date_id")
                                .removeEventListener(this)
                        }
                    }
                })
        }
    }

    fun check_pref(db: DatabaseReference, pa: Boolean, userID : String) {
        db.child("channels").child(currentChannel.id).child("surveys")
            .child(userID).child("survey_pref")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    allow_admin = snapshot.child("visible_to_admin").value.toString().toBoolean()
                    survey_freq = snapshot.child("frequency").value.toString()
                    survey_enabled = snapshot.child("enabled").value.toString().toBoolean()

                    db.child("channels").child(currentChannel.id).child("surveys")
                        .child(userID).child("survey_pref")
                        .removeEventListener(this)

                    if (!pa) {
                        display_details()
                    }
                }
            })
    }
}