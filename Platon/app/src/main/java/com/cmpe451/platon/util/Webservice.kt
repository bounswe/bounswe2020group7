package com.cmpe451.platon.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface Webservice {


    @GET("api/follow/followers")
    fun getFollowers(@Query("following_id") followingId: Int,
                     @Header("auth_token") auth_token: String ) : Call<JsonObject>?

    @GET("api/follow/followings")
    fun getFollowing(@Query("follower_id") followingId: Int,
                     @Header("auth_token") auth_token: String ) : Call<JsonObject>?


}