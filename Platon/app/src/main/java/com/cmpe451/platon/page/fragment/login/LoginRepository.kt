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

    var loginResponse: MutableLiveData<Pair<Int, String>> = MutableLiveData()

    fun tryToLogin( mailStr: String, passStr: String) {

        val service = RetrofitClient.getService()
        val call = service.makeLogin(mailStr, passStr)!!

        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                when {
                    response.isSuccessful ->  loginResponse.value = Pair(response.code(), JSONObject(response.body().toString()).get("token").toString())
                    (response.errorBody() != null) -> loginResponse.value = Pair(response.code(),JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> loginResponse.value = Pair(response.code(),"Unknown error!")

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                call.clone().enqueue(this)
                //loginResponse.value = Pair(-1,"Unknown error!")
            }
        })
    }
}
