package com.android.sample.ui.activitydetails

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT_SM
import com.android.sample.resources.C.Tag.CARD_ELEVATION_DEFAULT
import com.android.sample.resources.C.Tag.LARGE_FONTSIZE
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_FONTSIZE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_MD
import com.android.sample.ui.camera.CarouselNoModif
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.components.performOfflineAwareAction
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.UUID
import kotlin.math.min
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    listActivityViewModel: ListActivitiesViewModel,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    locationViewModel: LocationViewModel,
    imageViewModel: ImageViewModel
) {
  val activity = listActivityViewModel.selectedActivity.collectAsState().value
  val profile = profileViewModel.userState.collectAsState().value
  // Check if the user is already enrolled in the activity
  val isUserEnrolled = profile?.activities?.contains(activity?.uid) ?: false

  val activityTitle by remember { mutableStateOf(activity?.title) }
  val description by remember { mutableStateOf(activity?.description) }
  val location by remember { mutableStateOf(activity?.location) }
  val price by remember { mutableStateOf(activity?.price) }
  val dueDate by remember {
    mutableStateOf(
        activity?.date.let {
          val calendar = GregorianCalendar()
          if (activity != null) {
            calendar.time = activity.date.toDate()
          }
          return@let "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                    calendar.get(
                        Calendar.YEAR
                    )
                }"
        })
  }
  val placesTaken by remember { mutableStateOf(activity?.placesLeft) }
  val maxPlaces by remember { mutableStateOf(activity?.maxPlaces) }
  val distance = locationViewModel.getDistanceFromCurrentLocation(location)
  val context = LocalContext.current
  val networkManager = NetworkManager(context)
  val startTime by remember { mutableStateOf(activity?.startTime) }
  val duration by remember { mutableStateOf(activity?.duration) }
  var comments by remember { mutableStateOf(activity?.comments ?: listOf()) }

  var bitmaps by remember { mutableStateOf(listOf<Bitmap>()) }
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

  val creatorID = activity?.creator ?: ""
  var creator by remember {
    mutableStateOf(User(creatorID, "anonymous", "anonymous", listOf(), listOf(), "", listOf()))
  }
  activity?.participants?.firstOrNull { it.id == creatorID }?.let { creator = it }
  val uiState by listActivityViewModel.uiState.collectAsState()
  val activitiesList = (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
  val nbActivitiesCreated = activitiesList.filter { it.creator == creator.id }.size
  val hourDateViewModel = HourDateViewModel()

  Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Activity Details", color = Color.White) },
            modifier = Modifier.testTag("topAppBar"),
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("goBackButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back")
                  }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA), // Background color
                    titleContentColor = Color.White // Title text color
                    ))
      }) { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .padding(MEDIUM_PADDING.dp)
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
                          .aspectRatio(MEDIUM_PADDING / 9f)
                          .padding(MEDIUM_PADDING.dp)
                          .background(Color.Gray, shape = RoundedCornerShape(STANDARD_PADDING.dp))
                          .testTag("image")) {
                    CarouselNoModif(itemsList = bitmaps, category = activity.category)
                    LikeButton(profile, activity, profileViewModel)
                  }

              // Title
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(vertical = STANDARD_PADDING.dp)
                          .testTag("title"),
                  contentAlignment = Alignment.Center) {
                    Text(
                        text = activityTitle ?: "title not specified",
                        modifier = Modifier.testTag("titleText"),
                        style = MaterialTheme.typography.headlineMedium)
                  }

              Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

              // Description
              Column(
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(150.dp)
                          .padding(STANDARD_PADDING.dp)
                          .border(1.dp, Color.Gray, shape = RoundedCornerShape(STANDARD_PADDING.dp))
                          .verticalScroll(rememberScrollState())
                          .testTag("description")) {
                    Text(
                        text = "Description:",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier =
                            Modifier.padding(
                                horizontal = STANDARD_PADDING.dp, vertical = SMALL_PADDING.dp))
                    Text(
                        text = description ?: "description not specified",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier =
                            Modifier.padding(horizontal = STANDARD_PADDING.dp)
                                .testTag("descriptionText"))
                  }

              Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

              // price
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.testTag("price").fillMaxWidth()) {
                    Icon(Icons.Filled.AttachMoney, contentDescription = "Price")
                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                    Text(
                        text = if (price != null) "${price.toString()} CHF" else "not defined yet",
                        modifier = Modifier.testTag("priceText"))
                    Spacer(modifier = Modifier.weight(WIDTH_FRACTION_MD))
                    PaymentInfoScreen(price ?: 0.0)
                  }

              Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

              // location
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.testTag("location")) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location")
                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                    Column {
                      Text(
                          text = location?.shortName ?: "No location",
                          modifier = Modifier.testTag("locationText"))
                      if (distance != null) {
                        val distanceString =
                            "Distance : " +
                                if (distance < 1) {
                                  "${round(distance * 1000).toInt()}m"
                                } else {
                                  "${round(distance * 10) / 10}km"
                                }
                        Text(text = distanceString, modifier = Modifier.testTag("distanceText"))
                        Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
                        // text field button to navigate to the activity's location on the map
                        // screen
                        Text(
                            text = stringResource(id = R.string.button_to_map),
                            modifier =
                                Modifier.testTag("activityToMapText")
                                    .clickable(
                                        onClick = { navigationActions.navigateTo(Screen.MAP) }),
                            style = TextStyle(textDecoration = TextDecoration.Underline))
                      }
                    }
                  }

              Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

              // schedule
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.testTag("schedule")) {
                    Icon(Icons.Default.DateRange, contentDescription = "Schedule")
                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                    Text(
                        text = if (startTime != null) "$dueDate at $startTime" else dueDate,
                        modifier = Modifier.testTag("scheduleText"))
                  }

              Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
              // duration
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.testTag("duration")) {
                    Icon(Icons.Default.Timelapse, contentDescription = "duration")
                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                    Text(
                        text = "Event length: ${duration ?: "not defined yet"}",
                        modifier = Modifier.testTag("durationText"))
                  }
              Spacer(modifier = Modifier.height(LARGE_PADDING.dp))

              CreatorRow(creator, nbActivitiesCreated)
              // Participants section
              Text(
                  text = "Participants: (${activity?.participants?.size}/${maxPlaces ?: 0})",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.padding(bottom = STANDARD_PADDING.dp))

              Spacer(modifier = Modifier.height(LARGE_PADDING.dp))

              // List of participants
              Column(modifier = Modifier.testTag("participants")) {
                activity?.participants?.forEach { participant ->
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier =
                          Modifier.padding(vertical = SMALL_PADDING.dp)
                              .testTag(participant.name)
                              .clickable {
                                if (participant.id == profile?.id) {
                                  navigationActions.navigateTo(Screen.PROFILE)
                                } else {
                                  listActivityViewModel.selectUser(participant)
                                  navigationActions.navigateTo(Screen.PARTICIPANT_PROFILE)
                                }
                              }) {
                        // Placeholder for participant picture
                        if (participant.photo == null) {
                          Box(
                              modifier =
                                  Modifier.size(BUTTON_HEIGHT_SM.dp)
                                      .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                                      .padding(STANDARD_PADDING.dp)) {
                                Image(
                                    painter =
                                        painterResource(id = R.drawable.default_profile_image),
                                    contentDescription = "Participant Image",
                                    modifier =
                                        Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)))
                              }
                        } else {
                          // Profile Picture
                          ProfileImage(
                              userId = participant.id,
                              modifier = Modifier.size(BUTTON_HEIGHT_SM.dp).clip(CircleShape),
                              imageViewModel = imageViewModel)
                        }
                        Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))

                        // Participant name
                        Text(
                            text = participant.name, // Display the participant's name
                            style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(LARGE_PADDING.dp))
                        Text(
                            text = "Rating : ",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Blank/10", style = MaterialTheme.typography.bodyMedium)
                      }
                }
              }

              Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))

              // Enroll button
              if (activity?.status == ActivityStatus.ACTIVE && profile != null) {

                if (hourDateViewModel.combineDateAndTime(activity.date, activity.startTime) <=
                    Timestamp.now()) {
                  Text(
                      text = stringResource(R.string.activity_past),
                      modifier = Modifier.testTag("archivedActivity"))
                } else if (activity.creator != profile.id) {
                  Button(
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
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(horizontal = LARGE_PADDING.dp)
                              .testTag("enrollButton")) {
                        if (isUserEnrolled) Text(text = "Leave") else Text(text = "Enroll")
                      }
                } else {
                  // Creator of the activity
                  Button(
                      onClick = {
                        performOfflineAwareAction(
                            context = context,
                            networkManager = networkManager,
                            onPerform = { navigationActions.navigateTo(Screen.EDIT_ACTIVITY) })
                      },
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(horizontal = LARGE_PADDING.dp)
                              .testTag("editButton")) {
                        Text(text = "Edit")
                      }
                }
              } else if (activity?.status == ActivityStatus.FINISHED) {
                Text(text = "Activity is not active", modifier = Modifier.testTag("notActiveText"))
              } else {
                Text(
                    text = "You need to be logged in to enroll",
                    modifier = Modifier.testTag("notLoggedInText"))
                Button(
                    onClick = { navigationActions.navigateTo(Screen.AUTH) },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = LARGE_PADDING.dp)
                            .testTag("loginButton")) {
                      Text(text = "Login/Register")
                    }
              }

              Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

              // Export to calendar button
              if (activity != null) {
                ExportActivityToCalendarButton(
                    activity = activity, viewModel = listActivityViewModel, context = context)
              }

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
                    listActivityViewModel.updateActivity(activity!!.copy(comments = comments))
                  },
                  onReplyComment = { replyContent, comment ->
                    val reply =
                        Comment(
                            uid = UUID.randomUUID().toString(),
                            userId = profile?.id ?: "anonymous",
                            userName = profile?.name ?: "anonymous",
                            content = replyContent,
                            timestamp = Timestamp.now())
                    // listActivityViewModel.addReplyToComment(activity!!.uid, comment.uid, reply)
                    comment.replies += reply
                    comments = comments.map { if (it.uid == comment.uid) comment else it }
                    listActivityViewModel.updateActivity(activity!!.copy(comments = comments))
                  },
                  onDeleteComment = deleteComment,
                  creatorId = activity?.creator ?: "anonymous")
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
    creatorId: String
) {
  val newCommentText = remember { mutableStateOf("") }
  val context = LocalContext.current
  Column(modifier = Modifier.fillMaxWidth().padding(STANDARD_PADDING.dp)) {
    Text(text = "Comments", style = MaterialTheme.typography.headlineSmall)

    // Display all comments
    comments.forEach { comment ->
      CommentItem(
          profileId,
          comment,
          creatorId,
          onReplyComment,
          onDeleteComment,
          allowReplies = true,
      ) // Set allowReplies to true for top-level comments
    }

    Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))

    if (profileId == "anonymous") {
      // Message for users who are not logged in
      Text(
          text = "You need to be logged in to add or reply to comments.",
          modifier = Modifier.padding(SMALL_PADDING.dp).testTag("notLoggedInMessage"))
    } else {
      // Input field for new comments if the user is logged in
      OutlinedTextField(
          value = newCommentText.value,
          onValueChange = { newCommentText.value = it },
          label = { Text("Add a comment") },
          modifier = Modifier.fillMaxWidth().testTag("CommentInputField"))

      Button(
          onClick = {
            performOfflineAwareAction(
                context = context,
                networkManager = NetworkManager(context),
                onPerform = {
                  onAddComment(newCommentText.value)
                  newCommentText.value = ""
                })
          },
          modifier = Modifier.padding(top = STANDARD_PADDING.dp).testTag("PostCommentButton")) {
            Text("Post Comment")
          }
    }
  }
}

