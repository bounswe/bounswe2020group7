package com.cmpe451.platon.network.models


data class Following(
        val number_of_pages: Int,
        val followings: List<FollowPerson>
)

data class Followers(
        val number_of_pages: Int,
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

data class Notifications(
        val notification_list:List<Notification>,
        val number_of_pages:Int
)


data class FollowRequests(
        val number_of_pages: Int,
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

