package com.cmpe451.platon.networkmodels.models

import androidx.room.Entity
import androidx.room.PrimaryKey


data class User (
        val id: Int,
        val e_mail: String,
        val name: String,
        val surname: String,
        val job: String,
        val researchgate_name:String?,
        val google_scholar_name: String?,
        val rate:Double=-1.0,
        val profile_photo:String?,
        val isPrivate:Boolean
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
        val isPrivate:Boolean
)