package com.cmpe451.platon.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.VibrationEffect
import android.os.Vibrator
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.login.view.LoginFragment

/**
 * Class to keep definitive and serial data.
 */
class Definitions {

    val FORGOT_PASS_ADDRESS = ""
    val LOGIN_ADDRESS = ""
    val REGISTER_ADDRESS = ""
    val TRENDING_PROJECT_GUESTS_ADDRESS = ""
    val UPCOMING_EVENTS_ADDRESS = ""

    class User(val name:String, val surname:String, val rating: Double, val bio:String)
  
    /**
     * Trending projects are serialized according to this.
     * @param project_title Title of the trending project
     * @param img Drawable of the trending project, nullable
     * @param description A short description of the trending project
     * @param reason The reason for trending
     */
    class TrendingProject(val project_title:String, val img: Drawable?, val description:String, val reason:TREND) {
        /**
         * Reason for trending is chosen by enumerator.
         */
        enum class TREND {
            POPULAR, MOST_LIKED, NEW_COMERS, HOT
        }
    }


    /**
     * Upcoming events are serialized according to this.
     * @param title Title of the upcoming event
     * @param img Drawable of the trending project, nullable
     * @param desc A short description of the upcoming event
     * @param type Type of the upcoming event
     * @param date Date as string of the event
     */
    class UpcomingEvent (val title:String, val desc:String, val img: Drawable? ,val type:TYPE, val date:String){
        /**
         * Type of the event chosen by this.
         */
        enum class TYPE {
            CONFERENCE, JOURNAL
        }
    }


    fun vibrate(ms:Long=50, activity:BaseActivity){
        val vib = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

}
