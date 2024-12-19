package com.android.sample.ui.listActivities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.CategoryColorMap
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.categories
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.map.HandleLocationPermissionsAndTracking
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT_SM
import com.android.sample.resources.C.Tag.END_Y
import com.android.sample.resources.C.Tag.GRADIENT_MAX
import com.android.sample.resources.C.Tag.LARGE_IMAGE_SIZE
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND_BUTTON
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.android.sample.resources.C.Tag.MAIN_COLOR_LIGHT
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.PRIMARY_COLOR
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.SMALL_TEXT_FONTSIZE
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.START_Y
import com.android.sample.resources.C.Tag.TEXT_FONTSIZE
import com.android.sample.ui.camera.getImageResourceIdForCategory
import com.android.sample.ui.components.SearchBar
import com.android.sample.ui.dialogs.FilterDialog
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.round

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun ListActivitiesScreen(
    viewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    locationViewModel: LocationViewModel,
    modifier: Modifier = Modifier,
    imageViewModel: ImageViewModel
) {
  val uiState by viewModel.uiState.collectAsState()
  val options = categories.map { it.name }
  val profile = profileViewModel.userState.collectAsState().value
  var searchText by remember { mutableStateOf("") }
  var showFilterDialog by remember { mutableStateOf(false) }
  val checkedList = remember { mutableStateListOf<Int>() }
  val hourDateViewModel: HourDateViewModel = HourDateViewModel()

  HandleLocationPermissionsAndTracking(locationViewModel = locationViewModel)

  Scaffold(
      modifier = modifier.testTag("listActivitiesScreen"),
      topBar = {
        SearchBar(
            onValueChange = { searchText = it }, value = searchText, { showFilterDialog = true })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.OVERVIEW)
      },
  ) { paddingValues ->
    Column(
        modifier =
            Modifier.fillMaxSize().padding(paddingValues).background(Color(MAIN_BACKGROUND))) {
          if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                onFilter = {
                    price,
                    placesAvailable,
                    minDateTimestamp,
                    maxDateTimestamp,
                    startTime,
                    endTime,
                    distance,
                    seeOnlyPRO ->
                  viewModel.updateFilterState(
                      price,
                      placesAvailable,
                      minDateTimestamp,
                      maxDateTimestamp,
                      startTime,
                      endTime,
                      distance,
                      seeOnlyPRO)
                })
          }
          Box(
              modifier =
                  Modifier.height(BUTTON_HEIGHT_SM.dp)
                      .testTag("segmentedButtonRow")
                      .fillMaxWidth()
                      .padding(horizontal = STANDARD_PADDING.dp)
                      .background(Color.Transparent)) {
                MultiChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth().background(Color.Transparent)) {
                      options.forEachIndexed { index, label ->
                        SegmentedButton(
                            modifier =
                                Modifier.testTag("segmentedButton$label")
                                    .fillMaxWidth()
                                    .padding(horizontal = (SMALL_PADDING / 2).dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color.Transparent,
                                        shape =
                                            SegmentedButtonDefaults.itemShape(
                                                index = index, count = options.size)),
                            colors =
                                SegmentedButtonDefaults.colors(
                                    activeContentColor = Color(MAIN_COLOR_DARK),
                                    activeBorderColor = Color(MAIN_COLOR_DARK),
                                    inactiveBorderColor = Color(MAIN_COLOR_DARK),
                                    inactiveContainerColor = Color(MAIN_BACKGROUND_BUTTON),
                                    activeContainerColor = Color(MAIN_COLOR_LIGHT)),
                            shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
                            onCheckedChange = {
                              if (index in checkedList) {
                                checkedList.remove(index)
                              } else {
                                checkedList.add(index)
                              }
                            },
                            checked = index in checkedList) {
                              Text(
                                  label,
                                  fontSize = SMALL_TEXT_FONTSIZE.sp,
                                  color = Color(PRIMARY_COLOR))
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
                if (checkedList.isNotEmpty()) {
                  activitiesList =
                      activitiesList.filter {
                        checkedList.contains(categories.indexOf(it.category))
                      }
                }
                activitiesList =
                    activitiesList.filter {
                      // log the activity to see which one faisl in combine date and time
                      val activityTimestamp =
                          hourDateViewModel.combineDateAndTime(
                              it.date, it.startTime) // Combine date and startTime
                      activityTimestamp >= Timestamp.now() // Compare with current time
                    }
                if (activitiesList.isEmpty()) {
                  if (checkedList.isEmpty()) {
                    Text(
                        text = "There is no activity yet.",
                        modifier =
                            Modifier.padding(STANDARD_PADDING.dp)
                                .align(Alignment.Center)
                                .testTag("emptyActivityPrompt"),
                        color = MaterialTheme.colorScheme.onSurface)
                  } else {
                    Text(
                        text = "There is no activity of these categories yet.",
                        modifier =
                            Modifier.padding(STANDARD_PADDING.dp)
                                .align(Alignment.Center)
                                .testTag("emptyActivityPrompt"),
                        color = MaterialTheme.colorScheme.onSurface)
                  }
                } else {
                  val filteredActivities =
                      activitiesList.filter {
                        if (it.price > viewModel.maxPrice) false
                        else if (viewModel.availablePlaces != null &&
                            (it.maxPlaces - it.placesLeft) <= viewModel.availablePlaces!!)
                            false
                        else if (viewModel.minDate != null && it.date < viewModel.minDate!!) false
                        else if (viewModel.maxDate != null && it.date > viewModel.maxDate!!) false
                        else if (viewModel.startTime != null &&
                            hourDateViewModel.isBeginGreaterThanEnd(
                                it.startTime, viewModel.startTime!!))
                            false
                        else if (viewModel.endTime != null &&
                            hourDateViewModel.isBeginGreaterThanEnd(
                                viewModel.endTime!!,
                                hourDateViewModel.addDurationToTime(it.startTime, it.duration)))
                            false
                        else if (viewModel.distance != null &&
                            viewModel.distance!! <
                                (locationViewModel.getDistanceFromCurrentLocation(it.location)
                                    ?: 0f))
                            false
                        else if (viewModel.onlyPRO && it.type != ActivityType.PRO) false
                        else {
                          if (searchText.isEmpty() || searchText.isBlank()) true
                          else {
                            it.title.contains(searchText, ignoreCase = true) ||
                                it.description.contains(searchText, ignoreCase = true) ||
                                it.location?.shortName?.contains(searchText, ignoreCase = true)
                                    ?: false
                          }
                        }
                      }
                  LazyColumn(
                      modifier =
                          Modifier.fillMaxSize()
                              .padding(horizontal = STANDARD_PADDING.dp)
                              .testTag("lazyColumn")
                              .background(Color(MAIN_BACKGROUND)),
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
                                locationViewModel.getDistanceFromCurrentLocation(activity.location),
                                imageViewModel = imageViewModel)
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
    distance: Float? = null,
    imageViewModel: ImageViewModel
) {
  val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
  val formattedDate = dateFormat.format(activity.date.toDate())

  val isLiked =
      remember(activity.uid, profile?.likedActivities) {
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
        Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFD5EAF3))) {
          // Box for overlaying the title on the image
          Box(modifier = Modifier.fillMaxWidth().height(LARGE_IMAGE_SIZE.dp)) {
            var bitmaps by remember { mutableStateOf(listOf<Bitmap>()) }
            LaunchedEffect(activity.uid) {
              imageViewModel.fetchActivityImagesAsBitmaps(
                  activity.uid, onSuccess = { urls -> bitmaps = urls }, onFailure = {})
            }
            // Display the activity image
            if (activity.images.isNotEmpty() && bitmaps.isNotEmpty()) {
              Image(
                  bitmap = bitmaps[0].asImageBitmap(),
                  contentDescription = activity.title,
                  modifier = Modifier.fillMaxWidth().height(LARGE_IMAGE_SIZE.dp),
                  contentScale = ContentScale.FillWidth)
            } else {
              Image(
                  painter = painterResource(getImageResourceIdForCategory(activity.category)),
                  contentDescription = activity.title,
                  modifier = Modifier.fillMaxWidth().height(LARGE_IMAGE_SIZE.dp),
                  contentScale = ContentScale.Crop)
            }

            // Apply a dark gradient overlay at the bottom to improve contrast
            DarkGradient()

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
            Row(
                modifier =
                    Modifier.align(Alignment.TopEnd)
                        .padding(SMALL_PADDING.dp)
                        .testTag("activityStatusAndInterests")
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                  DisplayInterests(activity)

                  if (profile != null) {
                    if (profile.activities?.contains(activity.uid) == true) {

                      Row(
                          horizontalArrangement = Arrangement.End,
                          verticalAlignment = Alignment.CenterVertically,
                          modifier = Modifier.testTag("activityStatus")) {
                            if (profile.id == activity.creator) {
                              Box(
                                  modifier =
                                      Modifier.padding(SMALL_PADDING.dp)
                                          .testTag("activityStatusPresent")
                                          .background(
                                              Color(PRIMARY_COLOR),
                                              shape =
                                                  RoundedCornerShape(
                                                      TEXT_FONTSIZE
                                                          .dp)) // Purple background with rounded
                                          // corners
                                          .padding(
                                              horizontal = SMALL_PADDING.dp,
                                              vertical = SMALL_PADDING.dp)) {
                                    Text(
                                        text = "YOUR ACTIVITY",
                                        style =
                                            MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold),
                                        modifier =
                                            Modifier.testTag("yourActivityStatus")
                                                .padding(horizontal = STANDARD_PADDING.dp))
                                  }
                            }
                            if (profile.id != activity.creator ||
                                activity.participants.find { it.id == profile.id } != null) {
                              Box(
                                  modifier =
                                      Modifier.padding(TEXT_FONTSIZE.dp)
                                          .testTag("activityStatusEnrolledBox")
                                          .background(
                                              Color(PRIMARY_COLOR),
                                              shape =
                                                  RoundedCornerShape(
                                                      TEXT_FONTSIZE
                                                          .dp)) // Purple background with rounded
                                          // corners
                                          .padding(
                                              horizontal = STANDARD_PADDING.dp,
                                              vertical = SMALL_PADDING.dp) // Inner padding for text
                                  ) {
                                    Text(
                                        text = "ENROLLED",
                                        style =
                                            MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold),
                                        modifier =
                                            Modifier.testTag("enrolledText")
                                                .padding(horizontal = STANDARD_PADDING.dp))
                                  }
                            }
                          }
                    }
                  }
                }
          }

          Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
          Row(
              modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                // Display the date
                DisplayIcon(Icons.Filled.CalendarMonth, "calendar")

                Text(
                    text = formattedDate,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = Color.Black, // Light gray color for the date
                            fontStyle = FontStyle.Italic),
                    modifier = Modifier.weight(1f).testTag("dateText") // Takes up remaining space
                    )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MEDIUM_PADDING.dp),
                    modifier = Modifier.wrapContentWidth() // Ajuste sa largeur au contenu
                    ) {
                      // Prix
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        DisplayIcon(Icons.Outlined.AttachMoney, "price")
                        Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                        Text(
                            text = if (activity.price == 0.0) "Free" else "${activity.price}CHF",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray, fontStyle = FontStyle.Italic),
                            modifier = Modifier.testTag("priceText"))
                      }

                      // Bouton "Like"
                      if (profile != null) {
                        IconButton(
                            onClick = {
                              val newLikeState = !isLiked.value
                              isLiked.value = newLikeState

                              if (newLikeState) {
                                profileViewModel.addLikedActivity(profile.id, activity.uid)
                              } else {
                                profileViewModel.removeLikedActivity(profile.id, activity.uid)
                              }
                            },
                            modifier = Modifier.testTag("likeButton${isLiked.value}")) {
                              Icon(
                                  imageVector =
                                      if (isLiked.value) Icons.Filled.Favorite
                                      else Icons.Outlined.FavoriteBorder,
                                  contentDescription = if (isLiked.value) "Liked" else "Not Liked",
                                  tint = Color(PRIMARY_COLOR))
                            }
                      }
                    }
              }

          Row(
              modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                // Location on the left
                DisplayIcon(Icons.Filled.LocationOn, "location")

                Text(
                    text =
                        (activity.location?.shortName ?: "No location") +
                            if (distance != null) {
                              if (distance < 1) {
                                " | ${round(distance * 1000).toInt()}m"
                              } else {
                                " | ${round(distance * 10) / 10}km"
                              }
                            } else "",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic, color = Color.Black),
                    modifier =
                        Modifier.weight(1f)
                            .testTag("locationAndDistanceText") // Takes up remaining space
                    )
                DisplayIcon(Icons.Filled.Groups, "participants")

                Spacer(modifier = Modifier.width(SMALL_PADDING.dp))

                Text(
                    text = "${activity.participants.size}/${activity.maxPlaces}",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            fontSize = MEDIUM_PADDING.sp),
                    modifier =
                        Modifier.align(Alignment.CenterVertically)
                            .padding(end = MEDIUM_PADDING.dp)
                            .testTag("participantsText"))
              }

          Spacer(modifier = Modifier.height(SMALL_PADDING.dp))

          // Display the activity description
          Text(
              text = activity.description,
              style =
                  MaterialTheme.typography.bodyMedium.copy(color = Color.Black, lineHeight = 20.sp),
              maxLines = 3,
              overflow = TextOverflow.Ellipsis, // add "..." when description is too long
              modifier =
                  Modifier.padding(horizontal = MEDIUM_PADDING.dp).testTag("descriptionText"))
          Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
        }
      }
}

