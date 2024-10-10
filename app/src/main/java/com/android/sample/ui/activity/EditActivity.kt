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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityRepositoryFirestore
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ListActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    lAV: ListActivityViewModel,
    aRF: ActivityRepositoryFirestore,
) {
  val t_activity = lAV.selectedActivity.collectAsState().value
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
            title = { Text("Create a new activity") },
            navigationIcon = {
              Icon(
                  Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "", // Add a valid content description
              )
            },
            modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F0F0)))
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
                        uid = aRF.getNewUid(),
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
                lAV.addActivity(activity)
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
              Text("Create")
            }
          }
        }
      },
  )
}

@Preview
@Composable
fun PreviewEditActivityScreen() {
  EditActivityScreen(
      lAV = ListActivityViewModel(ActivityRepositoryFirestore(Firebase.firestore)),
      aRF = ActivityRepositoryFirestore(Firebase.firestore))
}
