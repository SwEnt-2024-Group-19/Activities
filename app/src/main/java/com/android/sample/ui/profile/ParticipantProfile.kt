package com.android.sample.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun ParticipantProfileScreen(
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    imageViewModel: ImageViewModel,
    profileViewModel: ProfileViewModel,
    signInViewModel: SignInViewModel
) {

  val selectedParticipant = listActivitiesViewModel.selectedUser.collectAsState().value
  if (selectedParticipant != null) {
    ProfileScreen(
        selectedParticipant.id,
        profileViewModel,
        navigationActions,
        listActivitiesViewModel,
        imageViewModel,
         signInViewModel)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantLoadingScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("loadingScreen"),
      topBar = {
        TopAppBar(
            title = { Text(text = "Profile", modifier = Modifier.testTag("profileText")) },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        modifier = Modifier.testTag("goBackIcon"),
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
