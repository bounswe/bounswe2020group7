package com.cmpe451.platon.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.cmpe451.platon.`interface`.NetworkListener

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ConnectionStateMonitor (private var callback: NetworkListener) : ConnectivityManager.NetworkCallback() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private var networkRequest : NetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

    fun enable(context : Context){
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, this)
        if (connectivityManager.activeNetworkInfo == null || connectivityManager.activeNetworkInfo!!.isConnected.not()){
            callback.onLost()
        }
    }

    fun disable(context: Context){
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        callback.onAcquired()
    }

    override fun onLost(network: Network) {
        callback.onLost()
    }
}