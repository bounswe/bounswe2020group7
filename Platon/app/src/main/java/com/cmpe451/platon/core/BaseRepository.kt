package com.cmpe451.platon.core

import android.content.SharedPreferences
import android.widget.Toast
//import com.cmpe451.elevator.HttpRequest
import com.cmpe451.platon.`interface`.HttpRequestListener
import com.cmpe451.platon.`interface`.Listener
import com.cmpe451.platon.util.HttpRequest
import org.json.JSONObject

class BaseRepository(private val sharedPreferences: SharedPreferences) {


    fun makeRequest(path: String, method: String, contentType: String? = null, body: JSONObject? = null, callback: HttpRequestListener){

        var myListener = object : Listener {
            override fun onRequestCompleted(result: JSONObject?) {
            }
            override fun onFailure(errorMessage: String) {
            }
        }

        requestHandler("buttonName", myListener)

    }

    fun requestHandler(clickedButton: String ,callback: Listener){
        val method = "POST"
        var buttonName: String
        buttonName = "buton" + clickedButton


        val body = JSONObject (
                mapOf(
                        "name" to  buttonName,
                        "value" to "ON"
                )
        )
        val listener = object : Listener {
            override fun onRequestCompleted(result: JSONObject?) {
                print("istek atıldı")
            }
            override fun onFailure(errorMessage: String) {
                callback.onFailure(errorMessage)
            }
        }

        HttpRequest("google.com", method,body,listener = listener).execute()
    }
}