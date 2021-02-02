package ie.wit.teamcom.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Department
import ie.wit.teamcom.models.Meeting
import kotlinx.android.synthetic.main.fragment_create_meeting.view.*
import kotlinx.android.synthetic.main.fragment_post_comments.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.*

class CreateMeetingFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View
    var dd = ""
    var mm = ""
    var yyyy = ""
    var h = ""
    var m = ""
    var new_meeting = Meeting()
    var depts = ArrayList<String>()
    var deptsList = ArrayList<Department>()
    var member_dept = Department()
    lateinit var loader : androidx.appcompat.app.AlertDialog


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
        root = inflater.inflate(R.layout.fragment_create_meeting, container, false)
        activity?.title = getString(R.string.title_create_meetings)

        loader = createLoader(requireActivity())

        showLoader(loader, "Loading . . . ", "Loading Page . . . ")
        getAllDepartments()

        val date = Calendar.getInstance()

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


        root.txtDandT.text = " $dd/$mm/$yyyy @ $h:$m"

        ///INITIALLY ONLINE = TRUE
        root.textViewLoc.isVisible = false
        root.editTxtLoc.isVisible = false
        root.textViewOtherPlatform.isVisible = false
        root.editTxtOtherPlatform.isVisible = false

        hideLoader(loader)

        root.checkBoxCheckOnline.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (!b) {
                root.textViewLoc.isVisible = true
                root.editTxtLoc.isVisible = true

                root.textViewPlatform.isVisible = false
                root.spinnerPlatform.isVisible = false
                root.textViewID.isVisible = false
                root.editTxtID.isVisible = false
                root.textViewPasscode.isVisible = false
                root.editTxtPasscode.isVisible = false
            } else {
                root.textViewLoc.isVisible = false
                root.editTxtLoc.isVisible = false

                root.textViewPlatform.isVisible = true
                root.spinnerPlatform.isVisible = true
                root.textViewID.isVisible = true
                root.editTxtID.isVisible = true
                root.textViewPasscode.isVisible = true
                root.editTxtPasscode.isVisible = true
            }
        }
        var res: Resources = resources

        val adapter1 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // Layout
            res.getStringArray(R.array.platforms)
        )
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        root.spinnerPlatform.adapter = adapter1
        root.spinnerPlatform.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                if (root.spinnerPlatform.selectedItem == "Other") {
                    root.textViewOtherPlatform.isVisible = true
                    root.editTxtOtherPlatform.isVisible = true
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
        val newCalender = Calendar.getInstance()

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

        root.btnCreateNewMeeting.setOnClickListener {
            showLoader(loader, "Loading . . . ", "Validating . . .")
            validateForm()
            hideLoader(loader)
            if (validateForm()) {
                createMeeting()
            }
        }

        return root
    }

    private fun validateForm(): Boolean {
        var valid = true

        val title = root.editTxtTitle.text.toString()
        if (TextUtils.isEmpty(title)) {
            root.editTxtTitle.error = "Title Required."
            valid = false
        } else {
            root.editTxtTitle.error = null
        }

        val desc = root.editTxtDesc.text.toString()
        if (TextUtils.isEmpty(desc)) {
            root.editTxtDesc.error = "Description Required."
            valid = false
        } else {
            root.editTxtDesc.error = null
        }

        if (root.checkBoxCheckOnline.isChecked) {
            if (root.spinnerPlatform.selectedItem.toString() !== "Zoom") {
                val meeting_id = root.editTxtID.text.toString()
                if (TextUtils.isEmpty(meeting_id)) {
                    root.editTxtID.error = "Manual Meeting ID Required for Non-Zoom Meetings."
                    valid = false
                } else {
                    root.editTxtID.error = null
                }

                val passcode = root.editTxtPasscode.text.toString()
                if (TextUtils.isEmpty(passcode)) {
                    root.editTxtPasscode.error =
                        "Manual Meeting Passcode Required for Non-Zoom Meetings."
                    valid = false
                } else {
                    root.editTxtPasscode.error = null
                }
            }
        } else {
            val location = root.editTxtLoc.text.toString()
            if (TextUtils.isEmpty(location)) {
                root.editTxtLoc.error = "Location Required for In-Person Meetings."
                valid = false
            } else {
                root.editTxtLoc.error = null
            }
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

    fun createMeeting() {
        showLoader(loader, "Loading . . . ", "Creating Meeting ${new_meeting.meeting_title} . . .")
        new_meeting.meeting_date_as_string = "$dd/$mm/$yyyy"
        new_meeting.meeting_time_as_string = "$h:$m"

        app.generate_date_reminder_id(dd, mm, yyyy, h, mm, "00")
        new_meeting.meeting_date_id = app.reminder_due_date_id
        new_meeting.meeting_creator = app.currentActiveMember

        new_meeting.meeting_title = root.editTxtTitle.text.toString()
        new_meeting.meeting_desc = root.editTxtDesc.text.toString()
        new_meeting.meeting_uuid = UUID.randomUUID().toString()

        if (!root.checkBoxCheckOnline.isChecked) {
            new_meeting.meeting_location = root.editTxtLoc.text.toString()
        } else if (root.checkBoxCheckOnline.isChecked && root.spinnerPlatform.selectedItem.toString() !== "Other") {
            new_meeting.meeting_platform = root.spinnerPlatform.selectedItem.toString()
            new_meeting.meeting_passcode = root.editTxtPasscode.text.toString()
            new_meeting.meeting_id = root.editTxtID.text.toString()
        } else if (root.checkBoxCheckOnline.isChecked && root.spinnerPlatform.selectedItem.toString() == "Other") {
            new_meeting.meeting_platform = root.editTxtOtherPlatform.text.toString()
        }

        new_meeting.online = root.checkBoxCheckOnline.isChecked

        if (root.spinnerDept.selectedItem !== "All") {
            var j = 0
            deptsList.forEach {
                if (root.spinnerDept.selectedItem == deptsList[j].dept_name) {
                    member_dept = deptsList[j]
                    new_meeting.participants = member_dept.dept_members
                } else {
                    j++
                }
            }
        } else {
            deptsList.forEach {
                it.dept_members.forEach { member_it ->
                    new_meeting.participants.add(member_it)
                }
            }
        }

        writeNewMeeting(new_meeting)
    }

    fun writeNewMeeting(meeting: Meeting) {
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel!!.id}/meetings/${new_meeting.meeting_uuid}"] =
                        meeting
                    app.database.updateChildren(childUpdates)


                    app.generateDateID("1")
                    val logUpdates = HashMap<String, Any>()
                    var new_log = ie.wit.teamcom.models.Log(
                        log_id = app.valid_from_cal,
                        log_triggerer = app.currentActiveMember,
                        log_date = app.dateAsString,
                        log_time = app.timeAsString,
                        log_content = "${app.currentActiveMember.firstName} ${app.currentActiveMember.surname} created a new Meeting [${meeting.meeting_title} on ${meeting.meeting_date_as_string} @ ${meeting.meeting_time_as_string}]."
                    )
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)

                    hideLoader(loader)
                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                }
            })
        navigateTo(MeetingsFragment.newInstance(currentChannel))
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun getAllDepartments() {
        app.database.child("channels").child(currentChannel!!.id).child("departments")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    depts.add("All")
                    children.forEach {
                        val dept = it.getValue<Department>(Department::class.java)

                        depts.add(dept!!.dept_name)
                        deptsList.add(dept)

                        app.database.child("channel").child(currentChannel!!.id)
                            .child("departments")
                            .removeEventListener(this)
                    }
                    val adapter2 = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item, // Layout
                        depts
                    )
                    adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                    root.spinnerDept.adapter = adapter2
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            CreateMeetingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }
}