@Composable
fun CommentItem(
    profileId: String,
    comment: Comment,
    creatorId: String, // Pass the creator ID as a parameter
    onReplyComment: (String, Comment) -> Unit,
    onDeleteComment: (Comment) -> Unit,
    allowReplies: Boolean = true
) {
  var showReplyField by remember { mutableStateOf(false) }
  var replyText by remember { mutableStateOf("") }
  val context = LocalContext.current
  val networkManager = NetworkManager(context)

  Column(modifier = Modifier.padding(STANDARD_PADDING.dp)) {
    Row(
        verticalAlignment = Alignment.CenterVertically, // Align items vertically
        modifier = Modifier.padding(bottom = SMALL_PADDING.dp)) {
          // If the user is the creator, display a badge
          if (comment.userId == creatorId) {
            Box(
                modifier =
                    Modifier.padding(end = SMALL_PADDING.dp)
                        .background(color = Color.Gray, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)) {
                  Text(
                      text = "Creator",
                      style = MaterialTheme.typography.bodySmall.copy(color = Color.Yellow),
                      modifier = Modifier.testTag("creatorBadge_${comment.uid}"))
                }
          }
          // Display the user's name
          Text(
              text = "${comment.userName}:",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.testTag("commentUserName_${comment.uid}"))
        }

    // Display the comment content
    Text(
        text = comment.content,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag("commentContent_${comment.uid}"))

    // Display the timestamp
    Text(
        text = comment.timestamp.toDate().toString(),
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.testTag("commentTimestamp_${comment.uid}"))

    if (profileId != "anonymous") {
      Column {
        if (comment.userId == profileId) {
          Button(
              onClick = {
                performOfflineAwareAction(
                    context = context,
                    networkManager = networkManager,
                    onPerform = { onDeleteComment(comment) })
              },
              modifier =
                  Modifier.padding(top = SMALL_PADDING.dp, end = STANDARD_PADDING.dp)
                      .testTag("DeleteButton_${comment.uid}")) {
                Text("Delete")
              }
        }

        if (allowReplies) {
          // Toggle button to show/hide the reply input field
          Button(
              onClick = { showReplyField = !showReplyField },
              modifier =
                  Modifier.padding(top = SMALL_PADDING.dp)
                      .testTag(
                          "${if (showReplyField) "Cancel" else "Reply"}Button_${comment.uid}")) {
                Text(if (showReplyField) "Cancel" else "Reply")
              }
        }

        // Conditionally show the reply input field if the user is logged in
        if (showReplyField) {
          OutlinedTextField(
              value = replyText,
              onValueChange = { replyText = it },
              label = { Text("Reply") },
              modifier = Modifier.fillMaxWidth().testTag("replyInputField_${comment.uid}"))

          Button(
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
              modifier =
                  Modifier.padding(top = SMALL_PADDING.dp)
                      .testTag("postReplyButton_${comment.uid}")) {
                Text("Post Reply")
              }
        }
      }

      // Show replies for original comments, but do not allow replies on replies
      comment.replies.forEach { reply ->
        Box(modifier = Modifier.padding(start = MEDIUM_PADDING.dp)) {
          // Pass `allowReplies = false` for replies to prevent nesting
          CommentItem(
              profileId, reply, creatorId, onReplyComment, onDeleteComment, allowReplies = false)
        }
      }
    }
  }
}

