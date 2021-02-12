package ie.wit.teamcom.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.EventAdapter
import ie.wit.teamcom.adapters.EventListener_
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Event
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import kotlinx.android.synthetic.main.fragment_invites_list.view.*
import kotlinx.android.synthetic.main.fragment_task_stages.*
import kotlinx.android.synthetic.main.fragment_task_stages.view.*
import kotlinx.android.synthetic.main.popup_create_event.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*


class CalendarFragment : Fragment(), AnkoLogger, EventListener_ {

    lateinit var app: MainApp
    lateinit var root: View
    var day = ""
    var month = ""
    var event = Event()
    var eventsList = ArrayList<Event>()
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
        root = inflater.inflate(R.layout.fragment_calendar, container, false)
        activity?.title = getString(R.string.title_calendar)
        root.calendarRecyclerView.layoutManager = LinearLayoutManager(activity)

        loader = createLoader(requireActivity())

        var cal = Calendar.getInstance()
        val dateDialog = root.calPickDate

        dateDialog.minDate = Calendar.getInstance().timeInMillis

        root.btn_add_event.setOnClickListener {
            date_convert(dateDialog.year, dateDialog.month, dateDialog.dayOfMonth, true)
        }

        date_convert(dateDialog.year, dateDialog.month, dateDialog.dayOfMonth, false)

        dateDialog.setOnDateChangedListener { datePicker, yyyy, mm, dd ->

            date_convert(yyyy, mm, dd, false)

        }

        root.btn_scroll_to_events.setOnClickListener {
            root.calendar_scroll.smoothScrollTo(1000,0)
        }

        return root
    }

    fun date_convert(yyyy: Int, mm: Int, dd: Int, new_event: Boolean) {
        day = if (dd < 10) {
            "0$dd"
        } else {
            "$dd"
        }

        month = if (mm < 10) {
            "0${mm + 1}"
        } else {
            "${mm + 1}"
        }

        var selected_date = "$yyyy-$month-$day" //DATE TO YYYY/MM/DD for easy database sorting
        if (new_event){
            add_new_event(selected_date)
            getCurDateEvents(yyyy.toString(),month,day)
        } else {
            getEventsForDate(selected_date)
            getCurDateEvents(yyyy.toString(),month,day)
        }
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    fun getEventsForDate(date: String) {
        root.txtDatePicked.text = "Showing Events for: \n${date}"
    }

    fun add_new_event(date: String) {

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_create_event)
        val create_event = dialog.findViewById(R.id.btn_create_task) as Button
        val txt_date_selected = dialog.findViewById(R.id.txt_date_selected) as TextView
        val txt_event_desc = dialog.findViewById(R.id.txt_event_desc) as EditText
        val txt_event_name = dialog.findViewById(R.id.txt_event_name) as EditText
        val btn_color_1 = dialog.findViewById(R.id.btn_color_1) as Button
        val btn_color_2 = dialog.findViewById(R.id.btn_color_2) as Button
        val btn_color_3 = dialog.findViewById(R.id.btn_color_3) as Button
        val btn_color_4 = dialog.findViewById(R.id.btn_color_4) as Button
        val btn_color_5 = dialog.findViewById(R.id.btn_color_5) as Button
        val btn_color_6 = dialog.findViewById(R.id.btn_color_6) as Button
        val btn_color_7 = dialog.findViewById(R.id.btn_color_7) as Button
        val btn_color_8 = dialog.findViewById(R.id.btn_color_8) as Button
        val image_stat = dialog.findViewById(R.id.image_stat) as ImageView
        val cancel = dialog.findViewById(R.id.btn_cancel_event) as ImageButton

        txt_date_selected.text = date

        //https://stackoverflow.com/questions/11376516/change-drawable-color-programmatically
        val unwrappedDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_color_stat_24)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, Color.RED)

        //Default
        image_stat.setColorFilter(Color.parseColor("#6200EE"))
        event.event_color = "#6200EE"

        btn_color_1.setOnClickListener{
            image_stat.setColorFilter(Color.parseColor("#6200EE"))
            event.event_color = "#6200EE"
        }
        btn_color_2.setOnClickListener{
            image_stat.setColorFilter(Color.parseColor("#0057EE"))
            event.event_color = "#0057EE"
        }
        btn_color_3.setOnClickListener{
            image_stat.setColorFilter(Color.parseColor("#06CD8F"))
            event.event_color = "#06CD8F"
        }
        btn_color_4.setOnClickListener{
            image_stat.setColorFilter(Color.parseColor("#00B103"))
            event.event_color = "#00B103"
        }
        btn_color_5.setOnClickListener{
            image_stat.setColorFilter(Color.parseColor("#BCB200"))
            event.event_color = "#BCB200"
        }
        btn_color_6.setOnClickListener{
            image_stat.setColorFilter(Color.parseColor("#EE6700"))
            event.event_color = "#EE6700"
        }
        btn_color_7.setOnClickListener{
            image_stat.setColorFilter(Color.parseColor("#A30000"))
            event.event_color = "#A30000"
        }
        btn_color_8.setOnClickListener{
            image_stat.setColorFilter(Color.parseColor("#CA00EE"))
            event.event_color = "#CA00EE"
        }

        create_event.setOnClickListener{
//            showLoader(loader, "Loading . . .", "Validating . . .")
//            //validateForm()
//            hideLoader(loader)


            //if (validateForm()){
                showLoader(loader, "Loading . . .", "Creating Event ${event.event_name} . . .")
                event.event_name = txt_event_name.text.toString()
                event.event_desc = txt_event_desc.text.toString()

                var edited_date = txt_date_selected.text.toString().replace("-","/",false)
                event.event_date = edited_date
                event.event_type = "[TODO]"
                event.event_id = UUID.randomUUID().toString()
                event.event_participants = app.currentActiveMember

                app.database.child("channels").child(currentChannel.id)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val childUpdates = HashMap<String, Any>()
                            childUpdates["/channels/${currentChannel.id}/events/${event.event_date}/${event.event_id}"] = event
                            app.database.updateChildren(childUpdates)

                            hideLoader(loader)
                            app.database.child("channels").child(currentChannel.id)
                                .removeEventListener(this)
                            navigateTo(CalendarFragment.newInstance(currentChannel))
                        }
                    })
            //}
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


