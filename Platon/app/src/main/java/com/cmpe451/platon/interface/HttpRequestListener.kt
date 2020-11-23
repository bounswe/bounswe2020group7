package com.cmpe451.platon.`interface`

interface HttpRequestListener {
    fun onRequestCompleted(result: String)
    fun onFailure(errorMessage: String)
}