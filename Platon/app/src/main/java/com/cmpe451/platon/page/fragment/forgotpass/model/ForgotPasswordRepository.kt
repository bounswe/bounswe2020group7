package com.cmpe451.platon.page.fragment.forgotpass.model

/**
 * @author Burak Ömür
 */

import android.content.SharedPreferences
import com.cmpe451.platon.util.ApiClient
import com.cmpe451.platon.util.ApiInterface
import com.google.gson.JsonObject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers

class ForgotPasswordRepository(val sharedPreferences: SharedPreferences) {
    fun postPasswordForgotten(observer : Observer<JsonObject> ,  mail: String) {
        val api = ApiClient()
        api.initRetrofit()
        val service = api.getRetrofit().create(ApiInterface::class.java)

        val call = service.resetPasswordSendKeycode(mail)!!

        call.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun postResetPassword(observer : Observer<JsonObject> , token: String, pass1: String, pass2: String) {
        val api = ApiClient()
        api.initRetrofit()
        val service = api.getRetrofit().create(ApiInterface::class.java)

        val call = service.resetPassword(pass1, pass2, token)!!


        call.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }
}