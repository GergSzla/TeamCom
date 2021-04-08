package ie.wit.teamcom.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ie.wit.teamcom.R
import ie.wit.teamcom.adapters.CommentListener
import ie.wit.teamcom.adapters.CommentsAdapter
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.main.auth
import ie.wit.teamcom.models.Channel
import ie.wit.teamcom.models.Comment
import ie.wit.teamcom.models.Log
import ie.wit.teamcom.models.Post
import ie.wit.utils.SwipeToDeleteCallback
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_comment.view.*
import kotlinx.android.synthetic.main.fragment_create_meeting.view.*
import kotlinx.android.synthetic.main.fragment_news_feed.view.*
import kotlinx.android.synthetic.main.fragment_post_comments.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

class PostCommentsFragment : Fragment(), AnkoLogger, CommentListener {
    lateinit var root: View
    lateinit var app: MainApp
    var commentsList = ArrayList<Comment>()
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

        (currentPost.post_author.firstName + " " + currentPost.post_author.surname).also {
            root.txtPostUserView.text = it
        }
        (currentPost.post_date + " @ " + currentPost.post_time).also {
            root.txtPostTimeAndDateView.text = it
        }
        root.txtPostContentView.text = currentPost.post_content

        root.imgBtnPostComment.setOnClickListener {
            validateForm()
            if (validateForm()) {
                if (app.currentActiveMember.role.perm_create_posts || app.currentActiveMember.role.perm_admin) {
                    sendComment()
                } else {
                    makeText(
                        context, "You do not have the permissions to do this!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = root.commentsRecyclerView.adapter as CommentsAdapter
                delete_comment((viewHolder.itemView.tag as Comment))
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.commentsRecyclerView)

        var ref =
            FirebaseStorage.getInstance().getReference("photos/${currentPost.post_author.id}.jpg")
        ref.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it)
                .resize(260, 260)
                .transform(CropCircleTransformation())
                .into(root.card_post_pic)
        }

        if (currentPost.post_author.role.color_code.take(1) != "#") {
            root.txtPostUserView.setTextColor(Color.parseColor("#" + currentPost.post_author.role.color_code))
        } else {
            root.txtPostUserView.setTextColor(Color.parseColor(currentPost.post_author.role.color_code))
        }

        return root
    }

    fun delete_comment(comment: Comment) {
        if (comment.comment_author.id == auth.currentUser!!.uid) {
            val i = commentsList.indexOf(comment)
            app.database.child("channels").child(currentChannel.id).child("posts")
                .child(currentPost.id).child("post_comments").child("$i")
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.ref.removeValue()
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
        } else {
            makeText(
                context,
                "Comment Can Only Be Deleted By Its Owner or Members With The Appropriate Permissions.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val comment_input = root.editComment.text.toString()
        if (TextUtils.isEmpty(comment_input)) {
            root.editComment.error = "You Cannot Send An Empty Comment!"
            valid = false
        } else {
            root.editComment.error = null
        }

        return valid
    }

    override fun onResume() {
        super.onResume()
        app.activityResumed(currentChannel, app.currentActiveMember)
        getAllComments()
    }

    override fun onPause() {
        super.onPause()
        app.activityPaused(currentChannel, app.currentActiveMember)
    }

    fun setSwipeRefresh() {
        root.swiperefreshComments.setOnRefreshListener {
            root.swiperefreshComments.isRefreshing = true
            getAllComments()
        }
    }

    fun checkSwipeRefresh() {
        if (root.swiperefreshComments.isRefreshing) root.swiperefreshComments.isRefreshing = false
    }

    private fun sendComment() {
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

    private fun createPost() {
        app.database.child("channels").child(currentChannel!!.id).child("posts")
            .child(currentPost.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    val logUpdates = HashMap<String, Any>()

                    currentPost.post_comments.add(new_comment)
                    childUpdates["/channels/${currentChannel.id}/posts/${currentPost.id}"] =
                        currentPost
                    app.database.updateChildren(childUpdates)

                    var new_log = Log(
                        log_id = app.valid_from_cal,
                        log_triggerer = app.currentActiveMember,
                        log_date = app.dateAsString,
                        log_time = app.timeAsString,
                        log_content = "New Comment on " +
                                "${currentPost.post_author.firstName} ${currentPost.post_author.surname}'s post"
                    )
                    logUpdates["/channels/${currentChannel.id}/logs/${new_log.log_id}"] = new_log
                    app.database.updateChildren(logUpdates)

                    navigateTo(PostCommentsFragment.newInstance(currentChannel, currentPost))

                    app.database.child("channels").child(currentChannel!!.id).child("posts")
                        .child(currentPost.id)
                        .removeEventListener(this)
                    //startActivity(intentFor<ChannelsListActivity>().putExtra("user_key", user))
                }
            })
    }


    private fun getAllComments() {
        commentsList = ArrayList<Comment>()
        app.database.child("channels").child(currentChannel!!.id).child("posts")
            .child(currentPost.id).child("post_comments")
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
                        root.commentsRecyclerView.adapter =
                            CommentsAdapter(commentsList, this@PostCommentsFragment)
                        root.commentsRecyclerView.adapter?.notifyDataSetChanged()
                        app.database.child("channels").child(currentChannel!!.id).child("posts")
                            .child(currentPost.id).child("post_comments")
                            .removeEventListener(this)
                    }
                }
            })
        checkSwipeRefresh()
    }

    //TODO: Get Comment Likes

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

    private fun navigateTo(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onLikeCommentClicked(comment: Comment) {
        //TODO: Get Comment Likes
    }

    override fun onCommentEditClicked(comment: Comment) {
        val i = commentsList.indexOf(comment)

        app.database.child("channels").child(currentChannel.id).child("posts")
            .child(currentPost.id).child("post_comments").child("$i")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/channels/${currentChannel.id}/posts/${currentPost.id}/post_comments/$i/comment_content/"] =
                        comment.comment_content
                    app.database.updateChildren(childUpdates)

                    app.database.child("channels").child(currentChannel.id).child("posts")
                        .child(currentPost.id).child("post_comments").child("$i")
                        .removeEventListener(this)
                }
            })
    }
}