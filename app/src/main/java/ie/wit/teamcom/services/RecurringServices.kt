package ie.wit.teamcom.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
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
