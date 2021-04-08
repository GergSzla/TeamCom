package ie.wit.teamcom.activities

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.jetbrains.anko.AnkoLogger

class ForgotPasswordActivity : AppCompatActivity(), AnkoLogger {
    lateinit var app: MainApp
    lateinit var loader: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        app = application as MainApp

        auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        btnSendReset.setOnClickListener{
            if(validateForm()){
                send_reset(txt_forgot_pw_email.text.toString())
            }
        }

        btnBackToLogin.setOnClickListener{
            finish()
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = txt_forgot_pw_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            txt_forgot_pw_email.error = "Email Required."
            valid = false
        }
        return valid
    }

    private fun send_reset(email : String){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Reset Password Email Was Sent To $email",Toast.LENGTH_LONG).show()
                    "Password reset email has been successfully sent to $email!".also { txtStatus.text = it }
                    txtStatus.setTextColor(Color.parseColor("#00A817"))
                } else {
                    "An error has occurred! Please check the email was entered correctly and try again!".also { txtStatus.text = it }
                    txtStatus.setTextColor(Color.parseColor("#A30000"))
                }
            }
    }

    override fun onBackPressed() {
        finish()
    }

}