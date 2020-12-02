package com.cmpe451.platon.page.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.util.Definitions

class HomeViewModel: ViewModel() {

    private val repository: HomeRepository = HomeRepository()


    fun getUpcomingEvents(): ArrayList<Definitions.UpcomingEvent> {
        return repository.fetchUpcomingEvents()
    }

    fun getTrendingProjects(): ArrayList<Definitions.TrendingProject> {
        return  repository.fetchTrendingProjects()
    }

    fun getActivities(): ArrayList<Definitions.ActivityStream> {
        return repository.fetchActivityStream()
    }


}