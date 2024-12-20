package com.android.sample.ui.activitydetails

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.Comment
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.ACTIVITY_COMMENTS
import com.android.sample.resources.C.Tag.ACTIVITY_DETAILS
import com.android.sample.resources.C.Tag.ATTENDANT_DETAILS
import com.android.sample.resources.C.Tag.BUTTON_ELEVATION_DEFAULT
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT_LG
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT_MD
import com.android.sample.resources.C.Tag.DARK_GRAY
import com.android.sample.resources.C.Tag.DARK_YELLOW
import com.android.sample.resources.C.Tag.EXTRA_LARGE_PADDING
import com.android.sample.resources.C.Tag.ICON_BUTTON_SIZE
import com.android.sample.resources.C.Tag.IMAGE_HEIGHT_RATIO
import com.android.sample.resources.C.Tag.IMAGE_WIDTH_RATIO
import com.android.sample.resources.C.Tag.LARGE_FONT_WEIGHT
import com.android.sample.resources.C.Tag.LIGHT_BLUE
import com.android.sample.resources.C.Tag.MEDIUM_FONT_WEIGHT
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.NORMAL_PADDING
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.SMALL_BUTTON_WIDTH
import com.android.sample.resources.C.Tag.SMALL_FONT_WEIGHT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TEXT_WEIGHT
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_MD
import com.android.sample.ui.camera.CarouselNoModif
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.components.TextFieldWithErrorState
import com.android.sample.ui.components.performOfflineAwareAction
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.ReviewActivityButtons
import com.google.firebase.Timestamp
import java.util.UUID
import kotlin.math.min

/**
 * ActivityDetailsScreen Composable: Displays the activity details screen, including details,
 * participants, comments, and interaction options.
 *
 * @param listActivityViewModel ViewModel for managing activity details and state.
 * @param navigationActions Provides navigation between screens.
 * @param profileViewModel ViewModel for user profile state and operations.
 * @param locationViewModel ViewModel for handling location-based data.
 * @param imageViewModel ViewModel for fetching and displaying images.
 */