@Composable
fun DisplayInterests(activity: Activity) {

  if (activity.subcategory.contains("Other") || activity.subcategory == "None") {
    Spacer(modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp))
    return
  }

  Box(
      modifier =
          Modifier.padding(STANDARD_PADDING.dp)
              .testTag("interestPresent")
              .background(
                  CategoryColorMap[activity.category] ?: Color.Gray,
                  shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp))
              .padding(horizontal = SMALL_PADDING.dp, vertical = SMALL_PADDING.dp),
  ) {
    Text(
        text = activity.subcategory,
        style =
            MaterialTheme.typography.bodySmall.copy(
                color = Color(PRIMARY_COLOR), fontWeight = FontWeight.SemiBold),
        modifier = Modifier.testTag("subcategoryText").padding(horizontal = STANDARD_PADDING.dp))
  }
}

@Composable
fun DisplayIcon(imageVector: ImageVector, contentDescription: String) {
  Icon(
      imageVector = imageVector,
      contentDescription = contentDescription,
      tint = Color(PRIMARY_COLOR),
      modifier = Modifier.testTag("icon$contentDescription"))
}

@Composable
fun DarkGradient() {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(LARGE_IMAGE_SIZE.dp)
              .background(
                  Brush.verticalGradient(
                      colors = listOf(Color.Transparent, Color.Black.copy(alpha = GRADIENT_MAX)),
                      startY = START_Y,
                      endY = END_Y)))
}

fun Bitmap.resizeToFixedSize(width: Int, height: Int): Bitmap {
  return Bitmap.createScaledBitmap(this, width, height, true)
}
