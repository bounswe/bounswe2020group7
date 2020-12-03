package com.cmpe451.platon.networkmodels.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

data class Following(
        val followings: List<FollowPerson>
)

data class Followers(
        val followers: List<FollowPerson>
)

data class FollowPerson(
        val id: Int,
        val e_mail: String?,
        val name: String,
        val surname: String,
        val rate:Double?,
        val is_private:Boolean=false
)
