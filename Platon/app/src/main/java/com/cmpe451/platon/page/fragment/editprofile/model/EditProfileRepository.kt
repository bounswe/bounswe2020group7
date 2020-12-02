package com.cmpe451.platon.page.fragment.editprofile.model

import android.content.SharedPreferences
import com.cmpe451.platon.util.RetrofitClient
import com.cmpe451.platon.util.Webservice
import com.google.gson.JsonObject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class EditProfileRepository(val sharedPreferences: SharedPreferences) {
    fun editUser(name:String?,surname:String?,
                 job:String?, isPrivate:Boolean?,
                 profilePhoto:String?,
                 google_scholar_name:String?,
                 researchgate_name:String?,authToken: String){


        val service = RetrofitClient.getService()

        val call = service.editUserInfo(name, surname, job, true, isPrivate, profilePhoto, google_scholar_name, researchgate_name, authToken)!!

        call.enqueue(object :Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })
    }
}
