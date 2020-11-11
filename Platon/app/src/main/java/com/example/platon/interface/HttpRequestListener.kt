package com.example.platon.`interface`

import org.json.JSONObject

interface HttpRequestListener {
    fun onRequestCompleted(result: JSONObject?)
    fun onFailure(errorMessage: String)
}