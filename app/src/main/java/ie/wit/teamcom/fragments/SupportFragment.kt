package ie.wit.teamcom.fragments

import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Bug
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.fragment_support.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList
import java.util.HashMap

class SupportFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View
    var new_bug = Bug()
    var bugs_list = ArrayList<Bug>()

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
        root = inflater.inflate(R.layout.fragment_support, container, false)
        activity?.title = getString(R.string.menu_support)


        var res: Resources = resources

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // Layout
            res.getStringArray(R.array.pages)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        root.spinnerPages.adapter = adapter

        getAllBugs()

        root.btnSubmitBug.setOnClickListener {
            validateForm()
            if (validateForm()){
                new_bug.bug_no = bugs_list.size + 1
                new_bug.channel = currentChannel.channelName
                app.generateDateID("1")
                new_bug.date_reported = app.dateAsString
                new_bug.date_resolved = "-"
                new_bug.fixed = false
                new_bug.issue = root.editTxtIssue.text.toString()
                new_bug.issue_desc = root.editTxtIssueDesc.text.toString()
                new_bug.reported_by = app.currentActiveMember
                new_bug.id = "Bug_${bugs_list.size + 1}"
                new_bug.page = root.spinnerPages.selectedItem.toString()
                addBugReport()
            }
        }

        return root
    }

    private fun validateForm(): Boolean{
        var valid = true

        val issue = root.editTxtIssue.text.toString()
        if (TextUtils.isEmpty(issue)) {
            root.editTxtIssue.error = "Issue Required."
            valid = false
        } else {
            root.editTxtIssue.error = null
        }

        val desc = root.editTxtIssueDesc.text.toString()
        if (TextUtils.isEmpty(desc)) {
            root.editTxtIssueDesc.error = "Issue Description Required."
            valid = false
        } else {
            root.editTxtIssueDesc.error = null
        }

        return valid
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel,app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel,app.currentActiveMember)
    }

    fun getAllBugs(){
        app.database.child("bugs")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase bugs error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val bug = it.
                        getValue<Bug>(Bug::class.java)
                        bugs_list.add(bug!!)

                        app.database.child("bugs")
                            .removeEventListener(this)
                    }
                }
            })
    }

    fun addBugReport(){
        app.database.child("bugs")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/bugs/${new_bug.id}/"] = new_bug
                    app.database.updateChildren(childUpdates)

                    app.database.child("bugs")
                        .removeEventListener(this)
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            SupportFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }
}