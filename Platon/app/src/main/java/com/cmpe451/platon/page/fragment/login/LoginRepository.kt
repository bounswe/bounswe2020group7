package com.cmpe451.platon.page.fragment.login

/**
 * @author Burak Ömür
 */

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.util.RetrofitClient
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository() {

    var getToken: MutableLiveData<String> = MutableLiveData(null)
    var getResponseCode: MutableLiveData<Int> = MutableLiveData(null)


    fun tryToLogin( mailStr: String, passStr: String) {

        val service = RetrofitClient.getService()
        val call = service.makeLogin(mailStr, passStr)!!

        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200){
                    val token = JSONObject(response.body().toString()).get("token").toString()
                    getToken.value = token
                    Log.i("Token is", token)
                }
                getResponseCode.value = response.code()

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                getResponseCode.value = 501
            }
        })
    }
}
