package com.cmpe451.platon.networkmodels.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

data class Followings(
        val followings: List<OtherUser>
)

data class Followers(
        val followers: List<OtherUser>
)

data class OtherUser(
        val id: Int,
        val e_mail: String?,
        val name: String,
        val surname: String,
        val job: String?,
        val researchgate_name:String?,
        val google_scholar_name: String?,
        val rate:Double?,
        val profile_photo:String?,
        val isPrivate:Boolean=false

)
