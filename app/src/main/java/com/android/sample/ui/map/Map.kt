package com.android.sample.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.collections.firstNotNullOf

@Composable
fun MapScreen(
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions
) {

  val activities by listActivitiesViewModel.uiState.collectAsState()

  Scaffold(
      content = { pd ->
        val defaultLocation = LatLng(37.7749, -122.4194) // San Francisco
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
        // Camera position state, using the first ToDo location if available
        val cameraPositionState = rememberCameraPositionState {
          position = CameraPosition.fromLatLngZoom(firstToDoLocation, 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(pd).testTag("mapScreen"),
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
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.MAP)
      })
    
}
