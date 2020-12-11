package com.cmpe451.platon.page.activity.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.FollowRequests
import com.cmpe451.platon.network.models.Notifications
import com.cmpe451.platon.network.models.User
import com.google.gson.JsonObject

class HomeActivityViewModel(application: Application):AndroidViewModel(application){

    var getUserResourceResponse: LiveData<Resource<User>>
    val getUserFollowRequestsResourceResponse: MutableLiveData<Resource<FollowRequests>>
    val getUserNotificationsResourceResponse: MutableLiveData<Resource<Notifications>>
    val acceptRequestResourceResponse: MutableLiveData<Resource<JsonObject>>

    val repository = HomeActivityRepository()

    init {
        getUserResourceResponse = repository.userResourceResponse
        getUserFollowRequestsResourceResponse = repository.userFollowRequestsResourceResponse
        getUserNotificationsResourceResponse = repository.userNotificationsResourceResponse
        acceptRequestResourceResponse = repository.acceptRequestResourceResponse
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

}