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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HourglassFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.BIG_PADDING
import com.android.sample.resources.C.Tag.CREATED_ACTIVITIES
import com.android.sample.resources.C.Tag.DARK_BLUE_COLOR
import com.android.sample.resources.C.Tag.DARK_GRAY
import com.android.sample.resources.C.Tag.ENROLLED_ACTIVITIES
import com.android.sample.resources.C.Tag.HALF_SCREEN_TEXT_FIELD_PADDING
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_FONT_WEIGHT
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.LIGHT_PURPLE_COLOR
import com.android.sample.resources.C.Tag.MAXIMUM_FONT_WEIGHT
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.NORMAL_PADDING
import com.android.sample.resources.C.Tag.PAST_ACTIVITIES
import com.android.sample.resources.C.Tag.ROW_WIDTH
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.SUCCESS_COLOR
import com.android.sample.resources.C.Tag.TEXT_FONTSIZE
import com.android.sample.resources.C.Tag.VERY_LARGE_FONT_WEIGHT
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_MD
import com.android.sample.resources.C.Tag.colorOfCategory
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.camera.getImageResourceIdForCategory
import com.android.sample.ui.components.PlusButtonToCreate
import com.android.sample.ui.components.performOfflineAwareAction
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Calendar

@Composable
fun ProfileScreen(
    uid: String = "",
    userProfileViewModel: ProfileViewModel,
    navigationActions: NavigationActions,
    listActivitiesViewModel: ListActivitiesViewModel,
    imageViewModel: ImageViewModel
) {
  val context = LocalContext.current
  val networkManager = NetworkManager(context)

  // Observe the cached profile or network state
  val profileState by userProfileViewModel.userState.collectAsState()

  // State for the participant
  var participant by remember { mutableStateOf<User?>(null) }

  if (uid.isEmpty()) {
    // Handle the default profile case
    val user =
        if (networkManager.isNetworkAvailable()) {
          profileState
        } else {
          userProfileViewModel.loadCachedProfile()
        }

    when (user) {
      null -> LoadingScreen(navigationActions)
      else -> {
        UserProfile(
            user,
            navigationActions,
            imageViewModel,
            userProfileViewModel,
            listActivitiesViewModel,
            uid)
      }
    }
  } else {
    // Fetch user data for the given UID
    LaunchedEffect(uid) {
      userProfileViewModel.getUserData(uid) { fetchedParticipant ->
        participant = fetchedParticipant
      }
    }

    when (participant) {
      null -> ParticipantLoadingScreen(navigationActions) // Show a loading indicator
      else -> {
        UserProfile(
            participant!!,
            navigationActions,
            imageViewModel,
            userProfileViewModel,
            listActivitiesViewModel,
            uid)
      }
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
                  text = LocalContext.current.getString(R.string.no_profile),
                  modifier = Modifier.testTag("loadingText"),
                  color = Color.Black)
              Button(
                  onClick = { navigationActions.navigateTo(Screen.SIGN_UP) },
                  modifier = Modifier.testTag("signInButton")) {
                    Text(LocalContext.current.getString(R.string.go_to_sign_in_page))
                  }
            }
      }
}

