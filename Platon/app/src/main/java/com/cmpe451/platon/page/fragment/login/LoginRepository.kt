package com.cmpe451.platon.page.fragment.login

/**
 * @author Burak Ömür
 */

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository() {

    var loginResourceResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData(Resource.Loading())

    fun tryToLogin( mailStr: String, passStr: String) {

        val service = RetrofitClient.getService()
        val call = service.makeLogin(mailStr, passStr)

        call.enqueue(object : Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null ->  loginResourceResponse.value = Resource.Success(response.body()!!)
                    (response.errorBody() != null) -> loginResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> loginResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }
}
