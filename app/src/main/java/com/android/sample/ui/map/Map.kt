package com.android.sample.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.LocationViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    navigationActions: NavigationActions,
    locationViewModel: LocationViewModel,
    listActivitiesViewModel: ListActivitiesViewModel
) {
  val context = LocalContext.current
  val currentLocation by locationViewModel.currentLocation.collectAsState()
  val coroutineScope = rememberCoroutineScope()
  val activities by listActivitiesViewModel.uiState.collectAsState()
  val defaultLocation = LatLng(46.519962, 6.633597) // EPFL
  val firstToDoLocation =
      try {
        val loc =
            (activities as ListActivitiesViewModel.ActivitiesUiState.Success)
                .activities
                .firstNotNullOf { it.location }
        LatLng(loc.latitude, loc.longitude)
      } catch (_: NoSuchElementException) {
        defaultLocation
      }
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(firstToDoLocation, 10f)
  }

  // Activity result launcher to request permissions
  val locationPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestPermission(),
          onResult = { isGranted ->
            if (isGranted) {
              locationViewModel.fetchCurrentLocation()
            } else {
              Log.d("MapScreen", "Location permission denied by the user.")
            }
          })

  LaunchedEffect(Unit) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
      locationViewModel.fetchCurrentLocation()
    } else {
      locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }

  Scaffold(
      content = { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          GoogleMap(
              modifier = Modifier.fillMaxSize().padding(padding).testTag("mapScreen"),
              cameraPositionState = cameraPositionState) {
                (activities as ListActivitiesViewModel.ActivitiesUiState.Success)
                    .activities
                    .filter { it.location != null }
                    .forEach { item ->
                      Marker(
                          state =
                              MarkerState(
                                  position =
                                      LatLng(item.location!!.latitude, item.location!!.longitude)),
                          title = item.title,
                          snippet = item.description,
                      )
                    }
                currentLocation?.let {
                  Marker(
                      state = rememberMarkerState(position = LatLng(it.latitude, it.longitude)),
                      title = it.name,
                      snippet = "Lat: ${it.latitude}, Lon: ${it.longitude}",
                      icon =
                          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                }
              }

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