/** Display a single activity in a row */
@Composable
fun ActivityRow(
    activity: Activity,
    listActivitiesViewModel: ListActivitiesViewModel,
    userProfileViewModel: ProfileViewModel,
    navigationActions: NavigationActions,
    remainingTime: Boolean,
    user: User,
    isParticipant: Boolean=false,
    context: Context = LocalContext.current,
    imageViewModel: ImageViewModel,

) {

  Row(
      modifier =
          Modifier.testTag("activityRow").width(ROW_WIDTH.dp).clickable {
            listActivitiesViewModel.selectActivity(activity)
            userProfileViewModel.navigateToActivity(navigationActions, context)
          },
      verticalAlignment = Alignment.Top) {
        var bitmaps by remember { mutableStateOf(listOf<Bitmap>()) }

        imageViewModel.fetchActivityImagesAsBitmaps(
            activity.uid,
            onSuccess = { urls -> bitmaps = urls },
            onFailure = { Log.e("ActivityDetailsScreen", it.message.toString()) })

        // Display image
        if (activity.images.isNotEmpty() && bitmaps.isNotEmpty()) {
          Image(
              bitmap = bitmaps[0].asImageBitmap(),
              contentDescription = "Activity Image",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.width(HALF_SCREEN_TEXT_FIELD_PADDING.dp)
                      .height(HALF_SCREEN_TEXT_FIELD_PADDING.dp)
                      .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
                      .testTag("activityImage"))
        } else {
          Image(
              painter = painterResource(id = getImageResourceIdForCategory(activity.category)),
              contentDescription = "Activity Image",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.width(HALF_SCREEN_TEXT_FIELD_PADDING.dp)
                      .height(HALF_SCREEN_TEXT_FIELD_PADDING.dp)
                      .clip(RoundedCornerShape(MEDIUM_PADDING.dp))
                      .testTag("activityImage"))
        }

        Column(
            modifier = Modifier.weight(WIDTH_FRACTION_MD).padding(horizontal = MEDIUM_PADDING.dp)) {
              Text(
                  text = activity.title,
                  fontSize = SUBTITLE_FONTSIZE.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black,
                  modifier = Modifier.testTag("activityTitle"))

              if (remainingTime) {
                RemainingTime(System.currentTimeMillis(), activity)
              }

              Text(
                  text = activity.description,
                  fontSize = SUBTITLE_FONTSIZE.sp,
                  color = Color.Gray,
                  maxLines = 3,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.testTag("activityDescription"))
            }
        if (!remainingTime && !isParticipant) {
          ReviewActivityButtons(activity.likes[user.id]) { review ->
            listActivitiesViewModel.reviewActivity(activity, user.id, review)
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
            months >= 1 -> "In $months months"
            days in 6..30 -> "In $days days"
            days in 1..5 -> "In $days days and $hours hours"
            days < 1 -> "In $hours h $minutes min"
            else -> "In $days days, $hours hours and $minutes minutes, $months months"
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
fun InterestBox(interest: Interest) {
  Box(
      modifier =
          Modifier.background(
                  colorOfCategory(interest.category), RoundedCornerShape(STANDARD_PADDING.dp))
              .padding(horizontal = TEXT_FONTSIZE.dp, vertical = STANDARD_PADDING.dp)
              .testTag(interest.name),
      contentAlignment = Alignment.Center) {
        Text(text = interest.name, fontSize = SUBTITLE_FONTSIZE.sp, color = Color.Black)
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfile(
    user: User,
    navigationActions: NavigationActions,
    imageViewModel: ImageViewModel,
    profileViewModel: ProfileViewModel,
    listActivitiesViewModel: ListActivitiesViewModel,
    uid: String
) {
  var activityType by remember { mutableIntStateOf(CREATED_ACTIVITIES) }
  val uiState by listActivitiesViewModel.uiState.collectAsState()
  val activitiesList = (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
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
        if (uid == "") {
          BottomNavigationMenu(
              onTabSelect = { route -> navigationActions.navigateTo(route) },
              tabList = LIST_TOP_LEVEL_DESTINATION,
              selectedItem = Route.PROFILE)
        }
      },
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("profileTopBar"),
            title = {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    modifier = Modifier.testTag("userName"),
                    text = user.name,
                    style =
                        TextStyle(
                            fontSize = MEDIUM_PADDING.sp,
                            fontWeight = FontWeight(MAXIMUM_FONT_WEIGHT),
                            color = Color(DARK_GRAY),
                        ))
              }
            },
            navigationIcon = {
              if (uid != "") {
                GoBackButton(navigationActions)
              }
            },
            actions = {
              if (uid == "") {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.testTag("moreOptionsButton")) {
                      Icon(
                          imageVector = Icons.Default.Settings,
                          contentDescription = "More options",
                          modifier = Modifier.testTag("settingsIcon"))
                    }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                  DropdownMenuItem(
                      modifier = Modifier.testTag("logoutMenuItem"),
                      text = { Text(LocalContext.current.getString(R.string.logout)) },
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
                      modifier = Modifier.testTag("editProfileMenuItem"),
                      text = { Text(LocalContext.current.getString(R.string.edit_profile)) },
                      onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) })
                }
              }
            })
      }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).testTag("profileContentColumn"),
            verticalArrangement = Arrangement.spacedBy(NORMAL_PADDING.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally) {
              ProfileHeader(user, imageViewModel, userActivities)

              ShowInterests(user)

              Row(
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  verticalAlignment = Alignment.Top,
                  modifier = Modifier.fillMaxWidth().testTag("activityTypeRow")) {
                    IconButton(
                        onClick = { activityType = CREATED_ACTIVITIES },
                        modifier =
                            if (activityType == CREATED_ACTIVITIES)
                                Modifier.background(Color.LightGray, shape = CircleShape)
                                    .testTag("createdActivities")
                            else Modifier.testTag("createdActivities")) {
                          Icon(Icons.Outlined.Edit, contentDescription = "Created")
                        }

                    IconButton(
                        onClick = { activityType = ENROLLED_ACTIVITIES },
                        modifier =
                            if (activityType == ENROLLED_ACTIVITIES)
                                Modifier.background(Color.LightGray, shape = CircleShape)
                                    .testTag("enrolledActivities")
                            else Modifier.testTag("enrolledActivities")) {
                          Icon(Icons.Outlined.Groups, contentDescription = "Enrolled")
                        }
                    IconButton(
                        onClick = { activityType = PAST_ACTIVITIES },
                        modifier =
                            if (activityType == PAST_ACTIVITIES)
                                Modifier.background(Color.LightGray, shape = CircleShape)
                                    .testTag("passedActivities")
                            else Modifier.testTag("passedActivities")) {
                          Icon(Icons.Outlined.HourglassFull, contentDescription = "Passed")
                        }
                  }
              Column(
                  verticalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp, Alignment.Top),
                  horizontalAlignment = Alignment.Start,
                  modifier = Modifier.testTag("activitiesColumn")) {
                    DisplayActivitiesList(
                        userActivities,
                        activityType,
                        user,
                        hourDateViewModel,
                        navigationActions,
                        profileViewModel,
                        listActivitiesViewModel,
                        imageViewModel,
                        uid,)
                  }
            }
      }
}

