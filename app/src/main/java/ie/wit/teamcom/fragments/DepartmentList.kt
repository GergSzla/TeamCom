package ie.wit.teamcom.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.DepartmentAdapter
import ie.wit.teamcom.adapters.DepartmentListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Department
import ie.wit.teamcom.models.Log
import kotlinx.android.synthetic.main.fragment_department_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DepartmentList : Fragment(),AnkoLogger, DepartmentListener {
    lateinit var app: MainApp
    lateinit var root: View
    var currentChannel = Channel()
    var departmentList = ArrayList<Department>()
    var dept = Department()
    var orderDateId:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
        }
        app.getAllMembers(currentChannel.id)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_department_list, container, false)
        activity?.title = getString(R.string.title_department_settings)

        root.btnAddNewDept.setOnClickListener {
            showDialog("")
        }

        root.departmentsRecyclerView.layoutManager = LinearLayoutManager(activity)

        setSwipeRefresh()

        return root
    }

    private fun showDialog(title: String) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_create_dept)
        val create = dialog.findViewById(R.id.buttonSubmitDept) as Button
        val cancel = dialog.findViewById(R.id.buttonCancelDept) as Button
        val dept_name = dialog.findViewById(R.id.txtDeptName) as EditText
        create.setOnClickListener {
            createDept(dept_name.text.toString())
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun showEditDialog(dept:Department) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_edit_dept)
        val create = dialog.findViewById(R.id.buttonSubmitDeptEdit) as Button
        val cancel = dialog.findViewById(R.id.buttonCancelDeptEdit) as Button
        val txtDeptNameEdit = dialog.findViewById(R.id.txtDeptNameEdit) as EditText
        create.setOnClickListener {
            editDept(txtDeptNameEdit.text.toString(),dept)
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    fun setSwipeRefresh() {
        root.swiperefreshDepartments.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshDepartments.isRefreshing = true
                getAllChannelDepartments()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        getAllChannelDepartments()
    }

    fun checkSwipeRefresh() {
        if (root.swiperefreshDepartments.isRefreshing) root.swiperefreshDepartments.isRefreshing = false
    }

    fun getAllChannelDepartments() {
        departmentList = ArrayList<Department>()
        app.database.child("channels").child(currentChannel!!.id).child("departments").orderByChild("date_order_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase departments error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val dept = it.getValue<Department>(Department::class.java)
                        departmentList.add(dept!!)
                        root.departmentsRecyclerView.adapter = DepartmentAdapter(
                            departmentList,
                            this@DepartmentList
                        )
                        root.departmentsRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("departments").orderByChild("date_order_id")
                            .removeEventListener(this)
                    }
                }
            })
    }

    private fun createDept(dept_name: String){
        dept.dept_name = dept_name
        dept.id = UUID.randomUUID().toString()
        app.generateDateID("1")
        dept.date_order_id = app.valid_from_cal
        writeNewDept()
    }
    private fun editDept(new_dept_name: String, dept2 : Department){
        dept2.dept_name = new_dept_name
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()


                    childUpdates["/channels/${currentChannel.id}/departments/${dept2.id}/"] = dept2
                    app.database.updateChildren(childUpdates)

                    app.generateDateID("1")
                    val logUpdates = HashMap<String, Any>()
                    var new_log = Log(
                        log_id = app.valid_from_cal,
                        log_triggerer = app.currentActiveMember,
                        log_date = app.dateAsString,
                        log_time = app.timeAsString,
                        log_content = "${app.currentActiveMember.firstName} ${app.currentActiveMember.surname} made changes to Department : ${dept.dept_name}."
                    )
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)

                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                    //startActivity(intentFor<ChannelsListActivity>().putExtra("user_key", user))
                }
            })
    }


    fun writeNewDept(){
        app.database.child("channels").child(currentChannel!!.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()


                    childUpdates["/channels/${currentChannel.id}/departments/${dept.id}"] = dept
                    app.database.updateChildren(childUpdates)

                    val logUpdates = HashMap<String, Any>()
                    var new_log = Log(log_id = app.valid_from_cal, log_triggerer = app.currentActiveMember, log_date = app.dateAsString, log_time = app.timeAsString, log_content = "${app.currentActiveMember.firstName} ${app.currentActiveMember.surname} created a new Department [${dept.dept_name}].")
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)

                    app.database.child("channels").child(currentChannel!!.id)
                        .removeEventListener(this)
                    //startActivity(intentFor<ChannelsListActivity>().putExtra("user_key", user))
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            DepartmentList().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                }
            }
    }

    override fun onDeptClick(dept: Department) {
        showEditDialog(dept)
    }
}