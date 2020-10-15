package ie.wit.teamcom.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import kotlinx.android.synthetic.main.activity_login_reg.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import java.util.*

class LoginRegActivity : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_reg)

        app = application as MainApp

        app.auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference


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
            //val AccountList = app.users.getAllAccounts() as ArrayList<Account>

            //validateForm()

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

        btnSignOut.setOnClickListener {
            app.auth.signOut()
            app.googleSignInClient.signOut()
            finish()
        }
        btnLogin.setOnClickListener {
            //validateForm()
            if(!(txtEmail.text.toString() == "" || txtPassword.text.toString() == "")){
                app.auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtPassword.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = app.auth.currentUser
                            app.database = FirebaseDatabase.getInstance().reference
                            startActivity(intentFor<ChannelsListActivity>())
                        } else {
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                toast("Email and Password fields are required to login!")
            }
        }
    }



    companion object {
        private const val TAG = "EmailPassword"
        private const val RC_SIGN_IN = 9001
    }

    private fun createAccount(email: String, password: String) {
        /*if (!validateForm()) {
            return
        }*/
        app.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = app.auth.currentUser
                    app.database = FirebaseDatabase.getInstance().reference
                    writeNewUserStats(Account(id = UUID.randomUUID().toString(), email = app.auth.currentUser!!.email.toString(), firstName = txtFirstName.text.toString(),
                        surname = txtSurname.text.toString(), loginUsed = "firebaseAuth"))

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            } }

    fun writeNewUserStats(user: Account) {
        val uid = app.auth.currentUser!!.uid


        app.database.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChild(uid)){
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/users/$uid"] = user
                        app.database.updateChildren(childUpdates)
                    }
                    app.database.child("user-stats")
                        .removeEventListener(this)
                    startActivity(intentFor<ChannelsListActivity>().putExtra("user_key",user))
                }
            })
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
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        app.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = app.auth.currentUser!!
                    val fname = user.displayName!!.substringBefore(" ")
                    val sname = user.displayName!!.substringAfter(" ")
                    writeNewUserStats(Account(id = UUID.randomUUID().toString(), email = app.auth.currentUser!!.email.toString(), firstName = fname,
                        surname = sname, image = 0, loginUsed = "google"))
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    override fun onBackPressed() {
        System.exit(0)
    }

}