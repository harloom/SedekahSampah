package com.overdrive.sedekahsampah.models

data class User(
 val uid : String,
 val displayName :String? = null,
 val photoUrl : String? =null,
 val lastLogin : Long? =null,
 val numberPhone : String? =null

)