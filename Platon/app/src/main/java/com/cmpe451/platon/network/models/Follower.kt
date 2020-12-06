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
