package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.adventurio.helpers.createLoader
import ie.wit.adventurio.helpers.hideLoader
import ie.wit.adventurio.helpers.showLoader
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.*
import kotlinx.android.synthetic.main.fragment_assign_roles.*
import kotlinx.android.synthetic.main.fragment_assign_roles.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.ArrayList
import java.util.HashMap


class AssignRolesFragment : Fragment(), AnkoLogger {
    lateinit var app: MainApp
    var selectedMember = Member()
    var roles = ArrayList<String>()
    var rolesList = ArrayList<Role>()
    var depts = ArrayList<String>()
    var deptsList = ArrayList<Department>()
    var member_role = Role()
    var member_dept = Department()

    lateinit var eventListener : ValueEventListener
    lateinit var root: View
    var currentChannel: Channel? = null
    lateinit var loader : androidx.appcompat.app.AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        arguments?.let {
            selectedMember = it.getParcelable("member_key")!!
            currentChannel = it.getParcelable("channel_key")
        }
        app.getAllMembers(currentChannel!!.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_assign_roles, container, false)
        activity?.title = getString(R.string.title_members_assignments)

        loader = createLoader(requireActivity())

        root.txtMemberName.text = "${selectedMember.firstName} ${selectedMember.surname}"

        showLoader(loader,"Loading . . .", "Getting Roles and Departments . . .")
        getRoleNames()
        getDeptNames()
        hideLoader(loader)

        root.btnReassignMember.setOnClickListener {
            showLoader(loader, "Loading . . .", "Assigning Roles and Departments . . . ")
            var role_selected = root.spinnerRole.selectedItem.toString()
            var dept_selected = root.spinnerDepartment.selectedItem.toString()
            var i = 0
            rolesList.forEach{
                if (role_selected == rolesList[i].role_name){
                    member_role = rolesList[i]
                } else {
                    i++
                }
            }

            var j = 0
            deptsList.forEach{
                if (dept_selected == deptsList[j].dept_name){
                    member_dept = deptsList[j]
                } else {
                    j++
                }
            }

            member_dept.dept_members.add(selectedMember)

            app.database.child("channels").child(currentChannel!!.id).child("members").child(selectedMember.id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userRoleUpdates = HashMap<String, Any>()
                        val userDeptUpdates = HashMap<String, Any>()
                        val deptUpdates = HashMap<String, Any>()

                        userRoleUpdates["/channels/${currentChannel!!.id}/members/${selectedMember.id}/role/"] = member_role
                        userDeptUpdates["/channels/${currentChannel!!.id}/members/${selectedMember.id}/department/"] = member_dept
                        deptUpdates["/channels/${currentChannel!!.id}/departments/${member_dept.id}/"] = member_dept


                        app.database.updateChildren(userRoleUpdates)
                        app.database.updateChildren(userDeptUpdates)
                        app.database.updateChildren(deptUpdates)


                        app.generateDateID("1")
                        val logUpdates = HashMap<String, Any>()
                        var new_log = Log(log_id = app.valid_from_cal, log_triggerer = app.currentActiveMember, log_date = app.dateAsString, log_time = app.timeAsString, log_content = "Role [${member_role.role_name}] and Department [${member_dept.dept_name}] has been assigned to ${selectedMember.firstName} ${selectedMember.surname}.")
                        logUpdates["/channels/${currentChannel!!.id}/logs/${new_log.log_id}"] = new_log
                        app.database.updateChildren(logUpdates)

                        hideLoader(loader)
                        app.database.child("channels").child(currentChannel!!.id).child("members").child(selectedMember.id)
                            .removeEventListener(this)
                    }
                })
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(ie.wit.teamcom.fragments.currentChannel,app.currentActiveMember)
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(ie.wit.teamcom.fragments.currentChannel,app.currentActiveMember)
    }

    fun getRoleNames(){
        app.database.child("channels").child(currentChannel!!.id).child("roles")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val role = it.
                        getValue<Role>(Role::class.java)

                        roles.add(role!!.role_name)
                        rolesList.add(role)

                        app.database.child("channel").child(currentChannel!!.id).child("roles")
                            .removeEventListener(this)
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item, // Layout
                        roles
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                    root.spinnerRole.adapter = adapter
                }
            })

    }

    fun getDeptNames(){
        app.database.child("channels").child(currentChannel!!.id).child("departments")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val dept = it.
                        getValue<Department>(Department::class.java)

                        depts.add(dept!!.dept_name)
                        deptsList.add(dept)

                        app.database.child("channel").child(currentChannel!!.id).child("departments")
                            .removeEventListener(this)
                    }
                    val adapter1 = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item, // Layout
                        depts
                    )
                    adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                    root.spinnerDepartment.adapter = adapter1
                }
            })

    }
    companion object {
        @JvmStatic
        fun newInstance(member: Member, channel: Channel) =
            AssignRolesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("member_key", member)
                    putParcelable("channel_key", channel)
                }
            }
    }
}