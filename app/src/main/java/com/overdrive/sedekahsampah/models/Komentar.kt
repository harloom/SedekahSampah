package com.overdrive.sedekahsampah.models

import com.google.firebase.firestore.DocumentId

data class Komentar (
    @DocumentId
    val id : String ,
    val idPost : String,
    val body :String,
    val timeStamp : Int
    )