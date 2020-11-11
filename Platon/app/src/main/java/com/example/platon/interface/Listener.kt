package com.example.platon.`interface`

import org.json.JSONObject

interface Listener {
    fun onRequestCompleted(result: JSONObject?)
    fun onFailure(errorMessage: String)
}