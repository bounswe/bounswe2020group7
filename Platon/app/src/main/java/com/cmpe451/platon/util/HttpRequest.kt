package com.cmpe451.platon.util

import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import com.cmpe451.platon.`interface`.Listener
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

open class HttpRequest(
        var path: String,
        var method: String,
        var body: JSONObject? = null,
        var token: String? = "",
        var listener: Listener
): AsyncTask<String, Void,JSONObject?>() {
    


    private val READ_TIMEOUT: Int = 10000
    private val CONNECTION_TIMEOUT: Int= 10000


    override fun doInBackground(vararg params: String?): JSONObject? {
        var result: JSONObject? = null

        val url = URL(path)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.readTimeout = READ_TIMEOUT
        connection.connectTimeout = CONNECTION_TIMEOUT
        connection.setChunkedStreamingMode(0)

        if(token.isNullOrEmpty().not()){
            connection.setRequestProperty("Authorization", "Bearer $token")
        }
        if(method == "POST"){
            connection.doOutput = true
            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Accept","application/json")
            connection.setRequestProperty("Content-Type", "application/json")

        }
        try {
            if(method == "POST"){
                connection.connect()
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.writeBytes(body.toString())
                outputStream.flush()
                outputStream.close()
            }
            if(connection.responseCode == 200){
                val stream = InputStreamReader(connection.inputStream)
                val bufferedReader = BufferedReader(stream)
                val data = bufferedReader.readText()
                bufferedReader.close()
                stream.close()
                result = JSONObject(data)
                Log.i("Connection established","Request handled successfully")
            }
            else {
                Log.e("Connection failed", "Connection failed with code ${connection.responseCode}")
            }
        }
        catch (e: Exception){
            Log.e("error", e.message.toString())
        }
        finally {
            connection.disconnect()
            Log.d("result: http", result.toString())
            return result
        }
    }

    override fun onPostExecute(result: JSONObject?) {
        listener.onRequestCompleted(result)
    }


}