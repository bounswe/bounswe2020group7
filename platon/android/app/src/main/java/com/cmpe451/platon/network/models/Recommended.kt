package com.cmpe451.platon.network.models

/*
 Data classes objects used by Retrofit in order to parse responses of the request. Namings of the objects are self explanatory.
 */

data class RecommendedUser(
    val id:Int,
    val name:String,
    val surname:String,
    val profile_photo:String,
    val job: String,
    val institution:String,
    val is_private:Int
)
data class RecommendedUserList(
    val recommendation_list:List<RecommendedUser>
)
data class RecommendedWorkspace(
    val contributor_list:List<ContributorList>,
    val creator_id:Int,
    val description:String,
    val id:Int,
    val state:Int,
    val title:String
)
data class ContributorList(
    val id:Int,
    val name:String,
    val surname: String
)
data class RecommendedWorkspaceList(
    val recommendation_list:List<RecommendedWorkspace>
)