package com.cmpe451.platon.page.activity.home.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.ActivityStream
import com.cmpe451.platon.network.models.ActivityStreamElement
import com.cmpe451.platon.network.models.TrendingProjects
import com.cmpe451.platon.network.models.UpcomingEvents
import com.cmpe451.platon.util.Definitions

class HomeViewModel: ViewModel() {

    private val repository: HomeRepository = HomeRepository()
    val getActivityStreamResourceResponse: MutableLiveData<Resource<ActivityStream>>

    val getTrendingProjectsResourceResponse: MutableLiveData<Resource<TrendingProjects>>
    val getUpcomingEventsResourceResponse: MutableLiveData<Resource<UpcomingEvents>>
    val getCalendarResourceResponse: MutableLiveData<Resource<List<CalendarItem>>>
    init {
        getActivityStreamResourceResponse = repository.activityStreamResourceResponse
        getTrendingProjectsResourceResponse= repository.trendingProjectsResourceResponse
        getUpcomingEventsResourceResponse = repository.upcomingEventsResourceResponse

        getCalendarResourceResponse = repository.calendarResourceResponse

    }




    fun getUpcomingEvents(page:Int?, pageSize: Int?) {
        repository.fetchUpcomingEvents(page, pageSize)
    }

    fun getTrendingProjects(number:Int) {
        repository.fetchTrendingProjects(number)
    }

    fun getActivities(token:String, page:Int?, pageSize:Int?) {
        repository.getActivityStream(token,page, pageSize)
    }

    fun getCalendar(token: String){
        repository.getCalendar(token)
    }


}