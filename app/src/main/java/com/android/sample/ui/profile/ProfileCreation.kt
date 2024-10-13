package com.android.sample.ui.profile

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileCreationScreen(viewModel: ProfileViewModel, navigationActions: NavigationActions) {
  var name by remember { mutableStateOf("") }
  var surname by remember { mutableStateOf("") }
  var interests by remember { mutableStateOf("") }
  var activities by remember { mutableStateOf("") }
  var photo by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
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

        OutlinedTextField(
            value = interests,
            onValueChange = { interests = it },
            label = { Text("Interests (comma-separated)") },
            modifier = Modifier.testTag("interestsTextField"))
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = activities,
            onValueChange = { activities = it },
            label = { Text("Activities (comma-separated)") },
            modifier = Modifier.testTag("activitiesTextField"))
        Spacer(modifier = Modifier.padding(8.dp))

        OutlinedTextField(
            value = photo,
            onValueChange = { photo = it },
            label = { Text("Photo URL") },
            modifier = Modifier.testTag("photoTextField"))
        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            onClick = {
              val userProfile =
                  User(
                      id = uid,
                      name = name,
                      surname = surname,
                      interests = interests.split(","),
                      activities = emptyList(),
                      photo = photo)
              viewModel.createUserProfile(
                  userProfile = userProfile,
                  onSuccess = {
                    Log.d("ProfileCreation", "Profile created successfully")
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