//    private fun validateForm(): Boolean{
//        var valid = true
//
//        val name = txt_event_name.text.toString()
//        if (TextUtils.isEmpty(name)) {
//            txt_event_name.error = "Event Name Required."
//            valid = false
//        } else {
//            txt_event_name.error = null
//        }
//
//        if (txt_event_desc.text.toString() == ""){
//            event.event_desc = " "
//        }
//
//        return valid
//    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun setSwipeRefresh(yyyy: String, mm: String, dd: String) {
        root.swiperefreshEvents.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshEvents.isRefreshing = true
                getCurDateEvents(yyyy, mm, dd)
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefreshEvents.isRefreshing) root.swiperefreshEvents.isRefreshing = false
    }

    fun getCurDateEvents(yyyy: String, mm: String, dd: String) {
        eventsList = ArrayList<Event>()
        app.database.child("channels").child(currentChannel.id).child("events").child(yyyy).child(mm).child(dd)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase events error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    root.calendarRecyclerView.adapter = EventAdapter(
                        eventsList,
                        this@CalendarFragment
                    )
                    root.calendarRecyclerView.adapter?.notifyDataSetChanged()
                    children.forEach {
                        val event = it.getValue<Event>(Event::class.java)
                        eventsList.add(event!!)
                        root.calendarRecyclerView.adapter = EventAdapter(
                            eventsList,
                            this@CalendarFragment
                        )
                        root.calendarRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel.id).child("events").child(yyyy).child(mm).child(dd)
                            .removeEventListener(this)
                    }
                    root.btn_scroll_to_events.text = "Show Events (${eventsList.size})"
                }
            })

    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }
}