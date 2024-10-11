package com.android.sample.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityRepositoryFirestore
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ListActivityViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    listActivityViewModel: ListActivityViewModel,
    activityRepositoryFirestore: ActivityRepositoryFirestore,
    navigationActions: NavigationActions,
) {
  val t_activity = listActivityViewModel.selectedActivity.collectAsState().value
  var title by remember { mutableStateOf(t_activity?.title) }
  var description by remember { mutableStateOf(t_activity?.description) }
  var creator by remember { mutableStateOf(t_activity?.creator) }
  var location by remember { mutableStateOf(t_activity?.location) }
  var price by remember { mutableStateOf(t_activity?.price.toString()) }
  var placesLeft by remember { mutableStateOf(t_activity?.placesLeft.toString()) }
  var dueDate by remember { mutableStateOf(t_activity?.date) }
  val context = LocalContext.current
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
            title = { Text("Edit the activity") },
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back")
              }
            })
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues).fillMaxSize().background(color = Color(0xFFFFFFFF)),
        ) {
          Carousel()
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = title ?: "",
              onValueChange = { title = it },
              label = { Text("Title") },
              modifier = Modifier.padding(8.dp).fillMaxWidth(),
              placeholder = { Text("Give a title of the activity") },
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = description ?: "",
              onValueChange = { description = it },
              label = { Text("Description") },
              modifier = Modifier.padding(8.dp).fillMaxWidth(),
              placeholder = { Text("Describe the activity") },
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = dueDate ?: "",
              onValueChange = { dueDate = it },
              label = { Text("Date") },
              modifier = Modifier.padding(8.dp).fillMaxWidth(),
              placeholder = { Text("dd/mm/yyyy") },
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = price,
              onValueChange = { price = it },
              label = { Text("Price") },
              modifier = Modifier.padding(8.dp).fillMaxWidth(),
              placeholder = { Text("Price/person") },
          )

          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = placesLeft,
              onValueChange = { placesLeft = it },
              label = { Text("Places Left") },
              modifier = Modifier.padding(8.dp).fillMaxWidth(),
              placeholder = { Text("Places left/Total places") },
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
              value = location ?: "",
              onValueChange = { location = it },
              label = { Text("Location") },
              modifier = Modifier.padding(8.dp).fillMaxWidth(),
              placeholder = { Text("Where is it taking place") },
          )
          Spacer(modifier = Modifier.height(32.dp))
          Button(
              onClick = {
                val activity =
                    Activity(
                        uid = t_activity?.uid ?: "",
                        title = title ?: "",
                        description = description ?: "",
                        date = dueDate ?: "",
                        price = price.toDouble(),
                        placesLeft = parseFraction(placesLeft, 0) ?: 0,
                        maxPlaces = parseFraction(placesLeft, 2) ?: 0,
                        creator = creator ?: "",
                        status = ActivityStatus.ACTIVE,
                        location = location ?: "",
                        images = listOf(),
                    )
                listActivityViewModel.updateActivity(activity)
                navigationActions.navigateTo(Screen.OVERVIEW)
              },
              modifier = Modifier.width(300.dp).height(40.dp).align(Alignment.CenterHorizontally),
          ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                  Icons.Default.Done,
                  contentDescription = "add a new activity",
              )
              Text("Delete", color = Color.Red)
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
                listActivityViewModel.deleteActivityById(t_activity?.uid ?: "")
                navigationActions.navigateTo(Screen.OVERVIEW)
              },
              modifier = Modifier.width(300.dp).height(40.dp).align(Alignment.CenterHorizontally),
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
              Text("Create")
            }
          }
        }
      },
  )
}
