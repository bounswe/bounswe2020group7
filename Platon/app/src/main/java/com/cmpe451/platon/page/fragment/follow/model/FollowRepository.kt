package com.cmpe451.platon.page.fragment.follow.model

import android.content.SharedPreferences
import com.cmpe451.platon.`interface`.HttpRequestListener
import com.cmpe451.platon.util.RetrofitClient
import com.cmpe451.platon.util.Webservice
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowRepository(val sharedPreferences: SharedPreferences) {
    fun getFollowers(followingId: Int, authToken: String, callback: HttpRequestListener){

        var client: Webservice = RetrofitClient.webservice

        client.getFollowers(followingId, authToken)?.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                //handle error here
                val er = 0
            }
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                //your raw string response

                val stringResponse = (response.body() as JsonObject).toString()
                callback.onRequestCompleted(stringResponse)
                val ert = 5
            }
        })
    }
    fun getFollowing(followingId: Int, authToken: String, callback: HttpRequestListener){

        var client: Webservice = RetrofitClient.webservice

        client.getFollowing(followingId, authToken)?.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                //handle error here
                val er = 0
            }

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                //your raw string response

                val stringResponse = (response.body() as JsonObject).toString()
                callback.onRequestCompleted(stringResponse)
                val ert = 5
            }

        })
    }
}