@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    listActivityViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    locationViewModel: LocationViewModel,
    imageViewModel: ImageViewModel
) {
  // State variables for activity details

  val detailsType = listActivityViewModel.selectedDetailsType.collectAsState().value
  val activity = listActivityViewModel.selectedActivity.collectAsState().value
  val profile = profileViewModel.userState.collectAsState().value
  val isUserEnrolled = profile?.activities?.contains(activity?.uid) ?: false

  // Remember state for activity properties
  val activityTitle by remember { mutableStateOf(activity?.title) }
  val description by remember { mutableStateOf(activity?.description) }
  val location by remember { mutableStateOf(activity?.location) }
  val price by remember { mutableStateOf(activity?.price) }
  val dueDate by remember { mutableStateOf(activity?.date) }
  val placesTaken by remember { mutableStateOf(activity?.placesLeft) }
  val maxPlaces by remember { mutableStateOf(activity?.maxPlaces) }
  val context = LocalContext.current
  val networkManager = NetworkManager(context)
  val startTime by remember { mutableStateOf(activity?.startTime) }
  val duration by remember { mutableStateOf(activity?.duration) }
  var comments by remember { mutableStateOf(activity?.comments ?: listOf()) }

  var bitmaps by remember { mutableStateOf(listOf<Bitmap>()) }
  /**
   * Function to delete a comment, along with replies if applicable. Updates the activity's comments
   * list in the ViewModel.
   */
  val deleteComment: (Comment) -> Unit = { commentToDelete ->
    // Filter out the main comment and any replies associated with it
    val newComments = comments.filter { it.uid != commentToDelete.uid }
    if (newComments.size < comments.size) {
      comments = newComments
    } else {
      // Filter out the reply from the main comment
      val newReplies =
          comments.map { comment ->
            if (comment.replies.any { it.uid == commentToDelete.uid }) {
              comment.copy(replies = comment.replies.filter { it.uid != commentToDelete.uid })
            } else {
              comment
            }
          }
      comments = newReplies
    }

    // Update the activity with the modified comments list
    listActivityViewModel.updateActivity(activity!!.copy(comments = comments))
  }
  // Creator information and participants
  val creatorID = activity?.creator ?: ""
  var creator by remember {
    mutableStateOf(User(creatorID, "null", "null", listOf(), listOf(), "", listOf()))
  }

  activity?.participants?.firstOrNull { it.id == creatorID }?.let { creator = it }
  if (creator.name == "null") {
    profileViewModel.getUserData(
        creatorID,
        onResult = {
          if (it != null) {
            creator = it
          }
        })
  }
  Log.d("ActivityDetailsScreen", "ActivityDetails Creator: $creator")
  val uiState by listActivityViewModel.uiState.collectAsState()
  val activitiesList = (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
  val nbActivitiesCreated = activitiesList.filter { it.creator == creator.id }.size
  val hourDateViewModel = HourDateViewModel()

  // Fetch participants and update list
  var participantsList by remember { mutableStateOf(listOf<User>()) }
  LaunchedEffect(activity?.uid) {
    activity?.participants?.forEach { participant ->
      if (participant.id.isEmpty()) participantsList = participantsList + participant
      else {
        if (participant.id.length > 8) {
          profileViewModel.getUserData(
              participant.id,
              onResult = {
                if (it != null && !participantsList.contains(it))
                    participantsList = participantsList.plus(it)
              })
        }
      }
    }
  }
  // Main UI Scaffold
  Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
            title = {},
            modifier = Modifier.testTag("topAppBar"),
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("goBackButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back")
                  }
            })
      },
      bottomBar = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.wrapContentHeight()
                    .padding(horizontal = MEDIUM_PADDING.dp)
                    .testTag("bottomBar")) {
              // Like button to like/unlike the activity
              LikeButton(profile, activity, profileViewModel)
              Spacer(modifier = Modifier.width(SMALL_PADDING.dp))

              if (activity != null) {
                // Export activity to calendar
                ExportActivityToCalendarButton(
                    activity = activity, viewModel = listActivityViewModel, context = context)
              }

              Spacer(modifier = Modifier.weight(WIDTH_FRACTION_MD))
              // Enroll or edit activity button
              if (activity?.status == ActivityStatus.ACTIVE && profile != null) {

                if (hourDateViewModel.combineDateAndTime(activity.date, activity.startTime) <=
                    Timestamp.now()) {
                  Text(
                      text = stringResource(R.string.activity_past),
                      modifier = Modifier.testTag("archivedActivity"))
                } else if (activity.creator != profile.id) {
                  ElevatedButton(
                      onClick = {
                        performOfflineAwareAction(
                            context = context,
                            networkManager = networkManager,
                            onPerform = {
                              if (isUserEnrolled) {
                                // Logic to leave the activity once enrolled
                                val updatedActivity =
                                    activity.copy(
                                        placesLeft = min((placesTaken ?: 0) - 1, maxPlaces ?: 0),
                                        participants =
                                            activity.participants.filter { it.id != profile.id })
                                listActivityViewModel.updateActivity(updatedActivity)
                                profileViewModel.removeJoinedActivity(profile.id, activity.uid)
                                Toast.makeText(
                                        context,
                                        "Successfully left the activity",
                                        Toast.LENGTH_SHORT)
                                    .show()
                                navigationActions.navigateTo(Screen.PROFILE)
                              } else {
                                // Logic to enroll in the activity
                                if ((placesTaken ?: 0) < (maxPlaces ?: 0)) {
                                  val theActivity =
                                      activity.copy(
                                          placesLeft = min((placesTaken ?: 0) + 1, maxPlaces ?: 0),
                                          participants =
                                              activity.participants +
                                                  User(
                                                      name = profile.name,
                                                      surname = profile.surname,
                                                      id = profile.id,
                                                      photo = profile.photo,
                                                      interests = profile.interests,
                                                      activities = profile.activities))

                                  listActivityViewModel.updateActivity(theActivity)
                                  profileViewModel.addActivity(profile.id, theActivity.uid)
                                  Toast.makeText(context, "Enroll Successful", Toast.LENGTH_SHORT)
                                      .show()
                                  navigationActions.navigateTo(Screen.OVERVIEW)
                                } else {
                                  Toast.makeText(
                                          context,
                                          "Enroll failed, limit of places reached",
                                          Toast.LENGTH_SHORT)
                                      .show()
                                }
                                navigationActions.navigateTo(Screen.OVERVIEW)
                              }
                            })
                      },
                      colors = buttonColors(containerColor = Color(LIGHT_BLUE)),
                      elevation =
                          ButtonDefaults.elevatedButtonElevation(
                              defaultElevation = BUTTON_ELEVATION_DEFAULT.dp),
                      modifier = Modifier.testTag("enrollButton")) {
                        if (isUserEnrolled)
                            Text(
                                text = "Leave",
                                color = Color.Black,
                            )
                        else
                            Text(
                                text = "Enroll",
                                color = Color.Black,
                            )
                      }
                } else {
                  ElevatedButton(
                      onClick = {
                        performOfflineAwareAction(
                            context = context,
                            networkManager = networkManager,
                            onPerform = { navigationActions.navigateTo(Screen.EDIT_ACTIVITY) })
                      },
                      colors = buttonColors(containerColor = Color(LIGHT_BLUE)),
                      elevation =
                          ButtonDefaults.elevatedButtonElevation(
                              defaultElevation = BUTTON_ELEVATION_DEFAULT.dp),
                      modifier = Modifier.testTag("editButton")) {
                        Text(
                            text = "Edit",
                            color = Color.Black,
                        )
                      }
                }
              } else if (activity?.status == ActivityStatus.FINISHED) {
                Text(text = "Activity is not active", modifier = Modifier.testTag("notActiveText"))
              } else {
                Text(text = "Require Log", modifier = Modifier.testTag("notLoggedInText"))
                ElevatedButton(
                    onClick = { navigationActions.navigateTo(Screen.AUTH) },
                    colors = buttonColors(containerColor = Color(LIGHT_BLUE)),
                    elevation =
                        ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = BUTTON_ELEVATION_DEFAULT.dp),
                    modifier = Modifier.testTag("loginButton")) {
                      Text(
                          text = "Login/Register",
                          color = Color.Black,
                      )
                    }
              }
            }
      }) { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .testTag("activityDetailsScreen")) {
              // Image section
              LaunchedEffect(activity!!.uid) {
                imageViewModel.fetchActivityImagesAsBitmaps(
                    activity.uid,
                    onSuccess = { urls -> bitmaps = urls },
                    onFailure = { Log.e("ActivityDetailsScreen", it.message.toString()) })
              }
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .aspectRatio(IMAGE_WIDTH_RATIO / IMAGE_HEIGHT_RATIO)
                          .padding(vertical = MEDIUM_PADDING.dp)
                          .testTag("image")) {
                    CarouselNoModif(itemsList = bitmaps, category = activity.category)
                  }

              // Title
              Column(
                  modifier =
                      Modifier.padding(horizontal = EXTRA_LARGE_PADDING.dp)
                          .fillMaxWidth()
                          .testTag("title")) {
                    Text(
                        text = activityTitle ?: "title not specified",
                        modifier =
                            Modifier.align(Alignment.CenterHorizontally).testTag("titleText"),
                        style = MaterialTheme.typography.headlineMedium)
                  }

              Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

              // Tab bar
              Row(
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  verticalAlignment = Alignment.Top,
                  modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { listActivityViewModel.updateDetailsType(ACTIVITY_DETAILS) },
                        modifier =
                            Modifier.width(ICON_BUTTON_SIZE.dp)
                                .let {
                                  if (detailsType == ACTIVITY_DETAILS) {
                                    it.background(Color.LightGray, shape = CircleShape)
                                  } else {
                                    it
                                  }
                                }
                                .testTag("ACTIVITY_DETAILS button")) {
                          Icon(Icons.Outlined.Search, contentDescription = "ACTIVITY_DETAILS icon")
                        }

                    IconButton(
                        onClick = { listActivityViewModel.updateDetailsType(ATTENDANT_DETAILS) },
                        modifier =
                            Modifier.width(ICON_BUTTON_SIZE.dp)
                                .let {
                                  if (detailsType == ATTENDANT_DETAILS)
                                      it.background(Color.LightGray, shape = CircleShape)
                                  else it
                                }
                                .testTag("ATTENDANT_DETAILS button")) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Groups,
                                contentDescription = "ATTENDANT_DETAILS")
                            Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
                            Text(
                                text = "${activity?.participants?.size}/${maxPlaces ?: 0}",
                                style = MaterialTheme.typography.bodyMedium)
                          }
                        }
                    IconButton(
                        onClick = { listActivityViewModel.updateDetailsType(ACTIVITY_COMMENTS) },
                        modifier =
                            Modifier.width(ICON_BUTTON_SIZE.dp)
                                .let {
                                  if (detailsType == ACTIVITY_COMMENTS) {
                                    it.background(Color.LightGray, shape = CircleShape)
                                  } else {
                                    it
                                  }
                                }
                                .testTag("ACTIVITY_COMMENTS button")) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.ModeComment,
                                contentDescription = "ACTIVITY_COMMENTS")
                            Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
                            Text(
                                text = "${activity?.comments?.size ?: 0}",
                                style = MaterialTheme.typography.bodyMedium)
                          }
                        }
                  }
              when (detailsType) {
                ACTIVITY_DETAILS -> {
                  // Description
                  Column(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(STANDARD_PADDING.dp)
                              .wrapContentHeight()
                              .testTag("description")) {
                        Text(
                            text = description ?: "description not specified",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier =
                                Modifier.padding(horizontal = STANDARD_PADDING.dp)
                                    .testTag("descriptionText"))
                      }
                  // Horizontal line
                  Divider(
                      color = Color.LightGray, // Set divider color
                      thickness = 3.dp, // Set thickness of the line
                      modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))

                  // price

                  PaymentInfoScreen(price ?: 0.0)

                  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

                  // Due Time
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier =
                          Modifier.padding(horizontal = MEDIUM_PADDING.dp).testTag("schedule")) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Schedule",
                            tint = Color(LIGHT_BLUE))
                        Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                        Text(
                            text =
                                " ${
                              dueDate?.toDate().toString().take(11)
                          }th" +
                                    "${(dueDate?.toDate()?.year)?.plus(1900)}, " +
                                    "$startTime to ${
                                      hourDateViewModel.addDurationToTime(
                                          startTime.toString(),
                                          duration.toString()
                                      )
                                  }",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.testTag("scheduleText"))
                      }

                  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

                  // location
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier =
                          Modifier.wrapContentHeight()
                              .padding(horizontal = MEDIUM_PADDING.dp)
                              .testTag("location")) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(LIGHT_BLUE))
                        Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                        Text(
                            text = location?.shortName ?: "No location",
                            modifier =
                                Modifier.weight(
                                        TEXT_WEIGHT) // Dynamically takes remaining space while
                                    // respecting constraints
                                    .padding(
                                        end =
                                            SMALL_PADDING
                                                .dp) // Add space between the text and button
                                    .testTag("locationText"))
                        Spacer(modifier = Modifier.weight(WIDTH_FRACTION_MD))
                        // Info Icon with Click
                        ElevatedButton(
                            modifier = Modifier.testTag("activityToMapText"),
                            onClick = { navigationActions.navigateTo(Screen.MAP) },
                            colors = buttonColors(containerColor = Color(LIGHT_BLUE)),
                            elevation =
                                ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = BUTTON_ELEVATION_DEFAULT.dp),
                            content = {
                              Text(
                                  text = "See on map",
                                  style = MaterialTheme.typography.bodyMedium,
                                  color = Color.Black,
                                  modifier = Modifier.padding(end = SMALL_PADDING.dp))
                            })
                      }

                  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

                  Divider(
                      color = Color.LightGray, // Set divider color
                      thickness = 3.dp, // Set thickness of the line
                      modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))

                  CreatorRow(
                      creator,
                      profile,
                      nbActivitiesCreated,
                      imageViewModel,
                      listActivityViewModel,
                      navigationActions)
                }
                ATTENDANT_DETAILS -> {
                  // Participants section
                  Text(
                      text = "Participants",
                      style =
                          TextStyle(
                              fontSize = SUBTITLE_FONTSIZE.sp,
                              fontWeight = FontWeight(LARGE_FONT_WEIGHT),
                              color = Color(DARK_GRAY),
                              textAlign = TextAlign.Center,
                          ),
                      modifier =
                          Modifier.padding(bottom = MEDIUM_PADDING.dp).testTag("participantsTitle"))

                  // List of participants
                  Column(modifier = Modifier.fillMaxWidth().testTag("participants")) {
                    participantsList?.forEach { participant ->
                      Row(
                          verticalAlignment = Alignment.CenterVertically,
                          modifier = Modifier.padding(vertical = SMALL_PADDING.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier.padding(vertical = SMALL_PADDING.dp)
                                        .testTag("${participant.name} ${participant.surname} row")
                                        .clickable {
                                          if (participant.id == "") {
                                            Toast.makeText(
                                                    context,
                                                    "This user is not registered",
                                                    Toast.LENGTH_SHORT)
                                                .show()
                                          } else {
                                            if (participant.id == profile?.id) {
                                              navigationActions.navigateTo(Screen.PROFILE)
                                            } else {
                                              listActivityViewModel.selectUser(participant)
                                              navigationActions.navigateTo(
                                                  Screen.PARTICIPANT_PROFILE)
                                            }
                                          }
                                        }) {

                                  // Profile Picture
                                  ProfileImage(
                                      userId = participant.id,
                                      modifier =
                                          Modifier.size(BUTTON_HEIGHT_MD.dp).clip(CircleShape),
                                      imageViewModel = imageViewModel)

                                  Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))

                                  // Participant name
                                  Column(
                                      modifier = Modifier.testTag("participantColumn"),
                                      verticalArrangement = Arrangement.Center) {
                                        Text(
                                            text =
                                                "${participant.name} ${participant.surname}", // Display the participant's name
                                            style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            text =
                                                activitiesList.let { activities ->
                                                  val participantActivities =
                                                      activities.filter { activity ->
                                                        activity.participants
                                                            .map { it.id }
                                                            .contains(
                                                                participant
                                                                    .id) // Check if the participant
                                                        // is in the participants
                                                        // list
                                                      }
                                                  "joined ${participantActivities.size} Activities"
                                                },
                                            style =
                                                TextStyle(
                                                    fontSize = MEDIUM_PADDING.sp,
                                                    fontWeight = FontWeight(MEDIUM_FONT_WEIGHT),
                                                    color = Color(DARK_GRAY),
                                                    textAlign = TextAlign.Center,
                                                ),
                                            modifier = Modifier.testTag("activityCount"))
                                      }
                                }
                            Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .padding(
                                            vertical = SMALL_PADDING.dp,
                                            horizontal = MEDIUM_PADDING.dp)
                                        .testTag(
                                            "participantsRating ${participant.name} ${participant.surname}")) {
                                  val isPassed =
                                      hourDateViewModel.combineDateAndTime(
                                          activity.date, activity.startTime) <= Timestamp.now()
                                  val isCreator = profile?.id == activity.creator
                                  if (isPassed && isCreator && participant.id != activity.creator) {
                                    ReviewActivityButtons(
                                        currentReview = participant.likes[activity.uid]) {
                                          profileViewModel.addReview(
                                              participant.id, activity.uid, it)
                                        }
                                  } else {

                                    if (participant.getUserRatingAsAParticipant() >= 0) {
                                      Text(
                                          text = "Rating : ",
                                          fontWeight = FontWeight.Bold,
                                          style = MaterialTheme.typography.bodyMedium)
                                      Text(
                                          text =
                                              String.format(
                                                  "%.1f",
                                                  participant.getUserRatingAsAParticipant() * 5),
                                          style = MaterialTheme.typography.bodyMedium)
                                    }
                                  }
                                }
                          }
                    }
                    Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
                    if ((activity.participants.size) < maxPlaces!!) {
                      Text(
                          text = "${maxPlaces!! - (activity.participants.size )} places left",
                          style =
                              TextStyle(
                                  fontSize = MEDIUM_PADDING.sp,
                                  fontWeight = FontWeight(MEDIUM_FONT_WEIGHT),
                                  color = Color(DARK_GRAY),
                                  textAlign = TextAlign.Center,
                              ),
                          modifier =
                              Modifier.align(Alignment.CenterHorizontally).testTag("placesLeft"))
                    }
                  }
                }
                ACTIVITY_COMMENTS -> {

                  Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

                  // Comment section
                  CommentSection(
                      profileId = profile?.id ?: "anonymous",
                      comments = comments,
                      onAddComment = { content ->
                        val newComment =
                            Comment(
                                uid = UUID.randomUUID().toString(),
                                userId = profile?.id ?: "anonymous",
                                userName = profile?.name ?: "anonymous",
                                content = content,
                                timestamp = Timestamp.now())
                        // listActivityViewModel.addCommentToActivity(activity!!.uid, newComment)
                        comments += newComment
                        listActivityViewModel.updateActivity(activity.copy(comments = comments))
                      },
                      onReplyComment = { replyContent, comment ->
                        val reply =
                            Comment(
                                uid = UUID.randomUUID().toString(),
                                userId = profile?.id ?: "anonymous",
                                userName = profile?.name ?: "anonymous",
                                content = replyContent,
                                timestamp = Timestamp.now())
                        // listActivityViewModel.addReplyToComment(activity!!.uid, comment.uid,
                        // reply)
                        comment.replies += reply
                        comments = comments.map { if (it.uid == comment.uid) comment else it }
                        listActivityViewModel.updateActivity(activity!!.copy(comments = comments))
                      },
                      onDeleteComment = deleteComment,
                      creatorId = activity?.creator ?: "anonymous",
                      imageViewModel = imageViewModel)
                }
              }
            }
      }
}

