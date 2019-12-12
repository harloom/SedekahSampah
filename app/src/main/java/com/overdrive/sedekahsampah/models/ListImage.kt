package com.overdrive.sedekahsampah.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageStorage (
    @Transient
    @DocumentId
    val id : String?=null,
    val idPost : String?=null,
    val url : String?=null,
    val timeStamp : Timestamp?=null
):Parcelable

