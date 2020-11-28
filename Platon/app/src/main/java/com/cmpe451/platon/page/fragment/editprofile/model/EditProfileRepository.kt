package com.cmpe451.platon.page.fragment.editprofile.model

import android.content.SharedPreferences
import com.cmpe451.platon.`interface`.HttpRequestListener
import com.cmpe451.platon.util.RetrofitClient
import com.cmpe451.platon.util.Webservice
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field

class EditProfileRepository(val sharedPreferences: SharedPreferences) {
    fun editUser(name:String?,surname:String?,
                 job:String?, isPrivate:Boolean?,
                 profilePhoto:String?,
                 google_scholar_name:String?,
                 researchgate_name:String?,authToken: String, callback: HttpRequestListener){

        var client: Webservice = RetrofitClient.webservice

        client.editUserInfo(name,surname,job,true, isPrivate,profilePhoto, google_scholar_name, researchgate_name,authToken)?.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                //handle error here
                val er = 0
            }
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                //your raw string response

                val stringResponse = (response.body() as JsonObject).toString()
                callback.onRequestCompleted(stringResponse)
                val ert = 5
            }
        })
    }
}