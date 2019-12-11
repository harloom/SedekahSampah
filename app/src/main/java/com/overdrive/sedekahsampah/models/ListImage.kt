package com.overdrive.sedekahsampah.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageStorage (
    val id : String,
    val idPost : String,
    val url : String,
    val timeStamp : String
):Parcelable

@Parcelize
data class ListImage(
    var list : MutableList<ImageStorage>
) : Parcelable