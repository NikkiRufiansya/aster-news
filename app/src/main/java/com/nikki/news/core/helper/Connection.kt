package com.nikki.news.core.helper
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class Connection(context: Context) {
    private val context = context

    fun isConnectingToInternet(): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }
}