package ie.wit.teamcom.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import ie.wit.teamcom.R
import ie.wit.teamcom.main.MainApp
import ie.wit.teamcom.models.Comment
import kotlinx.android.synthetic.main.card_comment.view.*
import kotlinx.android.synthetic.main.card_post.view.*
import org.jetbrains.anko.AnkoLogger


interface CommentListener {
    fun onLikeCommentClicked(comment: Comment)
    fun onCommentEditClicked(comment: Comment)
}

class CommentsAdapter constructor(
    var comments: ArrayList<Comment>,
    private val listener: CommentListener
): RecyclerView.Adapter<CommentsAdapter.MainHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_comment, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val comment = comments[holder.adapterPosition]
        holder.bind(comment, listener)
    }

    override fun getItemCount(): Int = comments.size

    fun removeAt(position: Int) {
        comments.removeAt(position)
        notifyItemRemoved(position)
    }


    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(comment: Comment, listener: CommentListener) {
            itemView.tag = comment

            var auth: FirebaseAuth = FirebaseAuth.getInstance()
            var database: DatabaseReference = FirebaseDatabase.getInstance().reference

            itemView.txtCommentUser.text = "${comment.comment_author.firstName} ${comment.comment_author.surname}"
            itemView.editTxtCommentContent.isVisible = false
            itemView.btn_save_comment.isVisible = false


            if(comment.comment_author.role.color_code.take(1) != "#"){
                itemView.txtCommentUser.setTextColor(Color.parseColor("#" + comment.comment_author.role.color_code))
            } else {
                itemView.txtCommentUser.setTextColor(Color.parseColor(comment.comment_author.role.color_code))
            }

            itemView.txtCommentTimeAndDate.text = "${comment.comment_date} @ ${comment.comment_time}"
            itemView.txtCommentContent.text = "${comment.comment_content}"

            itemView.btn_comment_edit.isVisible = comment.comment_author.id == auth.currentUser!!.uid


            itemView.btn_comment_edit.setOnClickListener{
                itemView.editTxtCommentContent.isVisible = true
                itemView.btn_save_comment.isVisible = true
                itemView.txtCommentContent.isVisible = false

                itemView.editTxtCommentContent.setText(comment.comment_content)

//                listener.onCommentEditClicked(comment)
            }

            itemView.btn_save_comment.setOnClickListener {

//                database.child("users").child(user.id)
//                    .addValueEventListener(object : ValueEventListener {
//                        override fun onCancelled(error: DatabaseError) {
//                        }
//
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val childUpdates = HashMap<String, Any>()
//                            childUpdates["/users/${user.id}/firstName"] = user.firstName
//                            database.updateChildren(childUpdates)
//
//                            database.child("users").child(user.id)
//                                .removeEventListener(this)
//                        }
//                    })
                comment.comment_content = itemView.editTxtCommentContent.text.toString()
                listener.onCommentEditClicked(comment)

                itemView.editTxtCommentContent.isVisible = false
                itemView.btn_save_comment.isVisible = false
                itemView.txtCommentContent.isVisible = true


            }
            //itemView.btnLikeComment.text = "Like (${comment.comment_likes})"

            /*itemView.btnLikeComment.setOnClickListener(View.OnClickListener {
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onLikeCommentClicked(comment)
                    }
                }
            })*/
        }
    }
}