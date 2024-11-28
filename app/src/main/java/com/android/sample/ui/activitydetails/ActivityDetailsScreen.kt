package com.android.sample.ui.activitydetails

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.Comment
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
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
) {
  val activity = listActivityViewModel.selectedActivity.collectAsState().value
  val profile = profileViewModel.userState.collectAsState().value
  // Check if the user is already enrolled in the activity
  val isUserEnrolled = profile?.activities?.contains(activity?.uid) ?: false

  val creatorID = activity?.creator ?: ""
  var creator by remember {
    mutableStateOf(User(creatorID, "anonymous", "anonymous", listOf(), listOf(), "", listOf()))
  }

  LaunchedEffect(activity) {
    profileViewModel.getUserData(creatorID) { user ->
      if (user != null) {
        creator = user
        Log.d("ActivityDetailsScreen", "Creator updated in callback: $creator")
      }
    }
  }
  val uiState by listActivityViewModel.uiState.collectAsState()
  val activitiesList = (uiState as ListActivitiesViewModel.ActivitiesUiState.Success).activities
  val nbActivitiesCreated = activitiesList.filter { it.creator == creator.id }.size
  Log.d("ActivityDetailsScreen", "Creator current state: $creator")
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
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .aspectRatio(MEDIUM_PADDING / 9f)
                          .padding(MEDIUM_PADDING.dp)
                          .background(Color.Gray, shape = RoundedCornerShape(STANDARD_PADDING.dp))
                          .testTag("image")) {
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
                  modifier = Modifier.testTag("price")) {
                    Icon(Icons.Filled.AttachMoney, contentDescription = "Price")
                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                    Text(
                        text = if (price != null) "${price.toString()} CHF" else "not defined yet",
                        modifier = Modifier.testTag("priceText"))
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
                          text = location?.name ?: "No location",
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
              // Creator's informations
              CreatorRow(creator, nbActivitiesCreated)
              // Participants section
              Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
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
                                if (participant.name != profile?.name) {
                                  navigationActions.navigateTo(Screen.PROFILE)
                                }
                              }) {
                        // Placeholder for participant picture
                        if (participant.name != profile?.name) {
                          Box(
                              modifier =
                                  Modifier.size(BUTTON_HEIGHT.dp)
                                      .background(
                                          Color.Gray,
                                          shape = RoundedCornerShape(STANDARD_PADDING.dp))
                                      .padding(STANDARD_PADDING.dp)) {
                                Image(
                                    painter =
                                        painterResource(id = R.drawable.default_profile_image),
                                    contentDescription = "Participant Image",
                                    modifier =
                                        Modifier.fillMaxSize()
                                            .clip(RoundedCornerShape(STANDARD_PADDING.dp)))
                              }
                        } else {
                          // Profile Picture
                          ProfileImage(
                              userId = participant.id,
                              modifier = Modifier.size(BUTTON_HEIGHT.dp).clip(CircleShape))
                        }
                        Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))

                        // Participant name
                        Text(
                            text = participant.name, // Display the participant's name
                            style = MaterialTheme.typography.bodyMedium)
                      }
                }
              }

              Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))

              // Enroll button
              if (activity?.status == ActivityStatus.ACTIVE && profile != null) {

                if (activity.creator != profile.id) {
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
                  onDeleteComment = deleteComment)
            }
      }
}

@Composable
fun CommentSection(
    profileId: String,
    comments: List<Comment>,
    onAddComment: (String) -> Unit,
    onReplyComment: (String, Comment) -> Unit,
    onDeleteComment: (Comment) -> Unit
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
          onReplyComment,
          onDeleteComment,
          allowReplies = true) // Set allowReplies to true for top-level comments
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
    onReplyComment: (String, Comment) -> Unit,
    onDeleteComment: (Comment) -> Unit,
    allowReplies: Boolean = true
) {
  var showReplyField by remember { mutableStateOf(false) }
  var replyText by remember { mutableStateOf("") }
  val context = LocalContext.current
  val networkManager = NetworkManager(context)

  Column(modifier = Modifier.padding(STANDARD_PADDING.dp)) {
    Text(
        text = "${comment.userName}: ${comment.content}",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag("commentUserNameAndContent_${comment.uid}"))
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
          CommentItem(profileId, reply, onReplyComment, onDeleteComment, allowReplies = false)
        }
      }
    }
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

@OptIn(ExperimentalLayoutApi::class) // Required for FlowRow
@Composable
fun CreatorRow(creator: User, nbActivitiesCreated: Int) {
  FlowRow { // manages return to the line when the space is not enough
    // Placeholder for creator name
    Text(
        text = "Creator : ",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = STANDARD_PADDING.dp))
    Text(
        text = creator.name,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = STANDARD_PADDING.dp))
    Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
    // Placeholder for creator's rating
    Text(
        text = "Rating :",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = STANDARD_PADDING.dp))
    Text(
        text = " Blank/10",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = STANDARD_PADDING.dp))
    Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
    // Placeholder for creator's number of activities created
    Text(
        text = "NB activities created : ",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = STANDARD_PADDING.dp))
    Text(
        text = "$nbActivitiesCreated",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = STANDARD_PADDING.dp))
  }
}
