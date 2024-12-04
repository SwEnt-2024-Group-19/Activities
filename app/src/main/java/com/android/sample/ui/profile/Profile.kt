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
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.network.NetworkManager
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
import com.android.sample.ui.components.performOfflineAwareAction
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun ProfileScreen(
    userProfileViewModel: ProfileViewModel,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel,
    imageViewModel: ImageViewModel
) {
  val context = LocalContext.current
  val networkManager = NetworkManager(context)

  // Determine if we should use cached data
  val profileState by userProfileViewModel.userState.collectAsState()
  val user =
      if (networkManager.isNetworkAvailable()) {
        profileState
      } else {
        remember { mutableStateOf(userProfileViewModel.loadCachedProfile()) }.value
      }
  when (user) {
    null -> LoadingScreen(navigationActions) // Show a loading indicator or a retry button
    else -> {
      ProfileContent(
          user, navigationActions, listActivitiesViewModel, userProfileViewModel, imageViewModel)
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
    userProfileViewModel: ProfileViewModel,
    imageViewModel: ImageViewModel
) {
  val context = LocalContext.current
  val networkManager = NetworkManager(context)
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
                      performOfflineAwareAction(
                          context = context,
                          networkManager = networkManager,
                          onPerform = {
                            showMenu = false
                            userProfileViewModel.clearUserData()
                            Firebase.auth.signOut()
                            navigationActions.navigateTo(Screen.AUTH)
                          })
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
              item { ProfileHeader(user, imageViewModel) }

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
                  "Activities Created",
                  "created",
                  user,
                  listActivitiesViewModel,
                  navigationActions,
                  userProfileViewModel)
              displayActivitySection(
                  "Activities Enrolled in",
                  "enrolled",
                  user,
                  listActivitiesViewModel,
                  navigationActions,
                  userProfileViewModel)
              displayActivitySection(
                  "Past Activities",
                  "past",
                  user,
                  listActivitiesViewModel,
                  navigationActions,
                  userProfileViewModel)
            }
      }
}
/** Display the activity section based on the category */
fun LazyListScope.displayActivitySection(
    sectionTitle: String,
    category: String,
    user: User,
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    userProfileViewModel: ProfileViewModel
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
          category = category,
          userProfileViewModel = userProfileViewModel)
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
fun ProfileHeader(user: User, imageViewModel: ImageViewModel) {
  Spacer(Modifier.height(MEDIUM_PADDING.dp))
  Text(
      text = "Profile",
      fontSize = TOP_TITLE_SIZE.sp,
      modifier = Modifier.padding(top = MEDIUM_PADDING.dp))
  ProfileImage(
      userId = user.id,
      modifier = Modifier.size(IMAGE_SIZE.dp).clip(CircleShape).testTag("profilePicture"),
      imageViewModel)
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
    category: String,
    userProfileViewModel: ProfileViewModel
) {
  val uiState by listActivitiesViewModel.uiState.collectAsState()
  val activitiesList = (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
  val thisActivity = activitiesList.find { it.uid == activityId }
  val context = LocalContext.current
  thisActivity?.let { activity ->
    if (userProfileViewModel.shouldShowActivity(activity, user, category)) {
      ActivityRow(
          activity = activity,
          listActivitiesViewModel = listActivitiesViewModel,
          navigationActions = navigationActions,
          category = category,
          userId = user.id,
          context = context,
          testTag = "activity${category.capitalize()}")
    }
  }
}

/** Display a single activity in a row */
@Composable
fun ActivityRow(
    activity: Activity,
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    category: String,
    userId: String,
    testTag: String
) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .testTag(testTag)
              .padding(STANDARD_PADDING.dp)
              .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
              .clickable {
                listActivitiesViewModel.selectActivity(activity)
                userProfileViewModel.navigateToActivity(navigationActions, context)              },
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
        if (category == "past") {
          ReviewActivityButtons(activity.likes[userId]) { review ->
            listActivitiesViewModel.reviewActivity(activity, userId, review)
          }
        }
      }
}

@Composable
fun ReviewActivityButtons(currentReview: Boolean?, review: (Boolean?) -> Unit) {
  var isLiked: Boolean? by remember { mutableStateOf(currentReview) }
  Row {
    IconButton(
        onClick = {
          isLiked = if (isLiked == true) null else true
          review(isLiked)
        },
        colors =
            IconButtonDefaults.iconButtonColors(
                containerColor = if (isLiked == true) Color(0xFF048531) else Color.Transparent,
                contentColor =
                    if (isLiked == true) MaterialTheme.colorScheme.onError
                    else MaterialTheme.colorScheme.onSurface),
        modifier = Modifier.testTag("likeIconButton_${isLiked == true}")) {
          Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "Like")
        }
    Spacer(modifier = Modifier.width(MEDIUM_PADDING.dp))
    IconButton(
        onClick = {
          isLiked = if (isLiked == false) null else false
          review(isLiked)
        },
        colors =
            IconButtonDefaults.iconButtonColors(
                containerColor =
                    if (isLiked == false) MaterialTheme.colorScheme.error else Color.Transparent,
                contentColor =
                    if (isLiked == false) MaterialTheme.colorScheme.onError
                    else MaterialTheme.colorScheme.onSurface),
        modifier = Modifier.testTag("dislikeIconButton_${isLiked == false}")) {
          Icon(
              imageVector = Icons.Default.ThumbDown,
              contentDescription = "Dislike",
              tint =
                  if (isLiked == false) MaterialTheme.colorScheme.onError
                  else MaterialTheme.colorScheme.onSurface)
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
