package com.android.sample.ui.profile

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.ImagePicker
import com.android.sample.ui.ProfileImage
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileCreationScreen(viewModel: ProfileViewModel, navigationActions: NavigationActions) {
  var name by remember { mutableStateOf("") }
  var surname by remember { mutableStateOf("") }
  var interests by remember { mutableStateOf(listOf<String>()) }
  // var activities by remember { mutableStateOf("") }
  var photo by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

  val scrollState = rememberScrollState()

  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(16.dp)
              .verticalScroll(scrollState)
              .testTag("profileCreationScrollColumn"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Text(
            text = "Complete your profile creation",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold, // Set the text to be bold
            modifier =
                Modifier.padding(16.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .testTag("profileCreationTitle"))

        ProfileImage(
            url = photo,
            modifier = Modifier.size(100.dp).clip(CircleShape).testTag("profilePicture"))
        ImagePicker(onImagePicked = { photo = it.toString() }, buttonText = "Add Profile Picture")
        Spacer(modifier = Modifier.padding(48.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.testTag("nameTextField"))

        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Surname") },
            modifier = Modifier.testTag("surnameTextField"))
        Spacer(modifier = Modifier.padding(8.dp))

        var newInterest by remember { mutableStateOf("") }
        var newListInterests by remember { mutableStateOf(interests) }

        LazyRow(
            modifier = Modifier.padding(16.dp).testTag("interestsList"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              newListInterests.let {
                items(it.size, key = { it }) { index ->
                  InterestEditBox(
                      interest = newListInterests[index],
                      onRemove = { newListInterests = newListInterests - newListInterests[index] })
                }
              }
            }

        OutlinedTextField(
            value = newInterest,
            onValueChange = { newInterest = it },
            label = { Text("New Interest") },
            modifier = Modifier.width(200.dp).testTag("newInterestInput"))
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
              if (newInterest.isNotBlank()) {
                newListInterests = newListInterests.plus(newInterest)
                newInterest = "" // Clear the field after adding
              }
            },
            modifier =
                Modifier.padding(end = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("addInterestButton")) {
              Text("Add")
            }

        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            onClick = {
              val userProfile =
                  User(
                      id = uid,
                      name = name,
                      surname = surname,
                      interests = newListInterests,
                      activities = emptyList(),
                      photo = photo)
              viewModel.createUserProfile(
                  userProfile = userProfile,
                  onSuccess = {
                    Log.d("ProfileCreation", "Profile created successfully")
                    viewModel.fetchUserData(uid)
                    navigationActions.navigateTo(Screen.OVERVIEW)
                  },
                  onError = { error ->
                    errorMessage = error.message // Display error message if profile creation fails,
                  })
            },
            modifier = Modifier.testTag("createProfileButton")) {
              Text("Create Profile")
            }

        errorMessage?.let {
          Text(
              text = it,
              color = Color.Red,
              modifier = Modifier.padding(top = 8.dp).testTag("errorMessage"))
        }
      }
}
