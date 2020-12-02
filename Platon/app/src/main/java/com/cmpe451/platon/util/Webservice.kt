package com.cmpe451.platon.util

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

    @FormUrlEncoded
    @POST("api/auth_system/login")
    fun makeLogin(@Field("e_mail") email: String,
                  @Field("password") password: String) : Call<JsonObject>?


    @FormUrlEncoded
    @POST("api/auth_system/reset_password")
    fun resetPassword(@Field("new_password") pass1: String,
                      @Field("new_password_repeat") pass2: String ,
                      @Header("auth_token") token: String) : Call<JsonObject>?


    @GET("api/auth_system/reset_password")
    fun resetPasswordSendKeycode(@Query("e_mail") mail: String): Call<JsonObject>?

    @FormUrlEncoded
    @POST("api/auth_system/user")
    fun makeRegister(@Field("e_mail") email: String,
                     @Field("password") password: String ,
                     @Field("name") firstName: String,
                     @Field("surname") lastName: String,
                     @Field("job") job: String) : Call<JsonObject>?

    @GET("api/auth_system/self")
    fun getUserInfo(@Header("auth_token") auth_token: String ) : Call<JsonObject>?

    @GET("api/profile/research_information")
    fun getResearches(@Query("user_id") userId: Int,
                      @Header("auth_token") auth_token: String ) : Call<JsonObject>?

    @FormUrlEncoded
    @PUT("api/auth_system/user")
    fun editUserInfo(@Field("name") name:String?,
                     @Field("surname") surname:String?,
                     @Field("job") job:String?,
                     @Field("is_valid") is_valid:Boolean?,
                     @Field("is_private") is_private:Boolean?,
                     @Field("profile_photo") profilePhoto:String?,
                     @Field("google_scholar_name") google_scholar_name:String?,
                     @Field("researchgate_name") researchgate_name:String?,
                     @Header("auth_token") auth_token :String
    ) : Call<JsonObject>?


}