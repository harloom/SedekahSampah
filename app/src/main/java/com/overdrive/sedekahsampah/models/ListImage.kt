package com.overdrive.sedekahsampah.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageStorage (
    @DocumentId
    val id : String,
    val idPost : String,
    val url : String,
    val timeStamp : Timestamp
):Parcelable

@Parcelize
data class ListImage(
    var list : MutableList<ImageStorage>
) : Parcelable