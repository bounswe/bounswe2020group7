package com.cmpe451.platon.page.activity.home.fragment.otherprofile

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.OtherUser
import com.cmpe451.platon.network.models.Researches
import com.cmpe451.platon.network.RetrofitClient
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OtherProfileRepository() {

    val userResource: MutableLiveData<Resource<OtherUser>> = MutableLiveData()
    val researchesResource:MutableLiveData<Resource<Researches>> = MutableLiveData()
    var followResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var unFollowResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    fun getUser(userId:Int, token:String) {
        val service = RetrofitClient.getService()
        val call = service.getOtherUserInfo(userId, token)
        userResource.value = Resource.Loading()
        call.enqueue(object : Callback<OtherUser?> {
            override fun onResponse(call: Call<OtherUser?>, response: Response<OtherUser?>) {
                when{
                    response.isSuccessful && response.body() != null -> userResource.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> userResource.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userResource.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<OtherUser?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getResearch(token: String, userId: Int) {
        researchesResource.value = Resource.Loading()
        val service = RetrofitClient.getService()
        val call = service.getResearches(userId, token)

        call.enqueue(object: Callback<Researches?>{
            override fun onResponse(call: Call<Researches?>, response: Response<Researches?>) {
                when {
                    response.isSuccessful && response.body() != null -> researchesResource.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> researchesResource.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> researchesResource.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Researches?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    fun follow(followerId: Int, followingId: Int, authToken: String) {
        followResourceResponse.value = Resource.Loading()
        val service = RetrofitClient.getService()
        val call = service.follow(followerId, followingId, authToken)

        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> followResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> followResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> followResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun unfollow(followingId: Int, token: String) {
        val service = RetrofitClient.getService()
        val call = service.unfollow(followingId, token)
        unFollowResourceResponse.value = Resource.Loading()

        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> unFollowResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> unFollowResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> unFollowResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


}