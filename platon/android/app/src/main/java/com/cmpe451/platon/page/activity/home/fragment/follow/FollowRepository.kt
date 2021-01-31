package com.cmpe451.platon.page.activity.home.fragment.follow

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Followers
import com.cmpe451.platon.network.models.Following
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.RecommendedUserList
import com.cmpe451.platon.network.models.RecommendedWorkspaceList
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
 *  It is a bridge between end point and follow view model.
 */

class FollowRepository() {
    val followersResource: MutableLiveData<Resource<Followers>> = MutableLiveData(Resource.Loading())
    val followingResource: MutableLiveData<Resource<Following>> = MutableLiveData(Resource.Loading())
    val followRecommendationsResponse:MutableLiveData<Resource<RecommendedUserList>> = MutableLiveData()


    fun getFollowers(followingId: Int, authToken: String, page:Int, per_page:Int){
        val service = RetrofitClient.getService()
        service.getFollowers(followingId, page, per_page, authToken).enqueue(object : Callback<Followers?> {
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
    fun getFollowing(followingId: Int, authToken: String, page:Int, per_page:Int){
        val service = RetrofitClient.getService()
        service.getFollowing(followingId, page, per_page, authToken).enqueue(object : Callback<Following?> {

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
    fun getFollowRecommendations(num_of_rec:Int, token:String){
        val service = RetrofitClient.getService()
        val call = service.getRecommendedFollows(num_of_rec, token)
        followRecommendationsResponse.value = Resource.Loading()
        call.enqueue(object: Callback<RecommendedUserList?> {
            override fun onResponse(call: Call<RecommendedUserList?>, response: Response<RecommendedUserList?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        followRecommendationsResponse.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> followRecommendationsResponse.value = Resource.Error(
                        JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> followRecommendationsResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<RecommendedUserList?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }
}