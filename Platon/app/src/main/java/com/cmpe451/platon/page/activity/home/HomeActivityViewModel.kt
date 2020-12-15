package com.cmpe451.platon.page.activity.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject

class HomeActivityViewModel(application: Application):AndroidViewModel(application){

    var getUserResourceResponse: LiveData<Resource<User>>
    val getUserFollowRequestsResourceResponse: MutableLiveData<Resource<FollowRequests>>
    val getUserNotificationsResourceResponse: MutableLiveData<Resource<Notifications>>
    val acceptRequestResourceResponse: MutableLiveData<Resource<JsonObject>>

    val getSearchHistoryResourceResponse: MutableLiveData<Resource<SearchHistory>>
    val getSearchUserResourceResponse:MutableLiveData<Resource<UserSearch>>
    var getJobListResourceResponse: MutableLiveData<Resource<List<Job>>>

    val repository = HomeActivityRepository()

    init {
        getUserResourceResponse = repository.userResourceResponse
        getUserFollowRequestsResourceResponse = repository.userFollowRequestsResourceResponse
        getUserNotificationsResourceResponse = repository.userNotificationsResourceResponse
        acceptRequestResourceResponse = repository.acceptRequestResourceResponse
        getSearchHistoryResourceResponse = repository.searchHistoryResourceResponse
        getSearchUserResourceResponse = repository.searchUserResourceResponse
        getJobListResourceResponse = repository.jobListResourceResponse
    }

    fun getAllJobs() {
        repository.getAllJobs()
    }

    fun fetchUser(token:String?){
        if(token != null){
            repository.getUser(token)
        }
    }

    fun getNotifications(token:String){
        repository.getNotifications(token)
    }

    fun getFollowRequests(id:Int, token:String){
        repository.getFollowRequests(id, token)

    }


    fun acceptFollowRequest(followerId: Int, followingId:Int, token:String) {
        repository.acceptFollowRequest(followerId, followingId,token)
    }

    fun deleteFollowRequest(followerId: Int, followingId:Int, token:String) {
        repository.deleteFollowRequest(followerId, followingId,token)
    }

    fun fetchSearchHistory(token: String, i: Int) {
        repository.fetchSearchHistory(token, i)
    }

    fun searchUser(token:String, query:String, job:Int?, page:Int?, perPage:Int?){
        repository.searchUser(token, query, job, page, perPage)
    }

}