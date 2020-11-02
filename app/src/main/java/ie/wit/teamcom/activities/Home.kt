package ie.wit.teamcom.activities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.fragments.*
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Reminder
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.startActivity
import java.util.ArrayList
import java.util.HashMap


class Home : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var app: MainApp

    lateinit var ft: FragmentTransaction
    var user = Account()
    lateinit var eventListener : ValueEventListener
    var channel = Channel()
    var reminderList = ArrayList<Reminder>()
    var num_reminders = 0
    lateinit var notificationManager : NotificationManager
    var reminders_list = ArrayList<Reminder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)
        app = application as MainApp
        app.auth = FirebaseAuth.getInstance()


        navView.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //navView.getHeaderView(0).nav_header_email.text = app.auth.currentUser?.email

        ft = supportFragmentManager.beginTransaction()

        channel = intent.getParcelableExtra("channel_key")
        app.getAllMembers(channel.id)
        getUser()
        notificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager

    }
    fun getActiveReminders(channel_id: String){
        app.database.child("channels").child(channel_id).child("reminders").child(app.currentActiveMember.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val children = dataSnapshot.children
                    children.forEach {
                        val reminder = it.getValue<Reminder>(Reminder::class.java)
                        reminders_list.add(reminder!!)
                        app.database.child("channels").child(currentChannel!!.id).child("reminders")
                            .child(app.currentActiveMember.id).removeEventListener(this)
                    }
                    checkActiveReminders()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    fun checkActiveReminders(){
        app.generateDateID("1")
        var due_soon = 0
        //var reminders_desc = ArrayList<String>()
        var rems = ""

        reminders_list.forEach {
            if (it.rem_reminder_date_it >= app.valid_from_cal && it.rem_date_id <= app.valid_from_cal){
                due_soon ++
                rems +="\""+it.rem_msg+ "\""+", "
            } else if (it.rem_date_id > app.valid_from_cal){
                it.rem_status = "Overdue"
                val childUpdates = HashMap<String, Any>()
                childUpdates["/channels/${currentChannel!!.id}/reminders/${app.currentActiveMember.id}/${it.id}/"] = it
                app.database.updateChildren(childUpdates)
            }
        }
        if (due_soon != 0){
            rems = rems.substring(0, rems.length - 2)
            createNotificationChannel("ie.wit.teamcom",
                "${due_soon} Upcoming reminder(s)!",
                "${rems} are due within 24 hours!")
        }

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
    private fun getUser(){
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

                var newsFeedFragment = NewsFeedFragment.newInstance(channel)
                navigateTo(NewsFeedFragment.newInstance(channel))
                navView.getHeaderView(0).nav_header_name.text = "${user.firstName} ${user.surname}"
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

    fun createNotificationChannel(id: String, name: String,
                                  description: String) {

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


        val notification = Notification.Builder(this@Home,
            channelID)
            .setContentTitle(name)
            .setContentText(description)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .build()


        notificationManager?.notify(notificationID, notification)
    }

    fun recurring_methods(){
        getActiveReminders(channel.id)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            ///social
            R.id.nav_news_feed -> {
                recurring_methods()
                navigateTo(NewsFeedFragment.newInstance(channel))
            }
            R.id.nav_conversations -> {
                recurring_methods()
                navigateTo(ConversationFragment.newInstance(channel))
            }
            R.id.nav_meetings -> {
                recurring_methods()
                navigateTo(MeetingsFragment.newInstance())
            }


            ///Organizational
            R.id.nav_calendar -> {
                recurring_methods()
                navigateTo(CalendarFragment.newInstance(channel))
            }

            R.id.nav_tasks -> {
                recurring_methods()
                navigateTo(TasksFragment.newInstance())
            }

            R.id.nav_reminders -> {
                recurring_methods()
                navigateTo(RemindersFragment.newInstance(channel))
            }


            ///Channel
            R.id.nav_channel_settings -> {
                recurring_methods()
                navigateTo(SettingsFragment.newInstance(channel))
            }

            R.id.nav_log -> {
                recurring_methods()
                navigateTo(LogFragment.newInstance(channel))
            }

            R.id.nav_members -> {
                recurring_methods()
                navigateTo(MembersFragment.newInstance(channel))
            }
            R.id.nav_support -> {
                recurring_methods()
                navigateTo(SupportFragment.newInstance())
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





    private fun signOut(){
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
