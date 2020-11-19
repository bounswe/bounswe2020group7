package com.cmpe451.platon.util

import android.graphics.drawable.Drawable

class UpcomingEvent (val title:String, val desc:String, val img: Drawable? ,val type:TYPE, val date:String){

    enum class TYPE {
       CONFERENCE, JOURNAL
    }


}