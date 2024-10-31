package com.android.sample.ui.activitydetails

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.Comment
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.UUID
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    listActivityViewModel: ListActivitiesViewModel =
        viewModel(factory = ListActivitiesViewModel.Factory),
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel
) {
  val activity = listActivityViewModel.selectedActivity.collectAsState().value
  val profile = profileViewModel.userState.collectAsState().value

  val activityTitle by remember { mutableStateOf(activity?.title) }
  val description by remember { mutableStateOf(activity?.description) }
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
  val context = LocalContext.current
    val testcomment = Comment(
        uid = "1",
        userId = "1",
        userName = "John",
        content = "This is a comment",
        timestamp = Timestamp.now()
    )
  var comments by remember { mutableStateOf(activity?.comments ?: listOf(testcomment)) }

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Title", color = Color.White)
              }
            },
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("activityDetailsScreen")) {
              // Image section
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .aspectRatio(16 / 9f)
                          .padding(16.dp)
                          .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                          .testTag("image")) {
                    // Optional: Add placeholder text in the center
                    Text(
                        text = "Activity Image",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center))
                  }
              Spacer(modifier = Modifier.height(16.dp))

              // Title
              Box(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("title"),
                  contentAlignment = Alignment.Center) {
                    Text(
                        text = activityTitle ?: "title not specified",
                        modifier = Modifier.testTag("titleText"),
                        style = MaterialTheme.typography.headlineMedium)
                  }

              Spacer(modifier = Modifier.height(8.dp))

              // Description
              Column(
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(150.dp)
                          .padding(8.dp)
                          .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                          .verticalScroll(rememberScrollState())
                          .testTag("description")) {
                    Text(
                        text = "Description:",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    Text(
                        text = description ?: "description not specified",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp).testTag("descriptionText"))
                  }

              Spacer(modifier = Modifier.height(8.dp))
              //  price
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.testTag("price")) {
                    Icon(Icons.Filled.AttachMoney, contentDescription = "Price")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (price != null) "${price.toString()} CHF" else "not defined yet",
                        modifier = Modifier.testTag("priceText"))
                  }
              Spacer(modifier = Modifier.height(8.dp))
              // schedule
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.testTag("schedule")) {
                    Icon(Icons.Default.DateRange, contentDescription = "Schedule")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dueDate ?: "not defined yet",
                        modifier = Modifier.testTag("scheduleText"))
                  }

              Spacer(modifier = Modifier.height(32.dp))

              // Enroll button
              if (activity?.status == ActivityStatus.ACTIVE && profile != null) {
                Button(
                    onClick = {
                      if (((placesTaken ?: 0) >= 0) && ((placesTaken ?: 0) < (maxPlaces ?: 0))) {
                        val theActivity =
                            activity.copy(placesLeft = min((placesTaken ?: 0) + 1, maxPlaces ?: 0))
                        listActivityViewModel.updateActivity(theActivity)
                        profileViewModel.addActivity(profile.id, theActivity.uid)
                        Toast.makeText(context, "Enroll Successful", Toast.LENGTH_SHORT).show()
                        navigationActions.navigateTo(Screen.OVERVIEW)
                      } else {
                        Toast.makeText(
                                context,
                                "Enroll failed, limit of places reached",
                                Toast.LENGTH_SHORT)
                            .show()
                      }
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .testTag("enrollButton")) {
                      Text(text = "Enroll")
                    }
              }
            CommentSection(
                comments = comments,
                onAddComment = { content ->
                    val newComment = Comment(
                        uid = UUID.randomUUID().toString(),
                        userId = profile?.id ?: "anonymous",
                        userName = profile?.name ?: "anonymous",
                        content = content,
                        timestamp = Timestamp.now()
                    )
                    // listActivityViewModel.addCommentToActivity(activity!!.uid, newComment)
                    comments += newComment
                    listActivityViewModel.updateActivity(activity!!.copy(comments = comments))
                },
                onReplyComment = { replyContent, comment ->
                    val reply = Comment(
                        uid = UUID.randomUUID().toString(),
                        userId = profile?.id ?: "anonymous",
                        userName = profile?.name ?: "anonymous",
                        content = replyContent,
                        timestamp = Timestamp.now()
                    )
                    //listActivityViewModel.addReplyToComment(activity!!.uid, comment.uid, reply)
                    comment.replies += reply
                    comments = comments.map { if (it.uid == comment.uid) comment else it }
                    listActivityViewModel.updateActivity(activity!!.copy(comments = comments))
                }
            )
            }
      }
}

@Composable
fun CommentSection(
    comments: List<Comment>,
    onAddComment: (String) -> Unit,
    onReplyComment: (String, Comment) -> Unit
) {
    val newCommentText = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = "Comments", style = MaterialTheme.typography.headlineSmall)

        comments.forEach { comment ->
            CommentItem(comment, onReplyComment)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input field for new comments
        OutlinedTextField(
            value = newCommentText.value,
            onValueChange = { newCommentText.value = it },
            label = { Text("Add a comment") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onAddComment(newCommentText.value)
                newCommentText.value = ""
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Post Comment")
        }
    }
}

@Composable
fun CommentItem(comment: Comment, onReplyComment: (String, Comment) -> Unit) {
    var showReplyField by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "${comment.userName}: ${comment.content}", style = MaterialTheme.typography.bodyMedium)
        Text(text = comment.timestamp.toDate().toString(), style = MaterialTheme.typography.bodySmall)

        // Toggle button to show/hide the reply input field
        Button(
            onClick = { showReplyField = !showReplyField },
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(if (showReplyField) "Cancel" else "Reply")
        }

        // Conditionally show the reply input field
        if (showReplyField) {
            OutlinedTextField(
                value = replyText,
                onValueChange = { replyText = it },
                label = { Text("Reply") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onReplyComment(replyText, comment)
                    replyText = ""
                    showReplyField = false
                },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text("Post Reply")
            }
        }

        // Show replies indented
        comment.replies.forEach { reply ->
            Box(modifier = Modifier.padding(start = 16.dp)) {
                CommentItem(reply, onReplyComment)
            }
        }
    }
}

