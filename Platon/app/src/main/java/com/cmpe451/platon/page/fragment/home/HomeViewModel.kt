package com.cmpe451.platon.page.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.ActivityStream
import com.cmpe451.platon.network.models.ActivityStreamElement
import com.cmpe451.platon.util.Definitions

class HomeViewModel: ViewModel() {

    private val repository: HomeRepository = HomeRepository()
    val getActivityStreamResourceResponse: MutableLiveData<Resource<List<ActivityStreamElement>>>

    init {
        getActivityStreamResourceResponse = repository.activityStreamResourceResponse
    }




    fun getUpcomingEvents(): ArrayList<Definitions.UpcomingEvent> {
        return repository.fetchUpcomingEvents()
    }

    fun getTrendingProjects(): ArrayList<Definitions.TrendingProject> {
        return  repository.fetchTrendingProjects()
    }

    fun getActivities(token:String) {
        repository.getActivityStream(token)
    }


}