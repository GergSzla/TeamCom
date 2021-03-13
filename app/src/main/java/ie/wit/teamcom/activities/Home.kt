package ie.wit.teamcom.activities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.ConversationAdapter
import ie.wit.teamcom.adapters.EventAdapter
import ie.wit.teamcom.adapters.MeetingsAdapter
import ie.wit.teamcom.fragments.*
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import ie.wit.teamcom.services.RecurringServices
import ie.wit.teamcom.services.assistant_status
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_channels_list.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.card_channel.view.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import kotlinx.android.synthetic.main.fragment_conversation.view.*
import kotlinx.android.synthetic.main.fragment_meetings.view.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import java.util.*


class Home : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var app: MainApp

    lateinit var ft: FragmentTransaction
    var user = Account()
    lateinit var eventListener: ValueEventListener
    var channel = Channel()
    lateinit var notificationManager: NotificationManager
    var reminders_list = ArrayList<Reminder>()
    var meetingList = ArrayList<Meeting>()
    var conversationList = ArrayList<Conversation>()
    var events_list = ArrayList<Event>()
    var user_survey_pref = SurveyPref()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)
        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()

        navView.setNavigationItemSelectedListener(this)

        startService(Intent(this, RecurringServices::class.java))

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        ft = supportFragmentManager.beginTransaction()

        channel = intent.getParcelableExtra("channel_key")
        app.getAllMembers(channel.id)
        getUser()
        notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        navView.getHeaderView(0).btn_change_channel.setOnClickListener {
            finish()
        }
        startService()
    }

    fun startService() {
        val serviceIntent = Intent(this, RecurringServices::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService(v: View?) {
        //TODO: enable stop/start service
        val serviceIntent = Intent(this, RecurringServices::class.java)
        stopService(serviceIntent)
    }

    fun getActiveReminders(channel_id: String) {
        reminders_list = ArrayList<Reminder>()
        app.database.child("channels").child(channel_id).child("reminders")
            .child(app.currentActiveMember.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val children = dataSnapshot.children
                    children.forEach {
                        val reminder = it.getValue<Reminder>(Reminder::class.java)
                        reminders_list.add(reminder!!)
                        app.database.child("channels").child(channel_id).child("reminders")
                            .child(app.currentActiveMember.id).removeEventListener(this)
                    }
                    checkActiveReminders()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    /**
     * Gets users survey pref
     */
    fun getSurvey(channel_id: String) {
        app.database.child("channels").child(channel_id).child("surveys")
            .child(app.currentActiveMember.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val children = dataSnapshot.children
                    children.forEach {
                        user_survey_pref = it.getValue<SurveyPref>(SurveyPref::class.java)!!
                        app.database.child("channels").child(channel_id).child("surveys")
                            .child(app.currentActiveMember.id).removeEventListener(this)
                    }
                    checkSurvey()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    /**
     * checks if survey needs to prompt
     */
    fun checkSurvey() {
        if (user_survey_pref.enabled) {
            if (app.valid_from_cal <= user_survey_pref.next_date_id) {
                create_mh_survey_dialog()
            }
        }
    }

    fun create_mh_survey_dialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.survey_prompt_dialog)
            .setTitle("Mental Well-being Assessment!")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Proceed") { dialog, _ ->
                navigateTo(SurveyFormFragment.newInstance(channel))
                dialog.dismiss()
            }.show()
    }

    fun checkActiveReminders() {

        app.generateDateID("1")
        var due_soon = 0
        //var reminders_desc = ArrayList<String>()
        var rems = ""

        reminders_list.forEach {
            if (it.rem_reminder_date_it >= app.valid_from_cal && it.rem_date_id <= app.valid_from_cal) {
                due_soon++
                rems += "\"" + it.rem_msg + "\"" + ", "
            } else if (it.rem_date_id > app.valid_from_cal) {
                it.rem_status = "Overdue"
                val childUpdates = HashMap<String, Any>()
                childUpdates["/channels/${currentChannel!!.id}/reminders/${app.currentActiveMember.id}/${it.id}/"] =
                    it
                app.database.updateChildren(childUpdates)
            }
            var start_conv = app.valid_from_cal.toString()
            var new_id =
                start_conv.replaceRange(start_conv.length - 2, start_conv.length, "00").toLong()

            if (it.rem_date_id == new_id) {
                createNotificationChannel(
                    "ie.wit.teamcom",
                    "TeamCom Reminder:",
                    "$rems is due now!"
                )
            }
        }
        if (due_soon != 0) {
            rems = rems.substring(0, rems.length - 2)
            if (!assistant_status.contains("Upcoming reminder(s)!")) {
                assistant_status += "• $due_soon Upcoming reminder(s)!"
                startService()
            }
        }
    }

    fun checkEvents24hrs() {
        if (app.reminder_due_date_id == app.valid_from_cal) {
            if (events_list.size != 0) {
                createNotificationChannel(
                    "ie.wit.teamcom",
                    "Events Today:",
                    "(${events_list.size}) Events!"
                )
            }
        }
    }
    lateinit var mem_frag: ViewMemberFragment

    fun getTasks(){
        mem_frag.get_all_projects()
        Handler().postDelayed({
            checkUpcomingTasks()
        },
            2000 // value in milliseconds
        )
    }

    fun checkUpcomingTasks(){
        if (mem_frag.due_in_24_hrs != 0){
            if (!assistant_status.contains("Upcoming task(s)!")) {
                assistant_status += "• ${mem_frag.due_in_24_hrs} Upcoming task(s)!"
                startService()
            }
        }

        var start_conv = app.valid_from_cal.toString()
        var new_id =
            start_conv.replaceRange(start_conv.length - 2, start_conv.length, "00").toLong()

        if (mem_frag.overdue.size != 0){
            mem_frag.overdue.forEach {
                if(it.task_due_date_id == new_id){
                    createNotificationChannel(
                        "ie.wit.teamcom",
                        "Task due now!",
                        it.task_msg
                    )
                }
            }
        }
    }

    var unseen_msgs = ArrayList<Message>()
    fun getConversations(channel_id: String) {
        conversationList = ArrayList<Conversation>()
        app.database.child("channels").child(channel_id).child("conversations")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    var i = 0
                    children.forEach {
                        val convo = it.getValue<Conversation>(Conversation::class.java)
                        if (convo!!.participants[i].id == app.auth.currentUser!!.uid) {
                            convo.messages.forEach { _it ->
                                if (!_it.read_by.contains(app.currentActiveMember)){
                                    unseen_msgs.add( _it)
                                }
                            }
                        }
                        i++
                        app.database.child("channels").child(currentChannel!!.id)
                            .child("conversations")
                            .removeEventListener(this)
                    }
                    checkConversations()
                }
            })
    }

    fun checkConversations() {
        if(unseen_msgs.size != 0){
            unseen_msgs.forEach {
                createNotificationChannel(
                    "ie.wit.teamcom",
                    "Message from ${it.author.firstName} ${it.author.surname}",
                    it.content
                )
            }
        }
    }

    fun getMeetings(channel_id: String) {
        meetingList = ArrayList<Meeting>()
        app.database.child("channels").child(channel_id).child("meetings")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val meeting = it.getValue<Meeting>(Meeting::class.java)
                        meetingList.add(meeting!!)
                        app.database.child("channels").child(channel_id).child("meetings")
                            .removeEventListener(this)
                    }
                    checkUpcoming()
                }
            })
    }

    fun checkUpcoming() {
        var upcoming = 0
        meetingList.forEach {
            if (app.reminder_due_date_id == app.valid_from_cal) {
                upcoming++
            }
        }
        if (upcoming != 0) {
            createNotificationChannel(
                "ie.wit.teamcom",
                "Meetings Today:",
                "(${upcoming}) Meetings! How about setting some reminders?"
            )
        }

        var start_conv = app.valid_from_cal.toString()
        var new_id =
            start_conv.replaceRange(start_conv.length - 2, start_conv.length, "00").toLong()

        meetingList.forEach {
            if (it.meeting_date_id == new_id) {
                createNotificationChannel(
                    "ie.wit.teamcom",
                    "Meeting Starting Now:",
                    it.meeting_title
                )
            }
        }
    }

    fun getEvents(channel_id: String) {
        app.generateDateID("1")
        val year = app.valid_from_String.substring(6, 10)
        val month = app.valid_from_String.substring(3, 5)    // 01 2 34 5 6789
        val day = app.valid_from_String.substring(0, 2)      // dd / mm / yyyy
        app.generate_date_reminder_id(day, month, year, "0", "0", "0")

        events_list = ArrayList<Event>()
        app.database.child("channels").child(channel_id).child("events").child(year).child(month)
            .child(day)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val event = it.getValue<Event>(Event::class.java)
                        events_list.add(event!!)
                        app.database.child("channels").child(currentChannel.id).child("events")
                            .child(year).child(month).child(day)
                            .removeEventListener(this)
                    }
                    checkEvents24hrs()
                }
            })
    }

    //updates only image attribute of user
    fun updateUserProfile(uid: String?, image: Int) {
        app.database.child("user-stats").child(uid!!).child("image")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(image)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }

    //get logged in user
    private fun getUser() {
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
                getActiveReminders(channel.id)
                getMeetings(channel.id)

                var newsFeedFragment = NewsFeedFragment.newInstance(channel)
                navigateTo(NewsFeedFragment.newInstance(channel))
                navView.getHeaderView(0).nav_header_name.text = "${user.firstName} ${user.surname}"
                navView.getHeaderView(0).nav_channel_name.text = channel.channelName

                var ref = FirebaseStorage.getInstance()
                    .getReference("channel_photos/profile/${channel.id}.jpg")
                ref.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it)
                        .resize(150, 150)
                        .transform(CropCircleTransformation())
                        .into(navView.getHeaderView(0).img_channel_image)
                }

                uidRef.removeEventListener(this)

                //image upload check
                /*if(user.image == 0) {
                    if (app.auth.currentUser?.photoUrl != null) {
                        Picasso.get().load(app.auth.currentUser?.photoUrl)
                            .resize(180, 180)
                            .transform(CropCircleTransformation())
                            .into(navView.getHeaderView(0).homeProfImage, object : Callback {
                                override fun onSuccess() {
                                    // Drawable is ready
                                    uploadImageView(app, navView.getHeaderView(0).homeProfImage)
                                    user.image = 1 //set on first login to avoid stock image re-upload
                                    updateUserProfile(app.auth.currentUser!!.uid, user.image)
                                }

                                override fun onError(e: Exception) {}
                            })
                    } else {
                        Picasso.get().load(R.mipmap.ic_avatar)
                            .resize(180, 180)
                            .transform(CropCircleTransformation())
                            .into(navView.getHeaderView(0).homeProfImage, object : Callback {
                                override fun onSuccess() {
                                    // Drawable is ready
                                    uploadImageView(app, navView.getHeaderView(0).homeProfImage)
                                    user.image = 1
                                    updateUserProfile(app.auth.currentUser!!.uid, user.image)
                                }

                                override fun onError(e: Exception) {}
                            })
                    }
                } else if (user.image == 1){ // if this isnt the users first login
                    var ref = FirebaseStorage.getInstance().getReference("photos/${app.auth.currentUser!!.uid}.jpg")
                    ref.downloadUrl.addOnSuccessListener {
                        Picasso.get().load(it)
                            .resize(180, 180)
                            .transform(CropCircleTransformation())
                            .into(navView.getHeaderView(0).homeProfImage)
                    }
                }*/
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        uidRef.addListenerForSingleValueEvent(eventListener)

    }

    fun createNotificationChannel(
        id: String, name: String,
        description: String
    ) {

        val importance = NotificationManager.IMPORTANCE_LOW
        val notific_channel = NotificationChannel(id, name, importance)

        notific_channel.description = description
        notific_channel.enableLights(true)
        notific_channel.lightColor = Color.RED
        notific_channel.enableVibration(true)
        notific_channel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager?.createNotificationChannel(notific_channel)

        val notificationID = 101

        val channelID = id


        val notification = Notification.Builder(
            this@Home,
            channelID
        )
            .setContentTitle(name)
            .setContentText(description)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .build()


        notificationManager?.notify(notificationID, notification)
    }

    fun recurring_methods() {
        getActiveReminders(channel.id)
        getSurvey(channel.id)
        getEvents(channel.id)
        getMeetings(channel.id)
        getConversations(channel.id)
        getTasks()
    }

    val h2 = Handler()
    val r2: Runnable = object : Runnable {
        override fun run() {
            recurring_methods()
            h2.postDelayed(this, 10000)
        }
    }

    override fun onResume() {
        super.onResume()
        h2.postDelayed(r2, 20000);
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            ///social
            R.id.nav_news_feed -> {
                //recurring_methods()
                navigateTo(NewsFeedFragment.newInstance(channel))
            }
            R.id.nav_conversations -> {
                //recurring_methods()
                navigateTo(ConversationFragment.newInstance(channel))
            }
            R.id.nav_meetings -> {
//                recurring_methods()
                navigateTo(MeetingsFragment.newInstance(channel))
            }


            ///Organizational
            R.id.nav_calendar -> {
//                recurring_methods()
                navigateTo(CalendarFragment.newInstance(channel))
            }

            R.id.nav_tasks -> {
//                recurring_methods()
                navigateTo(ProjectListFragment.newInstance(channel))
            }

            R.id.nav_reminders -> {
//                recurring_methods()
                navigateTo(RemindersFragment.newInstance(channel))
            }


            ///Channel
            R.id.nav_channel_settings -> {
//                recurring_methods()
                navigateTo(SettingsFragment.newInstance(channel))
            }

            R.id.nav_log -> {
//                recurring_methods()
                navigateTo(LogFragment.newInstance(channel))
            }

            R.id.nav_members -> {
//                recurring_methods()
                navigateTo(MembersFragment.newInstance(channel))
            }
            R.id.nav_support -> {
//                recurring_methods()
                navigateTo(SupportFragment.newInstance(channel))
            }
            /////////////////////////
            R.id.nav_logout -> {
                signOut()
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            navigateTo(NewsFeedFragment.newInstance(channel))

    }


    private fun signOut() {
        app.auth.signOut()
        app.googleSignInClient.signOut()
        finish()
        startActivity<LoginRegActivity>()
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

}
