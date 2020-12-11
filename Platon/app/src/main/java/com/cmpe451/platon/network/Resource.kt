package com.cmpe451.platon.network

// A generic class that contains data and status about loading this data.
sealed class Resource<T>(
        var data: T? = null,
        val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

}