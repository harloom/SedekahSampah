package com.overdrive.sedekahsampah.models



data class Post (
 val id : String,
 val uid : String,
 val title : String,
 val body : String,
 val images : List<ListImage>,
 val timeStamp  : Int

)