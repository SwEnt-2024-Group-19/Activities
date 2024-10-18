package com.android.sample.ui.activity

import android.icu.util.GregorianCalendar
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.dialogs.AddUserDialog
import com.android.sample.ui.dialogs.SimpleUser
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
    profileViewModel: ProfileViewModel
) {

  var expanded by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf("Select a type") }
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  val creator = FirebaseAuth.getInstance().currentUser?.uid ?: ""
  var location by remember { mutableStateOf("") }
  var price by remember { mutableStateOf("") }
  var placesLeft by remember { mutableStateOf("") }
  var dueDate by remember { mutableStateOf("") }

  var startTime by remember { mutableStateOf("") }
  var duration by remember { mutableStateOf("") }
  var carouselItems by remember { mutableStateOf(items) }
  var showDialog by remember { mutableStateOf(false) }

  // Add scroll
  val scrollState = rememberScrollState()

  val attendees_: List<SimpleUser> = listOf<SimpleUser>()
  var attendees: List<SimpleUser> by remember { mutableStateOf(attendees_) }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("createActivityScreen"),
      topBar = {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.title_screen_create_activity)) },
        )
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(color = Color(0xFFFFFFFF)),
        ) {
          // Carousel()
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = title,
              onValueChange = { title = it },
              label = { Text("Title") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputTitleCreate"),
              placeholder = { Text(text = stringResource(id = R.string.request_activity_title)) },
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              label = { Text("Description") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputDescriptionCreate"),
              placeholder = {
                Text(text = stringResource(id = R.string.request_activity_description))
              },
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = dueDate,
              onValueChange = { dueDate = it },
              label = { Text("Date") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputDateCreate"),
              placeholder = {
                Text(text = stringResource(id = R.string.request_date_activity_withFormat))
              },
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = startTime,
              onValueChange = { startTime = it },
              label = { Text("Time") },
              modifier = Modifier.padding(8.dp).fillMaxWidth(),
              placeholder = { Text(text = stringResource(id = R.string.hour_min_format)) },
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = duration,
              onValueChange = { duration = it },
              label = { Text("Duration") },
              modifier = Modifier.padding(8.dp).fillMaxWidth(),
              placeholder = { Text(text = stringResource(id = R.string.hour_min_format)) },
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = price,
              onValueChange = { price = it },
              label = { Text("Price") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputPriceCreate"),
              placeholder = { Text(text = stringResource(id = R.string.request_price_activity)) },
          )

          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = placesLeft,
              onValueChange = { placesLeft = it },
              label = { Text("Places Left") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputPlacesCreate"),
              placeholder = {
                Text(text = stringResource(id = R.string.request_placesLeft_activity))
              },
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = location,
              onValueChange = { location = it },
              label = { Text("Location") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputLocationCreate"),
              placeholder = {
                Text(text = stringResource(id = R.string.request_location_activity))
              },
          )
          Spacer(modifier = Modifier.height(8.dp))

          ExposedDropdownMenuBox(
              modifier = Modifier.testTag("chooseTypeMenu"),
              expanded = expanded,
              onExpandedChange = { expanded = !expanded }) {
                TextField(
                    readOnly = true,
                    value = selectedOption,
                    onValueChange = {},
                    label = { Text("Activity Type") },
                    trailingIcon = {
                      ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor())
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                  types.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.name) },
                        onClick = {
                          selectedOption = selectionOption.name
                          expanded = false
                        })
                  }
                }
              }

          Spacer(modifier = Modifier.height(32.dp))

          Column(
              modifier = Modifier.fillMaxWidth().padding(8.dp).height(130.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.width(300.dp).height(40.dp).testTag("addAttendeeButton"),
                ) {
                  Row(
                      horizontalArrangement =
                          Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                      verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "add a new attendee",
                    )
                    Text("Add Attendee")
                  }
                }

                LazyRow(
                    modifier = Modifier.fillMaxHeight().height(85.dp).padding(8.dp),
                ) {
                  items(attendees.size) { index ->
                    Row(
                        modifier =
                            Modifier.padding(8.dp)
                                .background(Color(0xFFFFFFFF))
                                .testTag("attendeeRow${index}"),
                    ) {
                      Text(
                          text = attendees[index].name,
                          modifier = Modifier.padding(8.dp).testTag("attendeeName${index}"),
                          style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                          text = attendees[index].surname,
                          modifier = Modifier.padding(8.dp).testTag("attendeeSurname${index}"),
                          style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                          text = attendees[index].age.toString(),
                          modifier = Modifier.padding(8.dp).testTag("attendeeAge${index}"),
                          style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                      )
                      Button(
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
          if (showDialog) {
            AddUserDialog(
                onDismiss = { showDialog = false },
                onAddUser = { user -> attendees = attendees + user },
                modifier = Modifier.testTag("addUserDialog"))
          }

          Spacer(modifier = Modifier.height(32.dp))
          Button(
              enabled = title.isNotEmpty() && description.isNotEmpty() && dueDate.isNotEmpty(),
              onClick = {
                val calendar = GregorianCalendar()
                val parts = dueDate.split("/")
                if (parts.size == 3) {
                  try {
                    calendar.set(
                        parts[2].toInt(),
                        parts[1].toInt() - 1, // Months are 0-based
                        parts[0].toInt(),
                        0,
                        0,
                        0)
                    val activity =
                        Activity(
                            uid = listActivityViewModel.getNewUid(),
                            title = title,
                            description = description,
                            date = Timestamp(calendar.time),
                            startTime = startTime,
                            duration = duration,
                            price = price.toDouble(),
                            placesLeft = parseFraction(placesLeft, 0)?.toLong() ?: 0.toLong(),
                            maxPlaces = parseFraction(placesLeft, 1)?.toLong() ?: 0.toLong(),
                            creator = creator,
                            status = ActivityStatus.ACTIVE,
                            location = location,
                            images = carouselItems.map { it },
                            participants = attendees,
                            type = types.find { it.name == selectedOption } ?: types[0])
                    listActivityViewModel.addActivity(activity)
                    navigationActions.navigateTo(Screen.OVERVIEW)
                  } catch (_: NumberFormatException) {}
                }
              },
              modifier =
                  Modifier.width(300.dp)
                      .height(40.dp)
                      .testTag("createButton")
                      .align(Alignment.CenterHorizontally),
          ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                  Icons.Filled.Add,
                  contentDescription = "add a new activity",
              )
              Text(text = stringResource(id = R.string.button_create_activity))
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.ADD_ACTIVITY)
      })
}

var items = listOf<String>()

// @Composable
// fun Carousel() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(135.dp)
//            .padding(8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        LazyRow(
//            modifier = Modifier
//                .width(340.dp)
//                .height(135.dp),
//        ) {
//            items(items.size) { index ->
//                AsyncImage(
//                    model = items[index], // Utilise l'URL de l'image
//                    contentDescription = "image $index",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.padding(8.dp)
//                )
//            }
//        }
//        Spacer(modifier = Modifier.width(16.dp))
//        Column(
//            modifier =
//            Modifier
//                .padding(16.dp)
//                .size(30.dp)
//                .background(Color(0xFFFFFFFF)), // Use size modifier for simplicity
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.End // Center the icon horizontally
//        ) {
//            FloatingActionButton(
//                content = {
//                    Icon(
//                        imageVector = Icons.Outlined.AddCircle,
//                        contentDescription = "Add a new image"
//                    )
//                },
//                containerColor = Color(0xFFFFFFFF),
//                onClick = { /*TODO*/ },
//                modifier = Modifier
//                    .size(50.dp)
//                    .background(Color(0xFFFFFFFF)),
//            )
//
//            Text(
//                text = "Add Image",
//                modifier = Modifier.padding(8.dp),
//                color = Color(0xFF000000),
//                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
//            )
//        }
//    }
// }

fun parseFraction(fraction: String, index: Int): Int? {
  val parts = fraction.split("/")
  return parts[index].toIntOrNull()
}
