package com.android.sample.ui.activity

import android.graphics.Bitmap
import android.icu.util.GregorianCalendar
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.android.sample.model.camera.uploadActivityImages
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
    locationViewModel: LocationViewModel
) {
  val context = LocalContext.current
  var expanded by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf("Select a type") }
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  val creator = FirebaseAuth.getInstance().currentUser?.uid ?: ""

  var price by remember { mutableStateOf("") }
  var placesMax by remember { mutableStateOf("") }
  var dueDate by remember { mutableStateOf("") }
  val controller = remember {
    LifecycleCameraController(context).apply { setEnabledUseCases(CameraController.IMAGE_CAPTURE) }
  }
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

            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Date") },
                modifier =
                    Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputDateCreate"),
                placeholder = {
                  Text(text = stringResource(id = R.string.request_date_activity_withFormat))
                },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                label = { Text("Time") },
                modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputTimeCreate"),
                placeholder = { Text(text = stringResource(id = R.string.hour_min_format)) },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration") },
                modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth().testTag("inputDurationCreate"),
                placeholder = { Text(text = stringResource(id = R.string.hour_min_format)) },
                singleLine = true,
            )

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
                enabled = title.isNotEmpty() && description.isNotEmpty() && dueDate.isNotEmpty(),
                onClick = {
                  val timeFormat = startTime.split(":")
                  if (timeFormat.size != 2) {
                    Toast.makeText(
                            context, "Invalid format, time must be HH:MM.", Toast.LENGTH_SHORT)
                        .show()
                  }
                  val durationFormat = duration.split(":")
                  if (durationFormat.size != 2) {
                    Toast.makeText(
                            context, "Invalid format, duration must be HH:MM.", Toast.LENGTH_SHORT)
                        .show()
                  }
                  val calendar = GregorianCalendar()
                  val parts = dueDate.split("/")

                  val activityId = listActivityViewModel.getNewUid()
                  if (parts.size == 3 && timeFormat.size == 2 && durationFormat.size == 2) {
                    attendees += profileViewModel.userState.value!!
                    try {
                      calendar.set(
                          parts[2].toInt(),
                          parts[1].toInt() - 1, // Months are 0-based
                          parts[0].toInt(),
                          0,
                          0,
                          0)
                      uploadActivityImages(
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
                              date = Timestamp(calendar.time),
                              startTime = startTime,
                              duration = duration,
                              price = price.toDouble(),
                              placesLeft = attendees.size.toLong(),
                              maxPlaces = placesMax.toLongOrNull() ?: 0,
                              creator = creator,
                              status = ActivityStatus.ACTIVE,
                              location = selectedLocation,
                              images = items,
                              participants = attendees,
                              type = types.find { it.name == selectedOption } ?: types[0],
                              comments = listOf())
                      listActivityViewModel.addActivity(activity)
                      profileViewModel.addActivity(creator, activity.uid)
                      navigationActions.navigateTo(Screen.OVERVIEW)
                    } catch (_: NumberFormatException) {
                      println("There is an error")
                    }
                  }
                  if (parts.size != 3) {
                    Toast.makeText(
                            context, "Invalid format, date must be DD/MM/YYYY.", Toast.LENGTH_SHORT)
                        .show()
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
