package com.cmpe451.platon.page.fragment.preLogin.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView
import com.cmpe451.platon.util.Definitions.TrendingProject
import com.cmpe451.platon.util.Definitions.UpcomingEvent

interface PreLoginContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onPreLoginMade()
        fun getUpcomingEvents(): ArrayList<UpcomingEvent>
        fun getTrendingProjects(): ArrayList<TrendingProject>

    }

}