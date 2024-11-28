package com.android.sample.model.network

import android.content.Context
import android.net.ConnectivityManager

class NetworkManager(context: Context) {
  private val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  fun isNetworkAvailable(): Boolean {
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
  }
}
