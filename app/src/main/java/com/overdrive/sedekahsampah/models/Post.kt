package com.overdrive.sedekahsampah.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize


@Parcelize
data class  Post constructor (
 @Transient
 @DocumentId
 val id : String? =null,
 val uid : String? =null,
 val title : String? =null,
 val body : String? = null,
 val timeStamp  : Timestamp? = Timestamp.now()

) : Parcelable