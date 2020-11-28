package com.cmpe451.platon.networkmodels

data class UserInfoResponse (
        val id: Int,
        val e_mail: String,
        val google_scholar_name: String,
        val job: String,
        val surname: String,
        val name: String,
        val researchgate_name:String,
        val rate:Double,
        val profile_photo:String
//        val isPrivate:Boolean
)