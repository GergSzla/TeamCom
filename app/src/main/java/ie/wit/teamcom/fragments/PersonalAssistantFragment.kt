package ie.wit.teamcom.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Meeting
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.*
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.progressBar_mh
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txtDue14daysTasks
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txtDue24HrsTasks
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txtDue7daysTasks
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txt_mh_ov
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txt_overall_standing
import kotlinx.android.synthetic.main.fragment_personal_assistant.view.txt_percentage
import org.jetbrains.anko.AnkoLogger
import java.util.ArrayList

class PersonalAssistantFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    var currentChannel_ = Channel()
    lateinit var root: View
    lateinit var mem_frag: ViewMemberFragment
    lateinit var meet_frag: MeetingsFragment
    lateinit var rem_frag: RemindersFragment
    lateinit var loader: androidx.appcompat.app.AlertDialog
    var set_of_ans_1 = 0.0
    var set_of_ans_2 = 0.0

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
        mem_frag.get_all_projects(app.database, auth.currentUser!!.uid)
        mem_frag.get_mh_entry(app.database, true, auth.currentUser!!.uid)
            Handler().postDelayed(
                {
                    check_pref(app.database, auth.currentUser!!.uid)
                },
                2000 // value in milliseconds
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_personal_assistant, container, false)
        activity?.title = "Personal Assistant"
        loader = createLoader(requireActivity())

        if (user_mh.set_of_ans_2_per == 0.0) {
            root.txt_mental_health.isVisible = false
            root.dia_mh.isVisible = false
            root.txt_mh_ov.isVisible = false
            root.txt_overall_standing.isVisible = false
            root.current_mh.isVisible = false
        } else {
            root.txt_mental_health.isVisible = true
            root.dia_mh.isVisible = true
            root.txt_mh_ov.isVisible = true
            root.txt_overall_standing.isVisible = true
            root.current_mh.isVisible = true
        }

        showLoader(loader, "Loading . . .", "Loading Assistant Data . . .")
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
            },
            2000 // value in milliseconds
        )

        root.txt_reschedule.setOnClickListener {
            navigateTo(MeetingsFragment.newInstance(currentChannel))
        }

        root.txt_gotoTasks.setOnClickListener {
            navigateTo(ProjectListFragment.newInstance(currentChannel))
        }

        root.txt_gotoReminders.setOnClickListener {
            navigateTo(RemindersFragment.newInstance(currentChannel))
        }

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

    private fun check_next_meeting() {
        if (meetingList.size != 0) {
            val newMeetingList = meetingList.sortedBy { it.meeting_date_id }
            newMeetingList.forEach {
                if (it.participants.any { it_ -> it_.id == app.currentActiveMember.id }) {
                    user_meetingList.add(it)
                }
            }
            if (user_meetingList.size != 0) {

                user_meetingList.sortedBy { it.meeting_date_id }

                root.txtMTitle.text = user_meetingList[user_meetingList.size - 1].meeting_title
                root.txtMDesc.text = user_meetingList[user_meetingList.size - 1].meeting_desc
                (user_meetingList[user_meetingList.size - 1].meeting_date_as_string + ", " + user_meetingList[user_meetingList.size - 1].meeting_time_as_string).also {
                    root.txtMDateAndTime.text = it
                }
                (user_meetingList[user_meetingList.size - 1].meeting_date_as_string_end + ", " + user_meetingList[user_meetingList.size - 1].meeting_time_as_string_end).also {
                    root.txtMDateAndTime_end.text = it
                }
                if (user_meetingList[user_meetingList.size - 1].online) {
                    "Online via: ${user_meetingList[user_meetingList.size - 1].meeting_platform}".also {
                        root.txtMLocationElsePlatform.text = it
                    }
                } else {
                    "Location: ${user_meetingList[user_meetingList.size - 1].meeting_location}".also {
                        root.txtMLocationElsePlatform.text = it
                    }
                }
            } else {
                root.meetingSect.isVisible = false
            }

        } else {
            root.meetingSect.isVisible = false
        }
        Handler().postDelayed(
            {
                check_next_task()
            },
            1000 // value in milliseconds
        )
    }

    private fun check_next_reminder() {
        if (reminderList.size != 0) {
            reminderList.sortBy { it.rem_date_id }

            root.txtRemMsg.text = reminderList[0].rem_msg
            (reminderList[0].rem_date_as_string + " - " + reminderList[0].rem_time_as_string).also {
                root.txtRemDandT.text = it
            }

        } else {
            root.reminderSect.isVisible = false
        }
        hideLoader(loader)

        Handler().postDelayed(
            {
                analyse_meetings()
            },
            1000 // value in milliseconds
        )
    }

    private fun analyse_meetings() {
        if (meetingList.size != 0) {
            showLoader(loader, "Analysing . . .", "Analysing Meetings  . . .")
            var desc_meeting_str = ""

            app.getMeetings(currentChannel.id)

            Handler().postDelayed(
                {
                    desc_meeting_str += "- You have a total of ${app.upcoming_meetings} meeting(s) today"

                    if (string_range_2 != "") {
                        if (string_range_2 == "range_2" && app.upcoming_meetings >= 2) {
                            desc_meeting_str += ", we also detect some potential stress! Having a quick stretch " +
                                    "or a breath of fresh air can drastically decrease your stress!"
                        } else if ((string_range_2.takeLast(1)
                                .toInt()) > 2 && app.upcoming_meetings >= 2
                        ) {
                            desc_meeting_str += ", we also detect some mental health issues. If we're correct " +
                                    "and you're feeling overwhelmed, please take a quick break, have a stretch, get " +
                                    "some fresh air, or go for a calming walk when possible! " +
                                    "\nIf at all possible, check if one of these meetings can be rescheduled."
                        }
                    } else {
                        desc_meeting_str =
                            "No mental health survey entries found! For the assistant to give you suggestions, you need to:" +
                                    "\n- Enable Survey" +
                                    "\n- Have At Least One Entry"
                    }


                    root.txt_meeting_desc.text = desc_meeting_str

                    hideLoader(loader)
                    analyse_tasks()
                },
                1500 // value in milliseconds
            )

        }
    }

    private fun analyse_tasks() {
        if (ongoing.size != 0) {
            showLoader(loader, "Analysing . . .", "Analysing Tasks  . . .")
            var desc_tasks_str = ""

            Handler().postDelayed(
                {
                    desc_tasks_str =
                        "- You have a total of : \n- $due_in_24_hrs tasks(s) due in the next 24 Hours " +
                                "\n- $due_in_7_days task(s) due within 7 Days and " +
                                "\n- $due_in_14_days task(s) due within 14 days."

                    var score_2 = String.format("%.1f", user_mh.set_of_ans_2_per)
                    if (string_range_2 != "") {
                        if (due_in_24_hrs >= 3 && string_range_2 == "range_2") {
                            desc_tasks_str += "\n- You have 3 or more tasks due in 24 Hours with a mental " +
                                    "health survey score of $score_2. This score is slightly hinting at some " +
                                    "potential stress, or perhaps something more personal. " +
                                    "\nTry taking a few deep breaths, have a stretch and relax a little bit!"
                        } else if (((string_range_2.takeLast(1)
                                .toInt()) > 2 && (string_range_2.takeLast(1)
                                .toInt()) < 5) && due_in_24_hrs >= 3
                        ) {
                            desc_tasks_str += "\n- You have 3 or more tasks due in 24 Hours with a mental " +
                                    "health survey score of $score_2. This score is quite concerning and hints" +
                                    "that you may be stressed and overwhelmed. " +
                                    "\nPlease if and when possible, try a quick breathing exercise and go for " +
                                    "a calming walk." +
                                    "\nIf at all possible, reschedule one or more of these tasks or request it to be rescheduled."
                        } else if (((string_range_2.takeLast(1)
                                .toInt()) >= 5) && due_in_24_hrs >= 3
                        ) {
                            desc_tasks_str += "\n- You have 3 or more tasks due in 24 Hours with a mental " +
                                    "health survey score of $score_2. This score is very concerning and hints" +
                                    "that you may be stressed, overwhelmed, depressed etc. " +
                                    "\nPlease if and when possible, try a quick breathing exercise and go for " +
                                    "a calming walk." +
                                    "\nIf at all possible, reschedule one or more of these tasks or request it to be rescheduled." +
                                    "\n\nIf you're feeling down and feel like you need to talk to someone, you can feel free to talk to someone here: Samaritans (Freephone: 116 123)" +
                                    "\nDon't worry, everything is going to be okay! :) "
                        } else if ((string_range_2 == "range_1") && due_in_24_hrs >= 3) {
                            desc_tasks_str += "\n- You have 3 or more tasks due in 24 Hours with a mental " +
                                    "health survey score of $score_2. This score is excellent! " +
                                    "\n\nKeep up the great work!"
                        } else if ((string_range_2 == "range_1")) {
                            desc_tasks_str += "\n- No tasks due within 24 hours! :)"
                        }
                    } else {
                        desc_tasks_str =
                            "No mental health survey entries found! For the assistant to give you suggestions, you need to:" +
                                    "\n- Enable Survey" +
                                    "\n- Have At Least One Entry"
                    }
                    root.txt_tasks_desc.text = desc_tasks_str

                    hideLoader(loader)
                    analyse_reminders()
                },
                1500 // value in milliseconds
            )
        }
    }

    fun check_pref(db: DatabaseReference, userID : String) {
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

                    check_entries(userID)
                }
            })
    }

    fun check_entries( userID : String) {
        app.database.child("channels").child(currentChannel.id).child("surveys")
            .child(userID).child("entry")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    if(survey_enabled){
                        set_of_ans_1 = snapshot.child("set_of_ans_1_per").value.toString().toDouble()
                        set_of_ans_2 = snapshot.child("set_of_ans_2_per").value.toString().toDouble()
                    }

                    app.database.child("channels").child(currentChannel.id).child("surveys")
                        .child(userID).child("survey_pref")
                        .removeEventListener(this)

                }
            })
    }

    private fun analyse_reminders() {
        showLoader(loader, "Analysing . . .", "Analysing Reminders  . . .")



        hideLoader(loader)
    }

    private fun check_next_task() {
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
        Handler().postDelayed(
            {
                check_next_reminder()
            },
            1000 // value in milliseconds
        )
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
        fun newInstance(channel: Channel) =
            PersonalAssistantFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

}