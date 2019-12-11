package com.overdrive.sedekahsampah.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Post (
 val id : String,
 val uid : String,
 val title : String,
 val body : String,
 val images : List<ListImage>,
 val timeStamp  : Int

) : Parcelable