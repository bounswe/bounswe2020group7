package com.cmpe451.platon.networkmodels

data class Followers(
        val followers:List<Follower>
)
data class Followings(
        val followings:List<Follower>
)
data class Follower(
        val e_mail: String,
        val id: Int,
        val is_private: Boolean,
        val name: String,
        val rate: Int,
        val surname: String
)
