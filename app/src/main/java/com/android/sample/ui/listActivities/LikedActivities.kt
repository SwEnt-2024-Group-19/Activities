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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.LARGE_IMAGE_SIZE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.PRIMARY_COLOR
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_MD
import com.android.sample.ui.camera.getImageResourceIdForCategory
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
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
  val context = LocalContext.current
  val networkManager = NetworkManager(context)

  Scaffold(
      modifier = modifier.testTag("likedActivitiesScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.LIKED_ACTIVITIES)
      }) { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
          val likedActivitiesList =
              if (networkManager.isNetworkAvailable()) {
                profile?.likedActivities
              } else {
                // Fetch cached profile from Room if offline
                remember { mutableStateOf(profileViewModel.loadCachedProfile()?.likedActivities) }
                    .value
              }
          when (uiState) {
            is ListActivitiesViewModel.ActivitiesUiState.Success -> {
              if (profile == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.Center).fillMaxWidth(WIDTH_FRACTION_MD)) {
                      Text(
                          text =
                              "You are not logged in. Login or Register to see your liked activities.",
                          modifier =
                              Modifier.padding(bottom = MEDIUM_PADDING.dp)
                                  .testTag("notConnectedPrompt"),
                          color = MaterialTheme.colorScheme.onSurface,
                          style = MaterialTheme.typography.bodyMedium,
                          textAlign = TextAlign.Center)
                      Card(
                          shape = RoundedCornerShape(MEDIUM_PADDING.dp),
                          modifier =
                              Modifier.padding(MEDIUM_PADDING.dp).testTag("DefaultImageCarousel")) {
                            Button(
                                onClick = { navigationActions.navigateTo(Screen.SIGN_UP) },
                                modifier = Modifier.testTag("signInButton"),
                                shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp)) {
                                  Text(
                                      "Go to Sign Up Page",
                                      style = MaterialTheme.typography.labelLarge)
                                }
                          }
                    }
              }
              if (likedActivitiesList != null) {
                if (likedActivitiesList!!.isEmpty()) {
                  Text(
                      text = "There is no liked activity yet.",
                      modifier =
                          Modifier.padding(STANDARD_PADDING.dp)
                              .align(Alignment.Center)
                              .testTag("emptyLikedActivityPrompt"),
                      color = MaterialTheme.colorScheme.onSurface)
                } else {

                  LazyColumn(
                      modifier =
                          Modifier.padding(paddingValues)
                              .fillMaxSize()
                              .padding(MEDIUM_PADDING.dp)
                              .padding(horizontal = SMALL_PADDING.dp),
                      verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING.dp)) {
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
              Text(
                  text = "Error: ${error.message}",
                  modifier = Modifier.padding(STANDARD_PADDING.dp))
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

  val activity = allActivities.find { act -> act.uid == activityId }
  if (activity == null) {
    profileViewModel.removeLikedActivity(profile!!.id, activityId)
    return
  }

  val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
  val formattedDate = dateFormat.format(activity.date.toDate())

  var isLiked by remember {
    mutableStateOf(profile?.likedActivities?.contains(activity.uid) ?: false)
  }

  Card(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("activityCard")
              .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
              .clickable {
                listActivitiesViewModel.selectActivity(activity)
                navigationActions.navigateTo(Screen.ACTIVITY_DETAILS)
              },
      elevation = CardDefaults.cardElevation(STANDARD_PADDING.dp)) {
        Column {
          // Box for overlaying the title on the image
          Box(modifier = Modifier.fillMaxWidth().height(LARGE_IMAGE_SIZE.dp)) {
            // Display the activity image
            Image(
                painter = painterResource(getImageResourceIdForCategory(activity.category)),
                contentDescription = activity.title,
                modifier = Modifier.fillMaxWidth().height(LARGE_IMAGE_SIZE.dp),
                contentScale = ContentScale.Crop)
              DarkGradient()

            // Display the activity name on top of the image
            Text(
                text = activity.title,
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Title color set to black
                        ),
                modifier = Modifier.align(Alignment.BottomStart).padding(MEDIUM_PADDING.dp))
          }

          Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
          Row(
              modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                // Display the date
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Calendar",
                    tint = Color(PRIMARY_COLOR))
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
                        tint = if (isLiked) Color(PRIMARY_COLOR) else Color.Gray)
                  }
                }
              }

          Row(
              modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                // Location on the left
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "location",
                    tint = Color(PRIMARY_COLOR))
                Text(
                    text = activity.location?.shortName ?: "No location",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic, color = Color.Gray),
                    modifier = Modifier.weight(1f) // Takes up remaining space
                    )
                Icon(
                    imageVector = Icons.Filled.Groups,
                    contentDescription = "Participants",
                    tint = Color(PRIMARY_COLOR))
                Spacer(modifier = Modifier.width(SMALL_PADDING.dp))

                Text(
                    text = "${activity.placesLeft}/${activity.maxPlaces}",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            fontSize = MEDIUM_PADDING.sp),
                    modifier =
                        Modifier.align(Alignment.CenterVertically).padding(end = MEDIUM_PADDING.dp))
              }

          Spacer(modifier = Modifier.height(SMALL_PADDING.dp))

          // Display the activity description
            Text(
                text = activity.description,
                style =
                MaterialTheme.typography.bodyMedium.copy(color = Color.Black, lineHeight = 20.sp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis, // add "..." when description is too long
                modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp))
          Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
        }
      }
}
