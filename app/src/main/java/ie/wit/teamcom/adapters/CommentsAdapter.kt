package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Comment
import kotlinx.android.synthetic.main.card_comment.view.*


interface CommentListener {
    fun onLikeCommentClicked(comment: Comment)
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

            itemView.txtCommentUser.text = "${comment.comment_author.firstName} ${comment.comment_author.surname}"

            if(comment.comment_author.role.color_code.take(1) != "#"){
                itemView.txtCommentUser.setTextColor(Color.parseColor("#" + comment.comment_author.role.color_code))
            } else {
                itemView.txtCommentUser.setTextColor(Color.parseColor(comment.comment_author.role.color_code))
            }

            itemView.txtCommentTimeAndDate.text = "${comment.comment_date} @ ${comment.comment_time}"
            itemView.txtCommentContent.text = "${comment.comment_content}"

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