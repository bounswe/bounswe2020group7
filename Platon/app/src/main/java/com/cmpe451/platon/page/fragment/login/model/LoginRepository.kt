package com.cmpe451.platon.page.fragment.login.model

/**
 * @author Burak Ömür
 */

import android.content.SharedPreferences
import android.util.Log
import com.cmpe451.platon.page.fragment.login.presenter.LoginPresenter
import com.cmpe451.platon.util.ApiClient
import com.cmpe451.platon.util.ApiInterface
import com.cmpe451.platon.util.Definitions
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//import com.cmpe451.elevator.HttpRequest


class LoginRepository(val sharedPreferences: SharedPreferences){
    fun tryToLogin(mailStr: String, passStr: String): Boolean {

        val api = ApiClient()
        api.initRetrofit()
        val service = api.getRetrofit().create(ApiInterface::class.java)

        val call = service.makeLogin(mailStr, passStr)

        val callback: Callback<Definitions.Token?> = object: Callback<Definitions.Token?>{
            override fun onResponse(call: Call<Definitions.Token?>, response: Response<Definitions.Token?>) {
                var token:String? = "fail_server_side"
                if(response.isSuccessful){
                    Log.println(Log.INFO, "IMPORTANT_Resp",  (response.body() as Definitions.Token).token + "")
                    token = (response.body() as Definitions.Token).token
                }
                sharedPreferences.edit().putString("token", token).apply()

            }

            override fun onFailure(call: Call<Definitions.Token?>, t: Throwable) {
                val token:String? = "fail_client_side"
                sharedPreferences.edit().putString("token", token).apply()
            }
        }

        call?.enqueue(callback)

        return true
    }


}
