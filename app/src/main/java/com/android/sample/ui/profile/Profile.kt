package com.android.sample.ui.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.android.sample.model.activity.Activity
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
  }
}

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
  var showMenu by remember { mutableStateOf(false) }
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

              DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                      showMenu = false
                      userProfileViewModel.clearUserData()
                      Firebase.auth.signOut()
                      navigationActions.navigateTo(Screen.AUTH)
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
            Modifier.fillMaxSize().padding(innerPadding).testTag("profileContentColumn"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              item { ProfileHeader(user) }

              item { SectionTitle(title = "Interests", testTag = "interestsSection") }

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
              displayActivitySection(
                  "Activities Created", "created", user, listActivitiesViewModel, navigationActions)
              displayActivitySection(
                  "Activities Enrolled in",
                  "enrolled",
                  user,
                  listActivitiesViewModel,
                  navigationActions)
              displayActivitySection(
                  "Past Activities", "past", user, listActivitiesViewModel, navigationActions)
            }
      }
}
/** Display the activity section based on the category */
fun LazyListScope.displayActivitySection(
    sectionTitle: String,
    category: String,
    user: User,
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions
) {
  item {
    Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
    SectionTitle(title = sectionTitle, testTag = "${category}ActivitiesTitle")
  }

  user.activities?.let { activities ->
    items(activities.size) { index ->
      ActivityBox(
          activityId = activities[index],
          user = user,
          listActivitiesViewModel = listActivitiesViewModel,
          navigationActions = navigationActions,
          category = category)
    }
  }
}

@Composable
fun SectionTitle(title: String, testTag: String) {
  Text(
      text = title,
      fontSize = TITLE_FONTSIZE.sp,
      modifier =
          Modifier.padding(start = MEDIUM_PADDING.dp, top = MEDIUM_PADDING.dp).testTag(testTag))
}
/** Display the user's profile picture and name */
@Composable
fun ProfileHeader(user: User) {
  Spacer(Modifier.height(MEDIUM_PADDING.dp))
  Text(
      text = "Profile",
      fontSize = TOP_TITLE_SIZE.sp,
      modifier = Modifier.padding(top = MEDIUM_PADDING.dp))
  ProfileImage(
      userId = user.id,
      modifier = Modifier.size(IMAGE_SIZE.dp).clip(CircleShape).testTag("profilePicture"))
  Text(
      text = "${user.name} ${user.surname}",
      fontSize = TITLE_FONTSIZE.sp,
      modifier = Modifier.padding(top = STANDARD_PADDING.dp).testTag("userName"))
}
/** Display a single activity in a box, the same box is used for all categories */
@Composable
fun ActivityBox(
    activityId: String,
    user: User,
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    category: String
) {
  val uiState by listActivitiesViewModel.uiState.collectAsState()
  val activitiesList = (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
  val thisActivity = activitiesList.find { it.uid == activityId }

  thisActivity?.let { activity ->
    if (shouldShowActivity(activity, user, category)) {
      ActivityRow(
          activity = activity,
          onClickAction = {
            listActivitiesViewModel.selectActivity(activity)
            navigateToActivity(category, navigationActions)
          },
          testTag = "activity${category.capitalize()}")
    }
  }
}

/** Check if the activity should be displayed based on the category and the user's role in the */
fun shouldShowActivity(activity: Activity, user: User, category: String): Boolean {
  return when (category) {
    "created" -> activity.creator == user.id && activity.date > Timestamp.now()
    "enrolled" -> activity.creator != user.id && activity.date > Timestamp.now()
    "past" -> activity.date < Timestamp.now()
    else -> false
  }
}
/** Navigate to the appropriate screen based on the category */
fun navigateToActivity(category: String, navigationActions: NavigationActions) {
  when (category) {
    "created" -> navigationActions.navigateTo(Screen.EDIT_ACTIVITY)
    "past" -> navigationActions.navigateTo(Screen.EDIT_ACTIVITY)
    "enrolled" -> navigationActions.navigateTo(Screen.ACTIVITY_DETAILS)
  }
}
/** Display a single activity in a row */
@Composable
fun ActivityRow(activity: Activity, onClickAction: () -> Unit, testTag: String) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .testTag(testTag)
              .padding(STANDARD_PADDING.dp)
              .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
              .clickable { onClickAction() },
      verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.foot),
            contentDescription = "Activity Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(MEDIUM_PADDING.dp).padding(end = MEDIUM_PADDING.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
              text = activity.title,
              fontSize = SUBTITLE_FONTSIZE.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black)
          Text(text = activity.description, fontSize = SUBTITLE_FONTSIZE.sp, color = Color.Gray)
        }
      }
}
/** Display a single interest in a box */
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
