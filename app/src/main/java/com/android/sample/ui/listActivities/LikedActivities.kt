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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun LikedActivitiesScreen(
    viewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val profile = profileViewModel.userState.collectAsState().value
  val allActivities = (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities

  Scaffold(
      modifier = modifier.testTag("likedActivitiesScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      }) { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
          val likedActivitiesList by remember { mutableStateOf(profile?.likedActivities) }
          when (uiState) {
            is ListActivitiesViewModel.ActivitiesUiState.Success -> {
              if (profile == null) {
                Text(
                    text = "You are not logged in, Login or Register to see your liked activities",
                    modifier =
                        Modifier.padding(8.dp)
                            .align(Alignment.Center)
                            .testTag("notConnectedPrompt"),
                    color = MaterialTheme.colorScheme.onSurface)
                Button(
                    onClick = { navigationActions.navigateTo(Screen.SIGN_UP) },
                    modifier = Modifier.testTag("signInButton")) {
                      Text("Go to Sign In Page")
                    }
              }
              if (likedActivitiesList != null) {
                if (likedActivitiesList!!.isEmpty()) {
                  Text(
                      text = "There is no liked activity yet.",
                      modifier =
                          Modifier.padding(8.dp)
                              .align(Alignment.Center)
                              .testTag("emptyLikedActivityPrompt"),
                      color = MaterialTheme.colorScheme.onSurface)
                } else {

                  LazyColumn(
                      modifier =
                          Modifier.padding(paddingValues)
                              .fillMaxSize()
                              .padding(16.dp)
                              .padding(horizontal = 5.dp),
                      verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(likedActivitiesList!!) { activityId ->
                          ActivityCard2(
                              activityId = activityId,
                              navigationActions,
                              viewModel,
                              profileViewModel,
                              profile,
                              allActivities)
                        }
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
fun ActivityCard2(
    activityId: String,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel,
    profileViewModel: ProfileViewModel,
    profile: User?,
    allActivities: List<Activity>,
) {

  val activity = allActivities.filter { act -> act.uid == activityId }[0]

  val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
  val formattedDate = dateFormat.format(activity.date.toDate())

  var isLiked by remember {
    mutableStateOf(profile?.likedActivities?.contains(activity.uid) ?: false)
  }

  Card(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("activityCard")
              .clip(RoundedCornerShape(16.dp))
              .clickable {
                listActivitiesViewModel.selectActivity(activity)
                navigationActions.navigateTo(Screen.ACTIVITY_DETAILS)
              },
      elevation = CardDefaults.cardElevation(8.dp)) {
        Column {
          // Box for overlaying the title on the image
          Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            // Display the activity image
            Image(
                painter = painterResource(R.drawable.foot),
                contentDescription = activity.title,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop)

            // Display the activity name on top of the image
            Text(
                text = activity.title,
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Title color set to black
                        ),
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp))
          }

          Spacer(modifier = Modifier.height(8.dp))
          Row(
              modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                // Display the date
                Text(
                    text = formattedDate,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray, // Light gray color for the date
                            fontStyle = FontStyle.Italic),
                    modifier = Modifier.weight(1f) // Takes up remaining space
                    )

                if (profile != null) {
                  IconButton(
                      modifier = Modifier.testTag("favoriteIcon$isLiked"),
                      onClick = {
                        isLiked = !isLiked
                        if (isLiked) {
                          profileViewModel.addLikedActivity(profile.id, activity.uid)
                        } else {
                          profileViewModel.removeLikedActivity(profile.id, activity.uid)
                        }
                      },
                  ) {
                    Icon(
                        imageVector =
                            if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isLiked) "Liked" else "Not Liked",
                        tint = if (isLiked) Color.Black else Color.Gray)
                  }
                }
              }

          Row(
              modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
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
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 16.dp))
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