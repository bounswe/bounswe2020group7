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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//import com.cmpe451.elevator.HttpRequest


class LoginRepository(val sharedPreferences: SharedPreferences){
    fun tryToLogin(observer: Observer<JsonObject> , mailStr: String, passStr: String) {
        val api = ApiClient()
        api.initRetrofit()
        val service = api.getRetrofit().create(ApiInterface::class.java)

        val call = service.makeLogin(mailStr, passStr)!!

        call.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }


}
