package com.overdrive.sedekahsampah.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Post (
 @Transient

@DocumentId
 val id : String,
 val uid : String,
 val title : String,
 val body : String,
 val timeStamp  : Timestamp

) : Parcelable