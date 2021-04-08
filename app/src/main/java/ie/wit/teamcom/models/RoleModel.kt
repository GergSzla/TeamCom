package ie.wit.teamcom.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Role (
    var id:String = "" /*UUID*/,
    var role_name:String = "",
//    var permission_code: String = "",
    var perm_admin: Boolean = false,
    var perm_edit_channel: Boolean = false,
    var perm_view_logs: Boolean = false,
    var perm_manage_roles: Boolean = false,
    var perm_create_roles: Boolean = false,
    var perm_view_roles: Boolean = false,
    var perm_assign_roles: Boolean = false,
    var perm_view_tasks: Boolean = false,
    var perm_create_tasks: Boolean = false,
    var perm_create_invites: Boolean = false,
    var perm_view_invites: Boolean = false,
    var perm_create_group_chats: Boolean = false,
    var perm_kick_users: Boolean = false,
    var perm_create_group_chat_invite: Boolean = false,
    var perm_create_posts: Boolean = false,
    var perm_assign_tasks: Boolean = false,
    var perm_create_meetings: Boolean = false,
    var perm_request_meetings: Boolean = false,
    var perm_approve_meetings: Boolean = false,
    var perm_create_projects: Boolean = false,
    var perm_manage_projects: Boolean = false,
    var perm_create_events: Boolean = false,
    var perm_view_events: Boolean = false,
    var perm_view_members: Boolean = false,
    var perm_view_member_stats: Boolean = false,
    var color_code: String ="#000000",
    var isDefault: Boolean = false
) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "role_name" to role_name,
//            "permission_code" to permission_code,
            "perm_admin" to perm_admin,
            "perm_view_logs" to perm_view_logs,
            "perm_manage_roles" to perm_manage_roles,
            "perm_assign_roles" to perm_assign_roles,
            "perm_view_tasks" to perm_view_tasks,
            "perm_create_tasks" to perm_create_tasks,
            "perm_create_invites" to perm_create_invites,
            "perm_view_invites" to perm_view_invites,
            "perm_create_group_chats" to perm_create_group_chats,
            "perm_kick_users" to perm_kick_users,
            "perm_create_group_chat_invite" to perm_create_group_chat_invite,
            "perm_create_posts" to perm_create_posts,
            "perm_assign_tasks" to perm_assign_tasks,
            "perm_create_meetings" to perm_create_meetings,
            "perm_request_meetings" to perm_request_meetings,
            "perm_approve_meetings" to perm_approve_meetings,
            "perm_create_projects" to perm_create_projects,
            "perm_manage_projects" to perm_manage_projects,
            "perm_create_events" to perm_create_events,
            "perm_view_events" to perm_view_events,
            "perm_view_members" to perm_view_members,
            "perm_view_member_stats" to perm_view_member_stats,
            "color_code" to color_code,
            "isDefault" to isDefault
        )
    }
}