package ie.wit.teamcom.activities

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_change_password.btnBackToLogin
import kotlinx.android.synthetic.main.activity_change_password.btnSendReset
import kotlinx.android.synthetic.main.activity_change_password.txtStatus
import org.jetbrains.anko.AnkoLogger

class ChangePasswordActivity : AppCompatActivity(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var loader: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        app = application as MainApp

        auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference


        txtPassword_new.isVisible = false
        btnSendReset.isVisible = false
        btnCheck.setOnClickListener {
            if (validateCheckForm()) {
                if (txtPassword_1.text.toString() == txtPassword_2.text.toString()) {
                    reauthenticate_user(
                        txt_check_pw_email.text.toString(),
                        txtPassword_1.text.toString()
                    )
                }
            }
        }

        btnSendReset.setOnClickListener {
            if (validateForm()) {
                reset_password(txtPassword_new.text.toString())
            }
        }

        btnBackToLogin.setOnClickListener {
            finish()
        }


    }

    private fun reauthenticate_user(email: String, password: String) {
        val credential = EmailAuthProvider
            .getCredential(email, password)

        auth.currentUser!!.reauthenticate(credential)
            .addOnCompleteListener {
                Toast.makeText(this, "You have been re-authenticated, ", Toast.LENGTH_LONG).show()

                txtPassword_new.isVisible = true
                btnSendReset.isVisible = true

                txt_check_pw_email.isVisible = false
                txtPassword_1.isVisible = false
                txtPassword_2.isVisible = false
                btnCheck.isVisible = false
            }
    }

    private fun validateCheckForm(): Boolean {
        var valid = true

        val email = txt_check_pw_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            txt_check_pw_email.error = "Email Required."
            valid = false
        }
        val pw_1 = txtPassword_1.text.toString()
        if (TextUtils.isEmpty(pw_1)) {
            txtPassword_1.error = "Password Required."
            valid = false
        }
        val pw_2 = txtPassword_2.text.toString()
        if (TextUtils.isEmpty(pw_2)) {
            txtPassword_2.error = "Password Required."
            valid = false
        }

        return valid
    }

    private fun validateForm(): Boolean {
        var valid = true

        val new_pw = txtPassword_new.text.toString()
        if (TextUtils.isEmpty(new_pw)) {
            txtPassword_new.error = "New Password Required."
            valid = false
        }
        return valid
    }

    private fun reset_password(new_pw: String) {

        auth.currentUser!!.updatePassword(new_pw)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Your password has successfully been changed!",Toast.LENGTH_LONG).show()
                        "Your password has successfully been changed!".also { txtStatus.text = it }
                        txtStatus.setTextColor(Color.parseColor("#00A817"))
                    } else {
                        "An error has occurred!".also { txtStatus.text = it }
                        txtStatus.setTextColor(Color.parseColor("#A30000"))
                    }
                }
    }
}