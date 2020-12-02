package com.cmpe451.platon.util

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class RetrofitClient {
    companion object{
        @Volatile
        private var INSTANCE: Webservice? = null
        fun getService():Webservice {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Retrofit.Builder()
                    .baseUrl(Definitions.API_URL)
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                    .build().create(Webservice::class.java)
                INSTANCE = instance
                return instance
            }
        }
    }

}