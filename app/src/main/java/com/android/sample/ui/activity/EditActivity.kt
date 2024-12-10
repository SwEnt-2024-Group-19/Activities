package com.android.sample.ui.activity

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
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
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.Carousel
import com.android.sample.ui.camera.GalleryScreen
import com.android.sample.ui.components.AttendantPreview
import com.android.sample.ui.components.MyDatePicker
import com.android.sample.ui.components.MyTimePicker
import com.android.sample.ui.dialogs.AddImageDialog
import com.android.sample.ui.dialogs.AddUserDialog
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    listActivityViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    locationViewModel: LocationViewModel,
    imageViewModel: ImageViewModel
) {
  val hourDateViewModel: HourDateViewModel = HourDateViewModel()
  val context = LocalContext.current
  var showDialog by remember { mutableStateOf(false) }
  var showDialogImage by remember { mutableStateOf(false) }
  var isCamOpen by remember { mutableStateOf(false) }
  val activity = listActivityViewModel.selectedActivity.collectAsState().value
  var title by remember { mutableStateOf(activity?.title ?: "") }
  var description by remember { mutableStateOf(activity?.description ?: "") }
  val creator by remember { mutableStateOf(activity?.creator ?: "") }
  var selectedLocation by remember { mutableStateOf(Location(0.0, 0.0, "No location")) }
  var price by remember { mutableStateOf(activity?.price.toString()) }
  var maxPlaces by remember { mutableStateOf(activity?.maxPlaces.toString()) }
  var attendees by remember { mutableStateOf(activity?.participants!!) }
  var startTime by remember { mutableStateOf(activity?.startTime) }
  var duration by remember {
    mutableStateOf(
        hourDateViewModel.addDurationToTime(startTime ?: "00:00", activity?.duration ?: "00:01"))
  }
  var expanded by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf(activity?.type.toString()) }
  var expandedType by remember { mutableStateOf(false) }
  var expandedCategory by remember { mutableStateOf(false) }
  var selectedOptionType by remember { mutableStateOf(activity?.type.toString()) }
  var selectedOptionCategory by remember { mutableStateOf(activity?.category.toString()) }
  val maxDescriptionLength = 500
  val maxTitleLength = 50
  val locationQuery by locationViewModel.query.collectAsState()
  var showDropdown by remember { mutableStateOf(false) }
  val locationSuggestions by
      locationViewModel.locationSuggestions.collectAsState(initial = emptyList<Location?>())

  var isGalleryOpen by remember { mutableStateOf(false) }
  var selectedImages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
  var items by remember { mutableStateOf(activity?.images ?: listOf()) }
  imageViewModel.fetchActivityImagesAsBitmaps(
      activity?.uid ?: "",
      { bitmaps -> selectedImages = bitmaps.toMutableStateList() },
      onFailure = { error ->
        Log.e("EditActivityScreen", "Failed to fetch images: ${error.message}")
      })
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
                  })
            }
            Carousel(
                openDialog = { showDialogImage = true },
                itemsList = selectedImages,
                deleteImage = { bitmap -> selectedImages = selectedImages.filter { it != bitmap } })
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            RemainingPlace(title, maxTitleLength)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier =
                    Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputTitleEdit"),
                placeholder = { Text(text = stringResource(id = R.string.request_activity_title)) },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            RemainingPlace(description, maxDescriptionLength)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier =
                    Modifier.padding(STANDARD_PADDING.dp)
                        .fillMaxWidth()
                        .testTag("inputDescriptionEdit"),
                placeholder = {
                  Text(text = stringResource(id = R.string.request_activity_description))
                },
            )
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            OutlinedButton(
                onClick = { dateIsOpen = true },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(STANDARD_PADDING.dp)
                        .testTag("changeDateButton"),
            ) {
              Icon(
                  Icons.Default.CalendarMonth,
                  contentDescription = "Change Date",
                  modifier = Modifier.testTag("changeDateIcon"),
              )
              Text(
                  "Change Date (Actual: ${dueDate.toDate().toString().take(11)}," +
                      "${dueDate.toDate().year + 1900})")
            }
            if (dateIsOpen) {
              MyDatePicker(
                  onDateSelected = { date ->
                    dueDate = date
                    dateIsOpen = false
                  },
                  isOpen = dateIsOpen,
                  initialDate =
                      dueDate
                          .toDate()
                          .toInstant()
                          .atZone(java.time.ZoneId.systemDefault())
                          .toLocalDate(),
              )
            }

            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            OutlinedButton(
                onClick = { timeIsOpen = true },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(STANDARD_PADDING.dp)
                        .testTag("changeTimeButton"),
            ) {
              Icon(
                  Icons.Default.Schedule,
                  contentDescription = "Change Time",
                  modifier = Modifier.testTag("changeTimeIcon"),
              )
              Text("Change Start Time (Actual: ${startTime})")
            }
            if (timeIsOpen) {
              MyTimePicker(
                  onTimeSelected = { time ->
                    startTime =
                        time
                            .toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalTime()
                            .toString()
                    timeIsOpen = false
                  },
                  isOpen = timeIsOpen,
              )
            }
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            OutlinedButton(
                onClick = { durationIsOpen = true },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(STANDARD_PADDING.dp)
                        .testTag("changeEndingTimeButton"),
            ) {
              Icon(
                  Icons.Default.HourglassTop,
                  contentDescription = "Change Ending Time",
                  modifier = Modifier.testTag("changeEndingTimeIcon"))
              Text("Change Ending Time (Actual: ${duration})")
            }
            if (durationIsOpen) {
              MyTimePicker(
                  onTimeSelected = { time ->
                    duration =
                        time
                            .toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalTime()
                            .toString()
                    durationIsOpen = false
                  },
                  isOpen = durationIsOpen,
              )
            }
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier =
                    Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputPriceEdit"),
                placeholder = { Text(text = stringResource(id = R.string.request_price_activity)) },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            OutlinedTextField(
                value = maxPlaces,
                onValueChange = { maxPlaces = it },
                label = { Text("Total Places") },
                modifier =
                    Modifier.padding(STANDARD_PADDING.dp)
                        .fillMaxWidth()
                        .testTag("inputPlacesLeftEdit"),
                placeholder = {
                  Text(text = stringResource(id = R.string.request_placesMax_activity))
                },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            ExposedDropdownMenuBox(
                modifier =
                    Modifier.testTag("chooseTypeMenu").fillMaxWidth().padding(STANDARD_PADDING.dp),
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType }) {
                  OutlinedTextField(
                      readOnly = true,
                      value = selectedOptionType,
                      onValueChange = {},
                      label = { Text("Activity Type") },
                      trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                      },
                      colors = ExposedDropdownMenuDefaults.textFieldColors(),
                      modifier = Modifier.menuAnchor().fillMaxWidth())
                  ExposedDropdownMenu(
                      expanded = expandedType,
                      onDismissRequest = { expandedType = false },
                      modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp)) {
                        types.forEach { selectionOption ->
                          DropdownMenuItem(
                              modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp),
                              text = { Text(selectionOption.name) },
                              onClick = {
                                selectedOptionType = selectionOption.name
                                expandedType = false
                              })
                        }
                      }
                }

            ExposedDropdownMenuBox(
                modifier =
                    Modifier.testTag("chooseCategoryMenu")
                        .fillMaxWidth()
                        .padding(STANDARD_PADDING.dp),
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }) {
                  OutlinedTextField(
                      readOnly = true,
                      value = selectedOptionCategory,
                      onValueChange = {},
                      label = { Text("Activity Category") },
                      trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                      },
                      colors = ExposedDropdownMenuDefaults.textFieldColors(),
                      modifier = Modifier.menuAnchor().fillMaxWidth().testTag("categoryTextField"))
                  ExposedDropdownMenu(
                      expanded = expandedCategory,
                      onDismissRequest = { expandedCategory = false },
                      modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp)) {
                        categories.forEach { selectionOption ->
                          DropdownMenuItem(
                              modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp),
                              text = { Text(selectionOption.name) },
                              onClick = {
                                selectedOptionCategory = selectionOption.name
                                expandedCategory = false
                              })
                        }
                      }
                }
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

            Box {
              OutlinedTextField(
                  value = locationQuery,
                  onValueChange = {
                    locationViewModel.setQuery(it)
                    showDropdown = it != "" // Show dropdown when user starts typing
                  },
                  label = { Text("Location") },
                  placeholder = { Text("Enter an Address or Location") },
                  modifier =
                      Modifier.padding(STANDARD_PADDING.dp)
                          .fillMaxWidth()
                          .testTag("inputLocationEdit"),
                  singleLine = true)

              // Dropdown menu for location suggestions
              DropdownMenu(
                  expanded = showDropdown && locationSuggestions.isNotEmpty(),
                  onDismissRequest = { showDropdown = false },
                  properties = PopupProperties(focusable = false)) {
                    locationSuggestions.filterNotNull().take(3).forEach { location ->
                      DropdownMenuItem(
                          text = {
                            Text(
                                text =
                                    location.name.take(30) +
                                        if (location.name.length > 30) "..."
                                        else "", // Limit name length
                                maxLines = 1 // Ensure name doesn't overflow
                                )
                          },
                          onClick = {
                            locationViewModel.setQuery(location.name)
                            selectedLocation = location
                            showDropdown = false // Close dropdown on selection
                          },
                          modifier = Modifier.padding(STANDARD_PADDING.dp))
                    }

                    if (locationSuggestions.size > 3) {
                      DropdownMenuItem(
                          text = { Text("More...") },
                          onClick = { /* TODO: Define behavior for 'More...' */},
                          modifier = Modifier.padding(STANDARD_PADDING.dp))
                    }
                  }
            }

            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

            Button(
                onClick = { showDialog = true },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(STANDARD_PADDING.dp)
                        .testTag("addAttendeeButton"),
            ) {
              Row(
                  horizontalArrangement =
                      Arrangement.spacedBy(STANDARD_PADDING.dp, Alignment.CenterHorizontally),
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "add a new attendee",
                )
                Text("Add Attendee")
              }
            }
            if (attendees.isNotEmpty()) {
              LazyRow(
                  modifier = Modifier.fillMaxHeight().height(85.dp).padding(STANDARD_PADDING.dp),
              ) {
                items(attendees.size) { index ->
                  AttendantPreview(
                      onProfileClick = {
                        if (attendees[index].id == creator) {
                          navigationActions.navigateTo(Screen.PROFILE)
                        } else if (attendees[index].id == "") {
                          Toast.makeText(context, "This user is not registered", Toast.LENGTH_SHORT)
                              .show()
                        } else {
                          listActivityViewModel.selectUser(attendees[index])
                          navigationActions.navigateTo(Screen.PARTICIPANT_PROFILE)
                        }
                      },
                      imageViewModel = imageViewModel,
                      deleteAttendant = { user -> attendees = attendees.filter { it != user } },
                      user = attendees[index],
                      index = index)
                }
              }
            }
            if (showDialog) {
              AddUserDialog(
                  onDismiss = { showDialog = false },
                  onAddUser = { user -> attendees = attendees + user },
              )
            }
            Spacer(Modifier.height(MEDIUM_PADDING.dp))

            Button(
                enabled =
                    title.isNotEmpty() &&
                        description.isNotEmpty() &&
                        price.isNotEmpty() &&
                        maxPlaces.isNotEmpty() &&
                        selectedOptionType != "Select a type" &&
                        selectedOptionCategory != "Select a category" &&
                        startTime?.isNotEmpty() ?: false &&
                        duration.isNotEmpty(),
                onClick = {
                  if (!hourDateViewModel.isBeginGreaterThanEnd(
                      startTime ?: "00:00", duration ?: "00:01")) {
                    Toast.makeText(
                            context, "Start time must be before end time", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                  }
                  try {
                    imageViewModel.uploadActivityImages(
                        activity?.uid ?: "",
                        selectedImages.toList(),
                        { urls -> items = urls },
                        { error ->
                          Log.e("EditActivityScreen", "Failed to upload images: ${error.message}")
                        })
                    val updatedActivity =
                        Activity(
                            uid = activity?.uid ?: "",
                            title = title,
                            description = description,
                            date = dueDate,
                            startTime = startTime ?: "",
                            duration =
                                hourDateViewModel.calculateDuration(
                                    startTime ?: "", duration ?: ""),
                            price = price.toDouble(),
                            placesLeft = attendees.size.toLong(),
                            maxPlaces = maxPlaces.toLongOrNull() ?: 0,
                            creator = creator,
                            status = ActivityStatus.ACTIVE,
                            location = selectedLocation,
                            images = activity?.images ?: listOf(),
                            type = types.find { it.name == selectedOption } ?: types[0],
                            participants = attendees,
                            category =
                                categories.find { it.name == selectedOptionCategory }
                                    ?: categories[0],
                            comments = activity?.comments ?: listOf())
                    listActivityViewModel.updateActivity(updatedActivity)
                    navigationActions.navigateTo(Screen.OVERVIEW)
                  } catch (_: Exception) {}
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
                  imageViewModel.removeAllActivityImages(
                      activity?.uid ?: "",
                      { Log.d("EditActivityScreen", "Images removed") },
                      { error ->
                        Log.e("EditActivityScreen", "Failed to remove images: ${error.message}")
                      })

                  navigationActions.navigateTo(Screen.OVERVIEW)
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
