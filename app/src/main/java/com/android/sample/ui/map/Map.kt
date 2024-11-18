package com.android.sample.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.*
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
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

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
                                onClick = {
                                    selectedActivity = item
                                    showBottomSheet = true
                                    true
                                }
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


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Ligne pour la croix en haut à droite
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, // Espace entre les éléments
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bouton "See more details" à gauche
                    Button(
                        onClick = {
                            selectedActivity?.let { listActivitiesViewModel.selectActivity(it) }
                            navigationActions.navigateTo(Screen.ACTIVITY_DETAILS)
                            showBottomSheet = false
                        },
                    ) {
                        Text("See more details")
                    }

                    // Icône de fermeture (croix) à droite
                    IconButton(onClick = { showBottomSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (selectedActivity != null) {
                    DisplayActivity(activity = selectedActivity!!)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

}
@Composable
fun DisplayActivity(activity: Activity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp).testTag("activityDetails")
    ) {
        // Image en haut
        if (activity.images.isNotEmpty()) {
            AsyncImage(
                model = activity.images.first(),
                contentDescription = "Activity image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)).testTag("activityImage"),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Titre et description
        Text(
            text = activity.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp).testTag("activityTitle"),
        )
        Text(
            text = activity.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp).testTag("activityDescription"),
        )
        Spacer(modifier = Modifier.height(12.dp))


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp).testTag("activityDate")
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Date",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("calendarIcon")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Date: ${activity.date.toDate()}",
                modifier = Modifier.testTag("calendarText")
            )
        }
        if (activity.location != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp).testTag("activityLocation")
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("locationIcon")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Location: ${activity.location!!.name}",
                    modifier = Modifier.testTag("locationText")
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().testTag("activityPrice")
        ) {
            Text(
                text = "Price: ${activity.price}€",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("priceText")
            )
            Text(
                text = "Places left: ${activity.placesLeft}/${activity.maxPlaces}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("placesLeft")
            )
        }
        Spacer(modifier = Modifier.height(12.dp))



    }
}


