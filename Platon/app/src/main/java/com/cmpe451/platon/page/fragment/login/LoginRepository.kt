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

    var token: MutableLiveData<String> = MutableLiveData()
    var loginResponse: MutableLiveData<String> = MutableLiveData()


    fun tryToLogin( mailStr: String, passStr: String) {

        val service = RetrofitClient.getService()
        val call = service.makeLogin(mailStr, passStr)!!

        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                when (response.isSuccessful) {
                    true -> {
                        token.value = JSONObject(response.body().toString()).get("token").toString()
                    }
                    false -> if (response.errorBody() != null) {
                        loginResponse.value = JSONObject(response.errorBody()!!.string()).get("error").toString()
                    } else {
                        loginResponse.value = "Unknown error!"
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                loginResponse.value = "Unknown error!"
            }
        })
    }
}
