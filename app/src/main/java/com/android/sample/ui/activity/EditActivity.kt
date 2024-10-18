package com.android.sample.ui.activity

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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.types
import com.android.sample.ui.dialogs.AddUserDialog
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    listActivityViewModel: ListActivitiesViewModel =
        viewModel(factory = ListActivitiesViewModel.Factory),
    navigationActions: NavigationActions,
) {
  var showDialog by remember { mutableStateOf(false) }
  val activity = listActivityViewModel.selectedActivity.collectAsState().value
  var title by remember { mutableStateOf(activity?.title ?: "") }
  var description by remember { mutableStateOf(activity?.description ?: "") }
  val creator by remember { mutableStateOf(activity?.creator ?: "") }
  var location by remember { mutableStateOf(activity?.location ?: "") }
  var price by remember { mutableStateOf(activity?.price.toString() ?: "") }
  var placesLeft by remember { mutableStateOf(activity?.placesLeft.toString() ?: "") }
  var attendees by remember { mutableStateOf(activity?.participants ?: listOf()) }
  var startTime by remember { mutableStateOf(activity?.startTime) }
  var duration by remember { mutableStateOf(activity?.duration) }
  var expanded by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf(activity?.type.toString()) }

  var dueDate by remember {
    mutableStateOf(
        activity?.date.let {
          val calendar = GregorianCalendar()
          if (activity != null) {
            calendar.time = activity.date.toDate()
          }
          return@let "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                    calendar.get(
                        Calendar.YEAR
                    )
                }"
        })
  }
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
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .fillMaxSize()
                    .background(color = Color(0xFFFFFFFF))
                    .verticalScroll(rememberScrollState()),
        ) {
          // Carousel()
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = title ?: "",
              onValueChange = { title = it },
              label = { Text("Title") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputTitleEdit"),
              placeholder = { Text(text = stringResource(id = R.string.request_activity_title)) },
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = description ?: "",
              onValueChange = { description = it },
              label = { Text("Description") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputDescriptionEdit"),
              placeholder = {
                Text(text = stringResource(id = R.string.request_activity_description))
              },
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = dueDate,
              onValueChange = { dueDate = it },
              label = { Text("Date") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputDateEdit"),
              placeholder = {
                Text(text = stringResource(id = R.string.request_date_activity_withFormat))
              },
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = startTime ?: "",
              onValueChange = { startTime = it },
              label = { Text("Time") },
              modifier = Modifier.padding(8.dp).fillMaxWidth(),
              placeholder = { Text(text = stringResource(id = R.string.hour_min_format)) },
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = duration ?: "",
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
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputPriceEdit"),
              placeholder = { Text(text = stringResource(id = R.string.request_price_activity)) },
          )

          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = placesLeft,
              onValueChange = { placesLeft = it },
              label = { Text("Places Left") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputPlacesLeftEdit"),
              placeholder = {
                Text(text = stringResource(id = R.string.request_placesLeft_activity))
              },
          )
          Spacer(modifier = Modifier.height(8.dp))

          ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                readOnly = true,
                value = selectedOption,
                onValueChange = {},
                label = { Text("Activity Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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

          OutlinedTextField(
              value = location ?: "",
              onValueChange = { location = it },
              label = { Text("Location") },
              modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("inputLocationEdit"),
              placeholder = {
                Text(text = stringResource(id = R.string.request_location_activity))
              },
          )
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
            )
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
                        parts[1].toInt() - 1, // Months are 0-based indexed
                        parts[0].toInt(),
                        0,
                        0,
                        0)

                    val updatedActivity =
                        Activity(
                            uid = activity?.uid ?: "",
                            title = title ?: "",
                            description = description ?: "",
                            date = Timestamp(calendar.time),
                            startTime = startTime ?: "",
                            duration = duration ?: "",
                            price = price.toDouble(),
                            placesLeft = parseFraction(placesLeft, 0)?.toLong() ?: 0.toLong(),
                            maxPlaces = parseFraction(placesLeft, 2)?.toLong() ?: 0.toLong(),
                            creator = creator ?: "",
                            status = ActivityStatus.ACTIVE,
                            location = location ?: "",
                            images = listOf(),
                            type = types.find { it.name == selectedOption } ?: types[0],
                            participants = attendees)

                    listActivityViewModel.updateActivity(updatedActivity)
                    navigationActions.navigateTo(Screen.OVERVIEW)
                  } catch (_: Exception) {}
                }
              },
              modifier =
                  Modifier.width(300.dp)
                      .height(40.dp)
                      .align(Alignment.CenterHorizontally)
                      .testTag("editButton"),
          ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                  Icons.Default.Done,
                  contentDescription = "add a new activity",
              )

              Text("Save", color = Color.White)
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
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
                  Modifier.width(300.dp)
                      .height(40.dp)
                      .align(Alignment.CenterHorizontally)
                      .testTag("deleteButton"),
          ) {
            Row(
                Modifier.background(Color.Transparent),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
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

// @Preview
// @Composable
// fun EditActivityScreenPreview() {
//  val navController = rememberNavController()
//  val navigationActions = NavigationActions(navController)
//  val lAV = ListActivitiesViewModel(ActivitiesRepositoryFirestore(Firebase.firestore))
//  lAV.selectActivity(
//      Activity(
//          uid = "1",
//          title = "Activity",
//          description = "Description",
//          date = Timestamp.now(),
//          price = 0.0,
//          placesLeft = 0,
//          maxPlaces = 0,
//          creator = "Creator",
//          status = ActivityStatus.ACTIVE,
//          location = "Location",
//          images = listOf(),
//          participants = listOf()))
//  EditActivityScreen(navigationActions = navigationActions)
// }
