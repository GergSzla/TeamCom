package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Comment
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_comment.view.*


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

            var ref = FirebaseStorage.getInstance().getReference("photos/${comment.comment_author.id}.jpg")
            ref.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it)
                    .resize(260, 260)
                    .transform(CropCircleTransformation())
                    .into(itemView.card_comment_image)
            }

            itemView.btn_save_comment.setOnClickListener {
                comment.comment_content = itemView.editTxtCommentContent.text.toString()
                listener.onCommentEditClicked(comment)

                itemView.editTxtCommentContent.isVisible = false
                itemView.btn_save_comment.isVisible = false
                itemView.txtCommentContent.isVisible = true
            }
        }
    }
}