package com.cmpe451.platon.network.models

import java.util.*

data class Workspace (
    val id: Int,
    val title: String,
    val max_collaborators:Int,
    val deadline:String,
    val is_private:Boolean?,
    val requirements:String,
    val description: String,
    val state: Int,
    val skills: List<String>,
)

data class Job(
    val id: Int,
    val name:String
)
