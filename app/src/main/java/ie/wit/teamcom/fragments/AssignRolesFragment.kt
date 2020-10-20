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
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Department
import ie.wit.teamcom.models.Member
import ie.wit.teamcom.models.Role
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        arguments?.let {
            selectedMember = it.getParcelable("member_key")!!
            currentChannel = it.getParcelable("channel_key")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_assign_roles, container, false)
        activity?.title = getString(R.string.title_members_assignments)


        root.txtMemberName.text = "${selectedMember.firstName} ${selectedMember.surname}"
        getRoleNames()
        getDeptNames()

        root.btnReassignMember.setOnClickListener {
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

            app.database.child("channels").child(currentChannel!!.id).child("members").child(selectedMember.id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val roleUpdates = HashMap<String, Any>()
                        val deptUpdates = HashMap<String, Any>()

                        roleUpdates["/channels/${currentChannel!!.id}/members/${selectedMember.id}/role/"] = member_role
                        deptUpdates["/channels/${currentChannel!!.id}/members/${selectedMember.id}/department/"] = member_dept

                        app.database.updateChildren(roleUpdates)
                        app.database.updateChildren(deptUpdates)


                        app.database.child("channels").child(currentChannel!!.id).child("members").child(selectedMember.id)
                            .removeEventListener(this)
                    }
                })
        }

        return root
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