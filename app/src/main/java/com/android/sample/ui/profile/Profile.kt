package com.android.sample.ui.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.android.sample.model.User
import com.android.sample.model.UserProfileViewModel

@Composable
fun ProfileScreen(userProfileViewModel: UserProfileViewModel) {

  userProfileViewModel.userState.let {
    it.value?.let { it1 -> Log.e("not an error", "name" + it1.name) }
  }
  val profileState = userProfileViewModel.userState.collectAsState()

  Log.e("not an error ", "interests are " + profileState.value?.interests.toString())
  when (val profile = profileState.value) {
    null -> LoadingScreen() // Show a loading indicator or a retry button
    else -> ProfileContent(user = profile) // Proceed with showing profile content
  }
}

@Composable
fun LoadingScreen() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text("Loading profile...", color = Color.Gray)
  }
}

@Composable
fun ProfileContent(user: User) {
  Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    Column(
        Modifier.fillMaxSize().padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(Modifier.height(16.dp))
          Text(text = "Profile", fontSize = 30.sp, modifier = Modifier.padding(top = 16.dp))

          // Profile Picture

          ProfileImage(url = user.photo, modifier = Modifier.size(100.dp).clip(CircleShape))

          // User Name and Surname
          Text(
              text = "${user.name} ${user.surname}",
              fontSize = 20.sp,
              modifier = Modifier.padding(top = 8.dp))

          // Interests
          Text(
              text = "Interests: ${user.interests?.joinToString(", ")}",
              fontSize = 18.sp,
              modifier = Modifier.padding(top = 8.dp))

          Spacer(modifier = Modifier.height(16.dp))

          // Activities Section
          Text(
              text = "Activities Created",
              fontSize = 24.sp,
              modifier = Modifier.padding(start = 16.dp, top = 16.dp))

          LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            user.activities?.let { activities ->
              items(activities.size) { index -> ActivityBox(activity = activities[index]) }
            }
          }
        }
  }
}

@Composable
fun ActivityBox(activity: String) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 8.dp)
              .height(60.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.LightGray), // Box background color
      contentAlignment = Alignment.Center) {
        Text(text = "Activity $activity.name", fontSize = 18.sp)
      }
}

@Composable
fun ProfileImage(url: String?, modifier: Modifier = Modifier) {
  val painter =
      rememberImagePainter(
          data = url, // URL of the image
          builder = {
            crossfade(true) // Optional: enable crossfade animation
            // Optional: placeholder image
            // Optional: error image if the URL load fails
          })

  Image(
      painter = painter,
      contentDescription = "Profile Image",
      modifier = modifier,
      contentScale = ContentScale.Crop // Adjust the scaling to suit your layout needs
      )
}