@Composable
fun CommentSection(
    profileId: String,
    comments: List<Comment>,
    onAddComment: (String) -> Unit,
    onReplyComment: (String, Comment) -> Unit,
    onDeleteComment: (Comment) -> Unit,
    creatorId: String,
    imageViewModel: ImageViewModel
) {
  val newCommentText = remember { mutableStateOf("") }
  val context = LocalContext.current
  Column(modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp)) {
    Text(text = "Comments", style = MaterialTheme.typography.headlineSmall)
    if (profileId == "anonymous") {
      // Message for users who are not logged in
      Text(
          text = "Require Log",
          modifier = Modifier.padding(SMALL_PADDING.dp).testTag("notLoggedInMessage"))
    } else {
      // Input field for new comments if the user is logged in
      TextFieldWithErrorState(
          value = newCommentText.value,
          onValueChange = { newCommentText.value = it },
          label = "Add Comment",
          modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth(),
          validation = { comment ->
            when {
              comment.isEmpty() -> context.getString(R.string.comment_empty)
              else -> null
            }
          },
          testTag = "inputComment",
          errorTestTag = "commentErrorText")

      ElevatedButton(
          enabled = newCommentText.value.isNotEmpty(),
          onClick = {
            performOfflineAwareAction(
                context = context,
                networkManager = NetworkManager(context),
                onPerform = {
                  onAddComment(newCommentText.value)
                  newCommentText.value = ""
                })
          },
          colors = buttonColors(containerColor = Color(LIGHT_BLUE)),
          elevation =
              ButtonDefaults.elevatedButtonElevation(
                  defaultElevation = BUTTON_ELEVATION_DEFAULT.dp),
          modifier = Modifier.padding(top = STANDARD_PADDING.dp).testTag("PostCommentButton")) {
            Text("Post Comment", color = Color.Black)
          }
    }

    // Display all comments
    comments.forEach { comment ->
      CommentItem(
          profileId,
          comment,
          creatorId,
          onReplyComment,
          onDeleteComment,
          allowReplies = true,
          imageViewModel) // Set allowReplies to true for top-level comments
    }
  }
}
/**
 * CommentSection Composable: Displays a comment section with the ability to add, reply to, and
 * delete comments.
 *
 * @param profileId The ID of the current user's profile.
 * @param comments A list of comments to display.
 * @param onAddComment Callback function to handle adding a new comment.
 * @param onReplyComment Callback function to handle replying to a comment.
 * @param onDeleteComment Callback function to handle deleting a comment.
 * @param creatorId The ID of the activity creator, to distinguish creator's comments.
 * @param imageViewModel ViewModel for managing user profile images.
 */
