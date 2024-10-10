package com.android.sample.ui.activitydetails

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    listToDosViewModel: ListActivitiesViewModel = viewModel(factory = ListActivitiesViewModel.Factory),
    navigationActions: NavigationActions
) {
    val activity = listToDosViewModel.selectedActivity.collectAsState().value
  var activityTitle by remember { mutableStateOf(activity?.title) }
  var description by remember { mutableStateOf(activity?.description) }
  var price by remember { mutableStateOf(activity?.price) }
  var schedule by remember { mutableStateOf(activity?.date) }

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
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .aspectRatio(16 / 9f)
                      .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                      .clip(RoundedCornerShape(8.dp))) {
                // Placeholder for the image
                Text(
                    text = "Activity Image",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White)
              }

          Spacer(modifier = Modifier.height(16.dp))

          // Title and description
          Text(text = activityTitle?:"", style = MaterialTheme.typography.titleMedium)
          Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Description:",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description?:"",
                style = MaterialTheme.typography.bodyMedium
            )

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
            Text(text = schedule?:"not defined yet")
          }

          Spacer(modifier = Modifier.height(32.dp))

          // Enroll button
          Button(onClick = {}, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text(text = "Enroll")
          }
        }
      }
}
