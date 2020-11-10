package ie.wit.teamcom.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.CommentListener
import ie.wit.teamcom.adapters.CommentsAdapter
import ie.wit.teamcom.adapters.PostAdapter
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Comment
import ie.wit.teamcom.models.Log
import ie.wit.teamcom.models.Post
import kotlinx.android.synthetic.main.fragment_post_comments.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

class PostCommentsFragment : Fragment(),AnkoLogger,CommentListener {
    lateinit var root: View
    lateinit var app: MainApp
    var commentsList = ArrayList<Comment>()
    var likesList = ArrayList<String>()
    var new_comment = Comment()
    var currentPost = Post()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            currentChannel = it.getParcelable("channel_key")!!
            currentPost = it.getParcelable("post_key")!!
        }
        app.getAllMembers(currentChannel.id)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_post_comments, container, false)
        activity?.title = getString(R.string.title_news_feed)
        root.commentsRecyclerView.layoutManager = LinearLayoutManager(activity)

        root.txtPostUserView.text = currentPost.post_author.firstName + " " + currentPost.post_author.surname
        root.txtPostTimeAndDateView.text = currentPost.post_date +" @ "+ currentPost.post_time
        root.txtPostContentView.text = currentPost.post_content


        root.imgBtnPostComment.setOnClickListener {
            sendComment()
        }
        setSwipeRefresh()
        return root
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel,app.currentActiveMember)
        getAllComments()
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel,app.currentActiveMember)
    }

    fun setSwipeRefresh() {
        root.swiperefreshComments.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefreshComments.isRefreshing = true
                getAllComments()
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefreshComments.isRefreshing) root.swiperefreshComments.isRefreshing = false
    }

    fun sendComment(){
        app.generateDateID("1")
        new_comment.comment_content = root.editComment.text.toString()
        new_comment.id = UUID.randomUUID().toString()
        new_comment.comment_date_id = app.valid_from_cal
        new_comment.comment_author = app.currentActiveMember
        new_comment.comment_date = app.dateAsString
        new_comment.comment_time = app.timeAsString
        createPost()
        root.editComment.setText("")
    }

    fun createPost(){
        app.database.child("channels").child(currentChannel!!.id).child("posts").child(currentPost.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    val logUpdates = HashMap<String, Any>()

                    currentPost.post_comments.add(new_comment)
                    childUpdates["/channels/${currentChannel.id}/posts/${currentPost.id}"] = currentPost
                    app.database.updateChildren(childUpdates)

                    var new_log = Log(log_id = app.valid_from_cal, log_triggerer = app.currentActiveMember, log_date = app.dateAsString, log_time = app.timeAsString, log_content = "New Comment on " +
                            "${currentPost.post_author.firstName} ${currentPost.post_author.surname}'s post")
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)


                    app.database.child("channels").child(currentChannel!!.id).child("posts").child(currentPost.id)
                        .removeEventListener(this)
                    //startActivity(intentFor<ChannelsListActivity>().putExtra("user_key", user))
                }
            })
    }


    fun getAllComments() {
        commentsList = ArrayList<Comment>()
        app.database.child("channels").child(currentChannel!!.id).child("posts").child(currentPost.id).child("post_comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase comments error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val comment = it.getValue<Comment>(Comment::class.java)
                        commentsList.add(comment!!)
                        root.commentsRecyclerView.adapter = CommentsAdapter(commentsList,this@PostCommentsFragment)
                        root.commentsRecyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("posts").child("post_comments")
                            .removeEventListener(this)
                    }
                }
            })
    }

    var alreadyLiked = false
    fun getAllCommentLikes(comment: Comment){
        likesList = ArrayList<String>()
        app.database.child("channels").child(currentChannel!!.id).child("posts").child(currentPost.id).child("post_comments").child(comment.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase comments error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val like = it.getValue<String>(String::class.java)
                        likesList.add(like!!)

                        checkSwipeRefresh()
                        app.database.child("channels").child(currentChannel!!.id).child("posts").child(currentPost.id).child("post_comments").child(comment.id)
                            .removeEventListener(this)
                    }
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(channel: Channel, post: Post) =
            PostCommentsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("channel_key", channel)
                    putParcelable("post_key", post)
                }
            }
    }

    override fun onLikeCommentClicked(comment: Comment) {
        //getAllCommentLikes(comment)
    }
}