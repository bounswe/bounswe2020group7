package com.cmpe451.platon.network.models


data class User (
        val id: Int,
        val e_mail: String,
        val name: String,
        val surname: String,
        val job: String,
        val institution: String?,
        val researchgate_name:String?,
        val google_scholar_name: String?,
        val rate:Double=-1.0,
        val profile_photo:String?,
        val is_private:Boolean
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
        val following_status:Int,
        val institution:String,
        val profile_photo:String?,
        val is_private:Boolean
)

data class Auth(
        val token: String,
        val user_id:Int
)

