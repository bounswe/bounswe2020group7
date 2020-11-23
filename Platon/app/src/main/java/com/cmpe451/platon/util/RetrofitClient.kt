package com.cmpe451.platon.util

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val webservice by lazy {
        Retrofit.Builder()
            .baseUrl(Definitions.API_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(Webservice::class.java)
    }

}