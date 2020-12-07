package com.cmpe451.platon.network.models


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

data class Notification(
        val id:Int,
        val link :String?,
        val related_users:List<Int>,
        val text: String?,
        val timestamp:String?


)

data class FollowRequests(
        val follow_requests:List<FollowRequest>
)

data class FollowRequest(
        val e_mail:String?,
        val id:Int,
        val is_private:Boolean,
        val name:String?,
        val surname:String?,
        var rate:Double
)

