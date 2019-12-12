package com.overdrive.sedekahsampah.ui.home.post

import android.graphics.Color
import android.text.SpannableStringBuilder
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.Post
import com.overdrive.sedekahsampah.models.User
import com.snov.timeagolibrary.PrettyTimeAgo
import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class PostAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Post>() {

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return  oldItem==newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return PostHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_post,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Post>) {
        differ.submitList(list)
    }

    class PostHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Post) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            itemView.caption_text.setExpandableText(item.title,item.body)
            itemView.timeAgo.text = PrettyTimeAgo.getTimeAgo(item.timeStamp.seconds * 1000)
            val docUser = FirebaseFirestore.getInstance().collection("users").document(item.uid)
            docUser.get().addOnSuccessListener {
                try {
                    val user = it.toObject(User::class.java)
                    itemView.tv_displayName.text = user?.displayName

                    Glide.with(context).load(user?.photoUrl).placeholder(R.color.colorBgGrey).into(itemView.photo_user)
                }catch ( e : Exception){
                    itemView.tv_displayName.text =  " Something is Error"

                }

            }.addOnFailureListener {
                itemView.tv_displayName.text =  " Something is Error"
            }

        }


        private fun TextView.setExpandableText(senderName: String, caption: String) {
            post {
                setCaption(senderName, caption)
                setOnClickListener {
                    let {
                        it.maxLines = MAX_LINES
                        it.text = getFullCaption(senderName, caption)
                    }
                }
            }
        }


        private fun TextView.setCaption(senderName: String, caption: String) {
            text = getFullCaption(senderName, caption)

            if (lineCount > DEFAULT_LINES) {
                val lastCharShown = layout.getLineVisibleEnd(DEFAULT_LINES - 1)
                maxLines = DEFAULT_LINES
                val moreString = context.getString(R.string.read_more)
                val suffix = " $moreString"
                // 3 is a "magic number" but it's just basically the length of the ellipsis we're going to insert
                val actionDisplayText = context.getString(R.string.more_dots) + suffix

                text = SpannableStringBuilder()
                    .bold { append(senderName) }
                    .append("  ")
                    .append(
                        caption.substring(
                            0,
                            lastCharShown - suffix.length - 3 - moreString.length - senderName.length
                        )
                    )
                    .color(Color.GRAY) { append(actionDisplayText) }
            }
        }


            private fun getFullCaption(
                senderName: String,
                caption: String
            ) = SpannableStringBuilder()
                .bold { append(senderName) }
                .append("  ")
                .append(caption)
        }
    }



    interface Interaction {
        fun onItemSelected(position: Int, item: Post)
    }



const val DEFAULT_LINES = 2
const val MAX_LINES = 20

