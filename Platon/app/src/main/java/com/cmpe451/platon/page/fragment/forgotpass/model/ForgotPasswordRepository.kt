package com.cmpe451.platon.page.fragment.forgotpass.model

/**
 * @author Burak Ömür
 */

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.cmpe451.platon.util.ApiClient
import com.cmpe451.platon.util.ApiInterface
import com.cmpe451.platon.util.Definitions
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordRepository(val sharedPreferences: SharedPreferences) {
    fun postPasswordForgotten(mail: String):Boolean {
        val api = ApiClient()
        api.initRetrofit()
        val service = api.getRetrofit().create(ApiInterface::class.java)

        val call = service.resetPasswordSendKeycode(mail)



        val callback: Callback<JsonObject?> = object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful){
                    sharedPreferences.edit().putBoolean("reset_key_sent", true).apply()
                    Log.println(Log.INFO, "FORGOT", (response.body() as JsonObject).toString())
                }else{
                    sharedPreferences.edit().putBoolean("reset_key_sent_fail", true).apply()
                    Log.println(Log.INFO, "FORGOT",  JSONObject(response.errorBody()?.string()!!).toString())
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.println(Log.INFO, "FORGOT", "Error")
            }
        }

        call?.enqueue(callback)


        return true
    }

    fun postResetPassword(token: String, pass1: String, pass2: String):Boolean {
        val api = ApiClient()
        api.initRetrofit()
        val service = api.getRetrofit().create(ApiInterface::class.java)

        val call = service.resetPassword(pass1, pass2, token)

        sharedPreferences.edit().remove("reset_success").apply()
        sharedPreferences.edit().remove("reset_fail").apply()

        val callback: Callback<JsonObject?> = object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful){
                    sharedPreferences.edit().putBoolean("reset_success", true).apply()
                    Log.println(Log.INFO, "FORGOT", (response.body() as JsonObject).toString())
                }else{
                    sharedPreferences.edit().putBoolean("reset_fail", true).apply()
                    Log.println(Log.INFO, "FORGOT",  JSONObject(response.errorBody()?.string()!!).toString())
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Log.println(Log.INFO, "FORGOT", "Error")
            }
        }

        call?.enqueue(callback)
        return true
    }
}