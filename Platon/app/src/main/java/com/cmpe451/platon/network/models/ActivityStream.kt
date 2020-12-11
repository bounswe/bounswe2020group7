package com.cmpe451.platon.network.models

data class ActivityStream(val data:List<ActivityStreamElement>?)


data class ActivityStreamElement(
        val image:String?,
        val message:String?
)