package com.cmpe451.platon.page.fragment.preLogin.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.ActivityCompat
import com.cmpe451.platon.R
import com.cmpe451.platon.util.Definitions.TrendingProject
import com.cmpe451.platon.util.Definitions.UpcomingEvent

class PreLoginRepository(){


    fun fetchUpcomingEvents():ArrayList<UpcomingEvent> {

        return arrayListOf(
                UpcomingEvent("CVPR",
                        "Computer Vision conference, yearly",
                        null,
                        UpcomingEvent.TYPE.CONFERENCE,
                        "12.02.2021"),
                        UpcomingEvent("IJSR",
                                "International Journal of Science and Research (IJSR) is a journal",
                                null,
                                UpcomingEvent.TYPE.JOURNAL,
                                "12.02.2021"),
                        UpcomingEvent("ACS Nano",
                                "Call for papers, acs nano",
                                null,
                                UpcomingEvent.TYPE.JOURNAL,
                                "12.02.2021"),
                        UpcomingEvent("ECCV",
                                "Computer Vision conference, yearly, European Conference on Computer Vision",
                                null,
                                UpcomingEvent.TYPE.CONFERENCE,
                                "12.02.2021"),
                        UpcomingEvent("KDD",
                                "This demonstration video shows you the virtual interface for KDD 2020. Learn more about how to participate, join the conversation, and meet your peers.",
                                null,
                                UpcomingEvent.TYPE.CONFERENCE,
                                "12.02.2021"),
                        UpcomingEvent("IEEE Transactions on Robotics",
                                "The publications of the Institute of Electrical and Electronics Engineers (IEEE) constitute around 30% of the world literature in the electrical and electronics engineering and computer science fields,[citation needed] publishing well over 100 peer-reviewed journals",
                                null,
                                UpcomingEvent.TYPE.JOURNAL,
                                "12.02.2021"),
                        UpcomingEvent("IEEE Transactions on Computers",
                                "IEEE Transactions on Computers is a monthly peer-reviewed scientific journal covering all aspects of computer design",
                                null,
                                UpcomingEvent.TYPE.JOURNAL,
                                "12.02.2021")


        )
    }

    fun fetchTrendingProjects():ArrayList<TrendingProject> {

        return arrayListOf(
                TrendingProject("Forest Landscape Restoration",null,
                        "In the face of the global COVID-19 recession, countries are looking at stimulus packages to kick-start their stalled economies. The recovery " +
                                "from this crisis also coincides with a critical opportunity to fight against ecosystem degradation and climate change. In this opinion article, I " +
                                "put in perspective that by investing in ecological restoration, governments do not have to choose between economic priorities and environmental concerns. " +
                                "First, I describe the restoration economy and give real-world examples of how investing in restoration activities can simultaneously ease pressure on the " +
                                "environment and create immediate jobs and revenues. Then I suggest that to obtain political attraction, a successful restoration strategy will require a" +
                                " triple-bottom-line approach to ensure that in addition to environmental objectives, stakeholders integrate socioeconomic outcomes in decision-making. Finally, " +
                                "I conclude that a new economic approach that prioritizes investment in our ecological capital will necessitate transdisciplinary policies to build bridges across " +
                                "the different silos of the economy and the environment.\n"
                        , TrendingProject.TREND.HOT),
                TrendingProject("Diffusion with stochastic resetting in comb-like structures",
                        null,
                        "To investigate diffusion in comb-like structures (e.g. three dimensional comb) under different stochastic resetting mechanisms", TrendingProject.TREND.POPULAR),
                TrendingProject("Frequency metrology and clocks", null,
                        "High-precision measurements with molecules may refine our knowledge of various fields of physics, from atmospheric and interstellar physics to the standard model or " +
                                "physics beyond it. Most of them can be cast as absorption frequency measurements, particularly " +
                                "in the mid-infrared \"molecular fingerprint\" region, creating the need for narrow-linewidth lasers of well-controlled frequency.", TrendingProject.TREND.MOST_LIKED),
                TrendingProject("Unique Features and Applications of Hollow Core Optical Fibres",
                        null,
                        "Investigate unique features of Hollow Core Fibres (stability of propagation time through them, low non-linearity, low dispersion, etc.) and look " +
                                "for new applications in which these features bring significant advantage over standard optical fibres used today.", TrendingProject.TREND.NEW_COMERS),
                TrendingProject("Purecomb (DARPA PULSE program)",
                        null,
                        "Extraction of ultra low noise microwave by optical frequeny division from femtosecond laser frequency comb. "
                        , TrendingProject.TREND.HOT),
        TrendingProject("Samsung-Dev", null, "Samsung IOT development.", TrendingProject.TREND.POPULAR),
        TrendingProject("Search Engine", null, "A turkish search engine.", TrendingProject.TREND.HOT),
        TrendingProject("Youtube video downloader", null, "To download video from youtube with simple and effective ways.", TrendingProject.TREND.HOT))

    }


}