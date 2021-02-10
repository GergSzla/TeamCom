package ie.wit.teamcom.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.PostAdapter
import ie.wit.teamcom.adapters.PostListener
import ie.wit.teamcom.interfaces.IOnBackPressed
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Log
import ie.wit.teamcom.models.Post
import kotlinx.android.synthetic.main.activity_channel_join.*
import kotlinx.android.synthetic.main.fragment_conversation.view.*
import kotlinx.android.synthetic.main.fragment_messaging.view.*
import kotlinx.android.synthetic.main.fragment_news_feed.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

class NewsFeedFragment : Fragment(), AnkoLogger, PostListener {
    lateinit var root: View
    lateinit var app: MainApp
    var postList = ArrayList<Post>()
    var likesList = ArrayList<String>()
    var new_post = Post()


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
        root = inflater.inflate(R.layout.fragment_news_feed, container, false)
        activity?.title = getString(R.string.title_news_feed)
        root.postsRecyclerView.layoutManager = LinearLayoutManager(activity)
        //getAllPosts()

        root.imgBtnSend.setOnClickListener {
            validateForm()
            if (validateForm()){
                sendPost()
            }
        }
        setSwipeRefresh()
        return root
    }

    private fun validateForm(): Boolean {
        var valid = true

        val post_input = root.editTextPost.text.toString()
        if (TextUtils.isEmpty(post_input)) {
            root.editTextPost.error = "You Cannot Send An Empty Post!"
            valid = false
        } else {
            root.editTextPost.error = null
        }

        return valid
    }

    fun sendPost(){
        app.generateDateID("1")
        new_post.post_content = root.editTextPost.text.toString()
        new_post.id = UUID.randomUUID().toString()
        new_post.post_date_id = app.valid_from_cal
        new_post.post_author = app.currentActiveMember
        new_post.post_date = app.dateAsString
        new_post.post_time = app.timeAsString
        createPost()
        root.editTextPost.setText("")
    }

    fun createPost(){
        app.database.child("channels").child(currentChannel.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    val logUpdates = HashMap<String, Any>()


                    childUpdates["/channels/${currentChannel.id}/posts/${new_post.id}"] = new_post
                    app.database.updateChildren(childUpdates)

                    var new_log = Log(
                        log_id = app.valid_from_cal,
                        log_triggerer = app.currentActiveMember,
                        log_date = app.dateAsString,
                        log_time = app.timeAsString,
                        log_content = "Created a new post on the News Feed!."
                    )
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)
                    getAllPosts()
                    checkSwipeRefresh()
                    app.database.child("channels").child(currentChannel.id)
                        .removeEventListener(this)
                }
            })
    }

    fun setSwipeRefresh() {
        root.swiperefreshPosts.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshPosts.isRefreshing = true
                getAllPosts()
            }
        })
    }
    fun checkSwipeRefresh() {
        if (root.swiperefreshPosts.isRefreshing) root.swiperefreshPosts.isRefreshing = false
    }

    fun getAllPosts() {
        postList = ArrayList<Post>()
        app.database.child("channels").child(currentChannel.id).child("posts").orderByChild("post_date_id")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase nf error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val post = it.getValue<Post>(Post::class.java)
                        postList.add(post!!)
                        root.postsRecyclerView.adapter = PostAdapter(
                            postList,
                            this@NewsFeedFragment
                        )
                        root.postsRecyclerView.adapter?.notifyDataSetChanged()
                        if (postList.size > 0 ) {
                            root.txtEmpty_posts.isVisible = false
                        }
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel.id).child("posts")
                            .orderByChild(
                                "post_date_id"
                            )
                            .removeEventListener(this)
                    }
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel) =
            NewsFeedFragment().apply {
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

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllPosts()
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }


    var alreadyLiked: Boolean = false
    fun getAllPostLikes(post: Post){
        likesList = ArrayList<String>()
        app.database.child("channels").child(currentChannel.id).child("posts").child(post.id).child(
            "liked_by"
        )
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase roles error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val like = it.getValue<String>(String::class.java)
                        likesList.add(like!!)
                        app.database.child("channels").child(currentChannel.id).child("posts")
                            .child(
                                post.id
                            ).child("liked_by")
                            .removeEventListener(this)
                    }
                    likesList.forEach {
                        if (it == app.currentActiveMember.id) {
                            alreadyLiked = true
                            return
                        }
                    }
                    if (!alreadyLiked) {
                        post.post_likes += 1
                        post.liked_by.add(app.currentActiveMember.id)
                    } else {
                        post.post_likes = post.post_likes - 1
                        post.liked_by.remove(app.currentActiveMember.id)
                    }

                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/posts/${post.id}/"] = post
                    app.database.updateChildren(childUpdates)
                    getAllPosts()

                }
            })
    }

    override fun onLikeClicked(post: Post) {
        getAllPostLikes(post)
    }

    override fun onCommentClicked(post: Post) {
        navigateTo(PostCommentsFragment.newInstance(currentChannel, post))
    }
}