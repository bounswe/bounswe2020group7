package com.cmpe451.platon.page.fragment.profilepage

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePageRepository() {

    val researchesResourceResponse: MutableLiveData<Resource<Researches>> = MutableLiveData(Resource.Loading())
    val userResourceResponse:MutableLiveData<Resource<User>> = MutableLiveData(Resource.Loading())

    val userFollowRequestsResourceResponse:MutableLiveData<Resource<FollowRequests>> = MutableLiveData(Resource.Loading())
    val userNotificationsResourceResponse:MutableLiveData<Resource<List<Notification>>> = MutableLiveData(Resource.Loading())

    fun getFollowRequests(id:Int, token:String){
        val service = RetrofitClient.getService()
        val call = service.getFollowRequests(id, token)

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

        call.enqueue(object :Callback<List<Notification>?>{
            override fun onResponse(call: Call<List<Notification>?>, response: Response<List<Notification>?>) {
                when{
                    response.isSuccessful && response.body() != null -> userNotificationsResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> userNotificationsResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userNotificationsResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<List<Notification>?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }



    fun getUser(token:String){
        val service = RetrofitClient.getService()
        val call = service.getUserInfo(token)
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


}