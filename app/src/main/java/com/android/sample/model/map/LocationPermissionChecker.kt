package com.android.sample.model.map

import android.content.Context
import androidx.core.content.ContextCompat
import javax.inject.Inject

class LocationPermissionChecker @Inject constructor(private val context: Context) : PermissionChecker {

    override fun hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
        android.content.pm.PackageManager.PERMISSION_GRANTED
  }
}
