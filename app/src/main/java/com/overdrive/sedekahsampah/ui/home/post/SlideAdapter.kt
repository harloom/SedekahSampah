package com.overdrive.sedekahsampah.ui.home.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.ImageStorage
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.item_thumbail.view.*
import java.lang.Exception

class SlideAdapter(private val context: Context, private val listImage : MutableList<ImageStorage> , private val callback : Interaction)  :
    SliderViewAdapter<SlideAdapter.SliderAdapterVH>() {
    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val view  =  LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout,null)
        return SliderAdapterVH(view,callback)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        viewHolder!!.bind(listImage[position] , listImage)
    }

    override fun getCount(): Int = listImage.size



    class SliderAdapterVH constructor(val itemView: View , val callback: Interaction) : SliderViewAdapter.ViewHolder (itemView){
        fun bind(
            imageStorage: ImageStorage,
            listImage: MutableList<ImageStorage>
        )= with(itemView)  {
          try {
              Glide.with(itemView).load(imageStorage.url)
                  .placeholder(R.color.colorBgGrey)
                  .into(itemView.findViewById(R.id.thumbail_post))

              itemView.thumbail_post.setOnClickListener {
                  callback.onImageSelected(listImage)
              }
          }catch (e : Exception){

          }
        }


    }


}