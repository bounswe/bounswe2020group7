package com.cmpe451.platon.page.fragment.forgotpass.model

/**
 * @author Burak Ömür
 */

import android.content.SharedPreferences
import com.cmpe451.platon.util.RetrofitClient
import com.google.gson.JsonObject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordRepository(val sharedPreferences: SharedPreferences) {
    fun postPasswordForgotten( mail: String) {
        val service = RetrofitClient.getService()
        val call = service.resetPasswordSendKeycode(mail)!!

        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }


        })
    }

    fun postResetPassword(token: String, pass1: String, pass2: String) {
        val service = RetrofitClient.getService()

        val call = service.resetPassword(pass1, pass2, token)!!


        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }


        })
    }
}