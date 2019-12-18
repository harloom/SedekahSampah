package com.overdrive.sedekahsampah.ui.home.post.komentar

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.Komentar
import com.overdrive.sedekahsampah.models.User
import com.snov.timeagolibrary.PrettyTimeAgo
import kotlinx.android.synthetic.main.item_komentar.view.*

class KomentarAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Komentar>() {

        override fun areItemsTheSame(oldItem: Komentar, newItem: Komentar): Boolean =  oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Komentar, newItem: Komentar): Boolean  =oldItem == newItem

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return KomentarHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_komentar,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is KomentarHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Komentar>) {
        differ.submitList(list)
    }

    class KomentarHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Komentar) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            itemView.imageView2.setOnClickListener {
                interaction?.onItemMoreClick(adapterPosition,item)
            }

            itemView.text_commentar_body.text = item.body
            itemView.text_commentar_time.text =  PrettyTimeAgo.getTimeAgo(item.timeStamp!!.seconds * 1000)

            val docUser = FirebaseFirestore.getInstance().collection("users").document(item.uid!!)
            docUser.get().addOnSuccessListener {
                try {
                    val user = it.toObject(User::class.java)
                    itemView.text_commentar_name.text = user?.displayName

                    Glide.with(context).load(user?.photoUrl).placeholder(R.color.colorBgGrey).into(itemView.image_commentar_profile)
                }catch ( e : Exception){
                    itemView.text_commentar_name.text =  " Something is Error"

                }

            }.addOnFailureListener {
                itemView.text_commentar_name.text =  " Something is Error"
            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Komentar)
        fun onItemMoreClick(position: Int, item: Komentar)
    }
}

