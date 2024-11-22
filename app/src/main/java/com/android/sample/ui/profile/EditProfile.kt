package com.android.sample.ui.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.camera.uploadProfilePicture
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.InterestCategories
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_IMAGE_SIZE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.GalleryScreen
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.dialogs.AddImageDialog
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
  var isCamOpen by remember { mutableStateOf(false) }
  var isGalleryOpen by remember { mutableStateOf(false) }
  var showDialogImage by remember { mutableStateOf(false) }
  var photo by remember { mutableStateOf(profile.photo) }
  val scrollState = rememberScrollState()

  val context = LocalContext.current
  var selectedImage by remember { mutableStateOf<Bitmap?>(null) }

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
        if (showDialogImage) {
          AddImageDialog(
              onDismiss = { showDialogImage = false },
              onGalleryClick = {
                showDialogImage = false
                isGalleryOpen = true
              },
              onCameraClick = {
                showDialogImage = false
                isCamOpen = true
              })
        }

        if (isGalleryOpen) {
          GalleryScreen(
              isGalleryOpen = { isGalleryOpen = false },
              addImage = { bitmap ->
                selectedImage = bitmap
                uploadProfilePicture(
                    profile.id,
                    bitmap,
                    onSuccess = { url -> photo = url },
                    onFailure = { error ->
                      Log.e(
                          "EditProfileScreen", "Failed to upload profile picture: ${error.message}")
                    })
              },
              context = context)
        }
        if (isCamOpen) {
          CameraScreen(
              paddingValues = paddingValues,
              controller =
                  remember {
                    LifecycleCameraController(context).apply {
                      setEnabledUseCases(CameraController.IMAGE_CAPTURE)
                    }
                  },
              context = context,
              isCamOpen = { isCamOpen = false },
              addElem = { bitmap ->
                selectedImage = bitmap
                uploadProfilePicture(
                    profile.id,
                    bitmap,
                    onSuccess = { url -> photo = url },
                    onFailure = { error ->
                      Log.e(
                          "EditProfileScreen", "Failed to upload profile picture: ${error.message}")
                    })
              })
        } else {
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(paddingValues)
                      .padding(MEDIUM_PADDING.dp)
                      .testTag("editProfileContent")
                      .verticalScroll(scrollState),
              verticalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp)) {
                ProfileImage(
                    userId = profile.id,
                    modifier =
                        Modifier.size(IMAGE_SIZE.dp).clip(CircleShape).testTag("profilePicture"))

                Button(
                    onClick = { showDialogImage = true },
                    modifier = Modifier.testTag("uploadPicture")) {
                      Text("Modify Profile Picture")
                    }

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
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(IMAGE_SIZE.dp)
                            .testTag("inputProfileSurname"))

                // Interest list and add button

                var newListInterests by remember { mutableStateOf(interests ?: emptyList()) }

                ManageInterests(
                    initialInterests = interests ?: emptyList(),
                    onUpdateInterests = { newListInterests = it })

                Button(
                    onClick = {
                      selectedImage?.let { bitmap ->
                        uploadProfilePicture(
                            profile.id,
                            bitmap,
                            onSuccess = { url ->
                              photo = url // Update photo URL in profile
                            },
                            onFailure = { error ->
                              Log.e(
                                  "EditProfileScreen",
                                  "Failed to upload profile picture: ${error.message}")
                            })
                      }
                      try {
                        profileViewModel.updateProfile(
                            User(
                                id = profile.id,
                                name = name,
                                surname = surname,
                                interests = newListInterests,
                                activities = profile.activities,
                                photo = photo,
                                likedActivities = profile.likedActivities))
                        navigationActions.goBack()
                      } catch (_: NumberFormatException) {}
                    },
                    modifier = Modifier.fillMaxWidth().testTag("profileSaveButton")) {
                      Text("Save", color = Color.White)
                    }
              }
        }
      })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageInterests(initialInterests: List<Interest>, onUpdateInterests: (List<Interest>) -> Unit) {

  var newListInterests by remember { mutableStateOf(initialInterests) }
  val categories = InterestCategories
  var selectedCategory by remember { mutableStateOf("") }
  var expanded by remember { mutableStateOf(false) }
  var newInterest by remember { mutableStateOf("") }

  Row(verticalAlignment = Alignment.CenterVertically) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
      OutlinedTextField(
          value = selectedCategory,
          onValueChange = { selectedCategory = it },
          label = { Text("Category") },
          readOnly = true,
          modifier = Modifier.menuAnchor().width(LARGE_IMAGE_SIZE.dp).testTag("categoryDropdown"))

      ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        categories.forEach { category ->
          DropdownMenuItem(
              text = { Text(category) },
              onClick = {
                selectedCategory = category
                expanded = false
              })
        }
      }
    }

    OutlinedTextField(
        value = newInterest,
        onValueChange = { newInterest = it },
        label = { Text("New Interest") },
        enabled = selectedCategory.isNotEmpty() && selectedCategory != "None",
        modifier = Modifier.width(LARGE_IMAGE_SIZE.dp).testTag("newInterestInput"))
  }

  Row(verticalAlignment = Alignment.CenterVertically) {
    Button(
        onClick = {
          if (newInterest.isNotBlank() &&
              selectedCategory.isNotBlank() &&
              selectedCategory != "None") {
            val updatedList = newListInterests + Interest(selectedCategory, newInterest)
            newListInterests = updatedList
            newInterest = ""
            selectedCategory = ""
            onUpdateInterests(updatedList)
          }
        },
        enabled =
            newInterest.isNotBlank() && selectedCategory.isNotBlank() && selectedCategory != "None",
        modifier = Modifier.testTag("addInterestButton")) {
          Text("Add")
        }
  }

  LazyRow(
      modifier = Modifier.testTag("interestsList"),
      horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp)) {
        items(newListInterests.size) { interest ->
          InterestEditBox(
              interest = newListInterests[interest].interest,
              onRemove = {
                val updatedList = newListInterests - newListInterests[interest]
                newListInterests = updatedList
                onUpdateInterests(updatedList)
              })
        }
      }
}

@Composable
fun InterestEditBox(interest: String, onRemove: () -> Unit) {
  Box(
      modifier =
          Modifier.background(Color.LightGray, RoundedCornerShape(STANDARD_PADDING.dp))
              .padding(horizontal = MEDIUM_PADDING.dp, vertical = STANDARD_PADDING.dp)
              .testTag("$interest")) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = interest, fontSize = SUBTITLE_FONTSIZE.sp, color = Color.Black)
          Spacer(Modifier.width(STANDARD_PADDING.dp))
          IconButton(onClick = onRemove, modifier = Modifier.testTag("removeInterest-$interest")) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove")
          }
        }
      }
}
