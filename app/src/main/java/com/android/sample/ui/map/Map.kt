package com.android.sample.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.resources.C.Tag.LARGE_IMAGE_SIZE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.TEXT_FONTSIZE
import com.android.sample.ui.components.NoInternetScreen
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navigationActions: NavigationActions,
    locationViewModel: LocationViewModel,
    listActivitiesViewModel: ListActivitiesViewModel,
) {
  val context = LocalContext.current
  val networkManager = NetworkManager(context)
  val currentLocation by locationViewModel.currentLocation.collectAsState()
  val coroutineScope = rememberCoroutineScope()
  val activities by listActivitiesViewModel.uiState.collectAsState()
  val activityDetail by listActivitiesViewModel.selectedActivity.collectAsState()
  val defaultLocation = LatLng(46.519962, 6.633597) // EPFL
  var selectedActivity by remember { mutableStateOf<Activity?>(null) }
  var showBottomSheet by remember { mutableStateOf(false) }
  val previousScreen = navigationActions.getPreviousRoute()

  val firstLocation =
      try {
        // the first location that displays is the last activity detail that was checked
        // if you don't come from an activity detail, it will be centered around user's position
        if (previousScreen == Screen.ACTIVITY_DETAILS) {
          activityDetail?.location?.let { LatLng(it.latitude, it.longitude) } ?: defaultLocation
        } else {
          currentLocation?.let { LatLng(it.latitude, it.longitude) } ?: defaultLocation
        }
      } catch (_: NoSuchElementException) {
        defaultLocation
      }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(firstLocation, 10f)
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
        if (!networkManager.isNetworkAvailable()) {
          NoInternetScreen(padding)
        } else {
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
                                        LatLng(
                                            item.location!!.latitude, item.location!!.longitude)),
                            title = item.title,
                            snippet = item.description,
                            onClick = {
                              selectedActivity = item
                              selectedActivity?.let { listActivitiesViewModel.selectActivity(it) }
                              showBottomSheet = true
                              true
                            })
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
                        .padding(MEDIUM_PADDING.dp)
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
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.MAP)
      })

  if (showBottomSheet) {
    ModalBottomSheet(
        onDismissRequest = { showBottomSheet = false },
    ) {
      Column(modifier = Modifier.fillMaxWidth().padding(MEDIUM_PADDING.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              SeeMoreDetailsButton(navigationActions)

              IconButton(onClick = { showBottomSheet = false }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface)
              }
            }
        selectedActivity?.let { DisplayActivity(activity = it) }

        Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
      }
    }
  }
}

@Composable
fun DisplayActivity(activity: Activity) {
  Column(modifier = Modifier.fillMaxWidth().padding(MEDIUM_PADDING.dp).testTag("activityDetails")) {
    if (activity.images.isNotEmpty()) {
      AsyncImage(
          model = activity.images.first(),
          contentDescription = "Activity image",
          modifier =
              Modifier.fillMaxWidth()
                  .height(LARGE_IMAGE_SIZE.dp)
                  .clip(RoundedCornerShape(TEXT_FONTSIZE.dp))
                  .testTag("activityImage"),
          contentScale = ContentScale.Crop)
      Spacer(modifier = Modifier.height(TEXT_FONTSIZE.dp))
    }

    Text(
        text = activity.title,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = STANDARD_PADDING.dp).testTag("activityTitle"),
    )
    Text(
        text = activity.description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = STANDARD_PADDING.dp).testTag("activityDescription"),
    )
    Spacer(modifier = Modifier.height(TEXT_FONTSIZE.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = STANDARD_PADDING.dp).testTag("activityDate")) {
          Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = "Date",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.testTag("calendarIcon"))
          Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
          Text(
              text = "Date: ${activity.date.toDate()}", modifier = Modifier.testTag("calendarText"))
        }
    if (activity.location != null) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = STANDARD_PADDING.dp).testTag("activityLocation")) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("locationIcon"))
            Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
            Text(
                text = "Location: ${activity.location!!.name}",
                modifier = Modifier.testTag("locationText"))
          }
    }
    Spacer(modifier = Modifier.height(TEXT_FONTSIZE.dp))

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().testTag("activityPrice")) {
          Text(
              text = "Price: ${activity.price}€",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.testTag("priceText"))
          Text(
              text = "Places left: ${activity.placesLeft}/${activity.maxPlaces}",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.testTag("placesLeft"))
        }
    Spacer(modifier = Modifier.height(TEXT_FONTSIZE.dp))
  }
}

@Composable
fun SeeMoreDetailsButton(navigationActions: NavigationActions) {
  Button(
      modifier = Modifier.testTag("seeMoreDetailsButton"),
      onClick = { navigationActions.navigateTo(Screen.ACTIVITY_DETAILS) },
  ) {
    Text(text = "See more details")
  }
}
