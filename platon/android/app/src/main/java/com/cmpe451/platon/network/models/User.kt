package com.cmpe451.platon.network.models

/*
 Data classes objects used by Retrofit in order to parse responses of the request. Namings of the objects are self explanatory.
 */

data class Comment(
        val comment_id: Int,
        val owner_id: Int,
        val commented_user_id: Int,
        val text:String,
        val timestamp:String,
        val rate:Double,
)


data class AllComments(
        val number_of_pages:Int,
        val result:List<Comment>
)
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
        val is_private:Boolean,
        val is_email_allowed:Boolean,
        val is_notification_allowed:Boolean
)

data class OtherUser(
        val can_comment:Boolean,
        val id: Int?,
        val e_mail: String?,
        val name: String?,
        val surname: String?,
        val job: String?,
        val researchgate_name:String?,
        val google_scholar_name: String?,
        val rate:Double?,
        val following_status:Int?,
        val institution:String?,
        val profile_photo:String?,
        val is_private:Boolean?
)

data class Auth(
        val token: String,
        val user_id:Int
)
data class Skill(
        val id:Int,
        val name: String
)
data class Skills(
        val skills:List<Skill>?
)
