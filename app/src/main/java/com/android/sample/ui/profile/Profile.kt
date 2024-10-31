package com.android.sample.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.sample.R
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun ProfileScreen(
    userProfileViewModel: ProfileViewModel,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel
) {

  val profileState = userProfileViewModel.userState.collectAsState()

  when (val profile = profileState.value) {
    null -> LoadingScreen(navigationActions) // Show a loading indicator or a retry button
    else ->
        ProfileContent(
            user = profile,
            navigationActions,
            listActivitiesViewModel) // Proceed with showing profile content
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("loadingScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
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
                  "You do not have a profile",
                  modifier = Modifier.testTag("loadingText"),
                  color = Color.Black)
              Button(
                  onClick = { navigationActions.navigateTo(Screen.SIGN_UP) },
                  modifier = Modifier.testTag("signInButton")) {
                    Text("Go to Sign In Page")
                  }
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    user: User,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel
) {

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("profileScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
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
      },
      floatingActionButton = {
        FloatingActionButton(onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) }) {
          Icon(Icons.Filled.ModeEdit, contentDescription = "Edit Profile")
        }
      }) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("profileContent"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
              item {
                Spacer(Modifier.height(16.dp))
                Text(text = "Profile", fontSize = 30.sp, modifier = Modifier.padding(top = 16.dp))

                // Profile Picture
                ProfileImage(
                    url = user.photo,
                    modifier = Modifier.size(100.dp).clip(CircleShape).testTag("profilePicture"))

                // User Name and Surname
                Text(
                    text = "${user.name} ${user.surname}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp).testTag("userName"))

                // Interests
                Text(
                    text = "Interests: ${user.interests?.joinToString(", ")}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp).testTag("interestsSection"))
              }

              item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Activities Created",
                    fontSize = 24.sp,
                    modifier = Modifier.testTag("activitiesCreatedTitle"))
              }

              user.activities?.let {
                items(it.size) { index ->
                  ActivityCreatedBox(
                      activity = user.activities[index],
                      user = user,
                      listActivitiesViewModel = listActivitiesViewModel,
                      navigationActions = navigationActions)
                }
              }

              item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Activities Enrolled in",
                    fontSize = 24.sp,
                    modifier = Modifier.testTag("activitiesEnrolledTitle"))
              }

              user.activities?.let {
                items(it.size) { index ->
                  ActivityEnrolledBox(
                      activity = user.activities[index],
                      user = user,
                      listActivitiesViewModel = listActivitiesViewModel,
                      navigationActions = navigationActions)
                }
              }
            }
      }
}

@Composable
fun ActivityCreatedBox(
    activity: String,
    user: User,
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions
) {
  val uiState by listActivitiesViewModel.uiState.collectAsState()
  val activitiesList = (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
  val thisActivity = activitiesList.find { it.uid == activity }

  if (thisActivity != null) {
    if (thisActivity.creator == user.id) {
      Row(
          modifier =
              Modifier.fillMaxWidth()
                  .testTag("activityCreated")
                  .padding(8.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .clickable {
                    listActivitiesViewModel.selectActivity(thisActivity)
                    navigationActions.navigateTo(Screen.EDIT_ACTIVITY)
                  },
          verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.foot),
                contentDescription = "Activity Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).padding(end = 16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                  text = thisActivity.title,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black)
              Text(text = thisActivity.description, fontSize = 14.sp, color = Color.Gray)
            }
          }
    }
  }
}

@Composable
fun ActivityEnrolledBox(
    activity: String,
    user: User,
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions
) {
  val uiState by listActivitiesViewModel.uiState.collectAsState()
  val activitiesList = (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
  val thisActivity = activitiesList.find { it.uid == activity }

  if (thisActivity != null) {
    if (thisActivity.creator != user.id) {
      Row(
          modifier =
              Modifier.fillMaxWidth()
                  .testTag("activityEnrolled")
                  .padding(8.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .clickable { navigationActions.navigateTo(Screen.ACTIVITY_DETAILS) },
          verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.foot),
                contentDescription = "Activity Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).padding(end = 16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                  text = thisActivity.title,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black)
              Text(text = thisActivity.description, fontSize = 14.sp, color = Color.Gray)
            }
          }
    }
  }
}

@Composable
fun ProfileImage(url: String?, modifier: Modifier = Modifier) {
  val painter =
      rememberAsyncImagePainter(
          ImageRequest.Builder(LocalContext.current)
              .data(
                  data = url // URL of the image
                  )
              .apply(
                  block =
                      fun ImageRequest.Builder.() {
                        crossfade(true)
                      })
              .build())

  Image(
      painter = painter,
      contentDescription = "Profile Image",
      modifier = modifier,
      contentScale = ContentScale.Crop)
}
