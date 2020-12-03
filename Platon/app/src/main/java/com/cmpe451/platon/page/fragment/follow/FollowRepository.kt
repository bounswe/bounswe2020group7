package com.cmpe451.platon.page.fragment.follow

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.networkmodels.models.FollowPerson
import com.cmpe451.platon.networkmodels.models.Followers
import com.cmpe451.platon.networkmodels.models.Following
import com.cmpe451.platon.util.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowRepository() {
    val followers: MutableLiveData<Followers> = MutableLiveData<Followers>(null)
    val following: MutableLiveData<Following> = MutableLiveData<Following>(null)

    fun getFollowers(followingId: Int, authToken: String){
        val service = RetrofitClient.getService()
        service.getFollowers(followingId, authToken)?.enqueue(object : Callback<Followers> {
            override fun onFailure(call: Call<Followers>, t: Throwable) {
                call.clone().enqueue(this)
            }
            override fun onResponse(call: Call<Followers>, response: Response<Followers>) {
                if(response.code() == 200){
                    followers.value =  response.body()
                }
            }
        })
    }
    fun getFollowing(followingId: Int, authToken: String){
        val service = RetrofitClient.getService()
        service.getFollowing(followingId, authToken)?.enqueue(object : Callback<Following> {
            override fun onFailure(call: Call<Following>, t: Throwable) {
                call.clone().enqueue(this)
            }

            override fun onResponse(call: Call<Following>, response: Response<Following>) {
                if(response.code() == 200){
                    following.value =  response.body()
                }
            }

        })
    }
}