@Composable
fun CommentItem(
    profileId: String,
    comment: Comment,
    creatorId: String, // Pass the creator ID as a parameter
    onReplyComment: (String, Comment) -> Unit,
    onDeleteComment: (Comment) -> Unit,
    allowReplies: Boolean = true,
    imageViewModel: ImageViewModel
) {
  var showReplyField by remember { mutableStateOf(false) }
  var replyText by remember { mutableStateOf("") }
  val context = LocalContext.current
  val networkManager = NetworkManager(context)

  Column(modifier = Modifier.padding(STANDARD_PADDING.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Profile Picture
      ProfileImage(
          userId = profileId,
          modifier = Modifier.size(BUTTON_HEIGHT_MD.dp).clip(CircleShape),
          imageViewModel = imageViewModel)
      // If the user is the creator, display a badge
      Column {
        // Display the user's name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp),
            modifier = Modifier.padding(horizontal = STANDARD_PADDING.dp)) {
              Text(
                  text = comment.userName,
                  style =
                      TextStyle(
                          fontSize = SUBTITLE_FONTSIZE.sp,
                          fontWeight = FontWeight(LARGE_FONT_WEIGHT),
                          color = Color(DARK_GRAY),
                          textAlign = TextAlign.Center,
                      ),
                  modifier = Modifier.testTag("commentUserName_${comment.uid}"))
              // Display the timestamp
              CommentTimestamp(comment)
              if (comment.userId == creatorId) {
                Box(
                    modifier =
                        Modifier.padding(end = SMALL_PADDING.dp)
                            .background(
                                color = Color(DARK_YELLOW),
                                shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp))
                            .padding(
                                horizontal = STANDARD_PADDING.dp, vertical = SMALL_PADDING.dp)) {
                      Text(
                          text = "Creator",
                          style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                          modifier = Modifier.testTag("creatorBadge_${comment.uid}"))
                    }
              }
            }

        // Display the comment content
        Text(
            text = comment.content,
            style =
                TextStyle(
                    fontSize = SUBTITLE_FONTSIZE.sp,
                    fontWeight = FontWeight(MEDIUM_FONT_WEIGHT),
                    color = Color(DARK_GRAY),
                    textAlign = TextAlign.Center,
                ),
            modifier =
                Modifier.padding(horizontal = STANDARD_PADDING.dp)
                    .testTag("commentContent_${comment.uid}"))
      }
    }

    Column {
      if (comment.userId == profileId) {
        if (profileId != "anonymous") {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp),
              modifier = Modifier.padding(horizontal = STANDARD_PADDING.dp)) {
                TextButton(
                    onClick = {
                      performOfflineAwareAction(
                          context = context,
                          networkManager = networkManager,
                          onPerform = { onDeleteComment(comment) })
                    },
                    modifier =
                        Modifier.padding(end = STANDARD_PADDING.dp)
                            .testTag("DeleteButton_${comment.uid}")) {
                      Text(
                          "Delete",
                          color = Color(DARK_GRAY),
                          style =
                              TextStyle(
                                  fontSize = SUBTITLE_FONTSIZE.sp,
                                  fontWeight = FontWeight(SMALL_FONT_WEIGHT),
                                  color = Color(DARK_GRAY),
                              ),
                      )
                    }
                if (allowReplies) {
                  // Toggle button to show/hide the reply input field
                  TextButton(
                      onClick = { showReplyField = !showReplyField },
                      modifier =
                          Modifier.testTag(
                              "${if (showReplyField) "Cancel" else "Reply"}Button_${comment.uid}")) {
                        Text(
                            if (showReplyField) "Cancel" else "Reply",
                            color = Color(DARK_GRAY),
                            style =
                                TextStyle(
                                    fontSize = SUBTITLE_FONTSIZE.sp,
                                    fontWeight = FontWeight(SMALL_FONT_WEIGHT),
                                    color = Color(DARK_GRAY),
                                ),
                        )
                      }
                }
              }
        }
      }

      // Conditionally show the reply input field if the user is logged in
      if (profileId != "anonymous") {
        if (showReplyField) {
          TextFieldWithErrorState(
              value = replyText,
              onValueChange = { replyText = it },
              label = "Add Comment",
              modifier = Modifier.padding(STANDARD_PADDING.dp).fillMaxWidth(),
              validation = { comment ->
                when {
                  comment.isEmpty() -> context.getString(R.string.comment_empty)
                  else -> null
                }
              },
              testTag = "replyInputField_${comment.uid}",
              errorTestTag = "replyErrorText")

          ElevatedButton(
              enabled = replyText.isNotEmpty(),
              onClick = {
                performOfflineAwareAction(
                    context = context,
                    networkManager = networkManager,
                    onPerform = {
                      onReplyComment(replyText, comment)
                      replyText = ""
                      showReplyField = false
                    })
              },
              colors = buttonColors(containerColor = Color(LIGHT_BLUE)),
              elevation =
                  ButtonDefaults.elevatedButtonElevation(
                      defaultElevation = BUTTON_ELEVATION_DEFAULT.dp),
              modifier =
                  Modifier.padding(top = SMALL_PADDING.dp)
                      .testTag("postReplyButton_${comment.uid}")) {
                Text("Post Reply", color = Color.Black)
              }
        }
      }
    }

    // Show replies for original comments, but do not allow replies on replies
    comment.replies.forEach { reply ->
      Box(modifier = Modifier.padding(start = MEDIUM_PADDING.dp)) {
        // Pass `allowReplies = false` for replies to prevent nesting
        CommentItem(
            profileId,
            reply,
            creatorId,
            onReplyComment,
            onDeleteComment,
            allowReplies = false,
            imageViewModel)
      }
    }
  }
}
/**
 * PaymentInfoScreen Composable: Displays the payment details of an activity and provides additional
 * information via a dialog.
 *
 * @param price The price of the activity, in CHF.
 */
