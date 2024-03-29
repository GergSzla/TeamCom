package ie.wit.teamcom.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Log
import ie.wit.teamcom.models.Role
import kotlinx.android.synthetic.main.fragment_role_create.*
import kotlinx.android.synthetic.main.fragment_role_create.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.*


class RoleCreateFragment : Fragment(), AnkoLogger {
    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel: Channel? = null

    //    var permissionCode: String = ""
    var role = Role()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        arguments?.let {
            currentChannel = it.getParcelable("channel_key")
            if (currentChannel == null) {
                currentChannel = it.getParcelable("channel_key_edit")
                role = it.getParcelable("role_key_edit")!!
            }
        }
        app.getAllMembers(currentChannel!!.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_role_create, container, false)
        activity?.title = getString(R.string.title_roles_settings)

        root.editPressed.isVisible = false

        if (role.id != "") {
            root.editPressed.isVisible = true
            root.btnCreateNewRole.isVisible = false
            root.editRoleName.setText(role.role_name)
            root.txtColorCode.setText(role.color_code)
//            root.permAdmin.isChecked = role.permission_code.elementAt(0).toString() == "1"
//            root.permViewLogs.isChecked = role.permission_code.elementAt(1).toString() == "1"
//            root.permManageRoles.isChecked = role.permission_code.elementAt(2).toString() == "1"
//            root.permViewTasks.isChecked = role.permission_code.elementAt(3).toString() == "1"
//            root.permCreateTasks.isChecked = role.permission_code.elementAt(4).toString() == "1"
//            root.permCreateInvite.isChecked = role.permission_code.elementAt(5).toString() == "1"
//            root.permCreateGroupChats.isChecked = role.permission_code.elementAt(6).toString() == "1"
//            root.permKickUser.isChecked = role.permission_code.elementAt(7).toString() == "1"
//            root.permPostOnFeed.isChecked = role.permission_code.elementAt(8).toString() == "1"
//            root.permCreateGCInvite.isChecked = role.permission_code.elementAt(9).toString() == "1"
//            root.permAssignTasksToMembers.isChecked = role.permission_code.elementAt(10).toString() == "1"
//            root.permCreateMeetings.isChecked = role.permission_code.elementAt(11).toString() == "1"
//            root.permRequestMeetings.isChecked = role.permission_code.elementAt(12).toString() == "1"
            root.permAdmin.isChecked = role.perm_admin
            root.permViewLogs.isChecked = role.perm_view_logs
            root.permManageRoles.isChecked = role.perm_manage_roles
            root.permViewTasks.isChecked = role.perm_view_tasks
            root.permCreateTasks.isChecked = role.perm_create_tasks
            root.permCreateInvite.isChecked = role.perm_create_invites
            root.permCreateGroupChats.isChecked = role.perm_create_group_chats
            root.permKickUser.isChecked = role.perm_kick_users
            root.permPostOnFeed.isChecked = role.perm_create_posts
            root.permCreateGCInvite.isChecked = role.perm_create_group_chat_invite
            root.permAssignTasksToMembers.isChecked = role.perm_assign_tasks
            root.permCreateMeetings.isChecked = role.perm_create_meetings
            root.permRequestMeetings.isChecked = role.perm_request_meetings
            root.permCreateProjects.isChecked = role.perm_create_projects
            root.permManageProjects.isChecked = role.perm_manage_projects
            root.permViewEvents.isChecked = role.perm_view_events
            root.permCreateEvents.isChecked = role.perm_create_events
            root.permCreateRoles.isChecked = role.perm_create_roles
            root.permViewRoles.isChecked = role.perm_view_roles
            root.permEditChannel.isChecked = role.perm_edit_channel
            root.permViewMembers.isChecked = role.perm_view_members
            root.permViewMembersStats.isChecked = role.perm_view_member_stats
            root.permAssignRoles.isChecked = role.perm_assign_roles
            root.permViewInvites.isChecked = role.perm_view_invites
            root.permApproveMeetings.isChecked = role.perm_approve_meetings


        }

        root.btnEditRole.setOnClickListener {
            if (root.editRoleName.text.toString() != "") {
//                permissionCode = ""
//                permissionCode += if(root.permAdmin.isChecked) "1" else "0"
//                permissionCode += if(root.permViewLogs.isChecked) "1" else "0"
//                permissionCode += if(root.permManageRoles.isChecked) "1" else "0"
//                permissionCode += if(root.permViewTasks.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateTasks.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateInvite.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateGroupChats.isChecked) "1" else "0"
//                permissionCode += if(root.permKickUser.isChecked) "1" else "0"
//                permissionCode += if(root.permPostOnFeed.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateGCInvite.isChecked) "1" else "0"
//                permissionCode += if(root.permAssignTasksToMembers.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateMeetings.isChecked) "1" else "0"
//                permissionCode += if(root.permRequestMeetings.isChecked) "1" else "0"
                role.perm_admin = root.permAdmin.isChecked
                role.perm_view_logs = root.permViewLogs.isChecked
                role.perm_manage_roles = root.permManageRoles.isChecked
                role.perm_view_tasks = root.permViewTasks.isChecked
                role.perm_create_tasks = root.permCreateTasks.isChecked
                role.perm_create_invites = root.permCreateInvite.isChecked
                role.perm_create_group_chats = root.permCreateGroupChats.isChecked
                role.perm_kick_users = root.permKickUser.isChecked
                role.perm_create_posts = root.permPostOnFeed.isChecked
                role.perm_create_group_chat_invite = root.permCreateGCInvite.isChecked
                role.perm_assign_tasks = root.permAssignTasksToMembers.isChecked
                role.perm_create_meetings = root.permCreateMeetings.isChecked
                role.perm_request_meetings = root.permRequestMeetings.isChecked
                role.perm_create_projects = root.permCreateProjects.isChecked
                role.perm_manage_projects = root.permManageProjects.isChecked
                role.perm_view_events = root.permViewEvents.isChecked
                role.perm_create_events = root.permCreateEvents.isChecked
                role.perm_create_roles = root.permCreateRoles.isChecked
                role.perm_view_roles = root.permViewRoles.isChecked
                role.perm_edit_channel = root.permEditChannel.isChecked
                role.perm_view_members = root.permViewMembers.isChecked
                role.perm_view_member_stats = root.permViewMembersStats.isChecked
                role.perm_assign_roles = root.permAssignRoles.isChecked
                role.perm_view_invites = root.permViewInvites.isChecked
                role.perm_approve_meetings = root.permApproveMeetings.isChecked

                role.role_name = editRoleName.text.toString()
//                role.permission_code = permissionCode
                role.color_code = txtColorCode.text.toString()
                writeNewRole(role)
            }
        }

