package ie.wit.teamcom.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Log
import ie.wit.teamcom.models.TaskStage
import kotlinx.android.synthetic.main.fragment_task_stages.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList
import java.util.HashMap

class TaskStagesFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var task_stage_list = ArrayList<TaskStage>()
    var task_stage_1 = TaskStage()
    var task_stage_2 = TaskStage()
    var task_stage_3 = TaskStage()
    var task_stage_4 = TaskStage()
    var task_stage_5 = TaskStage()
    var task_stage_6 = TaskStage()


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
        root = inflater.inflate(R.layout.fragment_task_stages, container, false)
        activity?.title = getString(R.string.taskStages)

        page_setup()

        getStages()

        root.checkboxStage3.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                root.txtChannelStage3.isEnabled = true
                root.txtColorCodeStage3.isEnabled = true
                root.btnColorPicker3.isEnabled = true
            } else {
                root.txtChannelStage3.isEnabled = false
                root.txtColorCodeStage3.isEnabled = false
                root.btnColorPicker3.isEnabled = false
            }
        }

        root.checkboxStage4.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                root.txtChannelStage4.isEnabled = true
                root.txtColorCodeStage4.isEnabled = true
                root.btnColorPicker4.isEnabled = true
            } else {
                root.txtChannelStage4.isEnabled = false
                root.txtColorCodeStage4.isEnabled = false
                root.btnColorPicker4.isEnabled = false
            }
        }

        root.checkboxStage5.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                root.txtChannelStage5.isEnabled = true
                root.txtColorCodeStage5.isEnabled = true
                root.btnColorPicker5.isEnabled = true
            } else {
                root.txtChannelStage5.isEnabled = false
                root.txtColorCodeStage5.isEnabled = false
                root.btnColorPicker5.isEnabled = false
            }
        }

        root.checkboxStage6.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                root.txtChannelStage6.isEnabled = true
                root.txtColorCodeStage6.isEnabled = true
                root.btnColorPicker6.isEnabled = true
            } else {
                root.txtChannelStage6.isEnabled = false
                root.txtColorCodeStage6.isEnabled = false
                root.btnColorPicker6.isEnabled = false
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
                root.txtColorCodeStage1.setText(Integer.toHexString(selectedColor).replaceRange(0,2, ""))
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
                    root.txtColorCodeStage2.setText(Integer.toHexString(selectedColor).replaceRange(0,2, ""))
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
                    root.txtColorCodeStage3.setText(Integer.toHexString(selectedColor).replaceRange(0,2, ""))
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
                    root.txtColorCodeStage4.setText(Integer.toHexString(selectedColor).replaceRange(0,2, ""))
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
                    root.txtColorCodeStage5.setText(Integer.toHexString(selectedColor).replaceRange(0,2, ""))
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
                root.txtColorCodeStage6.setText(Integer.toHexString(selectedColor).replaceRange(0,2, ""))
            }
            .setPositiveButton(
                "ok"
            ) { dialog, selectedColor, allColors -> (selectedColor) }
            .setNegativeButton(
                "cancel"
            ) { dialog, which -> }
            .build()
            .show()  }

        root.btnSaveStages.setOnClickListener {
            save_stages_changes()
        }

        return root
    }

    fun save_stages_changes(){

        //stage 1
        task_stage_1.stage_name = root.txtChannelStage1.text.toString()
        if(root.txtColorCodeStage1.text.toString().take(1) != "#"){
            task_stage_1.stage_color_code = "#"+root.txtColorCodeStage1.text.toString()
        } else {
            task_stage_1.stage_color_code = root.txtColorCodeStage1.text.toString()
        }

        //stage 2
        task_stage_2.stage_name = root.txtChannelStage2.text.toString()
        if(root.txtColorCodeStage2.text.toString().take(1) != "#"){
            task_stage_2.stage_color_code = "#"+root.txtColorCodeStage2.text.toString()
        } else {
            task_stage_2.stage_color_code = root.txtColorCodeStage2.text.toString()
        }

        //stage 3
        task_stage_3.stage_name = root.txtChannelStage3.text.toString()
        task_stage_3.stage_active = root.checkboxStage3.isChecked
        if(root.txtColorCodeStage3.text.toString().take(1) != "#"){
            task_stage_3.stage_color_code = "#"+root.txtColorCodeStage3.text.toString()
        } else {
            task_stage_3.stage_color_code = root.txtColorCodeStage3.text.toString()
        }

        //stage 4
        task_stage_4.stage_name = root.txtChannelStage4.text.toString()
        task_stage_4.stage_active = root.checkboxStage4.isChecked
        if(root.txtColorCodeStage4.text.toString().take(1) != "#"){
            task_stage_4.stage_color_code = "#"+root.txtColorCodeStage4.text.toString()
        } else {
            task_stage_4.stage_color_code = root.txtColorCodeStage4.text.toString()
        }

        //stage 5
        task_stage_5.stage_name = root.txtChannelStage5.text.toString()
        task_stage_5.stage_active = root.checkboxStage5.isChecked
        if(root.txtColorCodeStage5.text.toString().take(1) != "#"){
            task_stage_5.stage_color_code = "#"+root.txtColorCodeStage5.text.toString()
        } else {
            task_stage_5.stage_color_code = root.txtColorCodeStage5.text.toString()
        }

        //stage 6
        task_stage_6.stage_name = root.txtChannelStage6.text.toString()
        task_stage_6.stage_active = root.checkboxStage6.isChecked
        if(root.txtColorCodeStage6.text.toString().take(1) != "#"){
            task_stage_6.stage_color_code = "#"+root.txtColorCodeStage6.text.toString()
        } else {
            task_stage_6.stage_color_code = root.txtColorCodeStage6.text.toString()
        }

        val updated_stages = arrayListOf<TaskStage>(task_stage_1,task_stage_2,task_stage_3,task_stage_4,task_stage_5,task_stage_6)

        write_stages_changes(updated_stages)
    }

    fun write_stages_changes(stages: ArrayList<TaskStage>){
        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/task_stages/"] = stages
                    app.database.updateChildren(childUpdates)


                    app.generateDateID("1")
                    val logUpdates = HashMap<String, Any>()
                    var new_log = Log(log_id = app.valid_from_cal, log_triggerer = app.currentActiveMember, log_date = app.dateAsString, log_time = app.timeAsString, log_content = "Task Stages Updated.")
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)

                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                    navigateTo(NewsFeedFragment.newInstance(currentChannel))
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

    fun page_setup(){
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

    fun getStages(){
        task_stage_list = ArrayList<TaskStage>()
        app.database.child("channels").child(currentChannel!!.id).child("task_stages")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Stages error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val stage = it.getValue<TaskStage>(TaskStage::class.java)
                        task_stage_list.add(stage!!)

                        if(stage.stage_no == 1){
                            root.txtChannelStage1.setText(stage.stage_name)

                            if(stage.stage_color_code.take(1) != "#"){
                                root.txtColorCodeStage1.setText("#"+stage.stage_color_code)
                            } else {
                                root.txtColorCodeStage1.setText(stage.stage_color_code)
                            }

                            task_stage_1.id = stage.id
                            task_stage_1.stage_no = stage.stage_no
                        }
                        if(stage.stage_no == 2){
                            root.txtChannelStage2.setText(stage.stage_name)

                            if(stage.stage_color_code.take(1) != "#"){
                                root.txtColorCodeStage2.setText("#"+stage.stage_color_code)
                            } else {
                                root.txtColorCodeStage2.setText(stage.stage_color_code)
                            }

                            task_stage_2.id = stage.id
                            task_stage_2.stage_no = stage.stage_no
                        }
                        if(stage.stage_no == 3){
                            root.txtChannelStage3.setText(stage.stage_name)

                            if(stage.stage_color_code.take(1) != "#"){
                                root.txtColorCodeStage3.setText("#"+stage.stage_color_code)
                            } else {
                                root.txtColorCodeStage3.setText(stage.stage_color_code)
                            }

                            root.checkboxStage3.isChecked = stage.stage_active
                            task_stage_3.id = stage.id
                            task_stage_3.stage_no = stage.stage_no
                        }
                        if(stage.stage_no == 4){
                            root.txtChannelStage4.setText(stage.stage_name)

                            if(stage.stage_color_code.take(1) != "#"){
                                root.txtColorCodeStage4.setText("#"+stage.stage_color_code)
                            } else {
                                root.txtColorCodeStage4.setText(stage.stage_color_code)
                            }

                            root.checkboxStage4.isChecked = stage.stage_active
                            task_stage_4.id = stage.id
                            task_stage_4.stage_no = stage.stage_no
                        }
                        if(stage.stage_no == 5){
                            root.txtChannelStage5.setText(stage.stage_name)

                            if(stage.stage_color_code.take(1) != "#"){
                                root.txtColorCodeStage5.setText("#"+stage.stage_color_code)
                            } else {
                                root.txtColorCodeStage5.setText(stage.stage_color_code)
                            }

                            root.checkboxStage5.isChecked = stage.stage_active
                            task_stage_5.id = stage.id
                            task_stage_5.stage_no = stage.stage_no
                        }
                        if(stage.stage_no == 6){
                            root.txtChannelStage6.setText(stage.stage_name)

                            if(stage.stage_color_code.take(1) != "#"){
                                root.txtColorCodeStage6.setText("#"+stage.stage_color_code)
                            } else {
                                root.txtColorCodeStage6.setText(stage.stage_color_code)
                            }

                            root.checkboxStage6.isChecked = stage.stage_active
                            task_stage_6.id = stage.id
                            task_stage_6.stage_no = stage.stage_no
                        }

                        app.database.child("channels").child(currentChannel!!.id).child("task_stages")
                            .removeEventListener(this)
                    }
                }
            })
        /*task_stage_list.forEach {
            if(it.stage_no == 1){
                root.txtChannelStage1.setText(it.stage_name)

                if(it.stage_color_code.take(1) != "#"){
                    root.txtColorCodeStage1.setText("#"+it.stage_color_code)
                } else {
                    root.txtColorCodeStage1.setText(it.stage_color_code)
                }

                task_stage_1.id = it.id
                task_stage_1.stage_no = it.stage_no
            }
            if(it.stage_no == 2){
                root.txtChannelStage2.setText(it.stage_name)

                if(it.stage_color_code.take(1) != "#"){
                    root.txtColorCodeStage2.setText("#"+it.stage_color_code)
                } else {
                    root.txtColorCodeStage2.setText(it.stage_color_code)
                }

                task_stage_2.id = it.id
                task_stage_2.stage_no = it.stage_no
            }
            if(it.stage_no == 3){
                root.txtChannelStage3.setText(it.stage_name)

                if(it.stage_color_code.take(1) != "#"){
                    root.txtColorCodeStage3.setText("#"+it.stage_color_code)
                } else {
                    root.txtColorCodeStage3.setText(it.stage_color_code)
                }

                root.checkboxStage3.isChecked = it.stage_active
                task_stage_3.id = it.id
                task_stage_3.stage_no = it.stage_no
            }
            if(it.stage_no == 4){
                root.txtChannelStage4.setText(it.stage_name)

                if(it.stage_color_code.take(1) != "#"){
                    root.txtColorCodeStage4.setText("#"+it.stage_color_code)
                } else {
                    root.txtColorCodeStage4.setText(it.stage_color_code)
                }

                root.checkboxStage4.isChecked = it.stage_active
                task_stage_4.id = it.id
                task_stage_4.stage_no = it.stage_no
            }
            if(it.stage_no == 5){
                root.txtChannelStage5.setText(it.stage_name)

                if(it.stage_color_code.take(1) != "#"){
                    root.txtColorCodeStage5.setText("#"+it.stage_color_code)
                } else {
                    root.txtColorCodeStage5.setText(it.stage_color_code)
                }

                root.checkboxStage5.isChecked = it.stage_active
                task_stage_5.id = it.id
                task_stage_5.stage_no = it.stage_no
            }
            if(it.stage_no == 6){
                root.txtChannelStage6.setText(it.stage_name)

                if(it.stage_color_code.take(1) != "#"){
                    root.txtColorCodeStage6.setText("#"+it.stage_color_code)
                } else {
                    root.txtColorCodeStage6.setText(it.stage_color_code)
                }

                root.checkboxStage6.isChecked = it.stage_active
                task_stage_6.id = it.id
                task_stage_6.stage_no = it.stage_no
            }
        }*/
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            TaskStagesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }
}