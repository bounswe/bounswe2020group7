package com.cmpe451.platon.page.fragment.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.util.Definitions
import com.cmpe451.platon.util.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeRepository{




    fun fetchActivityStream():ArrayList<Definitions.ActivityStream> {
        return arrayListOf(
            Definitions.ActivityStream(
                null,
                "Edison created lightbulb project"
            ),
            Definitions.ActivityStream(
                    null,
                "Einstein unfollowed Relativity Theory",

                ),
            Definitions.ActivityStream(
                    null,
                "Burak Ömür joined artificial intelligent project",
            ),
            Definitions.ActivityStream(
                    null,
                "Alper Ahmetoğlu registered Platon."
            )
        )
    }

    fun fetchUpcomingEvents():ArrayList<Definitions.UpcomingEvent> {

        return arrayListOf(
            Definitions.UpcomingEvent(
                "CVPR",
                "Computer Vision conference, yearly",
                    null,
                Definitions.UpcomingEvent.TYPE.CONFERENCE,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "IJSR",
                "International Journal of Science and Research (IJSR) is a journal",
                    null,
                Definitions.UpcomingEvent.TYPE.JOURNAL,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "ACS Nano",
                "Call for papers, acs nano",
                    null,
                Definitions.UpcomingEvent.TYPE.JOURNAL,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "ECCV",
                "Computer Vision conference, yearly, European Conference on Computer Vision",
                    null,
                Definitions.UpcomingEvent.TYPE.CONFERENCE,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "KDD",
                "This demonstration video shows you the virtual interface for KDD 2020. Learn more about how to participate, join the conversation, and meet your peers.",
                    null,
                Definitions.UpcomingEvent.TYPE.CONFERENCE,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "IEEE Transactions on Robotics",
                "The publications of the Institute of Electrical and Electronics Engineers (IEEE) constitute around 30% of the world literature in the electrical and electronics engineering and computer science fields,[citation needed] publishing well over 100 peer-reviewed journals",
                    null,
                Definitions.UpcomingEvent.TYPE.JOURNAL,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "IEEE Transactions on Computers",
                "IEEE Transactions on Computers is a monthly peer-reviewed scientific journal covering all aspects of computer design",
                    null,
                Definitions.UpcomingEvent.TYPE.JOURNAL,
                "12.02.2021"
            )


        )
    }

    fun fetchTrendingProjects():ArrayList<Definitions.TrendingProject> {

        return arrayListOf(
            Definitions.TrendingProject(
                "Forest Landscape Restoration",
                    null,
                "In the face of the global COVID-19 recession, countries are looking at stimulus packages to kick-start their stalled economies. The recovery " +
                        "from this crisis also coincides with a critical opportunity to fight against ecosystem degradation and climate change. In this opinion article, I " +
                        "put in perspective that by investing in ecological restoration, governments do not have to choose between economic priorities and environmental concerns. " +
                        "First, I describe the restoration economy and give real-world examples of how investing in restoration activities can simultaneously ease pressure on the " +
                        "environment and create immediate jobs and revenues. Then I suggest that to obtain political attraction, a successful restoration strategy will require a" +
                        " triple-bottom-line approach to ensure that in addition to environmental objectives, stakeholders integrate socioeconomic outcomes in decision-making. Finally, " +
                        "I conclude that a new economic approach that prioritizes investment in our ecological capital will necessitate transdisciplinary policies to build bridges across " +
                        "the different silos of the economy and the environment.\n",
                Definitions.TrendingProject.TREND.HOT
            ),
            Definitions.TrendingProject(
                "Diffusion with stochastic resetting in comb-like structures",
                    null,
                "To investigate diffusion in comb-like structures (e.g. three dimensional comb) under different stochastic resetting mechanisms",
                Definitions.TrendingProject.TREND.POPULAR
            ),
            Definitions.TrendingProject(
                "Frequency metrology and clocks",
                    null,
                "High-precision measurements with molecules may refine our knowledge of various fields of physics, from atmospheric and interstellar physics to the standard model or " +
                        "physics beyond it. Most of them can be cast as absorption frequency measurements, particularly " +
                        "in the mid-infrared \"molecular fingerprint\" region, creating the need for narrow-linewidth lasers of well-controlled frequency.",
                Definitions.TrendingProject.TREND.MOST_LIKED
            ),
            Definitions.TrendingProject(
                "Unique Features and Applications of Hollow Core Optical Fibres",
                    null,
                "Investigate unique features of Hollow Core Fibres (stability of propagation time through them, low non-linearity, low dispersion, etc.) and look " +
                        "for new applications in which these features bring significant advantage over standard optical fibres used today.",
                Definitions.TrendingProject.TREND.NEW_COMERS
            ),
            Definitions.TrendingProject(
                "Purecomb (DARPA PULSE program)",
                    null,
                "Extraction of ultra low noise microwave by optical frequeny division from femtosecond laser frequency comb. ",
                Definitions.TrendingProject.TREND.HOT
            ),
            Definitions.TrendingProject(
                "Samsung-Dev",
                    null,
                "Samsung IOT development.",
                Definitions.TrendingProject.TREND.POPULAR
            ),
            Definitions.TrendingProject(
                "Search Engine",
                    null,
                "A turkish search engine.",
                Definitions.TrendingProject.TREND.HOT
            ),
            Definitions.TrendingProject(
                "Youtube video downloader",
                    null,
                "To download video from youtube with simple and effective ways.",
                Definitions.TrendingProject.TREND.HOT
            )
        )

    }


}