        root.btnDeleteRole.setOnClickListener {
            if (!role.isDefault) {
                deleteRole(role.id)
            } else {
                Toast.makeText(activity, "Cannot delete default Admin role!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        root.btnColorPicker.setOnClickListener {
            ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose color")
                .initialColor(Color.parseColor("#a20606"))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener { selectedColor ->
                    root.txtColorCode.setText(
                        Integer.toHexString(selectedColor).replaceRange(0, 2, "")
                    )
                }
                .setPositiveButton(
                    "ok"
                ) { dialog, selectedColor, allColors -> (selectedColor) }
                .setNegativeButton(
                    "cancel"
                ) { dialog, which -> }
                .build()
                .show()
        }



        root.btnCreateNewRole.setOnClickListener {
            if (root.editRoleName.text.toString() != "") {
//                permissionCode += if(root.permAdmin.isChecked) "1" else "0"
//                permissionCode += if(root.permViewLogs.isChecked) "1" else "0"
//                permissionCode += if(root.permManageRoles.isChecked) "1" else "0"
//                permissionCode += if(root.permViewTasks.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateTasks.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateInvite.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateGroupChats.isChecked) "1" else "0"
//                permissionCode += if(root.permKickUser.isChecked) "1" else "0"
//                permissionCode += if(root.permPostOnFeed.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateGCInvite.isChecked) "1" else "0"
//                permissionCode += if(root.permAssignTasksToMembers.isChecked) "1" else "0"
//                permissionCode += if(root.permCreateMeetings.isChecked) "1" else "0"
//                permissionCode += if(root.permRequestMeetings.isChecked) "1" else "0"
                role.perm_admin = root.permAdmin.isChecked
                role.perm_view_logs = root.permViewLogs.isChecked
                role.perm_manage_roles = root.permManageRoles.isChecked
                role.perm_view_tasks = root.permViewTasks.isChecked
                role.perm_create_tasks = root.permCreateTasks.isChecked
                role.perm_create_invites = root.permCreateInvite.isChecked
                role.perm_create_group_chats = root.permCreateGroupChats.isChecked
                role.perm_kick_users = root.permKickUser.isChecked
                role.perm_create_posts = root.permPostOnFeed.isChecked
                role.perm_create_group_chat_invite = root.permCreateGCInvite.isChecked
                role.perm_assign_tasks = root.permAssignTasksToMembers.isChecked
                role.perm_create_meetings = root.permCreateMeetings.isChecked
                role.perm_request_meetings = root.permRequestMeetings.isChecked
                role.perm_create_projects = root.permCreateProjects.isChecked
                role.perm_manage_projects = root.permManageProjects.isChecked
                role.perm_view_events = root.permViewEvents.isChecked
                role.perm_create_events = root.permCreateEvents.isChecked
                role.perm_create_roles = root.permCreateRoles.isChecked
                role.perm_view_roles = root.permViewRoles.isChecked
                role.perm_edit_channel = root.permEditChannel.isChecked
                role.perm_view_members = root.permViewMembers.isChecked
                role.perm_view_member_stats = root.permViewMembersStats.isChecked
                role.perm_assign_roles = root.permAssignRoles.isChecked
                role.perm_view_invites = root.permViewInvites.isChecked
                role.perm_approve_meetings = root.permApproveMeetings.isChecked

                role.role_name = editRoleName.text.toString()
//                role.permission_code = permissionCode
                role.color_code = txtColorCode.text.toString()
                role.id = UUID.randomUUID().toString()
                writeNewRole(role)
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel!!, app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel!!, app.currentActiveMember)
    }

    private fun deleteRole(roleId: String?) {
        val uid = auth.currentUser!!.uid
        app.database.child("channels").child(currentChannel!!.id).child("roles").child(roleId!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }

    private fun writeNewRole(role: Role) {
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel!!.id}/roles/${role.id}"] = role
                    app.database.updateChildren(childUpdates)


                    app.generateDateID("1")
                    val logUpdates = HashMap<String, Any>()
                    val new_log = Log(
                        log_id = app.valid_from_cal,
                        log_triggerer = app.currentActiveMember,
                        log_date = app.dateAsString,
                        log_time = app.timeAsString,
                        log_content = "New Role [${role.role_name}] created."
                    )
                    logUpdates["/channels/${currentChannel!!.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)

                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                    navigateTo(RoleListFragment.newInstance(currentChannel!!))
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

        fun editInstance(role: Role, channel: Channel) =
            RoleCreateFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key_edit", channel)
                    putParcelable("role_key_edit", role)
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