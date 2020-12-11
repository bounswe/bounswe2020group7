package com.cmpe451.platon.page.activity.home.fragment.follow

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Followers
import com.cmpe451.platon.network.models.Following
import com.cmpe451.platon.network.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowRepository() {
    val followersResource: MutableLiveData<Resource<Followers>> = MutableLiveData(Resource.Loading())
    val followingResource: MutableLiveData<Resource<Following>> = MutableLiveData(Resource.Loading())

    fun getFollowers(followingId: Int, authToken: String){
        val service = RetrofitClient.getService()
        service.getFollowers(followingId, authToken).enqueue(object : Callback<Followers?> {
            override fun onResponse(call: Call<Followers?>, response: Response<Followers?>) {
                when{
                    response.isSuccessful && response.body() != null-> followersResource.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> followersResource.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> followersResource.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<Followers?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }
    fun getFollowing(followingId: Int, authToken: String){
        val service = RetrofitClient.getService()
        service.getFollowing(followingId, authToken).enqueue(object : Callback<Following?> {

            override fun onResponse(call: Call<Following?>, response: Response<Following?>) {
                when{
                    response.isSuccessful && response.body() != null-> followingResource.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> followingResource.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> followingResource.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<Following?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }
}