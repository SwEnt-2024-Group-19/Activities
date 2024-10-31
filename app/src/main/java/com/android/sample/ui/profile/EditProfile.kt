package com.android.sample.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(profileViewModel: ProfileViewModel, navigationActions: NavigationActions) {
  val profile =
      profileViewModel.userState.collectAsState().value
          ?: return Text(text = "No profile selected.", color = Color.Red)

  var name by remember { mutableStateOf(profile.name) }
  var surname by remember { mutableStateOf(profile.surname) }
  var interests by remember { mutableStateOf(profile.interests) }
  var photo by remember { mutableStateOf(profile.photo) }

  Scaffold(
      modifier = Modifier.testTag("editProfileScreen"),
      topBar = {
        TopAppBar(
            title = { Text("Edit Profile", modifier = Modifier.testTag("editProfileTitle")) },
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
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              ProfileImage(
                  url = photo,
                  modifier = Modifier.size(100.dp).clip(CircleShape).testTag("profilePicture"))

              OutlinedTextField(
                  value = photo.toString(),
                  onValueChange = { photo = it },
                  label = { Text("Photo URL") },
                  placeholder = { Text("Your photo URL") },
                  modifier = Modifier.fillMaxWidth().testTag("inputProfilePhoto"))

              OutlinedTextField(
                  value = name,
                  onValueChange = { name = it },
                  label = { Text("Name") },
                  placeholder = { Text("Your Name") },
                  modifier = Modifier.fillMaxWidth().testTag("inputProfileName"))
              OutlinedTextField(
                  value = surname,
                  onValueChange = { surname = it },
                  label = { Text("Surname") },
                  placeholder = { Text("Your surname") },
                  modifier = Modifier.fillMaxWidth().height(100.dp).testTag("inputProfileSurname"))

              // Interest list and add button
              var newInterest by remember { mutableStateOf("") }
              var newListInterests by remember { mutableStateOf(interests) }

              LazyRow(
                  modifier = Modifier.padding(16.dp).testTag("interestsList"),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    newListInterests?.let {
                      items(it.size, key = { it }) { index ->
                        InterestEditBox(
                            interest = newListInterests!![index],
                            onRemove = {
                              newListInterests = newListInterests!! - newListInterests!![index]
                            })
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
                      newListInterests = newListInterests?.plus(newInterest)
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

              Button(
                  onClick = {
                    try {
                      profileViewModel.updateProfile(
                          User(
                              id = profile.id,
                              name = name,
                              surname = surname,
                              interests = newListInterests,
                              activities = profile.activities,
                              photo = photo))
                      navigationActions.goBack()
                    } catch (_: NumberFormatException) {}
                  },
                  modifier = Modifier.fillMaxWidth().testTag("profileSaveButton")) {
                    Text("Save", color = Color.White)
                  }
            }
      })
}

@Composable
fun InterestEditBox(interest: String, onRemove: () -> Unit) {
  Box(
      modifier =
          Modifier.background(Color.LightGray, RoundedCornerShape(8.dp))
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .testTag("$interest")) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = interest, fontSize = 18.sp, color = Color.Black)
          Spacer(Modifier.width(8.dp))
          IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove")
          }
        }
      }
}
