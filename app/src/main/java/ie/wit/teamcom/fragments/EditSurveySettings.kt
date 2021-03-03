package ie.wit.teamcom.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.SurveyPref
import kotlinx.android.synthetic.main.fragment_edit_survey_settings.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.HashMap


class EditSurveySettings : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var survey_preference = SurveyPref()

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
        root = inflater.inflate(R.layout.fragment_edit_survey_settings, container, false)
        activity?.title = getString(R.string.title_survey)

        get_survey_pref()
        root.toggle_survey.isChecked = survey_preference.enabled
        if(survey_preference.frequency == "Daily"){
            root.radioButton3.isChecked = true
        } else if(survey_preference.frequency == "Weekly"){
            root.radioButton4.isChecked = true
        } else if(survey_preference.frequency == "Biweekly"){
            root.radioButton5.isChecked = true
        } else if(survey_preference.frequency == "Monthly"){
            root.radioButton6.isChecked = true
        }

        root.btn_info_dialog.setOnClickListener{
            show_info()
        }

        root.btn_save_survey_pref.setOnClickListener{
            save_survey_preference()
        }

        root.toggle_survey.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                root.textView24.isVisible = true
                root.radio_group.isVisible = true
            } else {
                root.textView24.isVisible = false
                root.radio_group.isVisible = false
            }
        }

        return root
    }

    fun show_info(){
        AlertDialog.Builder(requireContext())
            .setView(R.layout.survey_info_dialog)
            .setTitle("Survey Information")
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun save_survey_preference(){
        app.generateDateID("1")
        survey_preference.next_date_id = app.valid_from_cal
        survey_preference.user_id = app.auth.currentUser!!.uid
        survey_preference.enabled = root.toggle_survey.isEnabled
        if (!root.toggle_survey.isEnabled){
            survey_preference.frequency = "null"
        } else {
            var selected_freq_id = root.radio_group.checkedRadioButtonId
            if (selected_freq_id != -1){
                var selected_freq = root.findViewById<RadioButton>(selected_freq_id)
                survey_preference.frequency = selected_freq.text.toString()
            }
        }

        update_pref()

    }

    fun update_pref(){
        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/surveys/${app.auth.currentUser!!.uid}/survey_pref/"] = survey_preference
                    app.database.updateChildren(childUpdates)
                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                }
            })
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

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            EditSurveySettings().apply {
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