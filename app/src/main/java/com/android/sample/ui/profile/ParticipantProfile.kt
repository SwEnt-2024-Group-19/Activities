package com.android.sample.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun ParticipantProfileScreen(
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    imageViewModel: ImageViewModel
) {

  val selectedParticipant = listActivitiesViewModel.selectedUser.collectAsState().value
  val profile = selectedParticipant
  when (profile) {
    null ->
        ParticipantLoadingScreen(navigationActions) // Show a loading indicator or a retry button
    else -> {
      ParticipantProfileContent(
          user = profile, navigationActions, listActivitiesViewModel, imageViewModel)
    }
  // Proceed with showing profile content
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantLoadingScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("loadingScreen"),
      topBar = {
        TopAppBar(
            title = { Text("Profile") },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back")
                  }
            })
      }) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  "No information available for this participant",
                  modifier = Modifier.testTag("loadingText"),
                  color = Color.Black)
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantProfileContent(
    user: User,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel,
    imageViewModel: ImageViewModel
) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("profileScreen"),
      topBar = {
        TopAppBar(
            title = { Text("Participant's Profile") },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back")
                  }
            },
            modifier = Modifier.testTag("topAppBar"))
      }) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding).testTag("ParticipantProfileContentColumn"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              item { ProfileHeader(user, imageViewModel) }
              item {
                // Interests Section
                SectionTitle(title = "Interests", testTag = "interestsSection")
              }
              item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp),
                    contentPadding = PaddingValues(horizontal = MEDIUM_PADDING.dp)) {
                      user.interests?.let { interests ->
                        items(interests.size) { index ->
                          InterestBox(interest = user.interests[index].interest)
                        }
                      }
                    }
              }

              // Display activities sections
              this@LazyColumn.displayActivitySection(
                  "Activities Created", "created", user, listActivitiesViewModel, navigationActions)
              this@LazyColumn.displayActivitySection(
                  "Activities Enrolled in",
                  "enrolled",
                  user,
                  listActivitiesViewModel,
                  navigationActions)
              this@LazyColumn.displayActivitySection(
                  "Past Activities", "past", user, listActivitiesViewModel, navigationActions)
            }
      }
}