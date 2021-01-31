package com.cmpe451.platon.network.models

/*
 Data classes objects used by Retrofit in order to parse responses of the request. Namings of the objects are self explanatory.
 */

data class Research(
    val id: Int,
    val title: String,
    val description: String,
    val year: Int
)


data class Researches(
    val research_info: List<Research>?,
    val number_of_pages: Int
)