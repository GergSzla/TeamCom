package ie.wit.teamcom.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.ReminderListener
import ie.wit.teamcom.adapters.RemindersAdapter
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Reminder
import kotlinx.android.synthetic.main.fragment_reminders.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.time.LocalDate
import java.util.*

class RemindersFragment : Fragment(), AnkoLogger, ReminderListener {

    lateinit var root: View
    lateinit var app: MainApp
    var reminderList = ArrayList<Reminder>()
    var new_reminder = Reminder()
    private var dialog: Dialog? = null
    private val adapter: RemindersAdapter? = null
    private val temp: List<Reminder>? = null
    private val empty: TextView? = null

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
        root = inflater.inflate(R.layout.fragment_reminders, container, false)
        activity?.title = getString(R.string.title_reminders)
        root.remindersRecyclerView.layoutManager = LinearLayoutManager(activity)

        root.btnAddNewReminder.setOnClickListener {
            addReminder()
        }
        setSwipeRefresh()
        return root
    }

    val newDate = Calendar.getInstance()
    val newTime = Calendar.getInstance()

    fun addReminder(){
        dialog = Dialog(requireContext())
        dialog!!.setContentView(R.layout.floating_popup)

        val textView: TextView = dialog!!.findViewById<TextView>(R.id.date)
        val select: Button
        val add: Button
        select = dialog!!.findViewById<Button>(R.id.selectDate)
        add = dialog!!.findViewById<Button>(R.id.addButton)
        val message: EditText = dialog!!.findViewById<EditText>(R.id.message)
        val newCalender = Calendar.getInstance()

        select.setOnClickListener{
            val dialog = DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth ->
                    val time = TimePickerDialog(
                        requireContext(),
                        { view, hourOfDay, minute ->
                            newDate[year, month, dayOfMonth, hourOfDay, minute] = 0
                            val tem = Calendar.getInstance()
                            Log.w("TIME", System.currentTimeMillis().toString() + "")
                            if (newDate.timeInMillis - tem.timeInMillis > 0) textView.setText(
                                newDate.time.toString()
                            ) else Toast.makeText(
                                requireContext(),
                                "Invalid time",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }, newTime[Calendar.HOUR_OF_DAY], newTime[Calendar.MINUTE], true
                    )
                    time.show()
                },
                newCalender.get(Calendar.YEAR),
                newCalender.get(Calendar.MONTH),
                newCalender.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis()
            dialog.show()
        }

        add.setOnClickListener {
            val remind_date = newDate
            new_reminder.rem_date = remind_date.toString()
            new_reminder.id = UUID.randomUUID().toString()
            app.generateDateID("1")

            var date_day = remind_date.get(Calendar.DATE).toString()
            var date_month = (remind_date.get(Calendar.MONTH) + 1).toString()
            var date_year = remind_date.get(Calendar.YEAR).toString()
            var date_hour = remind_date.get(Calendar.HOUR_OF_DAY).toString()
            var date_minute = remind_date.get(Calendar.MINUTE).toString()
            var date_seconds = remind_date.get(Calendar.SECOND).toString()

            app.generate_date_reminder_id(date_day,date_month,date_year,date_hour,date_minute,date_seconds)

            new_reminder.rem_date_id = app.reminder_date_id
            new_reminder.rem_date_as_string = app.rem_dateAsString
            new_reminder.rem_time_as_string = app.rem_timeAsString
            new_reminder.rem_msg = message.text.toString()

            createReminder()
        }
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()

    }

    fun createReminder(){
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel!!.id}/reminders/${app.currentActiveMember.id}/${new_reminder.id}/"] = new_reminder
                    app.database.updateChildren(childUpdates)




                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                    dialog!!.dismiss()
                }
            })
    }

    fun getAllReminders(){
        reminderList = ArrayList<Reminder>()
        app.database.child("channels").child(currentChannel!!.id).child("reminders").child(app.currentActiveMember.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase nf error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val reminder = it.getValue<Reminder>(Reminder::class.java)
                        reminderList.add(reminder!!)
                        root.remindersRecyclerView.adapter = RemindersAdapter(
                            reminderList,
                            this@RemindersFragment
                        )
                        root.remindersRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("reminders")
                            .child(
                                app.currentActiveMember.id
                            )
                            .removeEventListener(this)
                    }
                }
            })
    }

    fun setSwipeRefresh() {
        root.swiperefreshReminders.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshReminders.isRefreshing = true
                getAllReminders()
            }
        })
    }
    fun checkSwipeRefresh() {
        if (root.swiperefreshReminders.isRefreshing) root.swiperefreshReminders.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        getAllReminders()
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
            RemindersFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }
}