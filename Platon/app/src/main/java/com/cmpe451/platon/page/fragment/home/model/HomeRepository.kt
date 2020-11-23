package com.cmpe451.platon.page.fragment.home.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.ActivityCompat
import com.cmpe451.platon.R
import com.cmpe451.platon.util.Definitions


class HomeRepository(sharedPreferences: SharedPreferences){

    fun fetchActivityStream(context: Context?):ArrayList<Definitions.ActivityStream> {
        return arrayListOf(
            Definitions.ActivityStream(
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                "Ertuğrul Margot Robbie'nin fotoğrafını beğendi"
            ),
            Definitions.ActivityStream(
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                "Einstein Rölativite projesini takip etmeyi bıraktı.",

            ),
            Definitions.ActivityStream(
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                "Emanuel Chinenye Emenike yeni bir proje yarattı.",
            ),
            Definitions.ActivityStream(
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                "Kendi sağlığınız ve çevrenizdekilerin sağlığı için evde kal"
            )
        )
    }

    fun fetchUpcomingEvents(context: Context?):ArrayList<Definitions.UpcomingEvent> {

        return arrayListOf(
            Definitions.UpcomingEvent(
                "CVPR",
                "Computer Vision conference, yearly",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                Definitions.UpcomingEvent.TYPE.CONFERENCE,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "IJSR",
                "International Journal of Science and Research (IJSR) is a journal",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_account_24px) },
                Definitions.UpcomingEvent.TYPE.JOURNAL,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "ACS Nano",
                "Call for papers, acs nano",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_work_24px) },
                Definitions.UpcomingEvent.TYPE.JOURNAL,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "ECCV",
                "Computer Vision conference, yearly, European Conference on Computer Vision",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                Definitions.UpcomingEvent.TYPE.CONFERENCE,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "KDD",
                "This demonstration video shows you the virtual interface for KDD 2020. Learn more about how to participate, join the conversation, and meet your peers.",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                Definitions.UpcomingEvent.TYPE.CONFERENCE,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "IEEE Transactions on Robotics",
                "The publications of the Institute of Electrical and Electronics Engineers (IEEE) constitute around 30% of the world literature in the electrical and electronics engineering and computer science fields,[citation needed] publishing well over 100 peer-reviewed journals",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                Definitions.UpcomingEvent.TYPE.JOURNAL,
                "12.02.2021"
            ),
            Definitions.UpcomingEvent(
                "IEEE Transactions on Computers",
                "IEEE Transactions on Computers is a monthly peer-reviewed scientific journal covering all aspects of computer design",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                Definitions.UpcomingEvent.TYPE.JOURNAL,
                "12.02.2021"
            )


        )
    }

    fun fetchTrendingProjects(context: Context?):ArrayList<Definitions.TrendingProject> {

        return arrayListOf(
            Definitions.TrendingProject(
                "Forest Landscape Restoration",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
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
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                "To investigate diffusion in comb-like structures (e.g. three dimensional comb) under different stochastic resetting mechanisms",
                Definitions.TrendingProject.TREND.POPULAR
            ),
            Definitions.TrendingProject(
                "Frequency metrology and clocks",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_account_24px) },
                "High-precision measurements with molecules may refine our knowledge of various fields of physics, from atmospheric and interstellar physics to the standard model or " +
                        "physics beyond it. Most of them can be cast as absorption frequency measurements, particularly " +
                        "in the mid-infrared \"molecular fingerprint\" region, creating the need for narrow-linewidth lasers of well-controlled frequency.",
                Definitions.TrendingProject.TREND.MOST_LIKED
            ),
            Definitions.TrendingProject(
                "Unique Features and Applications of Hollow Core Optical Fibres",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_work_24px) },
                "Investigate unique features of Hollow Core Fibres (stability of propagation time through them, low non-linearity, low dispersion, etc.) and look " +
                        "for new applications in which these features bring significant advantage over standard optical fibres used today.",
                Definitions.TrendingProject.TREND.NEW_COMERS
            ),
            Definitions.TrendingProject(
                "Purecomb (DARPA PULSE program)",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                "Extraction of ultra low noise microwave by optical frequeny division from femtosecond laser frequency comb. ",
                Definitions.TrendingProject.TREND.HOT
            ),
            Definitions.TrendingProject(
                "Samsung-Dev",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                "Samsung IOT development.",
                Definitions.TrendingProject.TREND.POPULAR
            ),
            Definitions.TrendingProject(
                "Search Engine",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                "A turkish search engine.",
                Definitions.TrendingProject.TREND.HOT
            ),
            Definitions.TrendingProject(
                "Youtube video downloader",
                context?.let { ActivityCompat.getDrawable(it, R.drawable.ic_home_24px) },
                "To download video from youtube with simple and effective ways.",
                Definitions.TrendingProject.TREND.HOT
            )
        )

    }


}