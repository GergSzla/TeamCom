package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.MembersAdapter
import ie.wit.teamcom.adapters.MembersListener
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Account
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Member
import kotlinx.android.synthetic.main.fragment_members.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.ArrayList


var user = Member()
var currentChannel = Channel()
var memberList = ArrayList<Member>()



class MembersFragment : Fragment() , AnkoLogger, MembersListener {

    lateinit var app: MainApp
    lateinit var eventListener : ValueEventListener
    lateinit var root: View

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
        root = inflater.inflate(R.layout.fragment_members, container, false)
        activity?.title = getString(R.string.title_members_settings)
        root.membersRecyclerView.layoutManager = LinearLayoutManager(activity)
        getAllMembers()
        //setSwipeRefresh()
        return root
    }

    fun setSwipeRefresh() {
        root.swiperefreshMembers.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshMembers.isRefreshing = true
                getAllMembers()
            }
        })
    }
    fun checkSwipeRefresh() {
        if (root.swiperefreshMembers.isRefreshing) root.swiperefreshMembers.isRefreshing = false
    }


    fun getAllMembers() {
        memberList = ArrayList<Member>()
        app.database.child("channels").child(currentChannel!!.id).child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase roles error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val member = it.getValue<Member>(Member::class.java)
                        memberList.add(member!!)
                        root.membersRecyclerView.adapter = MembersAdapter(
                            memberList,
                            this@MembersFragment
                        )
                        root.membersRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("members")
                            .removeEventListener(this)
                    }
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            MembersFragment().apply {
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

    override fun onMemberClick(member: Member) {
        navigateTo(AssignRolesFragment.newInstance(member, currentChannel))
    }
}