@Composable
fun DisplayActivitiesList(
    userActivities: List<Activity>,
    activityType: Int,
    user: User,
    hourDateViewModel: HourDateViewModel,
    navigationActions: NavigationActions,
    userProfileViewModel: ProfileViewModel,
    listActivitiesViewModel: ListActivitiesViewModel,
    imageViewModel: ImageViewModel,
    uid: String,
) {
  var listToShow = emptyList<Activity>()
  when (activityType) {
    PAST_ACTIVITIES -> {
      listToShow =
          userActivities.filter {
            hourDateViewModel.combineDateAndTime(
                it.date, hourDateViewModel.addDurationToTime(it.startTime, it.duration)) <=
                Timestamp.now()
          }
    }
    CREATED_ACTIVITIES -> {
      listToShow =
          userActivities.filter {
            it.creator == user.id &&
                hourDateViewModel.combineDateAndTime(
                    it.date, hourDateViewModel.addDurationToTime(it.startTime, it.duration)) >
                    Timestamp.now()
          }
    }
    ENROLLED_ACTIVITIES -> {
      listToShow =
          userActivities.filter {
            (it.creator != user.id || it.participants.map { it.id }.contains(user.id)) &&
                hourDateViewModel.combineDateAndTime(
                    it.date, hourDateViewModel.addDurationToTime(it.startTime, it.duration)) >
                    Timestamp.now()
          }
    }
  }
  val remainingTime = activityType != PAST_ACTIVITIES

  if (listToShow.isEmpty()) {
    if (uid == "") {
      if (activityType == PAST_ACTIVITIES) {
        Text(
            LocalContext.current.getString(R.string.no_past_activities),
            modifier = Modifier.testTag("noActivitiesText"))
      } else {
        PlusButtonToCreate(navigationActions = navigationActions, activityType)
      }
    } else {
      Text(
          LocalContext.current.getString(R.string.participant_no_activities),
          modifier = Modifier.testTag("noActivitiesText"))
    }
  } else {
    LazyColumn(modifier = Modifier.testTag("activitiesList")) {
      items(listToShow.size) { index ->
        ActivityRow(
            activity = listToShow[index],
            listActivitiesViewModel = listActivitiesViewModel,
            navigationActions = navigationActions,
            userProfileViewModel = userProfileViewModel,
            user = user,
            isParticipant= uid != "",
            remainingTime = remainingTime,
            imageViewModel = imageViewModel)
        Spacer(modifier = Modifier.height(NORMAL_PADDING.dp))
      }
    }
  }
}

