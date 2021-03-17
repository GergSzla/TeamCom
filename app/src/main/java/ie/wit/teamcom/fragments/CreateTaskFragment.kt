package ie.wit.teamcom.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
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
    lateinit var loader: androidx.appcompat.app.AlertDialog


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

        loader = createLoader(requireActivity())

        showLoader(loader, "Loading . . .", "Loading Page . . .")
        getStages()
        "(1) Very Low".also { root.txtImportanceStatus.text = it }
        new_task.task_importance = 1

        val date = Calendar.getInstance()
        val newCalender = Calendar.getInstance()

        dd = if (date.get(Calendar.DATE) < 10) {
            "0" + "${date.get(Calendar.DATE)}"
        } else {
            "${date.get(Calendar.DATE)}"
        }

        mm = if ((date.get(Calendar.MONTH) + 1) < 10) {
            "0" + "${(date.get(Calendar.MONTH) + 1)}"
        } else {
            "${(date.get(Calendar.MONTH) + 1)}"
        }

        yyyy = "${date.get(Calendar.YEAR)}"
        h = if ((date.get(Calendar.HOUR_OF_DAY) + 1) < 10) {
            "0" + "${(date.get(Calendar.HOUR_OF_DAY) + 1)}"
        } else {
            "${(date.get(Calendar.HOUR_OF_DAY) + 1)}"
        }

        m = if (date.get(Calendar.MINUTE) < 10) {
            "0" + "${date.get(Calendar.MINUTE)}"
        } else {
            "${date.get(Calendar.MINUTE)}"
        }


        " $dd/$mm/$yyyy @ $h:$m".also { root.txtDandT.text = it }

        hideLoader(loader)

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
                        1 -> "(${progress}) Very Low".also { root.txtImportanceStatus.text = it }
                        2 -> "(${progress}) Low".also { root.txtImportanceStatus.text = it }
                        3 -> "(${progress}) Normal".also { root.txtImportanceStatus.text = it }
                        4 -> "(${progress}) High".also { root.txtImportanceStatus.text = it }
                        5 -> "(${progress}) Urgent".also { root.txtImportanceStatus.text = it }
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

                            if (date.timeInMillis - tem.timeInMillis > 0) "$dd/$mm/$yyyy @ $h:$m".also {
                                root.txtDandT.text = it
                            } else Toast.makeText(
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
            } else {
                "${date.get(Calendar.DATE)}"
            }

            mm = if ((date.get(Calendar.MONTH) + 1) < 10) {
                "0" + "${(date.get(Calendar.MONTH) + 1)}"
            } else {
                "${(date.get(Calendar.MONTH) + 1)}"
            }

            yyyy = "${date.get(Calendar.YEAR)}"
            h = if (date.get(Calendar.HOUR_OF_DAY) < 10) {
                "0" + "${date.get(Calendar.HOUR_OF_DAY)}"
            } else {
                "${date.get(Calendar.HOUR_OF_DAY)}"
            }

            m = if (date.get(Calendar.MINUTE) < 10) {
                "0" + "${date.get(Calendar.MINUTE)}"
            } else {
                "${date.get(Calendar.MINUTE)}"
            }


        }

        root.btnAddTask.setOnClickListener {
            showLoader(loader, "Loading . . .", "Validating . . .")
            validateForm()
            hideLoader(loader)
            if (validateForm()) {
                createNewTask()
            }
        }

        return root
    }

    private fun validateForm(): Boolean {
        var valid = true

        val task_name = root.txtTaskName.text.toString()
        if (TextUtils.isEmpty(task_name)) {
            root.txtTaskName.error = "Task Name Required."
            valid = false
        } else {
            root.txtTaskName.error = null
        }


        return valid
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    fun createNewTask() {
        showLoader(loader, "Loading . . .", "Creating New Task ${new_task.task_msg} . . .")
        new_task.id = UUID.randomUUID().toString()

        var i = 0
        members_list.forEach {
            if (root.spinnerAssignee.selectedItem == (members_list[i].firstName + " " + members_list[i].surname) || root.spinnerAssignee.selectedItem == (members_list[i].firstName + " " + members_list[i].surname + " (Me)")) {
                new_task.task_assignee = members_list[i]
            } else {
                i++
            }
        }

        tasks_stages_list.forEach {
            if (root.spinnerStage.selectedItem == it.stage_name) {
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

        if (new_task.task_current_stage == "Completed") {
            app.generateDateID("1")
            new_task.task_completed_date_id = app.valid_from_cal
        }

        selected_stage.stage_tasks.add(new_task)
        writeNewTask()
    }

    fun writeNewTask() {

        //TODO: FOREACH -> ADD TASK
        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/projects/${project.proj_id}/proj_task_stages/${selected_stage.stage_no - 1}/"] =
                        selected_stage
                    app.database.updateChildren(childUpdates)


                    hideLoader(loader)
                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                }
            })
    }

    fun getStages() {
        app.database.child("channels").child(currentChannel.id).child("projects")
            .child(project.proj_id).child("proj_task_stages")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val task = it.getValue<TaskStage>(TaskStage::class.java)

                        if (task!!.stage_name != "" && task.stage_active) {
                            tasks_stages.add(task.stage_name)
                            tasks_stages_list.add(task)
                        }

                        app.database.child("channel").child(currentChannel.id)
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

    fun getMembers() {
        members.add("")
        app.database.child("channels").child(currentChannel.id).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val member = it.getValue<Member>(Member::class.java)

                        if (member!!.id == app.currentActiveMember.id) {
                            members.add(member.firstName + " " + member.surname + " (Me)")
                            members_list.add(member)
                        }

                        app.database.child("channel").child(currentChannel.id).child("members")
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

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel, project: Project) =
            CreateTaskFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                    putParcelable("proj_key", project)
                }
            }
    }
}