package com.android.sample.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HourglassFull
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.BLACK_COLOR
import com.android.sample.resources.C.Tag.DARK_BLUE_COLOR
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LIGHT_PURPLE_COLOR
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.SUCCESS_COLOR
import com.android.sample.resources.C.Tag.TEXT_FONTSIZE
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TOP_TITLE_SIZE
import com.android.sample.resources.C.Tag.WIDTH_FRACTION
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.camera.getImageResourceIdForCategory
import com.android.sample.ui.components.PlusButtonToCreate
import com.android.sample.ui.components.performOfflineAwareAction
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Calendar


@Composable
fun ProfileScreen(
    userProfileViewModel: ProfileViewModel,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel,
    imageViewModel: ImageViewModel
) {
  val context = LocalContext.current
  val networkManager = NetworkManager(context)


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
      UserProfile(
          user, navigationActions, imageViewModel, userProfileViewModel, listActivitiesViewModel)
    }
  }
}

@Composable
fun LoadingScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier
          .fillMaxSize()
          .testTag("loadingScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      }) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileContent(
//    user: User,
//    navigationActions: NavigationActions,
//    listActivitiesViewModel: ListActivitiesViewModel,
//    userProfileViewModel: ProfileViewModel,
//    imageViewModel: ImageViewModel
//) {
//  val uiState by listActivitiesViewModel.uiState.collectAsState()
//  val context = LocalContext.current
//  val networkManager = NetworkManager(context)
//  var showMenu by remember { mutableStateOf(false) }
//  Log.d("ProfileScreen", "User photo: ${user.photo}")
//  Scaffold(
//      modifier = Modifier
//          .fillMaxSize()
//          .testTag("profileScreen"),
//      bottomBar = {
//        BottomNavigationMenu(
//            onTabSelect = { route -> navigationActions.navigateTo(route) },
//            tabList = LIST_TOP_LEVEL_DESTINATION,
//            selectedItem = navigationActions.currentRoute())
//      },
//      topBar = {
//        TopAppBar(
//            title = {
//              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("Profile")
//              }
//            },
//            actions = {
//              IconButton(
//                  onClick = { showMenu = true }, modifier = Modifier.testTag("moreOptionsButton")) {
//                    Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = "More options")
//                  }
//              DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
//                DropdownMenuItem(
//                    text = { Text("Logout") },
//                    onClick = {
//                      performOfflineAwareAction(
//                          context = context,
//                          networkManager = networkManager,
//                          onPerform = {
//                            showMenu = false
//                            userProfileViewModel.clearUserData()
//                            Firebase.auth.signOut()
//                            navigationActions.navigateTo(Screen.AUTH)
//                          })
//                    },
//                    enabled = Firebase.auth.currentUser?.isAnonymous == false)
//                DropdownMenuItem(
//                    text = { Text("Edit profile") },
//                    onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) })
//              }
//            })
//      }) { innerPadding ->
//        LazyColumn(
//            Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .testTag("profileContentColumn"),
//            horizontalAlignment = Alignment.CenterHorizontally) {
//              item { ProfileHeader(user, imageViewModel) }
//
//              item { SectionTitle(title = "Interests", testTag = "interestsSection") }
//
//              item {
//                LazyRow(
//                    horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp),
//                    contentPadding = PaddingValues(horizontal = MEDIUM_PADDING.dp)) {
//                      user.interests?.let { interests ->
//                        items(interests.size) { index ->
//                          InterestBox(interest = user.interests[index].interest)
//                        }
//                      }
//                    }
//              }
//
//              val activitiesList =
//                  (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
//              val usersActivity =
//                  activitiesList.filter {
//                    it.creator == user.id || it.participants.map { it.id }.contains(user.id)
//                  }
//              val hourDateViewModel = HourDateViewModel()
//              // Display activities sections
//              displayActivitySection(
//                  "Activities Created",
//                  "created",
//                  usersActivity.filter {
//                    it.creator == user.id &&
//                        hourDateViewModel.combineDateAndTime(
//                            it.date,
//                            hourDateViewModel.addDurationToTime(it.startTime, it.duration)) >
//                            Timestamp.now()
//                  },
//                  navigationActions,
//                  userProfileViewModel,
//                  listActivitiesViewModel,
//                  false,
//                  user)
//              displayActivitySection(
//                  "Activities Enrolled in",
//                  "enrolled",
//                  usersActivity.filter {
//                    it.creator != user.id &&
//                        hourDateViewModel.combineDateAndTime(
//                            it.date,
//                            hourDateViewModel.addDurationToTime(it.startTime, it.duration)) >
//                            Timestamp.now()
//                  },
//                  navigationActions,
//                  userProfileViewModel,
//                  listActivitiesViewModel,
//                  false,
//                  user)
//              displayActivitySection(
//                  "Past Activities",
//                  "past",
//                  usersActivity.filter {
//                    hourDateViewModel.combineDateAndTime(
//                        it.date, hourDateViewModel.addDurationToTime(it.startTime, it.duration)) <=
//                        Timestamp.now()
//                  },
//                  navigationActions,
//                  userProfileViewModel,
//                  listActivitiesViewModel,
//                  false,
//                  user)
//            }
//      }
//}
//
///** Display the activity section based on the category */
//fun LazyListScope.displayActivitySection(
//    sectionTitle: String,
//    category: String,
//    listActivities: List<Activity>,
//    navigationActions: NavigationActions,
//    userProfileViewModel: ProfileViewModel,
//    listActivitiesViewModel: ListActivitiesViewModel,
//    isParticipantProfile: Boolean,
//    user: User
//) {
//  item {
//    Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
//    SectionTitle(title = sectionTitle, testTag = "${category}ActivitiesTitle")
//  }
//  if (listActivities.isNotEmpty()) {
//    items(listActivities.size) { index ->
//      ActivityBox(
//          activity = listActivities[index],
//          listActivitiesViewModel = listActivitiesViewModel,
//          navigationActions = navigationActions,
//          category = category,
//          userProfileViewModel = userProfileViewModel,
//          user = user)
//    }
//  } else {
//    if (!isParticipantProfile) {
//      item { PlusButtonToCreate(navigationActions = navigationActions, category) }
//    } else {
//      item {
//        Text("This participant has no activities", modifier = Modifier.padding(MEDIUM_PADDING.dp))
//      }
//    }
//  }
//}
//
@Composable
fun SectionTitle(title: String, testTag: String) {
  Text(
      text = title,
      fontSize = TITLE_FONTSIZE.sp,
      modifier =
      Modifier
          .padding(start = MEDIUM_PADDING.dp, top = MEDIUM_PADDING.dp)
          .testTag(testTag))
}

///** Display the user's profile picture and name */
@Composable
fun ProfileHeader(user: User, imageViewModel: ImageViewModel) {
  Spacer(Modifier.height(MEDIUM_PADDING.dp))
  Text(
      text = "Profile",
      fontSize = TOP_TITLE_SIZE.sp,
      modifier = Modifier.padding(top = MEDIUM_PADDING.dp))
  ProfileImage(
      userId = user.id,
      modifier = Modifier
          .size(IMAGE_SIZE.dp)
          .clip(CircleShape)
          .testTag("profilePicture"),
      imageViewModel)
  Text(
      text = "${user.name} ${user.surname}",
      fontSize = TITLE_FONTSIZE.sp,
      modifier = Modifier
          .padding(top = STANDARD_PADDING.dp)
          .testTag("userName"))
}

/** Display a single activity in a box, the same box is used for all categories */
@Composable
fun ActivityBox(
    activity: Activity,
    listActivitiesViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    remainingTime:  Boolean,
    userProfileViewModel: ProfileViewModel,
    user: User,
    imageViewModel: ImageViewModel
) {
  val context = LocalContext.current
  ActivityRow(
      activity = activity,
      listActivitiesViewModel = listActivitiesViewModel,
      userProfileViewModel = userProfileViewModel,
      navigationActions = navigationActions,
      remainingTime = remainingTime,
      userId = user.id,
      context = context,
      testTag = "",
      imageViewModel = imageViewModel)
}

/** Display a single activity in a row */
@Composable
fun ActivityRow(
    activity: Activity,
    listActivitiesViewModel: ListActivitiesViewModel,
    userProfileViewModel: ProfileViewModel,
    navigationActions: NavigationActions,
    remainingTime: Boolean,
    userId: String,
    context: Context,
    testTag: String,
    imageViewModel: ImageViewModel
) {
    Row(
        modifier = Modifier
            .testTag(testTag)
            .width(408.dp)
            .clickable {
                listActivitiesViewModel.selectActivity(activity)
                userProfileViewModel.navigateToActivity(navigationActions, context)
            },
        verticalAlignment = Alignment.Top
    ) {
        var bitmaps by remember { mutableStateOf(listOf<Bitmap>()) }

        imageViewModel.fetchActivityImagesAsBitmaps(
            activity.uid,
            onSuccess = { urls -> bitmaps = urls },
            onFailure = { Log.e("ActivityDetailsScreen", it.message.toString()) }
        )

        // Display image
        if (activity.images.isNotEmpty() && bitmaps.isNotEmpty()) {
            Image(
                bitmap = bitmaps[0].asImageBitmap(),
                contentDescription = "Activity Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
            )
        } else {
            Image(
                painter = painterResource(id = getImageResourceIdForCategory(activity.category)),
                contentDescription = "Activity Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
            )
        }

        Column(modifier = Modifier.weight(WIDTH_FRACTION).padding(horizontal = 16.dp)) {

                Text(
                    text = activity.title,
                    fontSize = SUBTITLE_FONTSIZE.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

            if (remainingTime) {
                RemainingTime(System.currentTimeMillis(), activity)
            }


            Text(
                text = activity.description,
                fontSize = SUBTITLE_FONTSIZE.sp,
                color = Color.Gray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (!remainingTime) {
            ReviewActivityButtons(activity.likes[userId]) { review ->
                listActivitiesViewModel.reviewActivity(activity, userId, review)
            }
        }
    }
}

@Composable
fun RemainingTime(currentTimeMillis: Long, activity: Activity) {
  val startTimeParts = activity.startTime.split(":")
  val activityHour = startTimeParts[0].toInt()
  val activityMinute = startTimeParts[1].toInt()

  val activityDate = activity.date.toDate()
  val calendar =
      Calendar.getInstance().apply {
        time = activityDate
        set(Calendar.HOUR_OF_DAY, activityHour)
        set(Calendar.MINUTE, activityMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }
  val activityTimeMillis = calendar.timeInMillis

  val remainingTimeMillis = activityTimeMillis - currentTimeMillis

  val hours = remainingTimeMillis / (1000 * 60 * 60) % 24
  val minutes = remainingTimeMillis / (1000 * 60) % 60
  val days = remainingTimeMillis / (1000 * 60 * 60 * 24)
  val months = days / 30

  fun calculateColor(remainingTimeMillis: Long): Color {
    val totalTimeMillis = 30 * 24 * 60 * 60 * 1000L
    val fraction =
        (remainingTimeMillis.coerceAtLeast(0).toFloat() / totalTimeMillis).coerceIn(0f, 1f)
    return lerp(Color(LIGHT_PURPLE_COLOR), Color(DARK_BLUE_COLOR), 1 - fraction)
  }

  val textColor = calculateColor(remainingTimeMillis)

  Text(
      text =
          when {
            months > 1 -> "In $months months"
            days in 6..30 -> "In $days days"
            days in 1..5 -> "In $days days and $hours hours"
            days < 1 -> "In $hours h $minutes min"
            else -> ""
          },
      fontSize = SUBTITLE_FONTSIZE.sp,
      color = textColor,
      modifier = Modifier.testTag("remainingTime"))
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
                containerColor = if (isLiked == true) Color(SUCCESS_COLOR) else Color.Transparent,
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
      Modifier
          .background(Color.LightGray, RoundedCornerShape(STANDARD_PADDING.dp))
          .padding(horizontal = TEXT_FONTSIZE.dp, vertical = STANDARD_PADDING.dp)
          .testTag("$interest"),
      contentAlignment = Alignment.Center) {
        Text(text = interest, fontSize = SUBTITLE_FONTSIZE.sp, color = Color.Black)
      }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfile(user:User, navigationActions: NavigationActions, imageViewModel: ImageViewModel,profileViewModel: ProfileViewModel,listActivitiesViewModel: ListActivitiesViewModel) {
    var activityType by remember { mutableIntStateOf(0) }
    val uiState by listActivitiesViewModel.uiState.collectAsState()
    val activitiesList =
        (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
    val userActivities =
        activitiesList.filter {
            it.creator == user.id || it.participants.map { it.id }.contains(user.id)
        }
    val hourDateViewModel = HourDateViewModel()
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val networkManager = NetworkManager(context)

    Scaffold(
        bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
        topBar = {
        TopAppBar(
            title = {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  Text(
                      text = user.name,
                      style = TextStyle(
                          fontSize = 16.sp,

                          fontWeight = FontWeight(700),
                          color = Color(0xFF212121),

                          )
                  )
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
                            profileViewModel.clearUserData()
                            Firebase.auth.signOut()
                            navigationActions.navigateTo(Screen.AUTH)
                          })
                    },
                    enabled = Firebase.auth.currentUser?.isAnonymous == false)
                DropdownMenuItem(
                    text = { Text("Edit profile") },
                    onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) })
              }
            })
      })
     { innerPadding ->

         Column(
             modifier= Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ){


                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProfileImage(
                        userId = user.id,
                        modifier = Modifier
                            .size(IMAGE_SIZE.dp)
                            .clip(CircleShape)
                            .testTag("profilePicture"),
                        imageViewModel
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            15.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = userActivities.filter { it.creator == user.id }.size.toString(),
                            style = TextStyle(
                                fontSize = 16.sp,

                                fontWeight = FontWeight(600),
                                color = Color(0xFF212121),
                                textAlign = TextAlign.Center,
                            )
                        )
                        Text(
                            text = "Avtivities\nCreated",
                            style = TextStyle(
                                fontSize = 13.sp,

                                fontWeight = FontWeight(500),
                                color = Color(0xFF212121),

                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            15.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = userActivities.filter({
                                it.creator != user.id || it.participants.map { it.id }
                                    .contains(user.id)
                            }).size.toString(),
                            style = TextStyle(
                                fontSize = 16.sp,

                                fontWeight = FontWeight(600),
                                color = Color(0xFF212121),

                                textAlign = TextAlign.Center,
                            )
                        )
                        Text(
                            text = "Activities\njoined",
                            style = TextStyle(
                                fontSize = 13.sp,

                                fontWeight = FontWeight(500),
                                color = Color(0xFF212121),

                                textAlign = TextAlign.Center,
                            )
                        )

                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                10.dp,
                                Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Text(
                                text = "Blank",
                                style = TextStyle(
                                    fontSize = 16.sp,

                                    fontWeight = FontWeight(600),
                                    color = Color(0xFF212121),

                                    textAlign = TextAlign.Center,
                                )
                            )
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "ratingStar"
                            )
                        }
                        Text(
                            text = "Rating",
                            style = TextStyle(
                                fontSize = 13.sp,

                                fontWeight = FontWeight(500),
                                color = Color(0xFF212121),

                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                }


            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier= Modifier.padding(horizontal = 12.dp)
            ) {

                    Text(
                        text = "Interests",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 24.sp,
                             
                            fontWeight = FontWeight(400),
                            color = Color(0xFF212121),
                        )
                    )
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(110.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ) {
                IconButton(onClick = { activityType=0 }) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Created")
                }
                IconButton(onClick = { activityType=1 }) {
                    Icon(Icons.Outlined.Groups, contentDescription = "Enrolled")
                }
                IconButton(onClick = { activityType=2 }) {
                    Icon(Icons.Outlined.HourglassFull, contentDescription = "Passed")

                }

            }
             Column(
                 verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                 horizontalAlignment = Alignment.Start,
             ) {
             DisplayActivitiesList(userActivities, activityType,user, hourDateViewModel,navigationActions,profileViewModel,listActivitiesViewModel,imageViewModel)
            }
        }
    }

}

@Composable
fun DisplayActivitiesList(userActivities: List<Activity>, activityType: Int,user: User,hourDateViewModel: HourDateViewModel,navigationActions: NavigationActions,userProfileViewModel: ProfileViewModel,listActivitiesViewModel: ListActivitiesViewModel, imageViewModel: ImageViewModel) {
    var listToShow= emptyList<Activity>()
    when (activityType) {
        2 -> {
            listToShow = userActivities.filter {  hourDateViewModel.combineDateAndTime(
                it.date, hourDateViewModel.addDurationToTime(it.startTime, it.duration)) <=
                    Timestamp.now() }
        }
        0 -> {
            listToShow = userActivities.filter { it.creator == user.id }
        }
        1 -> {
            listToShow = userActivities.filter { it.creator != user.id || it.participants.map { it.id }.contains(user.id) }
        }
    }
    val remainingTime = activityType != 2

    LazyColumn {
        items(listToShow.size) { index ->
            ActivityBox(
                activity = listToShow[index],
                listActivitiesViewModel = listActivitiesViewModel,
                navigationActions = navigationActions,
                userProfileViewModel = userProfileViewModel,
                user = user,
                remainingTime = remainingTime,
                imageViewModel =imageViewModel
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
