package com.android.sample.model.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun HandleLocationPermissionsAndTracking(
    locationViewModel: LocationViewModel,
    onPermissionDenied: () -> Unit = {}
) {
  val context = LocalContext.current

  val locationPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestPermission(),
          onResult = { isGranted ->
            if (isGranted) {
              locationViewModel.startTrackingLocation()
            } else {
              Log.d("LocationPermissionHandler", "Location permission denied by the user.")
              onPermissionDenied()
            }
          })

  LaunchedEffect(Unit) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
      locationViewModel.startTrackingLocation()
    } else {
      locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }

  DisposableEffect(Unit) { onDispose { locationViewModel.stopTrackingLocation() } }
}
