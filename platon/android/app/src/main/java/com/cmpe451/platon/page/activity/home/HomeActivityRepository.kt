package com.cmpe451.platon.page.activity.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivityRepository {

    val acceptRequestResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val userFollowRequestsResourceResponse:MutableLiveData<Resource<FollowRequests>> = MutableLiveData()
    val userNotificationsResourceResponse:MutableLiveData<Resource<Notifications>> = MutableLiveData()
    val userResourceResponse: MutableLiveData<Resource<User>> = MutableLiveData()
    val searchHistoryResourceResponse:MutableLiveData<Resource<SearchHistory>> = MutableLiveData()
    val searchUserResourceResponse:MutableLiveData<Resource<Search>> = MutableLiveData()
    val searchWorkspaceResourceResponse:MutableLiveData<Resource<Search>> = MutableLiveData()
    val jobListResourceResponse:MutableLiveData<Resource<List<Job>>> = MutableLiveData()

    var userNotificationDeleteResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val searchUpcomingEventResourceResponse:MutableLiveData<Resource<Search>> = MutableLiveData()
    val invitationAnswerResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()


    fun deleteNotification(id: Int, token: String){
        val service = RetrofitClient.getService()
        val call = service.deleteNotification(id, token)

        userNotificationDeleteResourceResponse.value = Resource.Loading()
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful -> userNotificationDeleteResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> userNotificationDeleteResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->userNotificationDeleteResourceResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })

    }

    fun getAllJobs() {
        val service = RetrofitClient.getService()
        val call = service.getAllJobs()

        jobListResourceResponse.value = Resource.Loading()

        call.enqueue(object : Callback<List<Job>?> {
            override fun onResponse(call: Call<List<Job>?>, response: Response<List<Job>?>) {
                when {
                    response.isSuccessful -> jobListResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> jobListResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->jobListResourceResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<List<Job>?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getUser(token:String){
        val service = RetrofitClient.getService()
        val call = service.getUserInfo(token)
        userResourceResponse.value = Resource.Loading()
        call.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                when{
                    response.isSuccessful && response.body() != null -> userResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> userResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })

    }



    fun acceptFollowRequest(followerId: Int, followingId: Int, token: String) {
        val service = RetrofitClient.getService()
        val call = service.answerFollowRequests(followerId, followingId,1, token)
        acceptRequestResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> acceptRequestResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> acceptRequestResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> acceptRequestResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun deleteFollowRequest(followerId: Int, followingId: Int, token: String) {
        val service = RetrofitClient.getService()
        val call = service.answerFollowRequests(followerId, followingId,2, token)
        acceptRequestResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> acceptRequestResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> acceptRequestResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> acceptRequestResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getFollowRequests(id:Int, token:String, page: Int?, pageSize: Int?){
        val service = RetrofitClient.getService()
        val call = service.getFollowRequests(id, token, page, pageSize)
        userFollowRequestsResourceResponse.value = Resource.Loading()

        call.enqueue(object :Callback<FollowRequests?>{
            override fun onResponse(call: Call<FollowRequests?>, response: Response<FollowRequests?>) {
                when{
                    response.isSuccessful && response.body() != null -> userFollowRequestsResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> userFollowRequestsResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userFollowRequestsResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<FollowRequests?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })

    }

    fun getNotifications(token: String, page: Int?, pageSize:Int?){
        val service = RetrofitClient.getService()
        val call = service.getNotifications(token,page, pageSize)
        userNotificationsResourceResponse.value = Resource.Loading()
        call.enqueue(object :Callback<Notifications?>{
            override fun onResponse(call: Call<Notifications?>, response: Response<Notifications?>) {
                when{
                    response.isSuccessful && response.body() != null -> userNotificationsResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> userNotificationsResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userNotificationsResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Notifications?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    fun fetchSearchHistory(token: String, i: Int) {
        val service = RetrofitClient.getService()
        val call = service.getSearchHistory(token, i)
        searchHistoryResourceResponse.value = Resource.Loading()
        call.enqueue(object :Callback<SearchHistory?>{
            override fun onResponse(call: Call<SearchHistory?>, response: Response<SearchHistory?>) {
                when{
                    response.isSuccessful && response.body() != null -> searchHistoryResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> {
                        Log.i("Eerror", response.raw().toString())
                        searchHistoryResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    }
                    else -> searchHistoryResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<SearchHistory?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }


    fun searchUser(token: String?, query:String, jobs:Int?, sortBy:Int?, page:Int?, perPage:Int?){
        val service = RetrofitClient.getService()
        val call = service.searchUser(token, query,jobs, sortBy, page, perPage)

        searchUserResourceResponse.value = Resource.Loading()
        call.enqueue(object :Callback<Search?>{
            override fun onResponse(call: Call<Search?>, response: Response<Search?>) {
                when{
                    response.isSuccessful && response.body() != null -> searchUserResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> searchUserResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> searchUserResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Search?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }


    fun searchWorkspace(token: String?, query:String, skill:String?, creatorName:String?,
         creatorSurname:String?, startDateS:String?, startDateE:String?, deadlineS:String?, deadlineE: String?, sortBy:Int?, event:String?,page:Int?, perPage:Int?){
        val service = RetrofitClient.getService()
        val call = service.searchWorkspace(token, query,skill, creatorName, creatorSurname, startDateS, startDateE, deadlineS, deadlineE, sortBy, event, page, perPage)

        searchUserResourceResponse.value = Resource.Loading()
        call.enqueue(object :Callback<Search?>{
            override fun onResponse(call: Call<Search?>, response: Response<Search?>) {
                when{
                    response.isSuccessful && response.body() != null -> searchWorkspaceResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> searchWorkspaceResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> searchWorkspaceResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Search?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    val invitationsFromWsResourceResponse:MutableLiveData<Resource<List<WorkspaceInvitation>>> = MutableLiveData()

    fun getInvitationsFromWs(currUserToken: String, currentPage: Int, pageSize: Int) {
        val service = RetrofitClient.getService()
        val call = service.getInvitationsFromWs(currUserToken)

        invitationsFromWsResourceResponse.value = Resource.Loading()
        call.enqueue(object :Callback<List<WorkspaceInvitation>?>{
            override fun onResponse(call: Call<List<WorkspaceInvitation>?>, response: Response<List<WorkspaceInvitation>?>) {
                when{
                    response.isSuccessful && response.body() != null -> invitationsFromWsResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> invitationsFromWsResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> invitationsFromWsResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<List<WorkspaceInvitation>?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    fun answerWorkspaceInvitations(invitation_id:Int, is_accepted:Int, token:String){
        val service = RetrofitClient.getService()
        val call = service.answerWorkspaceInvitation(invitation_id,is_accepted,token)

        invitationAnswerResourceResponse.value = Resource.Loading()
        call.enqueue(object :Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful && response.body() != null -> invitationAnswerResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> invitationAnswerResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> invitationAnswerResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }


    fun searchUpcomingEvent(token: String?, query: String, dateS: String?, dateE: String?, deadlineS: String?, deadlineE: String?, sortBy: Int?, page: Int?, perPage: Int?) {
        val service = RetrofitClient.getService()
        val call = service.searchUpcomingEvent(token, query,dateS, dateE, deadlineS, deadlineE, sortBy, page, perPage)

        searchUpcomingEventResourceResponse.value = Resource.Loading()
        call.enqueue(object :Callback<Search?>{
            override fun onResponse(call: Call<Search?>, response: Response<Search?>) {
                when{
                    response.isSuccessful && response.body() != null -> searchUpcomingEventResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> searchUpcomingEventResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> searchUpcomingEventResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Search?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

}