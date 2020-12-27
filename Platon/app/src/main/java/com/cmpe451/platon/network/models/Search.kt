package com.cmpe451.platon.network.models

import android.telephony.cdma.CdmaCellLocation

data class SearchHistory(
        val search_history:List<SearchHistoryElement>)

data class SearchHistoryElement(
        val number_of_use:Int,
        val query: String
)

data class Search(
    val number_of_pages:Int,
    val result_list:List<SearchElement>
)

data class SearchElement(
    val id:Int,
    val is_private:Int?,
    val title: String?,
    val deadline:String?,

    //for upcoming event
    val acronym:String?,
    val location: String?,
    val link:String?,
    val date:String?,

    //for workspace
    val description: String?,
    val creation_time:String?,
    val state: Int?,
    val max_collaborators:Int?,
    val contributor_list: List<Contributor>?,

    //for user
    val job_id:Int?,
    val name:String?,
    val surname:String?,
    val profile_photo:String?
)
