package com.cmpe451.platon.page.fragment.profilepage

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePageRepository() {

    val researchesResourceResponse: MutableLiveData<Resource<Researches>> = MutableLiveData()
    val userResourceResponse:MutableLiveData<Resource<User>> = MutableLiveData()
    val acceptRequestResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    val userFollowRequestsResourceResponse:MutableLiveData<Resource<FollowRequests>> = MutableLiveData()
    val userNotificationsResourceResponse:MutableLiveData<Resource<Notifications>> = MutableLiveData()

    fun getFollowRequests(id:Int, token:String){
        val service = RetrofitClient.getService()
        val call = service.getFollowRequests(id, token)
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

    fun getNotifications(token: String){
        val service = RetrofitClient.getService()
        val call = service.getNotifications(token)
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



    fun getUser(token:String){
        val service = RetrofitClient.getService()
        val call = service.getUserInfo(token)
        userResourceResponse.value = Resource.Loading()
        call.enqueue(object :Callback<User?>{
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


    fun getResearches( userId: Int, authToken: String){
        val service = RetrofitClient.getService()
        val call = service.getResearches(userId, authToken)
        researchesResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<Researches?>{
            override fun onResponse(call: Call<Researches?>, response: Response<Researches?>) {
                when {
                    response.isSuccessful && response.body() != null -> researchesResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> researchesResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> researchesResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Researches?>, t: Throwable) {
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


}