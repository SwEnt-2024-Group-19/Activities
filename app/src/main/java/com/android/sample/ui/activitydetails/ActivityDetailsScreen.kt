package com.android.sample.ui.activitydetails

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    listActivityViewModel: ListActivitiesViewModel =
        viewModel(factory = ListActivitiesViewModel.Factory),
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel
) {
  val activity = listActivityViewModel.selectedActivity.collectAsState().value
  val profile =
      profileViewModel.userState.collectAsState().value
          ?: return Text(text = "No profile selected. Should not happen", color = Color.Black)

  val activityTitle by remember { mutableStateOf(activity?.title) }
  val description by remember { mutableStateOf(activity?.description) }
  val price by remember { mutableStateOf(activity?.price) }
  val dueDate by remember {
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
  val placesTaken by remember { mutableStateOf(activity?.placesTaken) }
  val maxPlaces by remember { mutableStateOf(activity?.maxPlaces) }

  val context = LocalContext.current

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Box(
                  modifier = Modifier.fillMaxWidth().testTag("topAppBar"),
                  contentAlignment = Alignment.Center) {
                    Text("Title", color = Color.White)
                  }
            },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("goBackButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back")
                  }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA), // Background color
                    titleContentColor = Color.White // Title text color
                    ))
      }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
          // Image section
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .aspectRatio(16 / 9f)
                      .padding(16.dp)
                      .background(Color.Gray, shape = RoundedCornerShape(8.dp))) {
                // Optional: Add placeholder text in the center
                Text(
                    text = "Activity Image",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center))
              }

          Spacer(modifier = Modifier.height(16.dp))

          // Title
          Box(
              modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
              contentAlignment = Alignment.Center) {
                Text(
                    text = activityTitle ?: "title not specified",
                    style =
                        MaterialTheme.typography.headlineMedium, // Change this to a larger style
                    modifier = Modifier.testTag("Title"))
              }

          Spacer(modifier = Modifier.height(8.dp))

          // Description
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(150.dp)
                      .padding(8.dp)
                      .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                      .verticalScroll(rememberScrollState())
                      .testTag("description")) {
                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                Text(
                    text = description ?: "description not specified",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp))
              }

          Spacer(modifier = Modifier.height(16.dp))

          //  price and schedule
          Spacer(modifier = Modifier.height(8.dp))
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("priceIcon")) {
                      Icon(Icons.Filled.AttachMoney, contentDescription = "Price")
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                          text =
                              if (price != null) "${price.toString()} CHF" else "not defined yet")
                    }
              }
          Spacer(modifier = Modifier.height(8.dp))
          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.testTag("schedule")) {
                Icon(Icons.Default.DateRange, contentDescription = "Schedule")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = dueDate ?: "not defined yet")
              }

          Spacer(modifier = Modifier.height(32.dp))

          // Enroll button
          Button(
              onClick = {
                if (((placesTaken ?: 0) >= 0) && ((placesTaken ?: 0) < (maxPlaces ?: 0))) {

                  val theActivity =
                      activity?.let { activity ->
                        Activity(
                            uid = activity.uid,
                            title = activity.title,
                            description = activity.description,
                            date = activity.date,
                            price = activity.price,
                            placesTaken = min((placesTaken ?: 0) + 1, maxPlaces ?: 0),
                            maxPlaces = activity.maxPlaces,
                            creator = activity.creator,
                            status = activity.status,
                            location = activity.location,
                            images = activity.images,
                            participants = activity.participants)

                      }
                  if (theActivity != null) {
                    listActivityViewModel.updateActivity(theActivity)
                    profileViewModel.addActivity(profile.id, theActivity.uid)
                  }
                  Toast.makeText(context, "Enroll Successful", Toast.LENGTH_SHORT).show()
                  navigationActions.navigateTo(Screen.OVERVIEW)
                } else {
                  Toast.makeText(
                          context, "Enroll failed, limit of places reached", Toast.LENGTH_SHORT)
                      .show()
                }
              },
              modifier =
                  Modifier.fillMaxWidth().padding(horizontal = 24.dp).testTag("enrollButton")) {
                Text(text = "Enroll")
              }
        }
      }
}

