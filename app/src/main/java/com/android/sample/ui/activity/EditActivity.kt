package com.android.sample.ui.activity

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.categories
import com.android.sample.model.activity.types
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.categoryOf
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.GalleryScreen
import com.android.sample.ui.dialogs.AddImageDialog
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/**
 * Composable function to display the Edit Activity screen. This screen allows the user to edit an
 * existing activity.
 *
 * @param listActivityViewModel ViewModel for managing the list of activities.
 * @param navigationActions Navigation actions for navigating between screens.
 * @param locationViewModel ViewModel for managing location data.
 * @param imageViewModel ViewModel for managing images.
 * @param profileViewModel ViewModel for managing user profiles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    listActivityViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    locationViewModel: LocationViewModel,
    imageViewModel: ImageViewModel,
    profileViewModel: ProfileViewModel
) {
  val hourDateViewModel = HourDateViewModel()
  val context = LocalContext.current
  var showDialog by remember { mutableStateOf(false) }
  var showDialogImage by remember { mutableStateOf(false) }
  var isCamOpen by remember { mutableStateOf(false) }
  val activity = listActivityViewModel.selectedActivity.collectAsState().value
  var title by remember { mutableStateOf(activity?.title ?: "") }
  var description by remember { mutableStateOf(activity?.description ?: "") }
  val creator by remember { mutableStateOf(activity?.creator ?: "") }
  var selectedLocation by remember {
    mutableStateOf(activity?.location ?: Location(0.0, 0.0, "name", "Origin"))
  }
  var price by remember { mutableStateOf(activity?.price.toString()) }
  var maxPlaces by remember { mutableStateOf(activity?.maxPlaces.toString()) }
  var attendees by remember { mutableStateOf(activity?.participants!!) }
  var startTime by remember { mutableStateOf(activity?.startTime) }
  var duration by remember { mutableStateOf(activity?.duration ?: "00:01") }
  var selectedOption by remember { mutableStateOf(activity?.type.toString()) }
  var expandedType by remember { mutableStateOf(false) }
  var expandedCategory by remember { mutableStateOf(false) }
  var expandedInterest by remember { mutableStateOf(false) }
  var selectedOptionType by remember { mutableStateOf(activity?.type.toString()) }
  var selectedOptionCategory by remember { mutableStateOf(activity?.category) }
  var selectedOptionInterest by remember { mutableStateOf(activity?.subcategory) }
  val maxDescriptionLength = 500
  val maxTitleLength = 50
  val locationQuery by locationViewModel.query.collectAsState()
  locationViewModel.setQuery(selectedLocation.name)
  var showDropdown by remember { mutableStateOf(false) }
  val locationSuggestions by
      locationViewModel.locationSuggestions.collectAsState(initial = emptyList<Location?>())

  var isGalleryOpen by remember { mutableStateOf(false) }
  var selectedImages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
  var items by remember { mutableStateOf(activity?.images ?: listOf()) }
  LaunchedEffect(activity!!.uid) {
    imageViewModel.fetchActivityImagesAsBitmaps(
        activity.uid,
        { bitmaps -> selectedImages = bitmaps.toMutableStateList() },
        onFailure = { error ->
          Log.e("EditActivityScreen", "Failed to fetch images: ${error.message}")
        })
  }
  // Handle the error, e.g., show a Toast or log the exception
  var dueDate by remember { mutableStateOf(activity?.date ?: Timestamp.now()) }
  var dateIsOpen by remember { mutableStateOf(false) }
  var timeIsOpen by remember { mutableStateOf(false) }
  var durationIsOpen by remember { mutableStateOf(false) }
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
            title = { Text("Edit the activity") },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back")
                  }
            })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      }) { paddingValues ->
        if (isGalleryOpen) {
          GalleryScreen(
              isGalleryOpen = { isGalleryOpen = false },
              addImage = { bitmap -> selectedImages = selectedImages + (bitmap) },
              context = context)
        }
        if (isCamOpen) {
          CameraScreen(
              paddingValues = paddingValues,
              controller =
                  remember {
                    LifecycleCameraController(context).apply {
                      setEnabledUseCases(CameraController.IMAGE_CAPTURE)
                    }
                  },
              context = context,
              isCamOpen = { isCamOpen = false },
              addElem = { bitmap -> selectedImages = selectedImages + (bitmap) })
        } else {
          Column(
              modifier =
                  Modifier.padding(paddingValues)
                      .fillMaxSize()
                      .background(color = Color(0xFFFFFFFF))
                      .verticalScroll(rememberScrollState())
                      .testTag("activityEditScreen"),
          ) {
            if (showDialogImage) {
              AddImageDialog(
                  onDismiss = { showDialogImage = false },
                  onGalleryClick = {
                    showDialogImage = false
                    isGalleryOpen = true
                  },
                  onCameraClick = {
                    showDialogImage = false
                    isCamOpen = true
                  },
                  onSelectDefault = { showDialogImage = false })
            }
            ActivityForm(
                context = context,
                selectedImages = selectedImages,
                onOpenDialogImage = { showDialogImage = true },
                onDeleteImage = { bitmap -> selectedImages -= (bitmap) },
                title = title,
                onTitleChange = { title = it },
                maxTitleSize = maxTitleLength,
                description = description,
                maxDescriptionSize = maxDescriptionLength,
                onDescriptionChange = { description = it },
                onClickDate = { dateIsOpen = true },
                onCloseDate = { dateIsOpen = false },
                onSelectDate = {
                  dueDate = it
                  dateIsOpen = false
                },
                dueDate = dueDate,
                dateIsOpen = dateIsOpen,
                dateIsSet = true,
                onClickStartingTime = { timeIsOpen = true },
                startTimeIsOpen = timeIsOpen,
                startTimeIsSet = true,
                onStartTimeSelected = { time ->
                  startTime =
                      time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().toString()
                  timeIsOpen = false
                },
                startTime = startTime ?: "00:00",
                onCloseStartTime = { timeIsOpen = false },
                onClickDurationTime = { durationIsOpen = true },
                durationIsOpen = durationIsOpen,
                durationIsSet = true,
                onSelectDuration = { time ->
                  duration =
                      time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().toString()
                  durationIsOpen = false
                },
                onCloseDuration = { durationIsOpen = false },
                duration = duration,
                price = price,
                onPriceChange = { price = it },
                placesMax = maxPlaces,
                onPlacesMaxChange = { maxPlaces = it },
                locationQuery = locationQuery,
                onLocationQueryChange = {
                  locationViewModel.setQuery(it)
                  showDropdown = it != "" // Show dropdown when user starts typing
                },
                showDropdown = showDropdown,
                locationSuggestions = locationSuggestions,
                onDismissLocation = { showDropdown = false },
                onLocationClick = { location ->
                  locationViewModel.setQuery(location.name)
                  selectedLocation = location
                  showDropdown = false // Close dropdown on selection
                },
                expandedType = expandedType,
                onExpandedTypeChange = { expandedType = !expandedType },
                onSelectType = { selectionOption ->
                  selectedOptionType = selectionOption.name
                  expandedType = false
                },
                onDismissType = { expandedType = false },
                selectedOptionType = selectedOptionType,
                expandedCategory = expandedCategory,
                onExpandedCategoryChange = { expandedCategory = !expandedCategory },
                onDismissCategory = { expandedCategory = false },
                selectedOptionCategory = selectedOptionCategory,
                selectedOptionInterest = selectedOptionInterest,
                expandedInterest = expandedInterest,
                onInterestExpandChange = { expandedInterest = !expandedInterest },
                onInterestDismiss = { expandedInterest = false },
                onInterestSelect = { selectionOption ->
                  selectedOptionInterest = selectionOption
                  expandedInterest = false
                },
                onSelectCategory = {
                  selectedOptionCategory = it
                  expandedCategory = false
                },
                attendees = attendees,
                showDialogUser = showDialog,
                deleteAttendant = { user -> attendees -= user },
                onDismissUserDialog = { showDialog = false },
                onAddUser = { user -> attendees += user },
                onOpenUserDialog = { showDialog = true },
                onProfileClick = { user ->
                  if (user.id == creator) {
                    navigationActions.navigateTo(Screen.PROFILE)
                  } else if (user.id == "") {
                    Toast.makeText(context, "This user is not registered", Toast.LENGTH_SHORT)
                        .show()
                  } else {
                    profileViewModel.clearUserData()
                    profileViewModel.fetchUserData(user.id)
                    profileViewModel.userState.value?.let {
                      listActivityViewModel.selectUser(it)
                      navigationActions.navigateTo(Screen.PARTICIPANT_PROFILE)
                    }
                  }
                },
                imageViewModel = imageViewModel,
            )
            Spacer(Modifier.height(MEDIUM_PADDING.dp))

            Button(
                enabled =
                    title.isNotEmpty() &&
                        description.isNotEmpty() &&
                        price.isNotEmpty() &&
                        maxPlaces.isNotEmpty() &&
                        selectedOptionType != "Select a type" &&
                        selectedOptionCategory != null &&
                        startTime?.isNotEmpty() ?: false &&
                        duration.isNotEmpty(),
                onClick = {
                  val activityTimestamps =
                      startTime?.let { hourDateViewModel.combineDateAndTime(dueDate, it) }
                  val activityDateTime = activityTimestamps?.toInstant()?.toEpochMilli()

                  // we disable creating activities 1 hour before start time
                  if (activityDateTime != null) {
                    if (activityDateTime - System.currentTimeMillis() <
                        TimeUnit.HOURS.toMillis(1)) {
                      Toast.makeText(
                              context,
                              context.getString(R.string.schedule_activity),
                              Toast.LENGTH_SHORT)
                          .show()
                      return@Button
                    } else if (price.isBlank() ||
                        price.toDoubleOrNull() == null ||
                        price.toDouble() < 0) {
                      Toast.makeText(
                              context,
                              context.getString(R.string.invalid_price_format),
                              Toast.LENGTH_SHORT)
                          .show()
                      return@Button
                    } else if (maxPlaces.isBlank() ||
                        maxPlaces.toLongOrNull() == null ||
                        maxPlaces.toLong() <= 0) {
                      Toast.makeText(
                              context,
                              context.getString(R.string.invalid_places_format),
                              Toast.LENGTH_SHORT)
                          .show()
                      return@Button
                    } else if (attendees.size >= maxPlaces.toLong()) {
                      Toast.makeText(
                              context,
                              context.getString(R.string.max_places_exceed),
                              Toast.LENGTH_SHORT)
                          .show()
                      return@Button
                    } else if (selectedOptionCategory != null &&
                        selectedOptionCategory != categoryOf[selectedOptionInterest]) {
                      Toast.makeText(
                              context,
                              context.getString(R.string.invalid_interest_category),
                              Toast.LENGTH_SHORT)
                          .show()
                      return@Button
                    } else {
                      try {
                        imageViewModel.uploadActivityImages(
                            activity?.uid ?: "",
                            selectedImages.toList(),
                            { urls -> items = urls },
                            { _ -> })
                        val updatedActivity =
                            Activity(
                                uid = activity?.uid ?: "",
                                title = title,
                                description = description,
                                date = dueDate,
                                startTime = startTime ?: "",
                                duration = duration,
                                price = price.toDouble(),
                                placesLeft = attendees.size.toLong(),
                                maxPlaces = maxPlaces.toLongOrNull() ?: 0,
                                creator = creator,
                                status = ActivityStatus.ACTIVE,
                                location = selectedLocation,
                                images = activity?.images ?: listOf(),
                                type = types.find { it.name == selectedOption } ?: types[0],
                                participants = attendees,
                                category = selectedOptionCategory ?: categories[0],
                                subcategory = selectedOptionInterest ?: "",
                                comments = activity?.comments ?: listOf())
                        listActivityViewModel.updateActivity(updatedActivity)
                        navigationActions.navigateTo(Screen.OVERVIEW)
                      } catch (_: Exception) {}
                    }
                  }
                },
                modifier =
                    Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp).testTag("editButton"),
            ) {
              Row(
                  horizontalArrangement =
                      Arrangement.spacedBy(STANDARD_PADDING.dp, Alignment.CenterHorizontally),
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                    Icons.Default.Done,
                    contentDescription = "add a new activity",
                )

                Text("Save", color = Color.White)
              }
            }
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
            Button(
                colors =
                    ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Red,
                        disabledContentColor = Color.Red,
                        disabledContainerColor = Color.Transparent,
                    ),
                onClick = {
                  listActivityViewModel.deleteActivityById(activity?.uid ?: "")
                  imageViewModel.removeAllActivityImages(activity?.uid ?: "", {}, { _ -> })

                  navigationActions.navigateTo(Screen.PROFILE)
                },
                modifier =
                    Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp).testTag("deleteButton"),
            ) {
              Row(
                  Modifier.background(Color.Transparent),
                  horizontalArrangement =
                      Arrangement.spacedBy(STANDARD_PADDING.dp, Alignment.CenterHorizontally),
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "add a new activity",
                )
                Text("Delete")
              }
            }
          }
        }
      }
}
