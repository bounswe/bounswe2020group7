package com.cmpe451.platon.util

import com.cmpe451.platon.util.Definitions.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    @FormUrlEncoded
    @POST("api/auth_system/login")
    fun makeLogin(@Field("e_mail") email: String,@Field("password") password: String) : Call<JsonObject>?


    @FormUrlEncoded
    @POST("api/auth_system/reset_password")
    fun resetPassword(@Field("new_password") pass1: String,@Field("new_password_repeat") pass2: String ,@Header("auth_token") token: String) : Call<JsonObject>?


    @GET("api/auth_system/reset_password")
    fun resetPasswordSendKeycode(@Query("e_mail") mail: String):Call<JsonObject>?

}