@Composable
fun PaymentInfoScreen(price: Double) {
  var showDialog by remember { mutableStateOf(false) }

  // Payment Section
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier = Modifier.padding(horizontal = MEDIUM_PADDING.dp).testTag("price")) {
        // Payment Text

        Icon(Icons.Filled.AttachMoney, contentDescription = "Price", tint = Color(LIGHT_BLUE))
        Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
        Text(
            text = if (price != null) "${price.toString()} CHF" else "not defined yet",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("priceText"))

        Spacer(modifier = Modifier.weight(WIDTH_FRACTION_MD))
        // Info Icon with Click
        ElevatedButton(
            modifier = Modifier.testTag("infoIconButton"),
            onClick = { showDialog = true },
            colors = buttonColors(containerColor = Color(LIGHT_BLUE)),
            elevation =
                ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = BUTTON_ELEVATION_DEFAULT.dp),
            content = {
              Text(
                  text = "Payment info",
                  style = MaterialTheme.typography.bodyMedium,
                  color = Color.Black,
                  modifier = Modifier.padding(end = SMALL_PADDING.dp).testTag("paymentInfo"))
            })

        // Info Dialog
        if (showDialog) {
          AlertDialog(
              modifier = Modifier.testTag("paymentInfoDialog"),
              onDismissRequest = { showDialog = false },
              confirmButton = {
                TextButton(
                    modifier = Modifier.testTag("okButton"), onClick = { showDialog = false }) {
                      Text(text = stringResource(id = R.string.ok))
                    }
              },
              title = {
                Text(
                    modifier = Modifier.testTag("paymentInfoTitle"),
                    text = stringResource(id = R.string.payment_info))
              },
              text = {
                if (price != 0.0) {
                  Text(
                      modifier = Modifier.testTag("paymentInfoText"),
                      text = stringResource(id = R.string.payment_explanation))
                } else {
                  Text(
                      modifier = Modifier.testTag("freeInfoText"),
                      text = stringResource(id = R.string.free_activity))
                }
              },
          )
        }
      }
}
/**
 * LikeButton Composable: A button that allows users to like or unlike an activity.
 *
 * @param profile The current user's profile, which contains their liked activities.
 * @param activity The activity to be liked or unliked.
 * @param profileViewModel The ViewModel managing the user's profile state and actions.
 */
