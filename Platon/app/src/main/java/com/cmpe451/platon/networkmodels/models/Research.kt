package com.cmpe451.platon.networkmodels.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation


data class Research(
    val id: Int,
    val uid:Int,
    val title: String,
    val description: String,
    val year: Int
)


data class Researches(
    val research_info: List<Research>
)