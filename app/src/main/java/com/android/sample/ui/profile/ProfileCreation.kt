package com.android.sample.ui.profile

import android.graphics.Bitmap
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.AUTH_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.CARD_ELEVATION_DEFAULT
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_MD
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.GalleryScreen
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.components.TextFieldWithErrorState
import com.android.sample.ui.dialogs.AddImageDialog
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileCreationScreen(
    viewModel: ProfileViewModel,
    navigationActions: NavigationActions,
    imageViewModel: ImageViewModel
) {
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
          imageViewModel.uploadProfilePicture(
              uid, bitmap, onSuccess = { url -> photo = url }, onFailure = { error -> })
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
          imageViewModel.uploadProfilePicture(
              uid, bitmap, onSuccess = { url -> photo = url }, onFailure = { error -> })
        })
  } else {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .padding(MEDIUM_PADDING.dp)
                .verticalScroll(scrollState)
                .testTag("profileCreationScrollColumn"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Text(
              text = "Complete your profile creation",
              fontSize = TITLE_FONTSIZE.sp,
              modifier =
                  Modifier.padding(MEDIUM_PADDING.dp)
                      .wrapContentWidth(Alignment.CenterHorizontally)
                      .testTag("profileCreationTitle"))
          Spacer(modifier = Modifier.padding(MEDIUM_PADDING.dp))
          ProfileImage(
              userId = uid,
              modifier =
                  Modifier.size((1.5 * IMAGE_SIZE).dp).clip(CircleShape).testTag("profilePicture"),
              imageViewModel)

          Box(
              modifier =
                  Modifier.testTag("uploadPicture")
                      .clickable { showDialogImage = true } // Handle click action
                      .padding(MEDIUM_PADDING.dp)
                      .background(Color.Transparent)) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Add a photo",
                    tint = Color.Black)
              }

          Spacer(modifier = Modifier.padding((2 * LARGE_PADDING).dp))

          Row(
              Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(STANDARD_PADDING.dp)) {
                TextFieldWithErrorState(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name",
                    validation = { input -> if (input.isBlank()) "Name cannot be empty" else null },
                    externalError = nameErrorState.value,
                    modifier = Modifier.weight(1f),
                    errorTestTag = "nameError",
                    testTag = "nameTextField")
                TextFieldWithErrorState(
                    value = surname,
                    onValueChange = { surname = it },
                    label = "Surname",
                    validation = { input ->
                      if (input.isBlank()) "Surname cannot be empty" else null
                    },
                    externalError = surnameErrorState.value,
                    modifier = Modifier.weight(1f),
                    errorTestTag = "surnameError",
                    testTag = "surnameTextField")
              }

          Spacer(modifier = Modifier.padding(STANDARD_PADDING.dp))

          var newListInterests by remember { mutableStateOf(interests) }

          ManageInterests(
              initialInterests = interests, onUpdateInterests = { newListInterests = it })

          Spacer(modifier = Modifier.padding(STANDARD_PADDING.dp))
          Card(
              modifier =
                  Modifier.fillMaxWidth(WIDTH_FRACTION_MD).testTag("ProfileCreationButtonCard"),
              colors =
                  CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
              elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION_DEFAULT.dp),
              shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp)) {
                Button(
                    onClick = {
                      // Set errors if fields are empty
                      nameErrorState.value = if (name.isBlank()) "Name cannot be empty" else null
                      surnameErrorState.value =
                          if (surname.isBlank()) "Surname cannot be empty" else null

                      // Proceed only if there are no errors
                      if (nameErrorState.value == null && surnameErrorState.value == null) {
                        selectedBitmap?.let { bitmap ->
                          imageViewModel.uploadProfilePicture(
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
                                likedActivities = emptyList())

                        viewModel.createUserProfile(
                            userProfile = userProfile,
                            onSuccess = {
                              viewModel.fetchUserData(uid)
                              navigationActions.navigateTo(Screen.OVERVIEW)
                            },
                            onError = { error -> errorMessage = error.message })
                      }
                    },
                    modifier =
                        Modifier.testTag("createProfileButton")
                            .fillMaxWidth()
                            .height(AUTH_BUTTON_HEIGHT.dp),
                    shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp)) {
                      Text("Create Profile", fontSize = SUBTITLE_FONTSIZE.sp)
                    }
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
