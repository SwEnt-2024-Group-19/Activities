package com.android.sample.ui.listActivities

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.types
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun ListActivitiesScreen(
    viewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  var selectedFilter by remember { mutableStateOf("") }
  var selectedIndex by remember { mutableStateOf(0) }
  val all = "ALL"
  val typesToString = types.map { it.name }
  val options = listOf(all) + typesToString

  Scaffold(
      modifier = modifier.testTag("listActivitiesScreen"),
      topBar = {
        Box(
            modifier =
            Modifier
                .height(35.dp)
                .testTag("segmentedButtonRow")) { // Set the desired height here
              SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, label ->
                  SegmentedButton(
                      modifier = Modifier.testTag("segmentedButton$label"),
                      shape =
                          SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                      onClick = { selectedIndex = index },
                      selected = index == selectedIndex) {
                        Text(label)
                      }
                }
              }
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      }) { paddingValues ->
        Box(modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)) {
          when (uiState) {
            is ListActivitiesViewModel.ActivitiesUiState.Success -> {
              var activitiesList =
                  (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
              if (selectedIndex != 0) {

                activitiesList = activitiesList.filter { it.type.name == options[selectedIndex] }
              }
              if (activitiesList.isEmpty()) {
                if (selectedIndex == 0) {
                  Text(
                      text = "There is no activity yet.",
                      modifier =
                      Modifier
                          .padding(8.dp)
                          .align(Alignment.Center)
                          .testTag("emptyActivityPrompt"),
                      color = MaterialTheme.colorScheme.onSurface)
                } else {
                  Text(
                      text = "There is no activity of this type yet.",
                      modifier =
                      Modifier
                          .padding(8.dp)
                          .align(Alignment.Center)
                          .testTag("emptyActivityPrompt"),
                      color = MaterialTheme.colorScheme.onSurface)
                }
              } else {

                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {
                      // Use LazyColumn to efficiently display the list of activities

                      items(activitiesList) { activity ->
                        ActivityCard(activity = activity, navigationActions, viewModel)
                      }
                    }
              }
            }
            is ListActivitiesViewModel.ActivitiesUiState.Error -> {
              val error = (uiState as ListActivitiesViewModel.ActivitiesUiState.Error).exception
              Text(text = "Error: ${error.message}", modifier = Modifier.padding(8.dp))
            }
          }
        }
      }
}

@Composable
fun ActivityCard(
    activity: Activity,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel
) {
  val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
  val formattedDate = dateFormat.format(activity.date.toDate())

  Card(
      modifier =
      Modifier
          .fillMaxWidth()
          .testTag("activityCard")
          .clip(RoundedCornerShape(16.dp))
          .clickable {
              listActivitiesViewModel.selectActivity(activity)
              navigationActions.navigateTo(Screen.ACTIVITY_DETAILS)
          },
      elevation = CardDefaults.cardElevation(8.dp)) {
        Column {
          // Box for overlaying the title on the image
          Box(modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)) {
            // Display the activity image
            Image(
                painter = painterResource(R.drawable.foot),
                contentDescription = activity.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop)

            // Display the activity name on top of the image
            Text(
                text = activity.title,
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Title color set to black
                        ),
                modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .testTag("titleActivity"))
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Display the date
          Text(
              text = formattedDate,
              style =
                  MaterialTheme.typography.bodySmall.copy(
                      color = Color.Gray, // Light gray color for the date
                      fontStyle = FontStyle.Italic),
              modifier = Modifier.padding(horizontal = 16.dp))

          Row(
              modifier = Modifier
                  .padding(horizontal = 16.dp, vertical = 4.dp)
                  .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                // Location on the left
                Text(
                    text = activity.location?.name ?: "No location",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic, color = Color.Gray),
                    modifier = Modifier.weight(1f) // Takes up remaining space
                    )
                Text(
                    text = "${activity.placesLeft}/${activity.maxPlaces}",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold, color = Color.Gray, fontSize = 16.sp),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 16.dp))
              }

          Spacer(modifier = Modifier.height(4.dp))

          // Display the activity description
          Text(
              text = activity.description,
              style =
                  MaterialTheme.typography.bodyMedium.copy(color = Color.Black, lineHeight = 20.sp),
              modifier = Modifier.padding(horizontal = 16.dp))
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
}
