package ie.wit.teamcom.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.activities.ChannelsListActivity
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Role
import kotlinx.android.synthetic.main.activity_login_reg.*
import kotlinx.android.synthetic.main.card_role.*
import kotlinx.android.synthetic.main.fragment_role_create.*
import kotlinx.android.synthetic.main.fragment_role_create.view.*
import kotlinx.android.synthetic.main.fragment_role_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import java.util.*


class RoleCreateFragment : Fragment(), AnkoLogger {
    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel: Channel? = null
    var permissionCode: String =""
    var role = Role()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        arguments?.let {
            currentChannel = it.getParcelable("channel_key")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_role_create, container, false)
        activity?.title = getString(R.string.title_roles_settings)

        root.btnCreateNewRole.setOnClickListener {
            if(root.editRoleName.text.toString() != ""){
                permissionCode += if(root.permAdmin.isChecked) "1" else "0"
                permissionCode += if(root.permViewLogs.isChecked) "1" else "0"
                permissionCode += if(root.permManageRoles.isChecked) "1" else "0"
                permissionCode += if(root.permViewTasks.isChecked) "1" else "0"
                permissionCode += if(root.permCreateTasks.isChecked) "1" else "0"
                permissionCode += if(root.permCreateInvite.isChecked) "1" else "0"
                permissionCode += if(root.permCreateGroupChats.isChecked) "1" else "0"
                permissionCode += if(root.permKickUser.isChecked) "1" else "0"
                permissionCode += if(root.permPostOnFeed.isChecked) "1" else "0"
                permissionCode += if(root.permCreateGCInvite.isChecked) "1" else "0"
                permissionCode += if(root.permAssignTasksToMembers.isChecked) "1" else "0"
                permissionCode += if(root.permCreateMeetings.isChecked) "1" else "0"
                permissionCode += if(root.permRequestMeetings.isChecked) "1" else "0"
                permissionCode += if(root.permViewDeptLog.isChecked) "1" else "0"

                role.role_name = editRoleName.text.toString()
                role.permission_code = permissionCode
                role.color_code = txtColorCode.text.toString()
                role.id = UUID.randomUUID().toString()
                writeNewRole(role)
            }
        }

        return root
    }

    private fun writeNewRole(role : Role) {
        val uid = app.auth.currentUser!!.uid


        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel!!.id}/roles/${role.role_name}"] = role
                    app.database.updateChildren(childUpdates)

                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                    //startActivity(intentFor<ChannelsListActivity>().putExtra("user_key", user))
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            RoleCreateFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }
}