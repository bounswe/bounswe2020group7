package com.cmpe451.platon.page.fragment.preLogin.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.ActivityCompat
import com.cmpe451.platon.R
import com.cmpe451.platon.util.TrendingProject
import com.cmpe451.platon.util.UpcomingEvent

//import com.cmpe451.elevator.HttpRequest


class PreLoginRepository(sharedPreferences: SharedPreferences){


    fun fetchUpcomingEvents(context:Context?):Array<UpcomingEvent> {

        return arrayOf(
                UpcomingEvent("CVPR", "Computer Vision conference, yearly", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, UpcomingEvent.TYPE.CONFERENCE, "12.02.2021"),
                UpcomingEvent("IJSR", "International Journal of Science and Research (IJSR) is a journal", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_account_24px) }, UpcomingEvent.TYPE.JOURNAL, "12.02.2021"),
                UpcomingEvent("ACS Nano", "Call for papers, acs nano", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_work_24px) }, UpcomingEvent.TYPE.JOURNAL, "12.02.2021"),
                UpcomingEvent("ECCV", "Computer Vision conference, yearly, European Conference on Computer Vision", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, UpcomingEvent.TYPE.CONFERENCE, "12.02.2021"),
                UpcomingEvent("KDD", "This demonstration video shows you the virtual interface for KDD 2020. Learn more about how to participate, join the conversation, and meet your peers.", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, UpcomingEvent.TYPE.CONFERENCE, "12.02.2021"),
                UpcomingEvent("IEEE Transactions on Robotics", "The publications of the Institute of Electrical and Electronics Engineers (IEEE) constitute around 30% of the world literature in the electrical and electronics engineering and computer science fields,[citation needed] publishing well over 100 peer-reviewed journals", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, UpcomingEvent.TYPE.JOURNAL, "12.02.2021"),
                        UpcomingEvent("IEEE Transactions on Computers", "IEEE Transactions on Computers is a monthly peer-reviewed scientific journal covering all aspects of computer design", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, UpcomingEvent.TYPE.JOURNAL, "12.02.2021")


        )
    }

    fun fetchTrendingProjects(context:Context?):Array<TrendingProject> {

        return arrayOf(
                TrendingProject("PLATON", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, "A collaboration application for all platforms.", TrendingProject.TREND.HOT),
                TrendingProject("ANDROID: Reborn", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, "A new version of android.", TrendingProject.TREND.POPULAR),
                TrendingProject("DRAGON CENTER", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_account_24px) }, "Msi is developing a new version of dragon center. Join now!", TrendingProject.TREND.MOST_LIKED),
                TrendingProject("COMPUTER VISION For Phones", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_work_24px) }, "A research for cv, prepared for cvvp.", TrendingProject.TREND.NEW_COMERS),
                TrendingProject("D++", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, "A new language development for childs.", TrendingProject.TREND.HOT),
        TrendingProject("Samsung-Dev", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, "Samsung IOT development.", TrendingProject.TREND.POPULAR),
        TrendingProject("Search Engine", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, "A turkish search engine.", TrendingProject.TREND.HOT),
        TrendingProject("Youtube video downloader", context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) }, "To download video from youtube with simple and effective ways.", TrendingProject.TREND.HOT))

    }


}