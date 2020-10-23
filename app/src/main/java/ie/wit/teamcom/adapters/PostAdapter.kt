package ie.wit.teamcom.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.teamcom.R
import ie.wit.teamcom.models.Post
import kotlinx.android.synthetic.main.card_post.view.*


interface PostListener {
    fun onLikeClicked(post: Post)
    fun onCommentClicked(post: Post)
}

class PostAdapter constructor(
    var posts: ArrayList<Post>,
    private val listener: PostListener
): RecyclerView.Adapter<PostAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.card_post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val post = posts[holder.adapterPosition]
        holder.bind(post, listener)
    }

    override fun getItemCount(): Int = posts.size

    fun removeAt(position: Int) {
        posts.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(post: Post, listener: PostListener) {
            itemView.tag = post

            itemView.txtPostUser.text = "${post.post_author.firstName} ${post.post_author.surname}"

            if(post.post_author.role.color_code.take(1) != "#"){
                itemView.txtPostUser.setTextColor(Color.parseColor("#" + post.post_author.role.color_code))
            } else {
                itemView.txtPostUser.setTextColor(Color.parseColor(post.post_author.role.color_code))
            }

            itemView.txtPostTimeAndDate.text = "${post.post_date} @ ${post.post_time}"
            itemView.txtPostContent.text = "${post.post_content}"

            itemView.btnLike.text = "Like (${post.post_likes})"
            itemView.btnComment.text = "Comments (${post.post_comments_total})"

            itemView.btnLike.setOnClickListener(View.OnClickListener {
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onLikeClicked(post)
                    }
                }
            })
            itemView.btnComment.setOnClickListener(View.OnClickListener {
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCommentClicked(post)
                    }
                }
            })
        }
    }
}