@Composable
fun LikeButton(profile: User?, activity: Activity?, profileViewModel: ProfileViewModel) {
  var isLiked by remember {
    mutableStateOf(activity?.let { profile?.likedActivities?.contains(it.uid) } ?: false)
  }
  val context = LocalContext.current
  val networkManager = NetworkManager(context)

  if (profile != null) {
    IconButton(
        modifier = Modifier.testTag("likeButton$isLiked"),
        onClick = {
          performOfflineAwareAction(
              context = context,
              networkManager = networkManager,
              onPerform = {
                isLiked = !isLiked
                if (isLiked) {
                  if (activity != null) {
                    profileViewModel.addLikedActivity(profile.id, activity.uid)
                  }
                } else {
                  if (activity != null) {
                    profileViewModel.removeLikedActivity(profile.id, activity.uid)
                  }
                }
              })
        },
    ) {
      Icon(
          imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
          contentDescription = if (isLiked) "Liked" else "Not Liked",
          tint = Color(LIGHT_BLUE))
    }
  }
}
/**
 * CreatorRow Composable: Displays information about the activity creator, including their profile
 * picture, name, number of activities created, and a navigation button to view their profile.
 *
 * @param creator The creator of the activity, represented as a User object.
 * @param profile The current user's profile.
 * @param nbActivitiesCreated The number of activities created by the creator.
 * @param imageViewModel The ViewModel managing user profile images.
 * @param listActivityViewModel The ViewModel managing activity-related state and logic.
 * @param navigationActions The navigation actions for transitioning between screens.
 */
