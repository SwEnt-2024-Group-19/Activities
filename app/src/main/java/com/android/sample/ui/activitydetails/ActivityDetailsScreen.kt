package com.android.sample.ui.activitydetails

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityRepositoryFirestore
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.ListActivityViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    listActivityViewModel: ListActivityViewModel =
        viewModel(factory = ListActivitiesViewModel.Factory),
    activityRepositoryFirestore: ActivityRepositoryFirestore,
    navigationActions: NavigationActions
) {
  val activity = listActivityViewModel.selectedActivity.collectAsState().value
  var activityTitle by remember { mutableStateOf(activity?.title) }
  var description by remember { mutableStateOf(activity?.description) }
  var price by remember { mutableStateOf(activity?.price) }
  var schedule by remember { mutableStateOf(activity?.date) }
  var location by remember { mutableStateOf(activity?.location) }
  var placesLeft by remember { mutableStateOf(activity?.placesLeft) }
  var maxPlaces by remember { mutableStateOf(activity?.maxPlaces) }
  var creator by remember { mutableStateOf(activity?.creator) }
  var status by remember { mutableStateOf(activity?.status) }

  val context = LocalContext.current

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Title", modifier = Modifier.testTag("editTodoTitle")) },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("goBackButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back")
                  }
            })
      }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
          // Image section
          Imagery()

          Spacer(modifier = Modifier.height(16.dp))

          // Title and description
          Text(text = activityTitle ?: "", style = MaterialTheme.typography.titleMedium)
          Spacer(modifier = Modifier.height(8.dp))

          Text(
              text = "Description:",
              style = MaterialTheme.typography.bodyMedium,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = description ?: "", style = MaterialTheme.typography.bodyMedium)

          Spacer(modifier = Modifier.height(16.dp))

          //  price and schedule
          Spacer(modifier = Modifier.height(8.dp))
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Filled.AttachMoney, contentDescription = "Price")
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = if (price != null) "${price.toString()} CHF" else "not defined yet")
                }
              }
          Spacer(modifier = Modifier.height(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, contentDescription = "Schedule")
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = schedule ?: "not defined yet")
          }

          Spacer(modifier = Modifier.height(32.dp))

          // Enroll button
          Button(
              onClick = {
                if ((placesLeft ?: 0) > 0) {
                  val theActivity =
                      Activity(
                          uid = activityRepositoryFirestore.getNewUid(),
                          title = activityTitle ?: "",
                          description = description ?: "",
                          date = schedule ?: "",
                          price = price ?: 0.0,
                          placesLeft = (placesLeft ?: 0).let { if (it > 0) it - 1 else 0 },
                          maxPlaces = maxPlaces ?: 0,
                          creator = creator ?: "",
                          status = status ?: ActivityStatus.ACTIVE,
                          location = location ?: "",
                          images = listOf(),
                      )
                  listActivityViewModel.addActivity(theActivity)
                  Toast.makeText(context, "Enroll Successful", Toast.LENGTH_SHORT).show()
                  navigationActions.navigateTo(Screen.OVERVIEW)
                } else {
                  Toast.makeText(
                          context, "Enroll failed, limit of places reached", Toast.LENGTH_SHORT)
                      .show()
                }
              },
              modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text(text = "Enroll")
              }
        }
      }
}

@Composable
fun Imagery() {
  LazyRow(
      modifier = Modifier.fillMaxWidth().height(200.dp).padding(3.dp),
  ) {
    items(com.android.sample.ui.activity.items.size) { index ->
      Image(
          painter = painterResource(id = com.android.sample.ui.activity.items[index].imageResId),
          contentDescription = com.android.sample.ui.activity.items[index].contentDescription,
          contentScale = ContentScale.Crop,
          modifier = Modifier.padding(8.dp))
    }
  }
}