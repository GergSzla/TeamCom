package ie.wit.teamcom.activities

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
import com.google.firebase.storage.FirebaseStorage
import ie.wit.teamcom.R
import ie.wit.teamcom.fragments.*
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity

class Home : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var app: MainApp

    lateinit var ft: FragmentTransaction
    var user = Account()
    lateinit var eventListener : ValueEventListener
    var channel = Channel()


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
        getUser()
        channel = intent.getParcelableExtra("channel_key")

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
                user.loginUsed = dataSnapshot.child("loginUsed").value.toString()

                var newsFeedFragment = NewsFeedFragment.newInstance(user)
                navigateTo(NewsFeedFragment.newInstance(user))
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            ///social
            R.id.nav_news_feed -> {
                navigateTo(NewsFeedFragment.newInstance(user))
            }
            R.id.nav_conversations -> {
                navigateTo(ConversationFragment.newInstance())
            }
            R.id.nav_meetings -> {
                navigateTo(MeetingsFragment.newInstance())
            }


            ///Organizational
            R.id.nav_calendar -> {
                navigateTo(CalendarFragment.newInstance())
            }

            R.id.nav_tasks -> {
                navigateTo(TasksFragment.newInstance())
            }

            R.id.nav_reminders -> {
                navigateTo(RemindersFragment.newInstance())
            }


            ///Channel
            R.id.nav_channel_settings -> {
                navigateTo(SettingsFragment.newInstance(channel))
            }

            R.id.nav_log -> {
                navigateTo(LogFragment.newInstance())
            }

            R.id.nav_members -> {
                navigateTo(MembersFragment.newInstance())
            }
            R.id.nav_support -> {
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
            navigateTo(NewsFeedFragment.newInstance(user))

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
