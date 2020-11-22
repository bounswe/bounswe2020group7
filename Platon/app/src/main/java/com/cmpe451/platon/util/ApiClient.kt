package com.cmpe451.platon.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private lateinit var retrofit: Retrofit

    fun initRetrofit(){
        retrofit = Retrofit.Builder().baseUrl(Definitions.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofit():Retrofit{
        return retrofit
    }

}