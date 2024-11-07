package com.android.sample.ui.map

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.android.sample.model.map.LocationViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun MapScreen(navigationActions: NavigationActions, locationViewModel: LocationViewModel) {
  val context = LocalContext.current
  val currentLocation by locationViewModel.currentLocation.collectAsState()
  val cameraPositionState = rememberCameraPositionState()
  val coroutineScope = rememberCoroutineScope()

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
      content = { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          GoogleMap(
              modifier = Modifier.fillMaxSize().testTag("mapScreen"),
              cameraPositionState = cameraPositionState) {
                currentLocation?.let {
                  Marker(
                          state = rememberMarkerState(position = LatLng(it.latitude, it.longitude)),
                          title = it.name,
                          snippet = "Lat: ${it.latitude}, Lon: ${it.longitude}",
                          icon =
                              BitmapDescriptorFactory.defaultMarker(
                                  BitmapDescriptorFactory.HUE_BLUE))
                      .apply {}
                }
              }

          // Floating Action Button positioned at the bottom right
          FloatingActionButton(
              modifier =
                  Modifier.align(Alignment.BottomStart)
                      .padding(16.dp)
                      .testTag("centerOnCurrentLocation"),
              onClick = {
                coroutineScope.launch {
                  currentLocation?.let {
                    val locationLatLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f),
                        durationMs = 800)
                  }
                }
              }) {
                Icon(Icons.Default.MyLocation, contentDescription = "Center on current location")
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
