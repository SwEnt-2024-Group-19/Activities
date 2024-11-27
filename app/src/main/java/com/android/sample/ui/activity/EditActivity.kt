package com.android.sample.ui.activity

import android.graphics.Bitmap
import android.widget.Toast
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.types
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationViewModel
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.WHITE_COLOR
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.GalleryScreen
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
    locationViewModel: LocationViewModel
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
  var placesLeft by remember { mutableStateOf(activity?.placesLeft.toString()) }
  var maxPlaces by remember { mutableStateOf(activity?.maxPlaces.toString()) }
  var attendees by remember { mutableStateOf(activity?.participants!!) }
  var startTime by remember { mutableStateOf(activity?.startTime) }
  var duration by remember { mutableStateOf(hourDateViewModel.addDurationToTime(startTime?:"",activity?.duration?:"")) }
  var expanded by remember { mutableStateOf(false) }
  val controller = remember {
    LifecycleCameraController(context).apply { setEnabledUseCases(CameraController.IMAGE_CAPTURE) }
  }
  var selectedOption by remember { mutableStateOf(activity?.type.toString()) }

  val locationQuery by locationViewModel.query.collectAsState()
  var showDropdown by remember { mutableStateOf(false) }
  val locationSuggestions by
      locationViewModel.locationSuggestions.collectAsState(initial = emptyList<Location?>())

  // var items by remember { mutableStateOf(activity?.images ?: listOf()) }
  var isGalleryOpen by remember { mutableStateOf(false) }
  var selectedImages = remember { mutableStateListOf<Bitmap>() }

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
              addImage = { bitmap -> selectedImages.add(bitmap) },
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
              addElem = { bitmap -> selectedImages.add(bitmap) })
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

            Spacer(modifier = Modifier.height(8.dp))
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
                  Icons.Default.CalendarMonth,
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
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }) {
                  OutlinedTextField(
                      readOnly = true,
                      value = selectedOption,
                      onValueChange = {},
                      label = { Text("Activity Type") },
                      trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                      },
                      colors = ExposedDropdownMenuDefaults.textFieldColors(),
                      modifier = Modifier.menuAnchor().fillMaxWidth())
                  ExposedDropdownMenu(
                      expanded = expanded,
                      onDismissRequest = { expanded = false },
                      modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp)) {
                        types.forEach { selectionOption ->
                          DropdownMenuItem(
                              modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp),
                              text = { Text(selectionOption.name) },
                              onClick = {
                                selectedOption = selectionOption.name
                                expanded = false
                              })
                        }
                      }
                }
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

            // Location Input with dropdown using ExposedDropdownMenuBox
            ExposedDropdownMenuBox(
                expanded = showDropdown && locationSuggestions.isNotEmpty(),
                onExpandedChange = { showDropdown = it } // Toggle dropdown visibility
                ) {
                  OutlinedTextField(
                      value = locationQuery,
                      onValueChange = {
                        locationViewModel.setQuery(it)
                        showDropdown = true // Show dropdown when user starts typing
                      },
                      label = { Text("Location") },
                      placeholder = { Text("Enter an Address or Location") },
                      modifier =
                          Modifier.menuAnchor() // Anchor the dropdown to this text field
                              .fillMaxWidth()
                              .testTag("inputLocationEdit"),
                      singleLine = true)

                  // Dropdown menu for location suggestions
                  // Another approach using DropdownMenu is in EditToDo.kt
                  ExposedDropdownMenu(
                      expanded = showDropdown && locationSuggestions.isNotEmpty(),
                      onDismissRequest = { showDropdown = false }) {
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
                              onClick = { /* Optionally show more results */},
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
                  Card(
                      modifier =
                          Modifier.padding(STANDARD_PADDING.dp)
                              .background(Color(WHITE_COLOR))
                              .testTag("attendeeRow${index}"),
                  ) {
                    Row {
                      Column(modifier = Modifier.padding(STANDARD_PADDING.dp)) {
                        Text(
                            text = "${attendees[index].name} ${attendees[index].surname}",
                            modifier = Modifier.testTag("attendeeName${index}"),
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                        )
                      }
                      IconButton(
                          onClick = { attendees = attendees.filter { it != attendees[index] } },
                          modifier =
                              Modifier.width(BUTTON_HEIGHT.dp)
                                  .height(BUTTON_HEIGHT.dp)
                                  .testTag("removeAttendeeButton"),
                      ) {
                        Icon(
                            Icons.Filled.PersonRemove,
                            contentDescription = "remove attendee",
                        )
                      }
                    }
                  }
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
                enabled = title.isNotEmpty() && description.isNotEmpty(),
                onClick = {
                  if (hourDateViewModel.isBeginGreaterThanEnd(
                      startTime ?: "00:00", duration ?: "00:01")) {
                    Toast.makeText(
                            context, "Start time must be before end time", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                  }
                  try {
                    val updatedActivity =
                        Activity(
                            uid = activity?.uid ?: "",
                            title = title,
                            description = description,
                            date = dueDate,
                            startTime = startTime ?: "",
                            duration = hourDateViewModel.calculateDuration(startTime ?: "", duration ?: ""),
                            price = price.toDouble(),
                            placesLeft = attendees.size.toLong(),
                            maxPlaces = maxPlaces.toLongOrNull() ?: 0,
                            creator = creator,
                            status = ActivityStatus.ACTIVE,
                            location = selectedLocation,
                            images = activity?.images ?: listOf(),
                            type = types.find { it.name == selectedOption } ?: types[0],
                            participants = attendees,
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
