package ie.wit.teamcom.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.Account
import kotlinx.android.synthetic.main.activity_login_reg.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import java.util.*

class LoginRegActivity : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var eventListener : ValueEventListener
    lateinit var loader : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_reg)

        app = application as MainApp

        auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        loader = createLoader(this)

        txtFirstName.isVisible = false
        txtSurname.isVisible = false
        btnShowLogin.isVisible = false
        btnRegister.isVisible = false

        btnShowReg.setOnClickListener {
            txtFirstName.isVisible = true
            txtSurname.isVisible = true
            btnShowLogin.isVisible = true
            btnRegister.isVisible = true
            btnShowReg.isVisible = false
            btnLogin.isVisible = false
            btnGoogleLogin.isVisible = false
        }

        btnShowLogin.setOnClickListener {
            txtFirstName.isVisible = false
            txtSurname.isVisible = false
            btnShowLogin.isVisible = false
            btnRegister.isVisible = false
            btnShowReg.isVisible = true
            btnLogin.isVisible = true
            btnGoogleLogin.isVisible = true
        }


        btnRegister.setOnClickListener{
            showLoader(loader, "Loading . . .","Validating . . .")
            validateForm(true)
            hideLoader(loader)
            if(!(txtEmail.text.toString() == "" ||
                        txtFirstName.text.toString() == "" ||
                        txtSurname.text.toString() == "" ||
                        txtPassword.text.toString() == "")){

                    createAccount(txtEmail.text.toString(),txtPassword.text.toString())

            }

        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        app.googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogleLogin.setOnClickListener {
            googleSignIn()
        }

        btnLogin.setOnClickListener {
            validateForm(false)
            if(!(txtEmail.text.toString() == "" || txtPassword.text.toString() == "")){
                auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtPassword.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            app.database = FirebaseDatabase.getInstance().reference
//                            startActivity(intentFor<ChannelsListActivity>().putExtra("user_key",user))
                            getUser(false)
                        } else {
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                toast("Email and Password fields are required to login!")
            }
        }
        check_auto_login()
    }

    private fun check_auto_login(){
        if(auth.currentUser !== null){
            getUser(true)
        }
    }


    private fun validateForm(register: Boolean): Boolean {
        var valid = true

        if (register){

            val fname = txtFirstName.text.toString()
            if (TextUtils.isEmpty(fname)) {
                txtFirstName.error = "First Name Required."
                valid = false
            } else {
                txtFirstName.error = null
            }

            val sname = txtSurname.text.toString()
            if (TextUtils.isEmpty(sname)) {
                txtSurname.error = "Surname/Second Name Required."
                valid = false
            } else {
                txtSurname.error = null
            }

            val email = txtEmail.text.toString()
            if (TextUtils.isEmpty(email)) {
                txtEmail.error = "Email Required."
                valid = false
            } else {
                txtEmail.error = null
            }

            val password = txtPassword.text.toString()
            if (TextUtils.isEmpty(password)) {
                txtPassword.error = "Password Required."
                valid = false
            } else {
                txtPassword.error = null
            }

        } else {
            val email = txtEmail.text.toString()
            if (TextUtils.isEmpty(email)) {
                txtEmail.error = "Email Required."
                valid = false
            } else {
                txtEmail.error = null
            }

            val password = txtPassword.text.toString()
            if (TextUtils.isEmpty(password)) {
                txtPassword.error = "Password Required."
                valid = false
            } else {
                txtPassword.error = null
            }

        }
        return valid
    }


    companion object {
        private const val TAG = "EmailPassword"
        private const val RC_SIGN_IN = 9001
    }

    private fun createAccount(email: String, password: String) {
        showLoader(loader, "Loading . . .", "Registering User . . .")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    app.database = FirebaseDatabase.getInstance().reference
                    writeNewUserStats(Account(id = auth.currentUser!!.uid, email = auth.currentUser!!.email.toString(), firstName = txtFirstName.text.toString(),
                        surname = txtSurname.text.toString(), login_used = "firebaseAuth"))
                    hideLoader(loader)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    hideLoader(loader)
                }
            } }

    fun writeNewUserStats(user: Account) {
        showLoader(loader, "Loading . . .", "Adding User to Firebase")

        val uid = auth.currentUser!!.uid
        app.database.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChild(uid)){
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/users/$uid"] = user
                        app.database.updateChildren(childUpdates)
                        hideLoader(loader)
                    }
                    getUser(false)
                    app.database.child("users")
                        .removeEventListener(this)
                }
            })
    }

    var user = Account()
    fun getUser(v : Boolean){
        showLoader(loader, "Loading . . .", "Loading User Data . . . ")
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
                user.auto_login = dataSnapshot.child("auto_login").value.toString().toBoolean()

                uidRef.removeEventListener(this)
                hideLoader(loader)
                if (!v){
                    startActivity(intentFor<ChannelsListActivity>().putExtra("user_key",user))
                } else if (v){
                    if (user.auto_login){
                        if (user.login_used == "google"){
                            googleSignIn()
                        } else if (user.login_used == "firebaseAuth"){
                            startActivity(intentFor<ChannelsListActivity>().putExtra("user_key",user))
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        uidRef.addListenerForSingleValueEvent(eventListener)

    }

    private fun googleSignIn() {
        val signInIntent = app.googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        showLoader(loader, "Loading . . .", "Logging In with Google...")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser!!
                    val fname = user.displayName!!.substringBefore(" ")
                    val sname = user.displayName!!.substringAfter(" ")
                    writeNewUserStats(
                        Account(id = auth.currentUser!!.uid, email = auth.currentUser.email.toString(), firstName = fname,
                        surname = sname, image = 0, login_used = "google")
                    )
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
                hideLoader(loader)
            }
    }

    override fun onBackPressed() {
        System.exit(0)
    }

}