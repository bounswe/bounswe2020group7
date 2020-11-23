package com.cmpe451.platon.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Webservice {


    @GET("api/follow/followers")
    fun getFollowers(@Query("following_id") followingId: Int,
                     @Header("auth_token") auth_token: String ) : Call<JsonObject>?

    @GET("api/profile/research_information")
    fun getResearches(@Query("user_id") userId: Int,
                     @Header("auth_token") auth_token: String ) : Call<JsonObject>?

    @GET("api/auth_system/user")
    fun getUserInfo(@Header("auth_token") auth_token: String ) : Call<JsonObject>?

}