package ie.wit.teamcom.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.MeetingMembersAdapter
import ie.wit.teamcom.adapters.MeetingMembersListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import ie.wit.utils.SwipeToRemoveSelectedMemberCallback
import ie.wit.utils.SwipeToSelectMemberCallback
import kotlinx.android.synthetic.main.fragment_create_meeting.view.*
import kotlinx.android.synthetic.main.fragment_members.view.*
import kotlinx.android.synthetic.main.fragment_post_comments.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.time.LocalDateTime
import java.util.*


class CreateMeetingFragment : Fragment(), AnkoLogger, MeetingMembersListener {

    lateinit var app: MainApp
    lateinit var root: View
    var dd = ""
    var mm = ""
    var yyyy = ""
    var h = ""
    var m = ""
    var new_meeting = Meeting()
    var channel_members = ArrayList<Member>()
    var selected_members = ArrayList<Member>()
    lateinit var loader: androidx.appcompat.app.AlertDialog
    var req_meeting = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
            req_meeting = it.getBoolean("meeting_req")!!
        }
        app.getAllMembers(currentChannel.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_create_meeting, container, false)
        activity?.title = getString(R.string.title_create_meetings)

        root.selectMemsRecyclerView.layoutManager = LinearLayoutManager(activity)
        root.selectedMemsRecyclerView.layoutManager = LinearLayoutManager(activity)

        loader = createLoader(requireActivity())

        showLoader(loader, "Loading . . . ", "Loading Page . . . ")


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
        h = if ((date.get(Calendar.HOUR_OF_DAY)) < 10) {
            "0" + "${(date.get(Calendar.HOUR_OF_DAY))}"
        } else {
            "${(date.get(Calendar.HOUR_OF_DAY))}"
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
                root.lin_external.isVisible = false
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
                root.lin_external.isVisible = true
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
                    root.lin_external.isVisible = false
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
            createWarningDialog()
        }
        root.btnRequestMeeting.setOnClickListener {
            createMeeting(true)
        }

        if (req_meeting) {
            root.btnCreateNewMeeting.isVisible = false
            root.btnRequestMeeting.isVisible = true
        } else {
            root.btnCreateNewMeeting.isVisible = true
            root.btnRequestMeeting.isVisible = false
        }
        root.btnRefr.setOnClickListener {
            root.swiperefreshCreateMeeting_1.isRefreshing = true
            root.swiperefreshCreateMeeting_2.isRefreshing = true
            channelMembersToRecycler()
        }

        root.btnCreateExt.setOnClickListener {
            if (root.spinnerPlatform.selectedItem == "Zoom") {
                val callIntent: Intent = Uri.parse("https://zoom.us/signin").let { uri ->
                    Intent(Intent.ACTION_VIEW, uri)
                }
                startActivity(callIntent)
            } else if (root.spinnerPlatform.selectedItem == "GoToMeeting") {
                val callIntent: Intent = Uri.parse("https://global.gotomeeting.com/").let { uri ->
                    Intent(Intent.ACTION_VIEW, uri)
                }
                startActivity(callIntent)
            } else if (root.spinnerPlatform.selectedItem == "Google Hangouts") {
                val callIntent: Intent = Uri.parse("https://hangouts.google.com/").let { uri ->
                    Intent(Intent.ACTION_VIEW, uri)
                }
                startActivity(callIntent)
            } else if (root.spinnerPlatform.selectedItem == "Skype") {
                val callIntent: Intent = Uri.parse("https://www.skype.com/en/").let { uri ->
                    Intent(Intent.ACTION_VIEW, uri)
                }
                startActivity(callIntent)
            } else if (root.spinnerPlatform.selectedItem == "Microsoft Teams") {
                val callIntent: Intent =
                    Uri.parse("https://www.microsoft.com/en-ie/microsoft-teams/group-chat-software")
                        .let { uri ->
                            Intent(Intent.ACTION_VIEW, uri)
                        }
                startActivity(callIntent)
            } else if (root.spinnerPlatform.selectedItem == "Join.me") {
                val callIntent: Intent = Uri.parse("https://www.join.me/").let { uri ->
                    Intent(Intent.ACTION_VIEW, uri)
                }
                startActivity(callIntent)
            }
        }

        getAllChannelMembers()

        val swipeMoveToSelectedHandler = object : SwipeToSelectMemberCallback(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter1 = root.selectMemsRecyclerView.adapter as MeetingMembersAdapter
                adapter1.removeAt(viewHolder.adapterPosition)
                move_to_selected((viewHolder.itemView.tag as Member))
            }
        }
        val itemTouchMoveHelper = ItemTouchHelper(swipeMoveToSelectedHandler)
        itemTouchMoveHelper.attachToRecyclerView(root.selectMemsRecyclerView)

        val swipeRemoveFromSelectedHandler = object : SwipeToRemoveSelectedMemberCallback(
            requireActivity()
        ) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter2 = root.selectedMemsRecyclerView.adapter as MeetingMembersAdapter
                adapter2.removeAt(viewHolder.adapterPosition)
                remove_from_selected((viewHolder.itemView.tag as Member))
            }
        }
        val itemTouchMoveBackHelper = ItemTouchHelper(swipeRemoveFromSelectedHandler)
        itemTouchMoveBackHelper.attachToRecyclerView(root.selectedMemsRecyclerView)

        return root
    }


    fun move_to_selected(member: Member) {
        channel_members.remove(member)
        selected_members.add(member)

        channelMembersToRecycler()
    }

    fun remove_from_selected(member: Member) {
        selected_members.remove(member)
        channel_members.add(member)

        channelMembersToRecycler()
    }

    fun setSwipeRefresh() {
        root.swiperefreshCreateMeeting_1.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshCreateMeeting_1.isRefreshing = true
                root.swiperefreshCreateMeeting_2.isRefreshing = true
                channelMembersToRecycler()
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefreshCreateMeeting_1.isRefreshing) root.swiperefreshCreateMeeting_1.isRefreshing =
            false
        if (root.swiperefreshCreateMeeting_2.isRefreshing) root.swiperefreshCreateMeeting_2.isRefreshing =
            false
    }

    fun getAllChannelMembers() {
        channel_members = ArrayList<Member>()
        app.database.child("channels").child(currentChannel.id).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase members error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val member = it.getValue<Member>(Member::class.java)
                        channel_members.add(member!!)
//                        root.selectMemsRecyclerView.adapter = MeetingMembersAdapter(
//                            channel_members,
//                            this@CreateMeetingFragment
//                        )
//                        root.selectMemsRecyclerView.adapter?.notifyDataSetChanged()
//                        checkSwipeRefresh()
                        channelMembersToRecycler()
                        app.database.child("channels").child(currentChannel!!.id).child("members")
                            .removeEventListener(this)
                    }
                }
            })
    }

    private fun createWarningDialog() {
        AlertDialog.Builder(requireContext())
            .setView(R.layout.warning_dialog)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Proceed") { dialog, _ ->
                dialog.dismiss()
                showLoader(loader, "Loading . . . ", "Validating . . .")
                validateForm()
                hideLoader(loader)
                if (validateForm()) {
                    createMeeting(false)
                }
            }.show()
    }

    fun channelMembersToRecycler() {
        channel_members.forEach {
            root.selectMemsRecyclerView.adapter = MeetingMembersAdapter(
                channel_members,
                this@CreateMeetingFragment
            )
            root.selectMemsRecyclerView.adapter?.notifyDataSetChanged()
        }
        selected_members.forEach {
            root.selectedMemsRecyclerView.adapter = MeetingMembersAdapter(
                selected_members,
                this@CreateMeetingFragment
            )
            root.selectedMemsRecyclerView.adapter?.notifyDataSetChanged()
        }
        checkSwipeRefresh()
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

        val duration = root.editTxtEnd.text.toString()
        if (TextUtils.isEmpty(duration)) {
            root.editTxtEnd.error = "Duration Required."
            valid = false
        } else {
            root.editTxtEnd.error = null
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

    fun createMeeting(request: Boolean) {
        showLoader(loader, "Loading . . . ", "Creating Meeting ${new_meeting.meeting_title} . . .")
        new_meeting.meeting_date_as_string = "$dd/$mm/$yyyy"
        new_meeting.meeting_time_as_string = "$h:$m"
        new_meeting.participants = selected_members

        app.generate_date_reminder_id(dd, mm, yyyy, (h.toInt() - 1).toString(), mm, "00")
        new_meeting.meeting_date_id = app.reminder_due_date_id
        new_meeting.meeting_creator = app.currentActiveMember


        var hrs = root.editTxtEnd.text.toString()
        var cal = LocalDateTime.of(yyyy.toInt(), mm.toInt(), dd.toInt(), h.toInt(), m.toInt())
        var end_date = cal.plusHours(hrs.toLong())

        app.generate_date_reminder_id(
            end_date.dayOfMonth.toString(),
            end_date.monthValue.toString(),
            end_date.year.toString(),
            (end_date.hour).toString(),
            end_date.minute.toString(),
            "00"
        )

        new_meeting.meeting_date_end_id = app.reminder_due_date_id

        new_meeting.meeting_date_as_string_end =
            app.rem_dateAsString //"${cal.dayOfMonth}/${cal.monthValue}/${cal.year}"
        new_meeting.meeting_time_as_string_end = app.rem_timeAsString//"${cal.hour}:${cal.minute}"

        new_meeting.requested_meeting = request


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
                    var new_log = Log(
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


    companion object {
        @JvmStatic
        fun newInstance(channel: Channel, req: Boolean) =
            CreateMeetingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                    putBoolean("meeting_req", req)
                }
            }
    }
}