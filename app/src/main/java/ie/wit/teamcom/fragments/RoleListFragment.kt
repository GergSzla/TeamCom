package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.RoleAdapter
import ie.wit.teamcom.adapters.RoleListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Role
import kotlinx.android.synthetic.main.activity_channels_list.*
import kotlinx.android.synthetic.main.activity_channels_list.view.*
import kotlinx.android.synthetic.main.fragment_role_list.*
import kotlinx.android.synthetic.main.fragment_role_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList


class RoleListFragment : Fragment(), AnkoLogger, RoleListener {
    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var rolesList = ArrayList<Role>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_role_list, container, false)
        activity?.title = getString(R.string.title_roles_settings)

        root.btnAddNewRole.setOnClickListener {
            if (app.currentActiveMember.role.perm_admin || app.currentActiveMember.role.perm_create_roles) {
                navigateTo(RoleCreateFragment.newInstance(currentChannel))
            } else {
                Toast.makeText(
                    context, "You do not have the permissions to create roles!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        root.rolesRecyclerView.layoutManager = LinearLayoutManager(activity)

        setSwipeRefresh()

        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllChannelRoles(auth.currentUser!!.uid)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    fun getAllChannelRoles(roleId: String?) {
        if (app.currentActiveMember.role.perm_admin || app.currentActiveMember.role.perm_view_roles){
            rolesList = ArrayList<Role>()
            app.database.child("channels").child(currentChannel!!.id).child("roles")
                .orderByChild("permission_code")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase roles error : ${error.message}")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        //hideLoader(loader)
                        val children = snapshot.children
                        children.forEach {
                            val role = it.getValue<Role>(Role::class.java)
                            rolesList.add(role!!)
                            root.rolesRecyclerView.adapter =
                                RoleAdapter(rolesList, this@RoleListFragment)
                            root.rolesRecyclerView.adapter?.notifyDataSetChanged()
                            checkSwipeRefresh()
                            app.database.child("channels").child(currentChannel!!.id).child("roles")
                                .orderByChild("permission_code")
                                .removeEventListener(this)
                        }
                    }
                })
        } else {
            Toast.makeText(
                context, "You do not have the permissions to view roles!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun setSwipeRefresh() {
        root.swiperefreshRoles.setOnRefreshListener {
            root.swiperefreshRoles.isRefreshing = true
            getAllChannelRoles(currentChannel!!.id)
        }
    }


    fun checkSwipeRefresh() {
        if (root.swiperefreshRoles.isRefreshing) root.swiperefreshRoles.isRefreshing = false
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            RoleListFragment().apply {
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

    override fun onRoleClick(role: Role) {
        if(app.currentActiveMember.role.perm_admin|| app.currentActiveMember.role.perm_manage_roles){
            navigateTo(RoleCreateFragment.editInstance(role, currentChannel))
        } else {
            Toast.makeText(
                context, "You do not have the permissions to edit roles!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}