package ie.wit.teamcom.activities

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ie.wit.teamcom.BuildConfig
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.Meeting
import kotlinx.android.synthetic.main.activity_zoom_meeting.*
import org.jetbrains.anko.AnkoLogger
import us.zoom.sdk.*


/**
 * https://marketplace.zoom.us/docs/sdk/native-sdks/android/build-an-app/implement-features
 */

class CreateZoomMeetingActivity : AppCompatActivity(), AnkoLogger {
    lateinit var app: MainApp
    var zoom_app_key: String = BuildConfig.ZOOM_APP_KEY
    var zoom_app_secret: String = BuildConfig.ZOOM_APP_SECRET
    var meeting = Meeting()

    private val authListener = object : ZoomSDKAuthenticationListener {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        override fun onZoomSDKLoginResult(result: Long) {
            if (result.toInt() == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(this@CreateZoomMeetingActivity)
            }
        }

        override fun onZoomIdentityExpired() = Unit
        override fun onZoomSDKLogoutResult(p0: Long) = Unit
        override fun onZoomAuthIdentityExpired() = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_meeting)
        app = application as MainApp
        auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference
        app.storage = FirebaseStorage.getInstance().reference

        meeting = intent.extras!!.getParcelable<Meeting>("meeting_key")!!


        initializeSdk(this)
        initViews()
    }

    private fun initializeSdk(context: Context) {
        val sdk = ZoomSDK.getInstance()

        val params = ZoomSDKInitParams().apply {
            appKey = zoom_app_key
            appSecret = zoom_app_secret
            domain = "zoom.us"
            enableLog = true // Optional: enable logging for debugging
        }

        val listener = object : ZoomSDKInitializeListener {
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) = Unit
            override fun onZoomAuthIdentityExpired() = Unit
        }

        sdk.initialize(context, listener, params)
    }

    private fun initViews() {
        join_button.setOnClickListener {
            join_meeting()
        }



    }

    /**
     * Join a meeting without any login/authentication with the meeting's number & password
     */
    private fun joinMeeting(context: Context, meetingNumber: String, pw: String) {
        val meetingService = ZoomSDK.getInstance().meetingService
        val options = JoinMeetingOptions()
        val params = JoinMeetingParams().apply {
            displayName = app.currentActiveMember.firstName + " " + app.currentActiveMember.surname
            meetingNo = meetingNumber
            password = pw
        }
        meetingService.joinMeetingWithParams(context, params, options)
    }

    private fun startMeeting(context: Context) {
        val zoomSdk = ZoomSDK.getInstance()
        if (zoomSdk.isLoggedIn) {
            val meetingService = zoomSdk.meetingService
            val options = StartMeetingOptions()
            meetingService.startInstantMeeting(context, options)
        }
    }

    private fun join_meeting() {
        joinMeeting(this@CreateZoomMeetingActivity, meeting.meeting_id.replace("\\s".toRegex(), ""), meeting.meeting_passcode.replace("\\s".toRegex(), ""))
    }


}