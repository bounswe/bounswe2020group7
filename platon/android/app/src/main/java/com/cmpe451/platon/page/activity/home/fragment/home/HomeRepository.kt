package com.cmpe451.platon.page.activity.home.fragment.home

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.*
import com.cmpe451.platon.util.Definitions
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
 *  It is a bridge between end point and home view model.
 */

class HomeRepository{


    val activityStreamResourceResponse:MutableLiveData<Resource<ActivityStream>> = MutableLiveData()

    val trendingProjectsResourceResponse:MutableLiveData<Resource<TrendingProjects>> = MutableLiveData()
    val upcomingEventsResourceResponse:MutableLiveData<Resource<UpcomingEvents>> = MutableLiveData()

    val calendarResourceResponse:MutableLiveData<Resource<List<CalendarItem>>> = MutableLiveData()

    fun getActivityStream(token:String, page:Int?, pageSize:Int?){
        val service = RetrofitClient.getService()
        val call = service.getActivityStream(token, page, pageSize)

        activityStreamResourceResponse.value = Resource.Loading()
        call.enqueue(object : Callback<ActivityStream?> {
            override fun onResponse(call: Call<ActivityStream?>, response: Response<ActivityStream?>) {
                when{
                    response.isSuccessful && response.body() != null -> activityStreamResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> activityStreamResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> activityStreamResourceResponse.value = Resource.Error("Unknown Error")
                }
            }

            override fun onFailure(call: Call<ActivityStream?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })

    }

    fun fetchUpcomingEvents(page: Int?, pageSize: Int?){

        val service = RetrofitClient.getService()
        val call = service.getUpcomingEvents(page, pageSize)

        upcomingEventsResourceResponse.value = Resource.Loading()
        call.enqueue(object : Callback<UpcomingEvents?> {
            override fun onResponse(call: Call<UpcomingEvents?>, response: Response<UpcomingEvents?>) {
                when{
                    response.isSuccessful && response.body() != null -> upcomingEventsResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> upcomingEventsResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> upcomingEventsResourceResponse.value = Resource.Error("Unknown Error")
                }
            }

            override fun onFailure(call: Call<UpcomingEvents?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })

    }

    fun fetchTrendingProjects( number:Int){
        val service = RetrofitClient.getService()
        val call = service.getTrendingProjects(number)

        trendingProjectsResourceResponse.value = Resource.Loading()
        call.enqueue(object : Callback<TrendingProjects?> {
            override fun onResponse(call: Call<TrendingProjects?>, response: Response<TrendingProjects?>) {
                when{
                    response.isSuccessful && response.body() != null -> trendingProjectsResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> trendingProjectsResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> trendingProjectsResourceResponse.value = Resource.Error("Unknown Error")
                }
            }

            override fun onFailure(call: Call<TrendingProjects?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })

    }


    fun getCalendar(token: String) {
        val service = RetrofitClient.getService()
        val call = service.getCalendar(token)

        calendarResourceResponse.value = Resource.Loading()
        call.enqueue(object : Callback<List<CalendarItem>?> {
            override fun onResponse(call: Call<List<CalendarItem>?>, response: Response<List<CalendarItem>?>) {
                when{
                    response.isSuccessful && response.body() != null -> calendarResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> calendarResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> calendarResourceResponse.value = Resource.Error("Unknown Error")
                }
            }

            override fun onFailure(call: Call<List<CalendarItem>?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }


}