@Composable
fun PaymentInfoScreen(price: Double) {
  var showDialog by remember { mutableStateOf(false) }

  // Payment Section
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier = Modifier.testTag("paymentSection")) {
        // Payment Text
        Text(
            text = "Payment info",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = SMALL_PADDING.dp).testTag("paymentInfo"))

        // Info Icon with Click
        IconButton(modifier = Modifier.testTag("infoIconButton"), onClick = { showDialog = true }) {
          Icon(
              painter = painterResource(id = android.R.drawable.ic_dialog_info),
              contentDescription = "Info",
              tint = Color.Gray)
        }
      }

  // Info Dialog
  if (showDialog) {
    AlertDialog(
        modifier = Modifier.testTag("paymentInfoDialog"),
        onDismissRequest = { showDialog = false },
        confirmButton = {
          TextButton(modifier = Modifier.testTag("okButton"), onClick = { showDialog = false }) {
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
          tint = if (isLiked) Color.Black else Color.LightGray)
    }
  }
}

@Composable
fun CreatorRow(creator: User, nbActivitiesCreated: Int) {
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = LARGE_PADDING.dp, vertical = MEDIUM_PADDING.dp)
              .testTag("creatorRow"),
      elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION_DEFAULT.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(MEDIUM_PADDING.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(
                  text = "${creator.name} ${creator.surname}",
                  style = MaterialTheme.typography.titleLarge,
                  color = MaterialTheme.colorScheme.onSurface,
                  fontSize = LARGE_FONTSIZE.sp,
                  modifier = Modifier.testTag("creatorName"))
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp),
                  modifier = Modifier.padding(all = SMALL_PADDING.dp)) {
                    Text(
                        text = "Blank",
                        modifier =
                            Modifier.align(Alignment.CenterVertically).testTag("creatorRating"),
                        color = Color.Black,
                        fontSize = MEDIUM_FONTSIZE.sp)
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Full Star",
                        tint = Color.Black,
                        modifier = Modifier.size(MEDIUM_FONTSIZE.dp).testTag("ratingStar"))
                    Spacer(modifier = Modifier.padding(STANDARD_PADDING.dp))
                    Text(
                        text = "$nbActivitiesCreated Activities Created",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = MEDIUM_FONTSIZE.sp,
                        modifier = Modifier.testTag("activityCount"))
                  }
            }
      }
}
