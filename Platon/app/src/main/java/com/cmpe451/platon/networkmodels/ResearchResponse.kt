package com.cmpe451.platon.networkmodels

data class ResearchResponse (
    val research_info: List<Research>
)

data class Research(
    val description: String,
    val id: Int,
    val title: String,
    val year: Int
)