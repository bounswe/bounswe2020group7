package com.cmpe451.platon.page.fragment.editprofile.model

import android.content.SharedPreferences
import android.util.Log
import com.cmpe451.platon.`interface`.HttpRequestListener
import com.cmpe451.platon.util.ApiClient
import com.cmpe451.platon.util.ApiInterface
import com.cmpe451.platon.util.RetrofitClient
import com.cmpe451.platon.util.Webservice
import com.google.gson.JsonObject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import java.util.*

class EditProfileRepository(val sharedPreferences: SharedPreferences) {
    fun editUser(observer:Observer<JsonObject>, name:String?,surname:String?,
                 job:String?, isPrivate:Boolean?,
                 profilePhoto:String?,
                 google_scholar_name:String?,
                 researchgate_name:String?,authToken: String){

        val api = ApiClient()
        api.initRetrofit()
        val service = api.getRetrofit().create(ApiInterface::class.java)

        val call = service.editUserInfo(name, surname, job, true, isPrivate, profilePhoto, google_scholar_name, researchgate_name, authToken)!!

        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }
}