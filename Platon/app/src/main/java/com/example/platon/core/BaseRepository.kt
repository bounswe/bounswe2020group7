package com.example.platon.core

import android.content.SharedPreferences
import android.widget.Toast
import com.example.elevator.HttpRequest
import com.example.platon.`interface`.HttpRequestListener
import com.example.platon.`interface`.Listener
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