package ie.wit.teamcom.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import ie.wit.teamcom.R
import ie.wit.teamcom.activities.Home
import ie.wit.teamcom.main.MainApp.Companion.CHANNEL_ID

var assistant_status: String = ""
var assistant_notification: String = ""

class RecurringServices : Service() {

    override fun onCreate() {
        super.onCreate()


    }



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        assistant_notification = if (assistant_status == ""){
            "Nothing's Happening Right Now! :)"
        } else {
            assistant_status
        }

        val notificationIntent = Intent(this, Home::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Personal Assistant:")
            .setContentText(assistant_notification)
            .setSmallIcon(R.drawable.logo2)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

//class RecurringServices : Service() {
//    lateinit var app: MainApp
//
//    lateinit var notificationManager: NotificationManager
//    var reminders_list = ArrayList<Reminder>()
//    var meetings_list = ArrayList<Meeting>()
//
//    override fun onBind(p0: Intent?): IBinder? {
//        return null
//    }
//    override fun onCreate() {
//        app = application as MainApp
//    }
//
//    override fun onDestroy() {
//        // clean up your service logic here
//    }
//
//    @TargetApi(Build.VERSION_CODES.P)
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        checkActiveReminders()
//        checkFinishedMeetings()
//
//        return START_STICKY
//    }
//
//    fun checkFinishedMeetings(){
//        app.generateDateID("1")
//
//        meetings_list.forEach {
//            if (it.meeting_date_end_id >= app.valid_from_cal) {
//                app.database.child("channels").child(currentChannel.id).child("meetings")
//                    .addListenerForSingleValueEvent(
//                        object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                snapshot.ref.removeValue()
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                            }
//                        })
//            }
//        }
//    }
//
//    fun checkActiveReminders() {
//        app.generateDateID("1")
//        var due_soon = 0
//        //var reminders_desc = ArrayList<String>()
//        var rems = ""
//
//        reminders_list.forEach {
//            if (it.rem_reminder_date_it >= app.valid_from_cal && it.rem_date_id <= app.valid_from_cal) {
//                due_soon++
//                rems += "\"" + it.rem_msg + "\"" + ", "
//            } else if (it.rem_date_id > app.valid_from_cal) {
//                it.rem_status = "Overdue"
//                val childUpdates = HashMap<String, Any>()
//                childUpdates["/channels/${currentChannel!!.id}/reminders/${app.currentActiveMember.id}/${it.id}/"] =
//                    it
//                app.database.updateChildren(childUpdates)
//            }
//        }
//        if (due_soon != 0) {
//            rems = rems.substring(0, rems.length - 2)
//            createNotificationChannel(
//                "ie.wit.teamcom",
//                "${due_soon} Upcoming reminder(s)!",
//                "${rems} are due within 24 hours!"
//            )
//        }
//    }
//
//    fun createNotificationChannel(
//        id: String, name: String,
//        description: String
//    ) {
//
//        val importance = NotificationManager.IMPORTANCE_LOW
//        val notific_channel = NotificationChannel(id, name, importance)
//
//        notific_channel.description = description
//        notific_channel.enableLights(true)
//        notific_channel.lightColor = Color.RED
//        notific_channel.enableVibration(true)
//        notific_channel.vibrationPattern =
//            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//        notificationManager?.createNotificationChannel(notific_channel)
//
//        val notificationID = 101
//
//        val channelID = id
//
//
//        val notification = Notification.Builder(
//            this@RecurringServices,
//            channelID
//        )
//            .setContentTitle(name)
//            .setContentText(description)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setChannelId(channelID)
//            .build()
//
//
//        notificationManager?.notify(notificationID, notification)
//    }
//
//}