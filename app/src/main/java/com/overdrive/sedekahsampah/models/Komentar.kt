package com.overdrive.sedekahsampah.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Komentar(
    @DocumentId
    val id: String?=null,
    val idPost: String?=null,
    val uid: String? =null,
    val body:String?=null,
    val timeStamp: Timestamp = Timestamp.now()
    )