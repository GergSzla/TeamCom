package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.*

class CalendarFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var root: View

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

        var day = ""
        var month = ""
        var cal = Calendar.getInstance()
        val dateDialog = root.calPickDate

        dateDialog.minDate = Calendar.getInstance().timeInMillis

        var today = "" //TODO: GET DATE ID RANGE? / SAVE DATE AS DD-MM-YYYY (STRING) TO DB

        //TODO: CONVERT TODAY'S DATE TO DD-MM-YYYY FORMAT
        getEventsForDate(today)

        dateDialog.setOnDateChangedListener { datePicker, yyyy, mm, dd ->

            day = if(dd < 10){
                "0$dd"
            } else {
                "$dd"
            }

            month = if(mm < 10){
                "0${mm + 1}"
            } else {
                "${mm + 1}"
            }

            var selected_date = "$day-$month-$yyyy"
            getEventsForDate(selected_date)
            //TODO: CONVERT DATEPICKER VALUE TO DD-MM-YYYY
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel,app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel,app.currentActiveMember)
    }

    fun getEventsForDate(date : String){
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