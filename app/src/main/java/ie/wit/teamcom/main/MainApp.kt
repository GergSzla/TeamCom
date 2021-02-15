package ie.wit.teamcom.main

import android.app.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ie.wit.teamcom.fragments.currentChannel
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Member
import ie.wit.teamcom.models.Reminder
import us.zoom.sdk.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainApp : Application() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var storage: StorageReference
    lateinit var userImage: Uri
    var currentActiveMember: Member = Member()
    var membersList = ArrayList<Member>()
    var reminders_list = ArrayList<Reminder>()

    var valid_from_cal: Long = 0
    var valid_to_cal: Long = 0
    var auto_delete_cal: Long = 0
    var dateAsString = ""
    var rem_dateAsString = ""
    var rem_timeAsString = ""
    var timeAsString = ""
    var reminder_due_date_id: Long = 0
    var valid_to_String = ""
    var valid_from_String = ""
    var user = Account()
    lateinit var eventListener: ValueEventListener
    lateinit var notificationManager: NotificationManager
    var user_is_online: Boolean = false

    override fun onCreate() {
        super.onCreate()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference

        notificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

    }

    fun isActivityVisible(): Boolean {
        return user_is_online
    }

    fun copy_invitation(inv_code: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copy Text", inv_code)

        clipboard.setPrimaryClip(clip)
    }

    fun activityResumed(channel: Channel, current_user: Member) {
        user_is_online = true
        database.child("channels").child(channel.id).child(current_user.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${channel.id}/members/${current_user.id}/online"] =
                        user_is_online

                    database.updateChildren(childUpdates)
                    database.child("channels").child(channel.id).child(current_user.id)
                        .removeEventListener(this)
                }
            })
    }

    fun activityPaused(channel: Channel, current_user: Member) {
        user_is_online = false
        database.child("channels").child(channel.id).child(current_user.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${channel.id}/members/${current_user.id}/online"] =
                        user_is_online

                    database.updateChildren(childUpdates)
                    database.child("channels").child(channel.id).child(current_user.id)
                        .removeEventListener(this)
                }
            })
    }

    fun createNotificationChannel(
        id: String, name: String,
        description: String
    ) {

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)

        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager?.createNotificationChannel(channel)

        val notificationID = 101

        val channelID = id

        val notification = Notification.Builder(
            this@MainApp,
            channelID
        )
            .setContentTitle(name)
            .setContentText(description)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .setActions()
            .build()

        notificationManager?.notify(notificationID, notification)
    }


    fun getAllMembers(channel_id: String) {
        membersList = ArrayList<Member>()
        database.child("channels").child(channel_id).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val member = it.getValue<Member>(Member::class.java)
                        membersList.add(member!!)

                        database.child("channels").child(channel_id).child("members")
                            .removeEventListener(this)
                    }
                    getCurrentActiveMember(channel_id)
                }
            })
    }

    fun getActiveReminders(channel_id: String) {
        database.child("channels").child(channel_id).child("reminders")
            .child(currentActiveMember.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val children = dataSnapshot.children
                    children.forEach {
                        val reminder = it.getValue<Reminder>(Reminder::class.java)
                        reminders_list.add(reminder!!)
                        database.child("channels").child(channel_id).child("reminders")
                            .child(currentActiveMember.id).removeEventListener(this)
                    }
                    checkActiveReminders()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    fun checkActiveReminders() {
        generateDateID("1")
        var due_soon = 0
        //var reminders_desc = ArrayList<String>()
        var rems = ""

        reminders_list.forEach {
            if (it.rem_reminder_date_it >= valid_from_cal && it.rem_date_id <= valid_from_cal) {
                due_soon++
                rems += "\"" + it.rem_msg + "\"" + ", "
            }
        }
        if (due_soon != 0) {
            rems = rems.substring(0, rems.length - 2)
            createNotificationChannel(
                "ie.wit.teamcom",
                "${due_soon} Upcoming reminder(s)!",
                "${rems} are due within 24 hours!"
            )
        }

    }

    fun getCurrentActiveMember(channel_id: String) {
        membersList.forEach {
            if (it.id == auth.currentUser!!.uid) {
                currentActiveMember = it
            }
        }
    }

    fun generateDateID(hrs: String) {
        /*
        GETS VALID FROM
         */
        var currentEndDateTime = LocalDateTime.now()
        var year = Calendar.getInstance().get(Calendar.YEAR).toString()
        var month = ""
        month = if (Calendar.getInstance().get(Calendar.MONTH) + 1 < 10) {
            "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1).toString()
        } else {
            (Calendar.getInstance().get(Calendar.MONTH) + 1).toString()
        }

        var day = ""
        day = if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10) {
            "0" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).toString()
        } else {
            (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).toString()
        }
        var hour = currentEndDateTime.format(DateTimeFormatter.ofPattern("HH")).toString()
        var minutes = currentEndDateTime.format(DateTimeFormatter.ofPattern("mm")).toString()
        var seconds = currentEndDateTime.format(DateTimeFormatter.ofPattern("ss")).toString()

        /*
        NOW GET FOR VALID TO (+HRS VALID)
         */
        valid_from_String =
            day + "/" + month + "/" + year + ", " + hour + ":" + minutes + ":" + seconds

        var dateId = year + month + day + hour + minutes + seconds

        var cal = LocalDateTime.now().plusHours(hrs.toLong())

        var year1 = cal.year.toString()
        var month1 = ""
        month1 = if (cal.monthValue < 10) {
            "0" + (cal.monthValue).toString()
        } else {
            (cal.monthValue).toString()
        }

        var day1 = ""
        day1 = if (cal.dayOfMonth < 10) {
            "0" + (cal.dayOfMonth).toString()
        } else {
            (cal.dayOfMonth).toString()
        }

        var hour1 = ""
        hour1 = if (cal.hour < 10) {
            "0" + (cal.hour).toString()
        } else {
            (cal.hour).toString()
        }

        var minutes1 = ""
        minutes1 = if (cal.minute < 10) {
            "0" + (cal.minute).toString()
        } else {
            (cal.minute).toString()
        }

        var seconds1 = ""
        seconds1 = if (cal.second < 10) {
            "0" + (cal.second).toString()
        } else {
            (cal.second).toString()
        }

        valid_to_String =
            day1 + "/" + month1 + "/" + year1 + ", " + hour1 + ":" + minutes1 + ":" + seconds1
        var dateId_end = year1 + month1 + day1 + hour1 + minutes1 + seconds1

        /*
        NOW GET AUTO DELETION
         */

        var cal1 = LocalDateTime.now().plusHours(hrs.toLong() + 36)

        var year2 = cal1.year.toString()
        var month2 = ""
        month2 = if (cal1.monthValue + 1 < 10) {
            "0" + (cal1.monthValue + 1).toString()
        } else {
            (cal1.monthValue + 1).toString()
        }

        var day2 = ""
        day2 = if (cal1.dayOfMonth < 10) {
            "0" + (cal1.dayOfMonth).toString()
        } else {
            (cal1.dayOfMonth).toString()
        }

        var hour2 = ""
        hour2 = if (cal1.hour < 10) {
            "0" + (cal1.hour).toString()
        } else {
            (cal1.hour).toString()
        }

        var minutes2 = ""
        minutes2 = if (cal1.minute < 10) {
            "0" + (cal1.minute).toString()
        } else {
            (cal1.minute).toString()
        }

        var seconds2 = ""
        seconds2 = if (cal1.second < 10) {
            "0" + (cal1.second).toString()
        } else {
            (cal1.second).toString()
        }

        var dateId_delete = year2 + month2 + day2 + hour2 + minutes2 + seconds2


        auto_delete_cal = 100000000000000 - dateId_delete.toLong()
        valid_to_cal = 100000000000000 - dateId_end.toLong()
        valid_from_cal = 100000000000000 - dateId.toLong()
        dateAsString = "$day/$month/$year"
        timeAsString = "$hour:$minutes"
    }


    fun generate_date_reminder_id(
        dd: String,
        m: String,
        yy: String,
        hh: String,
        mm: String,
        ss: String
    ) {
        var year = yy
        var month = ""
        month = if (m.toInt() < 10) {
            "0" + (m.toInt()).toString()
        } else {
            (m.toInt()).toString()
        }

        var day = ""
        day = if (dd.toInt() < 10) {
            "0" + (dd.toInt()).toString()
        } else {
            (dd.toInt()).toString()
        }

        var hour = ""
        hour = if (hh.toInt() < 10) {
            "0" + (hh.toInt()).toString()
        } else {
            (hh.toInt()).toString()
        }

        var minutes = ""
        minutes = if (mm.toInt() < 10) {
            "0" + (mm.toInt()).toString()
        } else {
            (mm.toInt()).toString()
        }

        var seconds = ""
        seconds = if (ss.toInt() < 10) {
            "0" + (ss.toInt()).toString()
        } else {
            (ss.toInt()).toString()
        }

        var date = year + month + day + hour + minutes + seconds
        rem_dateAsString = "${day}/${month}/${year}"
        rem_timeAsString = "${hour}:${minutes}"
        reminder_due_date_id = 100000000000000 - date.toLong()
    }

    fun getUser() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("users").child(uid)
        eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user.email = dataSnapshot.child("email").value.toString()
                user.firstName = dataSnapshot.child("firstName").value.toString()
                user.surname = dataSnapshot.child("surname").value.toString()
                user.id = dataSnapshot.child("id").value.toString()
                user.image = dataSnapshot.child("image").value.toString().toInt()
                user.login_used = dataSnapshot.child("login_used").value.toString()

                uidRef.removeEventListener(this)

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        uidRef.addListenerForSingleValueEvent(eventListener)

    }
}