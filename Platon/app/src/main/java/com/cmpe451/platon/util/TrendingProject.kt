package com.cmpe451.platon.util

import android.graphics.drawable.Drawable




class TrendingProject(val project_title:String, val img:Drawable?, val description:String, val reason:TREND) {
    enum class TREND {
        POPULAR, MOST_LIKED, NEW_COMERS, HOT
    }


}