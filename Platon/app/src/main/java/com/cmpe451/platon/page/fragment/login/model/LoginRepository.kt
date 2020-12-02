package com.cmpe451.platon.page.fragment.login.model

/**
 * @author Burak Ömür
 */

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.util.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {

    var getToken: MutableLiveData<String> = MutableLiveData(null)
    var getUser: MutableLiveData<User> = MutableLiveData(null)


    fun tryToLogin( mailStr: String, passStr: String) {

        val service = RetrofitClient.getService()

        val call = service.makeLogin(mailStr, passStr)!!

        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200){
                    val token = JSONObject(response.body().toString()).get("token").toString()
                    getToken.value = token
                    val newCall = service.getUserInfo(token)!!

                    newCall.enqueue(object: Callback<JsonObject>{
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.code() == 200) {
                                getUser.value = Gson().fromJson(response.body().toString(), User::class.java)

                            }
                        }
                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        }

                    })

                    Log.i("Token is", token)
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.i("Errortess",call.toString())
            }

        })
    }


}
