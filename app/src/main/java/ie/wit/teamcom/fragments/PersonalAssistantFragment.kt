package ie.wit.teamcom.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Meeting
import ie.wit.teamcom.models.Task
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.*
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.progressBar_mh
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txtDue14daysTasks
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txtDue24HrsTasks
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txtDue7daysTasks
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txt_mh_ov
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txt_overall_standing
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txt_percentage
import kotlinx.android.synthetic.main.fragment_view_member.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.ArrayList

class PersonalAssistantFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    var currentChannel_ = Channel()
    lateinit var root: View
    lateinit var mem_frag: ViewMemberFragment
    lateinit var meet_frag: MeetingsFragment
    lateinit var rem_frag: RemindersFragment
    lateinit var loader : androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        mem_frag = ViewMemberFragment()
        meet_frag = MeetingsFragment()
        rem_frag = RemindersFragment()

        arguments?.let {
            currentChannel_ = it.getParcelable("channel_key")!!
        }

        meet_frag.getAllMeetings(app.database, true)
        rem_frag.getAllReminders(app.database, true)
        mem_frag.get_all_projects(app.database)
        if (user_mh.set_of_ans_2_per.toString() != "") {
            mem_frag.get_mh_entry(app.database, true)
            Handler().postDelayed(
                {
                    mem_frag.check_pref(app.database, true)
                },
                2000 // value in milliseconds
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_personal_assistant, container, false)
        activity?.title = "Personal Assistant"
        loader = createLoader(requireActivity())


        if (survey_enabled) {
            root.progressBar_mh.progress = user_mh.set_of_ans_2_per.toInt()
            root.txt_percentage.text = String.format("%.1f", user_mh.set_of_ans_2_per)
            root.txt_overall_standing.text = string_overall

            if (allow_admin) {
                "- visible only to admins".also { root.txt_opt_visible_to_admin.text = it }
            } else {
                "- not visible to admins (or anyone)".also {
                    root.txt_opt_visible_to_admin.text = it
                }
            }

            "- surveyed $survey_freq".also { root.txt_opt_frequency.text = it }
        } else {
            root.progressBar_mh.isVisible = false
            root.txt_percentage.isVisible = false
            root.txt_overall_standing.isVisible = false
            root.txt_mh_ov.isVisible = false

            "- not enabled".also { root.txt_opt_visible_to_admin.text = it }
            root.txt_opt_frequency.text = ""
        }

        Handler().postDelayed(
            {
                check_next_meeting()
                check_next_reminder()
                check_next_task()
            },
            2000 // value in milliseconds
        )
        hideLoader(loader)
        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    var user_meetingList = ArrayList<Meeting>()

    fun check_next_meeting() {
        if (meetingList.size != 0) {
            meetingList.sortBy { it.meeting_date_id }
            meetingList.forEach {
                if (it.participants.any { it_ -> it_.id == app.currentActiveMember.id }) {
                    user_meetingList.add(it)
                }
            }
            if (user_meetingList.size != 0) {
                root.txtMTitle.text = user_meetingList[0].meeting_title
                root.txtMDesc.text = user_meetingList[0].meeting_desc
                (user_meetingList[0].meeting_date_as_string + ", " + user_meetingList[0].meeting_time_as_string).also {
                    root.txtMDateAndTime.text = it
                }
                (user_meetingList[0].meeting_date_as_string_end + ", " + user_meetingList[0].meeting_time_as_string_end).also {
                    root.txtMDateAndTime_end.text = it
                }
                if (user_meetingList[0].online) {
                    root.txtMLocationElsePlatform.text =
                        "Online via: ${meetingList[0].meeting_platform}"
                } else {
                    root.txtMLocationElsePlatform.text =
                        "Location: ${meetingList[0].meeting_location}"
                }
            } else {
                root.meetingSect.isVisible = false
            }

        } else {
            root.meetingSect.isVisible = false
        }
    }

    fun check_next_reminder() {
        if (reminderList.size != 0) {
            reminderList.sortBy { it.rem_date_id }

            root.txtRemMsg.text = reminderList[0].rem_msg
            (reminderList[0].rem_date_as_string + " - " + reminderList[0].rem_time_as_string).also {
                root.txtRemDandT.text = it
            }

        } else {
            root.reminderSect.isVisible = false
        }
    }


    fun check_next_task() {
        if (ongoing.size != 0) {
            ongoing.sortBy { it.task_due_date_id }

            root.txt_task_title.text = ongoing[0].task_msg
            (ongoing[0].task_importance.toString() + "/5").also { root.txt_importance.text = it }
            root.progressImportance.progress = ongoing[0].task_importance
            root.txt_status.text = ongoing[0].task_current_stage

            if (ongoing[0].task_current_stage_color.take(1) != "#") {
                root.txt_status.setTextColor(Color.parseColor("#" + ongoing[0].task_current_stage_color))
            } else {
                root.txt_status.setTextColor(Color.parseColor(ongoing[0].task_current_stage_color))
            }
            (ongoing[0].task_due_date_as_string + ", " + ongoing[0].task_due_time_as_string).also {
                root.txt_dueDate.text = it
            }

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

            root.txtDue24HrsTasks.text = due_in_24_hrs.toString()
            root.txtDue7daysTasks.text = due_in_7_days.toString()
            root.txtDue14daysTasks.text = due_in_14_days.toString()
        } else {
            root.taskSect.isVisible = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            PersonalAssistantFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

}