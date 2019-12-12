package com.overdrive.sedekahsampah.ui.home.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.ImageStorage
import com.overdrive.sedekahsampah.models.ListImage
import com.smarteist.autoimageslider.SliderViewAdapter
import java.lang.Exception

class SlideAdapter (private val context : Context ,private val  listImage: ListImage)  :
    SliderViewAdapter<SlideAdapter.SliderAdapterVH>() {
    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val view  =  LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout,null)
        return SliderAdapterVH(view)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        viewHolder!!.bind(listImage.list[position]);
    }

    override fun getCount(): Int = listImage.list.size



    class SliderAdapterVH constructor(val itemView: View) : SliderViewAdapter.ViewHolder (itemView){
        fun bind(imageStorage: ImageStorage)= with(itemView)  {
          try {
              Glide.with(itemView).load(imageStorage.url)
                  .placeholder(R.color.colorBgGrey)
                  .into(itemView.findViewById(R.id.thumbail_post))
          }catch (e : Exception){

          }
        }


    }


}