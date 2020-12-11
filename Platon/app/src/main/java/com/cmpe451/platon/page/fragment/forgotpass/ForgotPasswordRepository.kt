package com.cmpe451.platon.page.fragment.forgotpass

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

class ForgotPasswordRepository() {

    var resetResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData(Resource.Loading())
    var sendResetMailResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData(Resource.Loading())


    fun sendResetKeycodeToMail( mail: String) {
        val service = RetrofitClient.getService()
        val call = service.resetPasswordSendKeycode(mail)

        call.enqueue(object : Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful && response.body() != null-> sendResetMailResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> sendResetMailResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> sendResetMailResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    fun resetPassword(token: String, pass1: String, pass2: String) {
        val service = RetrofitClient.getService()
        val call = service.resetPassword(pass1, pass2, token)

        call.enqueue(object : Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful && response.body() != null-> resetResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> resetResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> resetResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }
}