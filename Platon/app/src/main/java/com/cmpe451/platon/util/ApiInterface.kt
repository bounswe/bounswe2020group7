package com.cmpe451.platon.util

import com.cmpe451.platon.util.Definitions.User
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded


interface ApiInterface {
    @FormUrlEncoded
    @POST("api/auth_system/login")
    fun makeLogin(@Field("e_mail") email: String,@Field("password") password: String) : Call<Definitions.Token?>?

}