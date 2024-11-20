package com.android.sample.ui.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TEXT_FONTSIZE
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TOP_TITLE_SIZE
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ProfileScreen(
    userProfileViewModel: ProfileViewModel,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel
) {

  val profileState = userProfileViewModel.userState.collectAsState()
  when (val profile = profileState.value) {
    null -> LoadingScreen(navigationActions) // Show a loading indicator or a retry button
    else -> {
      ProfileContent(
          user = profile, navigationActions, listActivitiesViewModel, userProfileViewModel)
    }
  // Proceed with showing profile content
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
    listActivitiesViewModel: ListActivitiesViewModel,
    userProfileViewModel: ProfileViewModel
) {
  var showMenu by remember { mutableStateOf(false) } // To control the visibility of the menu
  Log.d("ProfileScreen", "User photo: ${user.photo}")
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
            },
            actions = {
              IconButton(
                  onClick = { showMenu = true }, modifier = Modifier.testTag("moreOptionsButton")) {
                    Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = "More options")
                  }

              // DropdownMenu for options
              DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                      showMenu = false
                      userProfileViewModel.clearUserData()
                      Firebase.auth.signOut()
                      navigationActions.navigateTo(Screen.AUTH)
                      // Handle logout action
                    },
                    enabled = Firebase.auth.currentUser?.isAnonymous == false)
              }
            })
      },
      floatingActionButton = {
        FloatingActionButton(onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) }) {
          Icon(Icons.Filled.ModeEdit, contentDescription = "Edit Profile")
        }
      }) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {
              item {
                Spacer(Modifier.height(MEDIUM_PADDING.dp))
                Text(
                    text = "Profile",
                    fontSize = TOP_TITLE_SIZE.sp,
                    modifier = Modifier.padding(top = MEDIUM_PADDING.dp))

                // Profile Picture
                ProfileImage(
                    userId = user.id,
                    modifier =
                        Modifier.size(IMAGE_SIZE.dp).clip(CircleShape).testTag("profilePicture"))

                // User Name and Surname
                Text(
                    text = "${user.name} ${user.surname}",
                    fontSize = TITLE_FONTSIZE.sp,
                    modifier = Modifier.padding(top = STANDARD_PADDING.dp).testTag("userName"))
              }
              item {
                // Interests Section
                Text(
                    text = "Interests",
                    fontSize = TITLE_FONTSIZE.sp,
                    modifier =
                        Modifier.padding(start = MEDIUM_PADDING.dp, top = MEDIUM_PADDING.dp)
                            .testTag("interestsSection"))
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

              item {

                // Activities Section

                Text(
                    text = "Activities Created",
                    fontSize = TITLE_FONTSIZE.sp,
                    modifier =
                        Modifier.padding(start = MEDIUM_PADDING.dp, top = MEDIUM_PADDING.dp)
                            .testTag("activitiesCreatedTitle"))
              }

              // Activities Created

              user.activities?.let { activities ->
                items(activities.size) { index ->
                  ActivityCreatedBox(
                      activity = activities[index],
                      user,
                      listActivitiesViewModel,
                      navigationActions)
                }
              }

              item { // Activities Enrolled in
                Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))

                Text(
                    text = "Activities Enrolled in",
                    fontSize = TITLE_FONTSIZE.sp,
                    modifier =
                        Modifier.padding(start = MEDIUM_PADDING.dp, top = MEDIUM_PADDING.dp)
                            .testTag("activitiesEnrolledTitle"))
              }

              // Activities Enrolled

              user.activities?.let { activities ->
                items(activities.size) { index ->
                  ActivityEnrolledBox(
                      activity = activities[index],
                      user,
                      listActivitiesViewModel,
                      navigationActions)
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
                  .padding(STANDARD_PADDING.dp)
                  .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
                  .clickable {
                    listActivitiesViewModel.selectActivity(thisActivity)
                    navigationActions.navigateTo(Screen.EDIT_ACTIVITY)
                  },
          verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.foot),
                contentDescription = "Activity Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(MEDIUM_PADDING.dp).padding(end = MEDIUM_PADDING.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                  text = thisActivity.title,
                  fontSize = SUBTITLE_FONTSIZE.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black)
              Text(
                  text = thisActivity.description,
                  fontSize = SUBTITLE_FONTSIZE.sp,
                  color = Color.Gray)
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
                  .padding(STANDARD_PADDING.dp)
                  .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
                  .clickable {
                    listActivitiesViewModel.selectActivity(thisActivity)
                    navigationActions.navigateTo(Screen.ACTIVITY_DETAILS)
                  },
          verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.foot),
                contentDescription = "Activity Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(MEDIUM_PADDING.dp).padding(end = MEDIUM_PADDING.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                  text = thisActivity.title,
                  fontSize = SUBTITLE_FONTSIZE.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black)
              Text(
                  text = thisActivity.description,
                  fontSize = SUBTITLE_FONTSIZE.sp,
                  color = Color.Gray)
            }
          }
    }
  }
}

@Composable
fun InterestBox(interest: String) {
  Box(
      modifier =
          Modifier.background(Color.LightGray, RoundedCornerShape(STANDARD_PADDING.dp))
              .padding(horizontal = TEXT_FONTSIZE.dp, vertical = STANDARD_PADDING.dp)
              .testTag("$interest"),
      contentAlignment = Alignment.Center) {
        Text(text = interest, fontSize = SUBTITLE_FONTSIZE.sp, color = Color.Black)
      }
}
