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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.Button
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
import com.android.sample.model.activity.categories
import com.android.sample.model.activity.types
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.Carousel
import com.android.sample.ui.camera.GalleryScreen
import com.android.sample.ui.components.MyDatePicker
import com.android.sample.ui.components.MyTimePicker
import com.android.sample.ui.dialogs.AddImageDialog
import com.android.sample.ui.dialogs.AddUserDialog
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(
    listActivityViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    locationViewModel: LocationViewModel,
    imageViewModel: ImageViewModel
) {
  val hourDateViewModel: HourDateViewModel = HourDateViewModel()
  val context = LocalContext.current
  var expandedType by remember { mutableStateOf(false) }
  var expandedCategory by remember { mutableStateOf(false) }
  var selectedOptionType by remember { mutableStateOf("Select a type") }
  var selectedOptionCategory by remember { mutableStateOf("Select a category") }
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  val creator = FirebaseAuth.getInstance().currentUser?.uid ?: ""
  var dateIsOpen by remember { mutableStateOf(false) }
  var dateIsSet by remember { mutableStateOf(false) }
  var startTimeIsOpen by remember { mutableStateOf(false) }
  var startTimeIsSet by remember { mutableStateOf(false) }
  var durationIsOpen by remember { mutableStateOf(false) }
  var durationIsSet by remember { mutableStateOf(false) }
  var price by remember { mutableStateOf("") }
  var placesMax by remember { mutableStateOf("") }

  var dueDate by remember { mutableStateOf(Timestamp.now()) }
  var isCamOpen by remember { mutableStateOf(false) }
  var isGalleryOpen by remember { mutableStateOf(false) }
  var startTime by remember { mutableStateOf("") }
  var duration by remember { mutableStateOf("") }
  var showDialogUser by remember { mutableStateOf(false) }
  var showDialogImage by remember { mutableStateOf(false) }

  val locationQuery by locationViewModel.query.collectAsState()
  var showDropdown by remember { mutableStateOf(false) }
  //  val locationSuggestions by locationViewModel.locationSuggestions.collectAsState()
  val locationSuggestions by
      locationViewModel.locationSuggestions.collectAsState(initial = emptyList<Location?>())

  var selectedLocation by remember { mutableStateOf<Location?>(null) }
  // Add scroll
  val scrollState = rememberScrollState()

  // Attendees
  val attendees_: List<User> = listOf<User>()
  var attendees: List<User> by remember { mutableStateOf(attendees_) }
  var selectedImages = remember { mutableStateListOf<Bitmap>() }
  var items = remember { mutableStateListOf<String>() }
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("createActivityScreen"),
      topBar = {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.title_screen_create_activity)) },
        )
      },
      content = { paddingValues ->
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
                      .verticalScroll(scrollState)
                      .testTag("activityCreateScreen"),
          ) {
            if (isGalleryOpen) {
              GalleryScreen(
                  isGalleryOpen = { isGalleryOpen = false },
                  addImage = { bitmap -> selectedImages.add(bitmap) },
                  context = context)
            }
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
                deleteImage = { bitmap -> selectedImages.remove(bitmap) })
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier =
                    Modifier.padding(STANDARD_PADDING.dp)
                        .fillMaxWidth()
                        .testTag("inputTitleCreate"),
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
                        .testTag("inputDescriptionCreate"),
                placeholder = {
                  Text(text = stringResource(id = R.string.request_activity_description))
                })
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            OutlinedButton(
                onClick = { dateIsOpen = true },
                modifier =
                    Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp).testTag("inputDateCreate"),
            ) {
              Icon(
                  Icons.Filled.CalendarMonth,
                  contentDescription = "select date",
                  modifier = Modifier.padding(end = STANDARD_PADDING.dp).testTag("iconDateCreate"))
              if (dateIsSet)
                  Text(
                      "Selected date: ${dueDate.toDate().toString().take(11)}," +
                          "${dueDate.toDate().year+1900}  (click to change)")
              else Text("Select Date for the activity")
            }
            if (dateIsOpen) {
              MyDatePicker(
                  onDateSelected = { date ->
                    dueDate = date
                    dateIsOpen = false
                    dateIsSet = true
                  },
                  isOpen = dateIsOpen,
                  null)
            }
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            OutlinedButton(
                onClick = { startTimeIsOpen = true },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(STANDARD_PADDING.dp)
                        .testTag("inputStartTimeCreate"),
            ) {
              Icon(
                  Icons.Filled.AccessTime,
                  contentDescription = "select start time",
                  modifier =
                      Modifier.padding(end = STANDARD_PADDING.dp).testTag("iconStartTimeCreate"))
              if (startTimeIsSet) Text("Start time: ${startTime} (click to change)")
              else Text("Select start time")
            }
            if (startTimeIsOpen) {
              MyTimePicker(
                  onTimeSelected = { time ->
                    startTime =
                        time
                            .toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalTime()
                            .toString()
                    startTimeIsOpen = false
                    startTimeIsSet = true
                  },
                  isOpen = startTimeIsOpen)
            }

            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            OutlinedButton(
                onClick = { durationIsOpen = true },
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(STANDARD_PADDING.dp)
                        .testTag("inputEndTimeCreate"),
            ) {
              Icon(
                  Icons.Filled.HourglassTop,
                  contentDescription = "select duration",
                  modifier =
                      Modifier.padding(end = STANDARD_PADDING.dp)
                          .align(Alignment.CenterVertically)
                          .testTag("iconEndTimeCreate"))
              if (durationIsSet) Text("Finishing Time: ${duration} (click to change)")
              else Text("Select End Time")
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
                    durationIsSet = true
                  },
                  isOpen = durationIsOpen)
            }
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier =
                    Modifier.padding(STANDARD_PADDING.dp)
                        .fillMaxWidth()
                        .testTag("inputPriceCreate"),
                placeholder = { Text(text = stringResource(id = R.string.request_price_activity)) },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
            OutlinedTextField(
                value = placesMax,
                onValueChange = { placesMax = it },
                label = { Text("Total Places") },
                modifier =
                    Modifier.padding(STANDARD_PADDING.dp)
                        .fillMaxWidth()
                        .testTag("inputPlacesCreate"),
                placeholder = {
                  Text(text = stringResource(id = R.string.request_placesMax_activity))
                },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

            // Location Input with dropdown using ExposedDropdownMenuBox
            ExposedDropdownMenuBox(
                expanded = showDropdown && locationSuggestions.isNotEmpty(),
                onExpandedChange = { showDropdown = it }, // Toggle dropdown visibility ,
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
                          .padding(STANDARD_PADDING.dp)
                          .fillMaxWidth()
                          .testTag("inputLocationCreate"),
                  singleLine = true)

              // Dropdown menu for location suggestions
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
                          onClick = {},
                          modifier = Modifier.padding(STANDARD_PADDING.dp))
                    }
                  }
            }
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

            ExposedDropdownMenuBox(
                modifier =
                    Modifier.testTag("chooseTypeMenu")
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(STANDARD_PADDING.dp),
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
                      modifier = Modifier.menuAnchor().fillMaxWidth().testTag("typeTextField"))
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
                        .align(Alignment.CenterHorizontally)
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

            Spacer(modifier = Modifier.height(LARGE_PADDING.dp))

            Button(
                onClick = { showDialogUser = true },
                modifier =
                    Modifier.width(BUTTON_WIDTH.dp)
                        .height(BUTTON_HEIGHT.dp)
                        .testTag("addAttendeeButton")
                        .align(Alignment.CenterHorizontally),
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
                              .background(Color(0xFFFFFFFF))
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
                              Modifier.width(40.dp).height(40.dp).testTag("removeAttendeeButton"),
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

            if (showDialogUser) {
              AddUserDialog(
                  onDismiss = { showDialogUser = false },
                  onAddUser = { user -> attendees = attendees + user },
              )
            }
            Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
            Button(
                enabled = title.isNotEmpty() && description.isNotEmpty(),
                onClick = {
                  val activityId = listActivityViewModel.getNewUid()
                  if (creator == "") {
                    Toast.makeText(
                            context,
                            "You must be logged in to create an activity.",
                            Toast.LENGTH_SHORT)
                        .show()
                  } else if (!hourDateViewModel.isBeginGreaterThanEnd(startTime, duration)) {
                    Toast.makeText(
                            context,
                            "The start time must be before the end time.",
                            Toast.LENGTH_SHORT)
                        .show()
                  } else if (price.toDoubleOrNull() == null) {
                    Toast.makeText(context, "Invalid price format.", Toast.LENGTH_SHORT).show()
                  } else if (placesMax.toLongOrNull() == null) {
                    Toast.makeText(context, "Invalid places format.", Toast.LENGTH_SHORT).show()
                  } else if (selectedLocation == null) {
                    Toast.makeText(context, "You must select a location.", Toast.LENGTH_SHORT)
                        .show()
                  } else {
                    attendees += profileViewModel.userState.value!!
                    try {
                      imageViewModel.uploadActivityImages(
                          activityId,
                          selectedImages,
                          onSuccess = { imageUrls ->
                            items.addAll(imageUrls) // Store URLs in items to retrieve later
                          },
                          onFailure = { exception ->
                            Toast.makeText(
                                    context,
                                    "Failed to upload images: ${exception.message}",
                                    Toast.LENGTH_SHORT)
                                .show()
                          })
                      val activity =
                          Activity(
                              uid = activityId,
                              title = title,
                              description = description,
                              date = dueDate,
                              startTime = startTime,
                              duration = hourDateViewModel.calculateDuration(startTime, duration),
                              price = price.toDouble(),
                              placesLeft = attendees.size.toLong(),
                              maxPlaces = placesMax.toLongOrNull() ?: 0,
                              creator = creator,
                              status = ActivityStatus.ACTIVE,
                              location = selectedLocation,
                              images = items,
                              participants = attendees,
                              type = types.find { it.name == selectedOptionType } ?: types[0],
                              comments = listOf(),
                              category =
                                  categories.find { it.name == selectedOptionCategory }
                                      ?: categories[0])
                      listActivityViewModel.addActivity(activity)
                      profileViewModel.addActivity(creator, activity.uid)
                      navigationActions.navigateTo(Screen.OVERVIEW)
                    } catch (_: NumberFormatException) {
                      println("There is an error")
                    }
                  }
                },
                modifier =
                    Modifier.width(BUTTON_WIDTH.dp)
                        .height(BUTTON_HEIGHT.dp)
                        .testTag("createButton")
                        .align(Alignment.CenterHorizontally),
            ) {
              Row(
                  horizontalArrangement =
                      Arrangement.spacedBy(STANDARD_PADDING.dp, Alignment.CenterHorizontally),
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "add a new activity",
                )
                Text(text = stringResource(id = R.string.button_create_activity))
              }
            }
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.ADD_ACTIVITY)
      })
}
