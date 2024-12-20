package com.android.sample.ui.listActivities

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_MD
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun LikedActivitiesScreen(
    viewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
    imageViewModel: ImageViewModel
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
                if (likedActivitiesList.isEmpty()) {
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
                        items(likedActivitiesList) { activityId ->
                          val activity = allActivities.find { act -> act.uid == activityId }
                          if (activity == null) {
                            profileViewModel.removeLikedActivity(profile!!.id, activityId)
                            return@items
                          }
                          ActivityCard(
                              activity = activity,
                              navigationActions,
                              viewModel,
                              profileViewModel,
                              profile,
                              null,
                              imageViewModel = imageViewModel)
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
