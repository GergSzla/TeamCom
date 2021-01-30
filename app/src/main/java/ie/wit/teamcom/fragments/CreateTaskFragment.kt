package ie.wit.teamcom.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.*


class CreateTaskFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View
    var new_task = Task()
    var tasks_stages = ArrayList<String>()
    var tasks_stages_list = ArrayList<TaskStage>()
    var members = ArrayList<String>()
    var members_list = ArrayList<Member>()
    var dd = ""
    var mm = ""
    var yyyy = ""
    var h = ""
    var m = ""
    var selected_stage = TaskStage()
    var project = Project()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
            project = it.getParcelable("proj_key")!!
        }
        app.getAllMembers(currentChannel.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_create_task, container, false)
        activity?.title = getString(R.string.title_create_tasks)

        getStages()
        root.txtImportanceStatus.text = "(1) Very Low"
        new_task.task_importance = 1

        val date = Calendar.getInstance()
        val newCalender = Calendar.getInstance()

        dd = if (date.get(Calendar.DATE) < 10) {
            "0" + "${date.get(Calendar.DATE)}"
        } else{
            "${date.get(Calendar.DATE)}"
        }

        mm = if ((date.get(Calendar.MONTH)+1) < 10) {
            "0" + "${(date.get(Calendar.MONTH)+1)}"
        } else{
            "${(date.get(Calendar.MONTH)+1)}"
        }

        yyyy = "${date.get(Calendar.YEAR)}"
        h = if ((date.get(Calendar.HOUR_OF_DAY) +1 )< 10) {
            "0" + "${(date.get(Calendar.HOUR_OF_DAY) +1 )}"
        } else{
            "${(date.get(Calendar.HOUR_OF_DAY) +1 )}"
        }

        m = if (date.get(Calendar.MINUTE) < 10) {
            "0" + "${date.get(Calendar.MINUTE)}"
        } else{
            "${date.get(Calendar.MINUTE)}"
        }


        root.txtDandT.text = " $dd/$mm/$yyyy @ $h:$m"

        root.seekBarImportance.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // TODO Auto-generated method stub
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // TODO Auto-generated method stub
                }

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    when (progress) {
                        1 -> root.txtImportanceStatus.text = "(${progress}) Very Low"
                        2 -> root.txtImportanceStatus.text = "(${progress}) Low"
                        3 -> root.txtImportanceStatus.text = "(${progress}) Normal"
                        4 -> root.txtImportanceStatus.text = "(${progress}) High"
                        5 -> root.txtImportanceStatus.text = "(${progress}) Urgent"
                    }
                    new_task.task_importance = progress
                }
            })



        root.btnSelectDate.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth ->
                    val time = TimePickerDialog(

                        requireContext(),
                        { view, hourOfDay, minute ->
                            date[year, month, dayOfMonth, hourOfDay, minute] = 0
                            val tem = Calendar.getInstance()
                            Log.w("TIME", System.currentTimeMillis().toString() + "")

                            dd = if (dayOfMonth < 10) {
                                "0$dayOfMonth"
                            } else {
                                "$dayOfMonth"
                            }

                            mm = if ((month + 1) < 10) {
                                "0" + "${((month) + 1)}"
                            } else {
                                "${((month) + 1)}"
                            }

                            yyyy = "$year"
                            h = if (hourOfDay < 10) {
                                "0$hourOfDay"
                            } else {
                                "$hourOfDay"
                            }

                            m = if (minute < 10) {
                                "0$minute"
                            } else {
                                "$minute"
                            }

                            if (date.timeInMillis - tem.timeInMillis > 0) root.txtDandT.text =
                                "$dd/$mm/$yyyy @ $h:$m" else Toast.makeText(
                                requireContext(),
                                "Invalid time",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }, date[Calendar.HOUR_OF_DAY], date[Calendar.MINUTE], true
                    )
                    time.show()
                },
                newCalender.get(Calendar.YEAR),
                newCalender.get(Calendar.MONTH),
                newCalender.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis()
            dialog.show()

            dd = if (date.get(Calendar.DATE) < 10) {
                "0" + "${date.get(Calendar.DATE)}"
            } else{
                "${date.get(Calendar.DATE)}"
            }

            mm = if ((date.get(Calendar.MONTH)+1) < 10) {
                "0" + "${(date.get(Calendar.MONTH)+1)}"
            } else{
                "${(date.get(Calendar.MONTH)+1)}"
            }

            yyyy = "${date.get(Calendar.YEAR)}"
            h = if (date.get(Calendar.HOUR_OF_DAY) < 10) {
                "0" + "${date.get(Calendar.HOUR_OF_DAY)}"
            } else{
                "${date.get(Calendar.HOUR_OF_DAY)}"
            }

            m = if (date.get(Calendar.MINUTE) < 10) {
                "0" + "${date.get(Calendar.MINUTE)}"
            } else{
                "${date.get(Calendar.MINUTE)}"
            }


        }

        root.btnAddTask.setOnClickListener {
            createNewTask()
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel,app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel,app.currentActiveMember)
    }

    fun createNewTask(){
        new_task.id = UUID.randomUUID().toString()

        var i = 0
        members_list.forEach{
            if (root.spinnerAssignee.selectedItem == (members_list[i].firstName+" "+members_list[i].surname ) || root.spinnerAssignee.selectedItem == (members_list[i].firstName+" "+members_list[i].surname+" (Me)" )){
                new_task.task_assignee = members_list[i]
            } else {
                i++
            }
        }

        tasks_stages_list.forEach {
            if(root.spinnerStage.selectedItem == it.stage_name){
                selected_stage = it
            }
        }


        new_task.task_creator = app.currentActiveMember
        new_task.task_desc = root.txtTaskDesc.text.toString()
        new_task.task_msg = root.txtTaskName.text.toString()
        app.generate_date_reminder_id(dd, mm, yyyy, h, m, "00")

        new_task.task_due_date_id = app.reminder_due_date_id
        new_task.task_due_date_as_string = app.rem_dateAsString
        new_task.task_due_time_as_string = app.rem_timeAsString
        new_task.task_current_stage = selected_stage.stage_name
        new_task.task_current_stage_color = selected_stage.stage_color_code

        selected_stage.stage_tasks.add(new_task)
        writeNewTask()
    }

    fun writeNewTask(){

        //TODO: FOREACH -> ADD TASK
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/projects/${project.proj_id}/proj_task_stages/${selected_stage.stage_no - 1}/"] =
                        selected_stage
                    app.database.updateChildren(childUpdates)


                    /*app.generateDateID("1")
                    val logUpdates = HashMap<String, Any>()
                    var new_log = ie.wit.teamcom.models.Log(
                        log_id = app.valid_from_cal,
                        log_triggerer = app.currentActiveMember,
                        log_date = app.dateAsString,
                        log_time = app.timeAsString,
                        log_content = "${app.currentActiveMember.firstName} ${app.currentActiveMember.surname} created a new Meeting [${meeting.meeting_title} on ${meeting.meeting_date_as_string} @ ${meeting.meeting_time_as_string}]."
                    )
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)*/

                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                }
            })
    }

    fun getStages(){
        app.database.child("channels").child(currentChannel!!.id).child("projects").child(project.proj_id).child("proj_task_stages")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val task = it.getValue<TaskStage>(TaskStage::class.java)

                        if (task!!.stage_name != "") {
                            tasks_stages.add(task!!.stage_name)
                            tasks_stages_list.add(task)
                        }

                        app.database.child("channel").child(currentChannel!!.id)
                            .child("projects").child(project.proj_id).child("proj_task_stages")
                            .removeEventListener(this)
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item, // Layout
                        tasks_stages
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                    root.spinnerStage.adapter = adapter
                }
            })
        getMembers()
    }

    fun getMembers(){
        members.add("")
        app.database.child("channels").child(currentChannel!!.id).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val member = it.getValue<Member>(Member::class.java)

                        if (member!!.id == app.currentActiveMember.id) {
                            members.add(member!!.firstName + " " + member!!.surname + " (Me)")
                            members_list.add(member)
                        }

                        app.database.child("channel").child(currentChannel!!.id).child("members")
                            .removeEventListener(this)
                    }
                    val adapter1 = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item, // Layout
                        members
                    )
                    adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                    root.spinnerAssignee.adapter = adapter1
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
        fun newInstance(channel: Channel,project: Project) =
            CreateTaskFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                    putParcelable("proj_key", project)
                }
            }
    }
}