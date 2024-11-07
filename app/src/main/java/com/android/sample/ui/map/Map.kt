package com.android.sample.ui.map

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import com.android.sample.model.map.LocationViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(navigationActions: NavigationActions, locationViewModel: LocationViewModel) {
  val context = LocalContext.current
  val currentLocation by locationViewModel.currentLocation.collectAsState()

  LaunchedEffect(Unit) {
    when (ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
      android.content.pm.PackageManager.PERMISSION_GRANTED -> {
        locationViewModel.fetchCurrentLocation()
      }
      else -> {
        Log.d("MapScreen", "No permission to access the location")
      }
    }
  }

  Scaffold(
      content = { pd ->
        val defaultLocation = LatLng(37.7749, -122.4194)
        val locationToUse =
            currentLocation?.let { LatLng(it.latitude, it.longitude) } ?: defaultLocation
        val cameraPositionState = rememberCameraPositionState {
          position = CameraPosition.fromLatLngZoom(locationToUse, 10f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(pd).testTag("mapScreen"),
            cameraPositionState = cameraPositionState) {
              currentLocation?.let {
                Marker(
                    state = rememberMarkerState(position = LatLng(it.latitude, it.longitude)),
                    title = it.name,
                    snippet = "Lat: ${it.latitude}, Lon: ${it.longitude}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
              }
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.MAP)
      })
}
