package com.cmpe451.platon.network.models


data class Research(
    val id: Int,
    val title: String,
    val description: String,
    val year: Int
)


data class Researches(
    val research_info: List<Research>,
    val number_of_pages: Int
)