@Composable
fun CreatorRow(
    creator: User,
    profile: User?,
    nbActivitiesCreated: Int,
    imageViewModel: ImageViewModel,
    listActivityViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions
) {
  val context = LocalContext.current
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(SMALL_PADDING.dp).testTag("creatorRow")) {
        ProfileImage(
            userId = creator.id,
            modifier = Modifier.size(BUTTON_HEIGHT_LG.dp).clip(CircleShape),
            imageViewModel = imageViewModel)

        Column(
            modifier = Modifier.testTag("creatorColumn"),
            verticalArrangement = Arrangement.Center) {
              ElevatedButton(
                  modifier = Modifier.height(SMALL_BUTTON_HEIGHT.dp).width(SMALL_BUTTON_WIDTH.dp),
                  onClick = {
                    if (creator.id == "") {
                      Toast.makeText(context, "This user is not registered", Toast.LENGTH_SHORT)
                          .show()
                    } else {
                      if (creator.id == profile?.id) {
                        navigationActions.navigateTo(Screen.PROFILE)
                      } else {
                        listActivityViewModel.selectUser(creator)
                        navigationActions.navigateTo(Screen.PARTICIPANT_PROFILE)
                      }
                    }
                  },
                  colors = buttonColors(containerColor = Color(DARK_YELLOW)),
                  elevation =
                      ButtonDefaults.elevatedButtonElevation(
                          defaultElevation = BUTTON_ELEVATION_DEFAULT.dp),
                  content = {
                    Text(
                        text = "Creator",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(end = SMALL_PADDING.dp))
                  })
              Text(
                  text = "${creator.name} ${creator.surname}",
                  style =
                      TextStyle(
                          fontSize = SUBTITLE_FONTSIZE.sp,
                          fontWeight = FontWeight(LARGE_FONT_WEIGHT),
                          color = Color(DARK_GRAY),
                          textAlign = TextAlign.Center,
                      ),
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.testTag("creatorName"))
              Text(
                  text = "Created $nbActivitiesCreated Activities",
                  style =
                      TextStyle(
                          fontSize = MEDIUM_PADDING.sp,
                          fontWeight = FontWeight(MEDIUM_FONT_WEIGHT),
                          color = Color(DARK_GRAY),
                          textAlign = TextAlign.Center,
                      ),
                  modifier = Modifier.testTag("activityCount"))
            }
        Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
      }
}
/**
 * getRelativeTimeSpanString: Converts a `Timestamp` object into a human-readable relative time
 * string.
 *
 * This function calculates the difference between the current time and the provided timestamp and
 * formats it as a relative time span, such as "5 minutes ago" or "2 days ago".
 *
 * @param timestamp The `Timestamp` object representing the time to be converted.
 * @return A `String` representing the relative time span from the current time.
 */
fun getRelativeTimeSpanString(timestamp: Timestamp): String {
  val currentTime = System.currentTimeMillis()
  val commentTime = timestamp.toDate().time
  return DateUtils.getRelativeTimeSpanString(commentTime, currentTime, DateUtils.MINUTE_IN_MILLIS)
      .toString()
}
/**
 * CommentTimestamp: A composable function that displays the relative timestamp of a comment.
 *
 * This function uses the provided `Comment` object to extract its timestamp and converts it into a
 * human-readable relative time string (e.g., "5 minutes ago"). It then renders the timestamp using
 * a `Text` composable with specific styling.
 *
 * @param comment The `Comment` object whose timestamp will be displayed.
 */
@Composable
fun CommentTimestamp(comment: Comment) {
  Text(
      text = getRelativeTimeSpanString(comment.timestamp),
      style =
          TextStyle(
              fontSize = NORMAL_PADDING.sp,
              fontWeight = FontWeight(SMALL_FONT_WEIGHT),
              color = Color(DARK_GRAY),
          ),
      modifier = Modifier.testTag("commentTimestamp_${comment.uid}"))
}