@Composable
fun ProfileHeader(user: User, imageViewModel: ImageViewModel, userActivities: List<Activity>) {
  Row(
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.testTag("profileHeader").fillMaxWidth()) {
        ProfileImage(
            userId = user.id,
            modifier = Modifier.size(IMAGE_SIZE.dp).clip(CircleShape).testTag("profilePicture"),
            imageViewModel)
        Row(
            horizontalArrangement =
                Arrangement.spacedBy(BIG_PADDING.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top,
        ) {
          HeaderItem(
              "Activities\nCreated",
              userActivities.filter { it.creator == user.id }.size.toString(),
              false)
          HeaderItem(
              "Activities\nJoined",
              userActivities
                  .filter({
                    it.creator != user.id || it.participants.map { it.id }.contains(user.id)
                  })
                  .size
                  .toString(),
              false)
          HeaderItem("Rating", "4.7", true)
        }
      }
}

@Composable
fun HeaderItem(field: String, number: String, isStar: Boolean) {
  Column(
      verticalArrangement = Arrangement.spacedBy(NORMAL_PADDING.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.testTag("headerItem")) {
        Row(
            horizontalArrangement =
                Arrangement.spacedBy(SMALL_PADDING.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
              modifier = Modifier.testTag("headerItemTitle"),
              text = number,
              style =
                  TextStyle(
                      fontSize = MEDIUM_PADDING.sp,
                      fontWeight = FontWeight(LARGE_FONT_WEIGHT),
                      color = Color(DARK_GRAY),
                      textAlign = TextAlign.Center,
                  ))
          if (isStar) {
            Icon(
                Icons.Default.Star,
                contentDescription = "ratingStar",
                modifier = Modifier.size(MEDIUM_PADDING.dp).testTag("ratingStar"))
          }
        }
        Text(
            modifier = Modifier.testTag("headerItemField"),
            text = field,
            style =
                TextStyle(
                    fontSize = TEXT_FONTSIZE.sp,
                    fontWeight = FontWeight(VERY_LARGE_FONT_WEIGHT),
                    color = Color(DARK_GRAY),
                    textAlign = TextAlign.Center,
                ))
      }
}

@Composable
fun ShowInterests(user: User) {
  Column(
      verticalArrangement = Arrangement.spacedBy(TEXT_FONTSIZE.dp, Alignment.Top),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(horizontal = TEXT_FONTSIZE.dp).testTag("interestsSection")) {
        Text(
            modifier = Modifier.testTag("interestsTitle"),
            text = "Interests",
            style =
                TextStyle(
                    fontSize = BIG_PADDING.sp,
                    lineHeight = LARGE_PADDING.sp,
                    fontWeight = FontWeight(LARGE_FONT_WEIGHT),
                    color = Color(DARK_GRAY),
                ))
        LazyRow(
            modifier = Modifier.testTag("interestsRow"),
            horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp),
            contentPadding = PaddingValues(horizontal = MEDIUM_PADDING.dp)) {
              user.interests?.let { interests ->
                items(interests.size) { index -> InterestBox(interest = user.interests[index]) }
              }
            }
      }
}

@Composable
fun GoBackButton(navigationActions: NavigationActions) {
  IconButton(
      modifier = Modifier.testTag("goBackButton"), onClick = { navigationActions.goBack() }) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
      }
}
