package com.overdrive.sedekahsampah.models

data class User(
 @Transient

 val uid : String,
 val displayName :String? = null,
 val photoUrl : String? =null,
 val lastLogin : Long? =null,
 val numberPhone : String? =null

)