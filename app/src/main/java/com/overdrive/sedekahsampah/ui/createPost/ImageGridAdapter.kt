package com.overdrive.sedekahsampah.ui.createPost

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide

import com.overdrive.sedekahsampah.R.layout.item_thumbail
import com.overdrive.sedekahsampah.models.uriImage
import kotlinx.android.synthetic.main.item_thumbail.view.*
import java.lang.Exception

class ImageGridAdapter constructor(
    private val context  :Context,
    private val list: List<uriImage>) : BaseAdapter() {


    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val img = list[p0]
        val inflater = p2?.context?.
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(item_thumbail, null)
        try {
            Glide.with(context).load(img.url).into(view.thumbail_post)
        }catch (e : Exception){

        }

        return view
    }

    override fun getItem(p0: Int): uriImage? {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return list.size
    }

}