package com.android.sample.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.model.map.LocationViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(navigationActions: NavigationActions, locationViewModel: LocationViewModel) {

  Scaffold(
      content = { pd ->
        val defaultLocation = LatLng(37.7749, -122.4194)
        val cameraPositionState = rememberCameraPositionState {
          position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(pd).testTag("mapScreen"),
            cameraPositionState = cameraPositionState) {}
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.MAP)
      })
}
