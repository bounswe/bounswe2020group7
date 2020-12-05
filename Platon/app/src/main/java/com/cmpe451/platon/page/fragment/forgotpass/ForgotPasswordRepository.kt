package com.cmpe451.platon.page.fragment.forgotpass

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

class ForgotPasswordRepository() {

    var resetResponse:MutableLiveData<Pair<Int, String>> = MutableLiveData()
    var sendResetMailResponse:MutableLiveData<Pair<Int, String>> = MutableLiveData()


    fun sendResetKeycodeToMail( mail: String) {
        val service = RetrofitClient.getService()
        val call = service.resetPasswordSendKeycode(mail)!!

        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                when{
                    response.isSuccessful -> sendResetMailResponse.value = Pair(response.code(), "Keycode sent to mail address")
                    response.errorBody() != null ->{
                        sendResetMailResponse.value = Pair(response.code(),JSONObject(response.errorBody()!!.string()).get("error").toString())
                    }
                    else -> sendResetMailResponse.value = Pair(response.code(),"Unknown Error!")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                sendResetMailResponse.value = Pair(-1,"Unknown Error!")
            }
        })
    }

    fun resetPassword(token: String, pass1: String, pass2: String) {
        val service = RetrofitClient.getService()
        val call = service.resetPassword(pass1, pass2, token)!!

        call.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                when{
                    response.isSuccessful -> resetResponse.value = Pair(response.code(), "Password successfully changed!")
                    response.errorBody() != null -> resetResponse.value = Pair(response.code(), JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> resetResponse.value = Pair(response.code(), "Unknown error")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                resetResponse.value = Pair(-1, "Unknown error")
            }
        })
    }
}