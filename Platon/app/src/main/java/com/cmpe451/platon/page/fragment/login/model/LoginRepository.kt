package com.cmpe451.platon.page.fragment.login.model

/**
 * @author Burak Ömür
 */

import android.content.SharedPreferences
import android.util.Log
import com.cmpe451.platon.page.fragment.login.presenter.LoginPresenter
import com.cmpe451.platon.util.ApiClient
import com.cmpe451.platon.util.ApiInterface
import com.cmpe451.platon.util.Definitions
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//import com.cmpe451.elevator.HttpRequest


class LoginRepository(val sharedPreferences: SharedPreferences){
    fun tryToLogin(mailStr: String, passStr: String): Boolean {

        val api = ApiClient()
        api.initRetrofit()
        val service = api.getRetrofit().create(ApiInterface::class.java)

        val call = service.makeLogin(mailStr, passStr)

        sharedPreferences.edit().remove("token").apply()

        val callback: Callback<JsonObject?> = object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if(response.isSuccessful){
                    Log.println(Log.INFO, "IMPORTANT_Resp",  (response.body() as JsonObject).get("token").toString())
                    val token = (response.body() as JsonObject).get("token").toString()
                    sharedPreferences.edit().putString("token", token).apply()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.println(Log.INFO, "IMPORTANT_Resp",  "Error failure:!")
            }
        }

        call?.enqueue(callback)

        return true
    }


}
