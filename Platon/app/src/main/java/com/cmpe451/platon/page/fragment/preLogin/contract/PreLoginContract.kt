package com.cmpe451.platon.page.fragment.preLogin.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView
import com.cmpe451.platon.util.TrendingProject
import com.cmpe451.platon.util.UpcomingEvent

interface PreLoginContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onPreLoginMade()
        fun getUpcomingEvents(): Array<UpcomingEvent>
        fun getTrendingProjects(): Array<TrendingProject>

    }

}