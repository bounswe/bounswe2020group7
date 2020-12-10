package com.cmpe451.platon.network.models

data class Workspace (
    val id: Int,
    val title: String,
    val description: String,
    val state: String,
)

data class Job(
    val id: Int,
    val name:String
)