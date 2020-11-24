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

    @GET("api/profile/research_information")
    fun getResearches(@Query("user_id") userId: Int,
                     @Header("auth_token") auth_token: String ) : Call<JsonObject>?

    @GET("api/auth_system/self")
    fun getUserInfo(@Header("auth_token") auth_token: String ) : Call<JsonObject>?

    @FormUrlEncoded
    @PUT("api/auth_system/user")
    fun editUserInfo(@Field("name") name:String?,@Field("surname") surname:String?,
                     @Field("job") job:String?,@Field("is_valid") is_valid:Boolean?,@Field("is_private") is_private:Boolean?,
                     @Field("profile_photo") profilePhoto:String?,
                     @Field("google_scholar_name") google_scholar_name:String?,
                     @Field("researchgate_name") researchgate_name:String?,
                     @Header("auth_token") auth_token :String
    ) :Call<JsonObject>?

}