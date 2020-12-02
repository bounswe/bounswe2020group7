package com.cmpe451.platon.page.fragment.follow.model

import android.content.SharedPreferences
import com.cmpe451.platon.util.RetrofitClient
import com.cmpe451.platon.util.Webservice
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowRepository(val sharedPreferences: SharedPreferences) {
    fun getFollowers(followingId: Int, authToken: String){

        val service = RetrofitClient.getService()

        service.getFollowers(followingId, authToken)?.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
            }
        })
    }
    fun getFollowing(followingId: Int, authToken: String){

        val service = RetrofitClient.getService()

        service.getFollowing(followingId, authToken)?.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
            }

        })
    }
}