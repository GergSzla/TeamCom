package ie.wit.teamcom.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import kotlinx.android.synthetic.main.fragment_task_stages.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.*

class TaskStagesFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var task_stage_1 = TaskStage()
    var task_stage_2 = TaskStage()
    var task_stage_3 = TaskStage()
    var task_stage_4 = TaskStage()
    var task_stage_5 = TaskStage()
    var task_stage_6 = TaskStage()
    var project = Project()
    var dd = ""
    var mm = ""
    var yyyy = ""
    var h = ""
    var m = ""
    lateinit var loader: androidx.appcompat.app.AlertDialog
    var edit = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
            edit = it.getBoolean("bool")
            if (edit) {
                project = it.getParcelable("project_key")!!
            }
        }
        app.getAllMembers(currentChannel.id)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_task_stages, container, false)
        activity?.title = getString(R.string.taskStages)

        loader = createLoader(requireActivity())

        showLoader(loader, "Loading . . . ", "Loading Page . . . ")
        page_setup()
        create_def_stages()
        hideLoader(loader)


        if (edit) {
            root.txt_project_name.setText(project.proj_name)
            root.txt_project_desc.setText(project.proj_description)
            (project.proj_due_date + " @ " + project.proj_due_time).also {
                root.txtProjDateAndTime.text = it
            }
            root.txtChannelStage1.setText(project.proj_task_stages[0].stage_name)
            root.txtColorCodeStage1.setText(project.proj_task_stages[0].stage_color_code)

            root.txtChannelStage2.setText(project.proj_task_stages[1].stage_name)
            root.txtColorCodeStage2.setText(project.proj_task_stages[1].stage_color_code)

            root.checkboxStage3.isVisible = project.proj_task_stages[2].stage_active
            root.txtChannelStage3.setText(project.proj_task_stages[2].stage_name)
            root.txtColorCodeStage3.setText(project.proj_task_stages[2].stage_color_code)

            root.checkboxStage4.isVisible = project.proj_task_stages[3].stage_active
            root.txtChannelStage4.setText(project.proj_task_stages[3].stage_name)
            root.txtColorCodeStage4.setText(project.proj_task_stages[3].stage_color_code)

            root.checkboxStage5.isVisible = project.proj_task_stages[4].stage_active
            root.txtChannelStage5.setText(project.proj_task_stages[4].stage_name)
            root.txtColorCodeStage5.setText(project.proj_task_stages[4].stage_color_code)

            root.checkboxStage6.isVisible = project.proj_task_stages[5].stage_active
            root.txtChannelStage6.setText(project.proj_task_stages[5].stage_name)
            root.txtColorCodeStage6.setText(project.proj_task_stages[5].stage_color_code)


        } else {
            root.checkboxStage3.setOnCheckedChangeListener { _, b ->
                if (b) {
                    root.txtChannelStage3.isEnabled = true
                    root.txtColorCodeStage3.isEnabled = true
                    root.btnColorPicker3.isEnabled = true
                } else {
                    root.txtChannelStage3.isEnabled = false
                    root.txtColorCodeStage3.isEnabled = false
                    root.btnColorPicker3.isEnabled = false
                }
            }

            root.checkboxStage4.setOnCheckedChangeListener { _, b ->
                if (b) {
                    root.txtChannelStage4.isEnabled = true
                    root.txtColorCodeStage4.isEnabled = true
                    root.btnColorPicker4.isEnabled = true
                } else {
                    root.txtChannelStage4.isEnabled = false
                    root.txtColorCodeStage4.isEnabled = false
                    root.btnColorPicker4.isEnabled = false
                }
            }

            root.checkboxStage5.setOnCheckedChangeListener { _, b ->
                if (b) {
                    root.txtChannelStage5.isEnabled = true
                    root.txtColorCodeStage5.isEnabled = true
                    root.btnColorPicker5.isEnabled = true
                } else {
                    root.txtChannelStage5.isEnabled = false
                    root.txtColorCodeStage5.isEnabled = false
                    root.btnColorPicker5.isEnabled = false
                }
            }

            root.checkboxStage6.setOnCheckedChangeListener { _, b ->
                if (b) {
                    root.txtChannelStage6.isEnabled = true
                    root.txtColorCodeStage6.isEnabled = true
                    root.btnColorPicker6.isEnabled = true
                } else {
                    root.txtChannelStage6.isEnabled = false
                    root.txtColorCodeStage6.isEnabled = false
                    root.btnColorPicker6.isEnabled = false
                }
            }
        }


        root.btnColorPicker1.setOnClickListener {
            ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose Stage 1 Color")
                .initialColor(Color.parseColor("#a20606"))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener { selectedColor ->
                    root.txtColorCodeStage1.setText(
                        Integer.toHexString(selectedColor).replaceRange(0, 2, "")
                    )
                }
                .setPositiveButton(
                    "ok"
                ) { dialog, selectedColor, allColors -> (selectedColor) }
                .setNegativeButton(
                    "cancel"
                ) { dialog, which -> }
                .build()
                .show()
        }

        root.btnColorPicker2.setOnClickListener {
            ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose Stage 2 Color")
                .initialColor(Color.parseColor("#a20606"))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener { selectedColor ->
                    root.txtColorCodeStage2.setText(
                        Integer.toHexString(selectedColor).replaceRange(0, 2, "")
                    )
                }
                .setPositiveButton(
                    "ok"
                ) { dialog, selectedColor, allColors -> (selectedColor) }
                .setNegativeButton(
                    "cancel"
                ) { dialog, which -> }
                .build()
                .show()
        }

        root.btnColorPicker3.setOnClickListener {
            ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose Stage 3 Color")
                .initialColor(Color.parseColor("#a20606"))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener { selectedColor ->
                    root.txtColorCodeStage3.setText(
                        Integer.toHexString(selectedColor).replaceRange(0, 2, "")
                    )
                }
                .setPositiveButton(
                    "ok"
                ) { dialog, selectedColor, allColors -> (selectedColor) }
                .setNegativeButton(
                    "cancel"
                ) { dialog, which -> }
                .build()
                .show()
        }

        root.btnColorPicker4.setOnClickListener {
            ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose Stage 4 Color")
                .initialColor(Color.parseColor("#a20606"))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener { selectedColor ->
                    root.txtColorCodeStage4.setText(
                        Integer.toHexString(selectedColor).replaceRange(0, 2, "")
                    )
                }
                .setPositiveButton(
                    "ok"
                ) { dialog, selectedColor, allColors -> (selectedColor) }
                .setNegativeButton(
                    "cancel"
                ) { dialog, which -> }
                .build()
                .show()
        }

        root.btnColorPicker5.setOnClickListener {
            ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose Stage 5 Color")
                .initialColor(Color.parseColor("#a20606"))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener { selectedColor ->
                    root.txtColorCodeStage5.setText(
                        Integer.toHexString(selectedColor).replaceRange(0, 2, "")
                    )
                }
                .setPositiveButton(
                    "ok"
                ) { dialog, selectedColor, allColors -> (selectedColor) }
                .setNegativeButton(
                    "cancel"
                ) { dialog, which -> }
                .build()
                .show()
        }

        root.btnColorPicker6.setOnClickListener {
            ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose Stage 6 Color")
                .initialColor(Color.parseColor("#a20606"))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener { selectedColor ->
                    root.txtColorCodeStage6.setText(
                        Integer.toHexString(selectedColor).replaceRange(0, 2, "")
                    )
                }
                .setPositiveButton(
                    "ok"
                ) { dialog, selectedColor, allColors -> (selectedColor) }
                .setNegativeButton(
                    "cancel"
                ) { dialog, which -> }
                .build()
                .show()
        }

        root.btnSaveStages.setOnClickListener {
            showLoader(loader, "Loading . . . ", "Validating . . . ")
            validateForm()
            hideLoader(loader)
            if (validateForm()) {
                save_stages_changes()
            }
        }


        val date = Calendar.getInstance()
        val newCalender = Calendar.getInstance()

        root.btnProjectDateSelect.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth ->
                    val time = TimePickerDialog(

                        requireContext(),
                        { view, hourOfDay, minute ->
                            date[year, month, dayOfMonth, hourOfDay, minute] = 0
                            val tem = Calendar.getInstance()
                            android.util.Log.w("TIME", System.currentTimeMillis().toString() + "")

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
                                root.txtProjDateAndTime.text = it
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

        return root
    }

    private fun validateForm(): Boolean {
        var valid = true

        val proj_name = root.txt_project_name.text.toString()
        if (TextUtils.isEmpty(proj_name)) {
            root.txt_project_name.error = "Project Name Required."
            valid = false
        } else {
            root.txt_project_name.error = null
        }

        val proj_desc = root.txt_project_desc.text.toString()
        if (TextUtils.isEmpty(proj_desc)) {
            root.txt_project_desc.error = "Project Description Required."
            valid = false
        } else {
            root.txt_project_desc.error = null
        }

        if (root.txtProjDateAndTime.text.toString() == "No Due Date Selected") {
            val proj_date = root.txtProjDateAndTime.text.toString()
            if (TextUtils.isEmpty(proj_date)) {
                root.txtProjDateAndTime.error = "Project Due Date Required."
                valid = false
            } else {
                root.txtProjDateAndTime.error = null
            }
        }

        val stage_1 = root.txtChannelStage1.text.toString()
        if (TextUtils.isEmpty(stage_1)) {
            root.txtChannelStage1.error = "Stage One Name Required."
            valid = false
        } else {
            root.txtChannelStage1.error = null
        }

        val stage_1_color = root.txtColorCodeStage1.text.toString()
        if (TextUtils.isEmpty(stage_1_color)) {
            root.txtColorCodeStage1.error = "Stage One Color Required."
            valid = false
        } else {
            root.txtColorCodeStage1.error = null
        }

        val stage_2 = root.txtChannelStage2.text.toString()
        if (TextUtils.isEmpty(stage_2)) {
            root.txtChannelStage2.error = "Stage Two Name Required."
            valid = false
        } else {
            root.txtChannelStage2.error = null
        }

        val stage_2_color = root.txtColorCodeStage2.text.toString()
        if (TextUtils.isEmpty(stage_2_color)) {
            root.txtColorCodeStage2.error = "Stage Two Color Required."
            valid = false
        } else {
            root.txtColorCodeStage2.error = null
        }

        //Opt Stage 3
        if (root.checkboxStage3.isChecked) {
            val stage_3 = root.txtChannelStage3.text.toString()
            if (TextUtils.isEmpty(stage_3)) {
                root.txtChannelStage3.error = "Stage Three Name Required."
                valid = false
            } else {
                root.txtChannelStage3.error = null
            }

            val stage_3_color = root.txtColorCodeStage3.text.toString()
            if (TextUtils.isEmpty(stage_3_color)) {
                root.txtColorCodeStage3.error = "Stage Three Color Required."
                valid = false
            } else {
                root.txtColorCodeStage3.error = null
            }
        } else {
            root.txtColorCodeStage3.error = null
            root.txtChannelStage3.error = null
        }

        //Opt Stage 4
        if (root.checkboxStage4.isChecked) {
            val stage_4 = root.txtChannelStage4.text.toString()
            if (TextUtils.isEmpty(stage_4)) {
                root.txtChannelStage4.error = "Stage Four Name Required."
                valid = false
            } else {
                root.txtChannelStage4.error = null
            }

            val stage_4_color = root.txtColorCodeStage4.text.toString()
            if (TextUtils.isEmpty(stage_4_color)) {
                root.txtColorCodeStage4.error = "Stage Four Color Required."
                valid = false
            } else {
                root.txtColorCodeStage4.error = null
            }
        } else {
            root.txtChannelStage4.error = null
            root.txtColorCodeStage4.error = null
        }

        //Opt Stage 5
        if (root.checkboxStage5.isChecked) {
            val stage_5 = root.txtChannelStage5.text.toString()
            if (TextUtils.isEmpty(stage_5)) {
                root.txtChannelStage5.error = "Stage Five Name Required."
                valid = false
            } else {
                root.txtChannelStage5.error = null
            }

            val stage_5_color = root.txtColorCodeStage5.text.toString()
            if (TextUtils.isEmpty(stage_5_color)) {
                root.txtColorCodeStage5.error = "Stage Five Color Required."
                valid = false
            } else {
                root.txtColorCodeStage5.error = null
            }
        } else {
            root.txtColorCodeStage5.error = null
            root.txtChannelStage5.error = null
        }

        //Opt Stage 6
        if (root.checkboxStage6.isChecked) {
            val stage_6 = root.txtChannelStage6.text.toString()
            if (TextUtils.isEmpty(stage_6)) {
                root.txtChannelStage6.error = "Stage Six Name Required."
                valid = false
            } else {
                root.txtChannelStage6.error = null
            }

            val stage_6_color = root.txtColorCodeStage6.text.toString()
            if (TextUtils.isEmpty(stage_6_color)) {
                root.txtColorCodeStage6.error = "Stage Six Color Required."
                valid = false
            } else {
                root.txtColorCodeStage6.error = null
            }
        } else {
            root.txtColorCodeStage6.error = null
            root.txtChannelStage6.error = null
        }

        return valid
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(ie.wit.teamcom.fragments.currentChannel, app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(ie.wit.teamcom.fragments.currentChannel, app.currentActiveMember)
    }

    private fun create_def_stages() {
        root.txtChannelStage1.setText(R.string.str_status_def)
        root.txtChannelStage2.setText(R.string.str_status_def_2)
        root.txtChannelStage3.setText(R.string.str_status_def_3)

        root.txtColorCodeStage1.setText(R.string.str_color_def)
        root.txtColorCodeStage2.setText(R.string.str_color_def_2)
        root.txtColorCodeStage3.setText(R.string.str_color_def_3)
    }

    private fun save_stages_changes() {
        showLoader(loader, "Loading . . . ", "Saving Task Stages . . . ")
        //stage 1
        if (!edit) {
            task_stage_1.id = UUID.randomUUID().toString()
        }
        task_stage_1.stage_no = 1
        task_stage_1.stage_name = root.txtChannelStage1.text.toString()
        task_stage_1.stage_active = true
        task_stage_1.stage_tasks = ArrayList<Task>()
        if (root.txtColorCodeStage1.text.toString().take(1) != "#") {
            task_stage_1.stage_color_code = "#" + root.txtColorCodeStage1.text.toString()
        } else {
            task_stage_1.stage_color_code = root.txtColorCodeStage1.text.toString()
        }

        //stage 2
        if (!edit) {
            task_stage_2.id = UUID.randomUUID().toString()
        }
        task_stage_2.stage_no = 2

        task_stage_2.stage_name = root.txtChannelStage2.text.toString()
        task_stage_2.stage_active = true
        if (root.txtColorCodeStage2.text.toString().take(1) != "#") {
            task_stage_2.stage_color_code = "#" + root.txtColorCodeStage2.text.toString()
        } else {
            task_stage_2.stage_color_code = root.txtColorCodeStage2.text.toString()
        }

        //stage 3
        if (!edit) {
            task_stage_3.id = UUID.randomUUID().toString()
        }

        task_stage_2.stage_no = 3


        task_stage_3.stage_name = root.txtChannelStage3.text.toString()
        task_stage_3.stage_active = root.checkboxStage3.isChecked
        if (root.txtColorCodeStage3.text.toString().take(1) != "#") {
            task_stage_3.stage_color_code = "#" + root.txtColorCodeStage3.text.toString()
        } else {
            task_stage_3.stage_color_code = root.txtColorCodeStage3.text.toString()
        }

        //stage 4
        if (!edit) {
            task_stage_4.id = UUID.randomUUID().toString()
        }

        task_stage_4.stage_no = 4

        task_stage_4.stage_name = root.txtChannelStage4.text.toString()
        task_stage_4.stage_active = root.checkboxStage4.isChecked
        if (root.txtColorCodeStage4.text.toString().take(1) != "#") {
            task_stage_4.stage_color_code = "#" + root.txtColorCodeStage4.text.toString()
        } else {
            task_stage_4.stage_color_code = root.txtColorCodeStage4.text.toString()
        }

        //stage 5
        if (!edit) {
            task_stage_5.id = UUID.randomUUID().toString()
        }
        task_stage_5.stage_no = 5
        task_stage_5.stage_name = root.txtChannelStage5.text.toString()
        task_stage_5.stage_active = root.checkboxStage5.isChecked
        if (root.txtColorCodeStage5.text.toString().take(1) != "#") {
            task_stage_5.stage_color_code = "#" + root.txtColorCodeStage5.text.toString()
        } else {
            task_stage_5.stage_color_code = root.txtColorCodeStage5.text.toString()
        }

        //stage 6
        if (!edit) {
            task_stage_6.id = UUID.randomUUID().toString()
        }
        task_stage_6.stage_no = 6
        task_stage_6.stage_name = root.txtChannelStage6.text.toString()
        task_stage_6.stage_active = root.checkboxStage6.isChecked
        if (root.txtColorCodeStage6.text.toString().take(1) != "#") {
            task_stage_6.stage_color_code = "#" + root.txtColorCodeStage6.text.toString()
        } else {
            task_stage_6.stage_color_code = root.txtColorCodeStage6.text.toString()
        }

        val updated_stages = arrayListOf<TaskStage>(
            task_stage_1,
            task_stage_2,
            task_stage_3,
            task_stage_4,
            task_stage_5,
            task_stage_6
        )

        project.proj_name = root.txt_project_name.text.toString()
        project.proj_description = root.txt_project_desc.text.toString()
        if (!edit) {
            project.proj_id = UUID.randomUUID().toString()
        }

        app.generate_date_reminder_id(dd, mm, yyyy, h, mm, "00")
        project.proj_due_date_id = app.reminder_due_date_id
        project.proj_due_date = "$dd/$mm/$yyyy"
        project.proj_due_time = "$h:$m"
        project.proj_task_stages = updated_stages
        hideLoader(loader)
        create_project()
    }


    private fun create_project() {
        showLoader(loader, "Loading . . . ", "Creating Project ${project.proj_name} . . . ")
        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/projects/${project.proj_id}"] =
                        project
                    app.database.updateChildren(childUpdates)


                    app.generateDateID("1")
                    val logUpdates = HashMap<String, Any>()
                    var new_log = Log(
                        log_id = app.valid_from_cal,
                        log_triggerer = app.currentActiveMember,
                        log_date = app.dateAsString,
                        log_time = app.timeAsString,
                        log_content = "New Project, ${project.proj_name} has been created."
                    )
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)

                    hideLoader(loader)
                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                    navigateTo(ProjectListFragment.newInstance(currentChannel))
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

    private fun page_setup() {
        root.checkboxStage3.isChecked = false
        root.checkboxStage4.isChecked = false
        root.checkboxStage5.isChecked = false
        root.checkboxStage6.isChecked = false

        root.txtChannelStage3.isEnabled = false
        root.txtColorCodeStage3.isEnabled = false
        root.btnColorPicker3.isEnabled = false

        root.txtChannelStage4.isEnabled = false
        root.txtColorCodeStage4.isEnabled = false
        root.btnColorPicker4.isEnabled = false


        root.txtChannelStage5.isEnabled = false
        root.txtColorCodeStage5.isEnabled = false
        root.btnColorPicker5.isEnabled = false

        root.txtChannelStage6.isEnabled = false
        root.txtColorCodeStage6.isEnabled = false
        root.btnColorPicker6.isEnabled = false

    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel, project: Project, edit: Boolean) =
            TaskStagesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                    putParcelable("project_key", project)
                    putBoolean("bool", edit)
                }
            }
    }
}