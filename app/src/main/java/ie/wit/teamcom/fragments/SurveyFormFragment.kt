package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.InviteAdapter
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Invite
import ie.wit.teamcom.models.SurveyPref
import ie.wit.teamcom.models.UserMHModel
import kotlinx.android.synthetic.main.fragment_invites_list.view.*
import kotlinx.android.synthetic.main.fragment_survey_form.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.HashMap

class SurveyFormFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var user_mh = UserMHModel()
    var survey_preference = SurveyPref()
    var rad_g_1_check = false
    var rad_g_2_check = false
    var rad_g_3_check = false
    var rad_g_4_check = false
    var rad_g_5_check = false
    var rad_g_6_check = false
    var rad_g_7_check = false
    var rad_g_8_check = false
    var rad_g_9_check = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_survey_form, container, false)
        activity?.title = getString(R.string.title_survey)


        get_survey_pref()

        root.radio_group1.setOnCheckedChangeListener { radioGroup, i ->
            if (!rad_g_1_check)
                root.form_comp_prog.incrementProgressBy(11)
            rad_g_1_check = true
        }
        root.radio_group2.setOnCheckedChangeListener { radioGroup, i ->
            if (!rad_g_2_check)
                root.form_comp_prog.incrementProgressBy(11)
            rad_g_2_check = true
        }
        root.radio_group3.setOnCheckedChangeListener { radioGroup, i ->
            if (!rad_g_3_check)
                root.form_comp_prog.incrementProgressBy(11)
            rad_g_3_check = true
        }
        root.radio_group4.setOnCheckedChangeListener { radioGroup, i ->
            if (!rad_g_4_check)
                root.form_comp_prog.incrementProgressBy(11)
            rad_g_4_check = true
        }
        root.radio_group5.setOnCheckedChangeListener { radioGroup, i ->
            if (!rad_g_5_check)
                root.form_comp_prog.incrementProgressBy(11)
            rad_g_5_check = true
        }
        root.radio_group6.setOnCheckedChangeListener { radioGroup, i ->
            if (!rad_g_6_check)
                root.form_comp_prog.incrementProgressBy(11)
            rad_g_6_check = true
        }
        root.radio_group7.setOnCheckedChangeListener { radioGroup, i ->
            if (!rad_g_7_check)
                root.form_comp_prog.incrementProgressBy(11)
            rad_g_7_check = true
        }
        root.radio_group8.setOnCheckedChangeListener { radioGroup, i ->
            if (!rad_g_8_check)
                root.form_comp_prog.incrementProgressBy(11)
            rad_g_8_check = true
        }
        root.radio_group9.setOnCheckedChangeListener { radioGroup, i ->
            if (!rad_g_9_check)
                root.form_comp_prog.incrementProgressBy(12)
            rad_g_9_check = true
        }

        root.btn_save_survey.setOnClickListener {
            save_survey()
        }


        return root
    }

    fun get_survey_pref(){
        app.database.child("channels").child(currentChannel.id).child("surveys").child(app.auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {

                        val survey_pref = it.getValue<SurveyPref>(SurveyPref::class.java)
                        survey_preference = survey_pref!!

                        app.database.child("channels").child(currentChannel!!.id)
                            .removeEventListener(this)
                    }
                }
            })
    }

    var no_valid = 0 //counts questions where answer != N/A or I'd Rather Not Answer
    var answer_1 = ""
    var answer_2 = ""
    var answer_3 = ""
    var answer_4 = ""
    fun save_survey() {
        if (root.radio_group1.checkedRadioButtonId != -1) {
            var selected_1 =
                root.findViewById<RadioButton>(root.radio_group1.checkedRadioButtonId)
            answer_1 = selected_1.text.toString()
            if (answer_1 !== "I'd Rather Not Answer") {
                no_valid += 1
            }
        }
        if (root.radio_group2.checkedRadioButtonId != -1) {
            var selected_2 =
                root.findViewById<RadioButton>(root.radio_group2.checkedRadioButtonId)

            answer_2 = selected_2.text.toString()
            if (answer_2 !== "I'd Rather Not Answer") {
                no_valid += 1
            }
        }
        if (root.radio_group3.checkedRadioButtonId != -1) {
            var selected_3 =
                root.findViewById<RadioButton>(root.radio_group3.checkedRadioButtonId)
            answer_3 = selected_3.text.toString()
            if (answer_3 !== "I'd Rather Not Answer") {
                no_valid += 1
            }
        }
        if (root.radio_group4.checkedRadioButtonId != -1) {
            var selected_4 =
                root.findViewById<RadioButton>(root.radio_group4.checkedRadioButtonId)

            answer_4 = selected_4.text.toString()
            if (answer_4 !== "I'd Rather Not Answer") {
                no_valid += 1
            }
        }

        calc_set_1(answer_1, answer_2, answer_3, answer_4)

    }

    var surveyable_percentage = 0.0
    var question_worth = 0.0
    fun calc_set_1(ans_1: String, ans_2: String, ans_3: String, ans_4: String) {
        if (no_valid > 0.0.toInt()) {
            surveyable_percentage = (100 - (100 / no_valid)).toDouble()
            question_worth = (100 / no_valid).toDouble()

        } else {
            surveyable_percentage = 100.0
            question_worth = 100.0
        }


        var set_1_pts_per = 0.0
        var set_2_pts_per = 0.0

        if (root._1opt1.isChecked) {
            set_1_pts_per += question_worth / 1
        } else if (root._1opt2.isChecked) {
            set_1_pts_per += question_worth / 2
        } else if (root._1opt3.isChecked) {
            set_1_pts_per += question_worth / 3
        } else if (root._1opt4.isChecked) {
            set_1_pts_per += question_worth / 4
        } else if (root._1opt5.isChecked) {
            set_1_pts_per += question_worth / 5
        }

        if (root._2opt1.isChecked) {
            set_1_pts_per += question_worth / 5
        } else if (root._2opt2.isChecked) {
            set_1_pts_per += question_worth / 4
        } else if (root._2opt3.isChecked) {
            set_1_pts_per += question_worth / 3
        } else if (root._2opt4.isChecked) {
            set_1_pts_per += question_worth / 2
        } else if (root._2opt5.isChecked) {
            set_1_pts_per += question_worth / 1
        }

        if (root._3opt1.isChecked) {
            set_1_pts_per += question_worth / 5
        } else if (root._3opt2.isChecked) {
            set_1_pts_per += question_worth / 4
        } else if (root._3opt3.isChecked) {
            set_1_pts_per += question_worth / 3
        } else if (root._3opt4.isChecked) {
            set_1_pts_per += question_worth / 2
        } else if (root._3opt5.isChecked) {
            set_1_pts_per += question_worth / 1
        }

        if (root._4opt1.isChecked) {
            set_1_pts_per += question_worth / 1
        } else if (root._4opt2.isChecked) {
            set_1_pts_per += question_worth / 2
        } else if (root._4opt3.isChecked) {
            set_1_pts_per += question_worth / 3
        } else if (root._4opt4.isChecked) {
            set_1_pts_per += question_worth / 4
        } else if (root._4opt5.isChecked) {
            set_1_pts_per += question_worth / 5
        }

        user_mh.set_of_ans_1_per = set_1_pts_per


        if (root._5opt1.isChecked) {
            set_2_pts_per += question_worth / 1
        } else if (root._5opt2.isChecked) {
            set_2_pts_per += question_worth / 2
        } else if (root._5opt3.isChecked) {
            set_2_pts_per += question_worth / 3
        } else if (root._5opt4.isChecked) {
            set_2_pts_per += question_worth / 4
        }

        if (root._2opt1.isChecked) {
            set_2_pts_per += question_worth / 5
        } else if (root._6opt2.isChecked) {
            set_2_pts_per += question_worth / 4
        } else if (root._6opt3.isChecked) {
            set_2_pts_per += question_worth / 3
        } else if (root._6opt4.isChecked) {
            set_2_pts_per += question_worth / 2
        }

        if (root._3opt1.isChecked) {
            set_2_pts_per += question_worth / 5
        } else if (root._7opt2.isChecked) {
            set_2_pts_per += question_worth / 4
        } else if (root._7opt3.isChecked) {
            set_2_pts_per += question_worth / 3
        } else if (root._7opt4.isChecked) {
            set_2_pts_per += question_worth / 2
        }

        if (root._4opt1.isChecked) {
            set_2_pts_per += question_worth / 1
        } else if (root._8opt2.isChecked) {
            set_2_pts_per += question_worth / 2
        } else if (root._8opt3.isChecked) {
            set_2_pts_per += question_worth / 3
        } else if (root._8opt4.isChecked) {
            set_2_pts_per += question_worth / 4
        }

        if (root._4opt1.isChecked) {
            set_2_pts_per += question_worth / 1
        } else if (root._9opt2.isChecked) {
            set_2_pts_per += question_worth / 2
        } else if (root._9opt3.isChecked) {
            set_2_pts_per += question_worth / 3
        } else if (root._9opt4.isChecked) {
            set_2_pts_per += question_worth / 4
        }

        user_mh.set_of_ans_2_per = set_2_pts_per
        user_mh.user_id = app.auth.currentUser!!.uid

        if(root.radio_group1.checkedRadioButtonId != -1 &&
            root.radio_group2.checkedRadioButtonId != -1 &&
            root.radio_group3.checkedRadioButtonId != -1 &&
            root.radio_group4.checkedRadioButtonId != -1 &&
            root.radio_group5.checkedRadioButtonId != -1 &&
            root.radio_group6.checkedRadioButtonId != -1 &&
            root.radio_group7.checkedRadioButtonId != -1 &&
            root.radio_group8.checkedRadioButtonId != -1 &&
            root.radio_group9.checkedRadioButtonId != -1){
            add_to_db()
        } else {
            Toast.makeText(context,"Information Missing", Toast.LENGTH_LONG)
        }
    }

    fun add_to_db(){
        var new_date_id = 0
        if (survey_preference.frequency == "Daily"){
            app.generateDateID("24")
        } else if (survey_preference.frequency == "Weekly") {
            app.generateDateID("168")
        } else if (survey_preference.frequency == "Biweekly") {
            app.generateDateID("336")
        } else if (survey_preference.frequency == "Monthly") {
            app.generateDateID("720")
        }

        survey_preference.next_date_id = app.valid_to_cal

        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    val childUpdates_ = HashMap<String, Any>()

                    childUpdates["/channels/${currentChannel.id}/surveys/${app.auth.currentUser!!.uid}/entry/"] = user_mh
                    childUpdates_["/channels/${currentChannel.id}/surveys/${app.auth.currentUser!!.uid}/survey_pref/"] = survey_preference

                    app.database.updateChildren(childUpdates)
                    app.database.updateChildren(childUpdates_)

                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                    navigateTo(NewsFeedFragment.newInstance(currentChannel))
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            SurveyFormFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
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
}