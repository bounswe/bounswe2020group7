package com.cmpe451.platon.network.models

data class SearchHistory(
        val search_history:List<SearchHistoryElement>)

data class SearchHistoryElement(
        val number_of_use:Int,
        val query: String
)

data class UserSearch(
    val number_of_pages:Int,
    val result_list:List<SearchElement>
)

data class SearchElement(
        val id:Int,
    val is_private:Int,
    val job_id:Int,
    val name:String,
    val surname:String,
    val profile_photo:String
)
