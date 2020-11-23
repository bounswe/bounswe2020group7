package com.cmpe451.platon.page.fragment.home.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView
import com.cmpe451.platon.util.Definitions

interface HomeContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun getUpcomingEvents(): ArrayList<Definitions.UpcomingEvent>
        fun getTrendingProjects(): ArrayList<Definitions.TrendingProject>
        fun getActivities(): ArrayList<Definitions.ActivityStream>
    }

}