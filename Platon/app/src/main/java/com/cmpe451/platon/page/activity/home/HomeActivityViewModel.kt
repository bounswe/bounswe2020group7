package com.cmpe451.platon.page.activity.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject

class HomeActivityViewModel(application: Application):AndroidViewModel(application){

    val getUserResourceResponse: MutableLiveData<Resource<User>>
    val getUserFollowRequestsResourceResponse: MutableLiveData<Resource<FollowRequests>>
    val getUserNotificationsResourceResponse: MutableLiveData<Resource<Notifications>>
    val acceptRequestResourceResponse: MutableLiveData<Resource<JsonObject>>

    val getSearchHistoryResourceResponse: MutableLiveData<Resource<SearchHistory>>
    val getSearchUserResourceResponse:MutableLiveData<Resource<Search>>
    val getSearchWorkspaceResourceResponse:MutableLiveData<Resource<Search>>

    val getJobListResourceResponse: MutableLiveData<Resource<List<Job>>>

    val repository = HomeActivityRepository()

    init {
        getUserResourceResponse = repository.userResourceResponse
        getUserFollowRequestsResourceResponse = repository.userFollowRequestsResourceResponse
        getUserNotificationsResourceResponse = repository.userNotificationsResourceResponse
        acceptRequestResourceResponse = repository.acceptRequestResourceResponse
        getSearchHistoryResourceResponse = repository.searchHistoryResourceResponse
        getSearchUserResourceResponse = repository.searchUserResourceResponse
        getJobListResourceResponse = repository.jobListResourceResponse
        getSearchWorkspaceResourceResponse=repository.searchWorkspaceResourceResponse
    }

    fun getAllJobs() {
        repository.getAllJobs()
    }

    fun fetchUser(token:String?){
        if(token != null){
            repository.getUser(token)
        }
    }

    fun getNotifications(token:String, page:Int?, pageSize:Int?){
        repository.getNotifications(token, page, pageSize)
    }

    fun getFollowRequests(id:Int, token:String, page: Int?, pageSize: Int?){
        repository.getFollowRequests(id, token, page, pageSize)

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

    fun searchUser(token:String?, query:String, job:Int?, page:Int?, perPage:Int?){
        repository.searchUser(token, query, job, page, perPage)
    }

    fun searchWorkspace(token:String?, query:String , skill:String?, event:String?, page:Int?, perPage:Int?){
        repository.searchWorkspace(token, query, skill,event, page, perPage)
    }

}