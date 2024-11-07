package com.android.sample.model.map

import android.content.Context
import androidx.core.content.ContextCompat

class LocationPermissionChecker(private val context: Context) {
  fun hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
        android.content.pm.PackageManager.PERMISSION_GRANTED
  }
}
