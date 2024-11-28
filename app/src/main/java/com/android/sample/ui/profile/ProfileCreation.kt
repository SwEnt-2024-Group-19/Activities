package com.android.sample.ui.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.camera.uploadProfilePicture
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.GalleryScreen
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.components.TextFieldWithErrorState
import com.android.sample.ui.dialogs.AddImageDialog
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCreationScreen(viewModel: ProfileViewModel, navigationActions: NavigationActions) {
  var name by remember { mutableStateOf("") }
  val nameErrorState = remember { mutableStateOf<String?>(null) }
  var surname by remember { mutableStateOf("") }
  val surnameErrorState = remember { mutableStateOf<String?>(null) }
  var interests by remember { mutableStateOf(listOf<Interest>()) }
  var photo by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var isCamOpen by remember { mutableStateOf(false) }
  var isGalleryOpen by remember { mutableStateOf(false) }
  var showDialogImage by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

  val scrollState = rememberScrollState()
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
          selectedBitmap = bitmap
          uploadProfilePicture(
              uid,
              bitmap,
              onSuccess = { url -> photo = url },
              onFailure = { error ->
                Log.e("ProfileCreationScreen", "Failed to upload profile picture: ${error.message}")
              })
        },
        context = context)
  }
  if (isCamOpen) {
    CameraScreen(
        paddingValues = PaddingValues(SMALL_PADDING.dp),
        controller =
            remember {
              LifecycleCameraController(context).apply {
                setEnabledUseCases(CameraController.IMAGE_CAPTURE)
              }
            },
        context = context,
        isCamOpen = { isCamOpen = false },
        addElem = { bitmap ->
          selectedBitmap = bitmap
          uploadProfilePicture(
              uid,
              bitmap,
              onSuccess = { url -> photo = url },
              onFailure = { error ->
                Log.e("ProfileCreationScreen", "Failed to upload profile picture: ${error.message}")
              })
        })
  } else {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(MEDIUM_PADDING.dp)
                .verticalScroll(scrollState)
                .testTag("profileCreationScrollColumn"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Text(
              text = "Complete your profile creation",
              fontSize = TITLE_FONTSIZE.sp,
              fontWeight = FontWeight.Bold, // Set the text to be bold
              modifier =
                  Modifier.padding(MEDIUM_PADDING.dp)
                      .wrapContentWidth(Alignment.CenterHorizontally)
                      .testTag("profileCreationTitle"))

          ProfileImage(
              userId = uid,
              modifier = Modifier.size(IMAGE_SIZE.dp).clip(CircleShape).testTag("profilePicture"))

          Button(
              onClick = { showDialogImage = true }, modifier = Modifier.testTag("uploadPicture")) {
                Text("Modify Profile Picture")
              }
          Spacer(modifier = Modifier.padding((2 * LARGE_PADDING).dp))

          TextFieldWithErrorState(
              value = name,
              onValueChange = { name = it },
              label = "Name",
              validation = { input -> if (input.isBlank()) "Name cannot be empty" else null },
              externalError = nameErrorState.value,
              modifier = Modifier.testTag("nameTextField"),
              errorTestTag = "nameError")

          Spacer(modifier = Modifier.padding(STANDARD_PADDING.dp))
          TextFieldWithErrorState(
              value = surname,
              onValueChange = { surname = it },
              label = "Surname",
              validation = { input -> if (input.isBlank()) "Surname cannot be empty" else null },
              externalError = surnameErrorState.value,
              modifier = Modifier.testTag("surnameTextField"),
              errorTestTag = "surnameError")
          Spacer(modifier = Modifier.padding(STANDARD_PADDING.dp))

          var newListInterests by remember { mutableStateOf(interests) }

          ManageInterests(
              initialInterests = interests, onUpdateInterests = { newListInterests = it })

          Spacer(modifier = Modifier.padding(STANDARD_PADDING.dp))
          Button(
              onClick = {
                // Set errors if fields are empty
                nameErrorState.value = if (name.isBlank()) "Name cannot be empty" else null
                surnameErrorState.value = if (surname.isBlank()) "Surname cannot be empty" else null

                // Proceed only if there are no errors
                if (nameErrorState.value == null && surnameErrorState.value == null) {
                  selectedBitmap?.let { bitmap ->
                    uploadProfilePicture(
                        uid,
                        bitmap,
                        onSuccess = { url ->
                          photo = url // Update photo URL in profile
                        },
                        onFailure = { error -> errorMessage = error.message })
                  }
                  val userProfile =
                      User(
                          id = uid,
                          name = name,
                          surname = surname,
                          interests = newListInterests,
                          activities = emptyList(),
                          photo = photo,
                          likedActivities = emptyList(),
                          nbActivitiesCreated = 0)

                  viewModel.createUserProfile(
                      userProfile = userProfile,
                      onSuccess = {
                        Log.d("ProfileCreation", "Profile created successfully")
                        viewModel.fetchUserData(uid)
                        navigationActions.navigateTo(Screen.OVERVIEW)
                      },
                      onError = { error -> errorMessage = error.message })
                }
              },
              modifier = Modifier.testTag("createProfileButton")) {
                Text("Create Profile")
              }

          errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = STANDARD_PADDING.dp).testTag("errorMessage"))
          }
        }
  }
}
