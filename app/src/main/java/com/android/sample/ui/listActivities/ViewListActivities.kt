package com.android.sample.ui.listActivities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.types
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.LARGE_IMAGE_SIZE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.ui.components.SearchBar
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun ListActivitiesScreen(
    viewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    locationViewModel: LocationViewModel,
    modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()
  var selectedIndex by remember { mutableIntStateOf(0) }
  val all = "ALL"
  val typesToString = types.map { it.name }
  val options = listOf(all) + typesToString
  val profile = profileViewModel.userState.collectAsState().value
  var searchText by remember { mutableStateOf("") }

  val locationPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestPermission(),
          onResult = { isGranted ->
            if (isGranted) {
              locationViewModel.fetchCurrentLocation()
            } else {
              Log.d("OverviewScreen", "Location permission denied by the user.")
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
      modifier = modifier.testTag("listActivitiesScreen"),
      topBar = { SearchBar(onValueChange = { searchText = it }, value = searchText) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
          Box(
              modifier =
                  Modifier.height(BUTTON_HEIGHT.dp)
                      .testTag("segmentedButtonRow")
                      .fillMaxWidth()
                      .padding(horizontal = STANDARD_PADDING.dp)) { // Set the desired height here
                SingleChoiceSegmentedButtonRow {
                  options.forEachIndexed { index, label ->
                    SegmentedButton(
                        modifier = Modifier.testTag("segmentedButton$label"),
                        shape =
                            SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                        onClick = { selectedIndex = index },
                        selected = index == selectedIndex) {
                          Text(label)
                        }
                  }
                }
              }
          Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
          Box(modifier = modifier.fillMaxWidth()) {
            when (uiState) {
              is ListActivitiesViewModel.ActivitiesUiState.Success -> {
                var activitiesList =
                    (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
                if (selectedIndex != 0) {
                  activitiesList = activitiesList.filter { it.type.name == options[selectedIndex] }
                }
                activitiesList = activitiesList.filter { it.date >= Timestamp.now() }
                if (activitiesList.isEmpty()) {
                  if (selectedIndex == 0) {
                    Text(
                        text = "There is no activity yet.",
                        modifier =
                            Modifier.padding(STANDARD_PADDING.dp)
                                .align(Alignment.Center)
                                .testTag("emptyActivityPrompt"),
                        color = MaterialTheme.colorScheme.onSurface)
                  } else {
                    Text(
                        text = "There is no activity of this type yet.",
                        modifier =
                            Modifier.padding(STANDARD_PADDING.dp)
                                .align(Alignment.Center)
                                .testTag("emptyActivityPrompt"),
                        color = MaterialTheme.colorScheme.onSurface)
                  }
                } else {
                  var filteredActivities =
                      activitiesList.filter {
                        if (searchText.isEmpty() || searchText.isBlank()) true
                        else {
                          it.title.contains(searchText, ignoreCase = true) ||
                              it.description.contains(searchText, ignoreCase = true) ||
                              it.location?.name?.contains(searchText, ignoreCase = true) ?: false
                        }
                      }
                  LazyColumn(
                      modifier =
                          Modifier.fillMaxSize()
                              .padding(horizontal = STANDARD_PADDING.dp)
                              .testTag("lazyColumn"),
                      verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING.dp)) {
                        // Use LazyColumn to efficiently display the list of activities

                        items(filteredActivities) { activity ->
                          if (activity.participants.size < activity.maxPlaces) {
                            ActivityCard(
                                activity = activity,
                                navigationActions,
                                viewModel,
                                profileViewModel,
                                profile,
                                locationViewModel.getDistanceFromCurrentLocation(activity.location))
                          }
                        }
                      }
                }
              }
              is ListActivitiesViewModel.ActivitiesUiState.Error -> {
                val error = (uiState as ListActivitiesViewModel.ActivitiesUiState.Error).exception
                Text(
                    text = "Error: ${error.message}",
                    modifier = Modifier.padding(STANDARD_PADDING.dp))
              }
            }
          }
        }
      }
}

@Composable
fun ActivityCard(
    activity: Activity,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel,
    profileViewModel: ProfileViewModel,
    profile: User?,
    distance: Float? = null
) {
  val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
  val formattedDate = dateFormat.format(activity.date.toDate())

  var isLiked by remember {
    mutableStateOf(profile?.likedActivities?.contains(activity.uid) ?: false)
  }

  Card(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("activityCard")
              .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
              .clickable {
                listActivitiesViewModel.selectActivity(activity)
                navigationActions.navigateTo(Screen.ACTIVITY_DETAILS)
              },
      elevation = CardDefaults.cardElevation(STANDARD_PADDING.dp)) {
        Column {
          // Box for overlaying the title on the image
          Box(modifier = Modifier.fillMaxWidth().height(LARGE_IMAGE_SIZE.dp)) {
            // Display the activity image
            Image(
                painter = painterResource(R.drawable.foot),
                contentDescription = activity.title,
                modifier = Modifier.fillMaxWidth().height(LARGE_IMAGE_SIZE.dp),
                contentScale = ContentScale.Crop)

            // Display the activity name on top of the image
            Text(
                text = activity.title,
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Title color set to black
                        ),
                modifier =
                    Modifier.align(Alignment.BottomStart)
                        .padding(MEDIUM_PADDING.dp)
                        .testTag("titleActivity"))
          }

          Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
          Row(
              modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                // Display the date
                Text(
                    text = formattedDate,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray, // Light gray color for the date
                            fontStyle = FontStyle.Italic),
                    modifier = Modifier.weight(1f) // Takes up remaining space
                    )

                if (profile != null) {
                  IconButton(
                      onClick = {
                        isLiked = !isLiked
                        if (isLiked) {
                          profileViewModel.addLikedActivity(profile.id, activity.uid)
                        } else {
                          profileViewModel.removeLikedActivity(profile.id, activity.uid)
                        }
                      },
                      modifier = Modifier.testTag("likeButton$isLiked"),
                  ) {
                    Icon(
                        imageVector =
                            if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isLiked) "Liked" else "Not Liked",
                        tint = if (isLiked) Color.Black else Color.Gray,
                    )
                  }
                }
              }

          Row(
              modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                // Location on the left
                Text(
                    text = activity.location?.name ?: "No location",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic, color = Color.Gray),
                    modifier = Modifier.weight(1f) // Takes up remaining space
                    )

                Text(
                    text = "${activity.placesLeft}/${activity.maxPlaces}",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            fontSize = MEDIUM_PADDING.sp),
                    modifier =
                        Modifier.align(Alignment.CenterVertically).padding(end = MEDIUM_PADDING.dp))
              }

          Spacer(modifier = Modifier.height(SMALL_PADDING.dp))

          if (distance != null) {
            val distanceString =
                "Distance : " +
                    if (distance < 1) {
                      "${round(distance * 1000)}m"
                    } else {
                      "${round(distance * 10) / 10}km"
                    }
            Text(
                text = distanceString,
                modifier =
                    Modifier.padding(horizontal = MEDIUM_PADDING.dp) // Takes up remaining space
                )
          }

          Spacer(modifier = Modifier.height(SMALL_PADDING.dp))

          // Display the activity description
          Text(
              text = activity.description,
              style =
                  MaterialTheme.typography.bodyMedium.copy(color = Color.Black, lineHeight = 20.sp),
              modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp))
          Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
        }
      }
}
