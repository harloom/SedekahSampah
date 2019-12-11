package com.overdrive.sedekahsampah.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListImage (
    val id : String,
    val idPost : String,
    val url : String,
    val timeStamp : String
):Parcelable

@Parcelize
data class UriImage(
    val url : String
) : Parcelable