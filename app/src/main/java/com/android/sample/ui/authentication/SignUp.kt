package com.android.sample.ui.authentication

import android.content.Context
import android.graphics.Bitmap
import android.util.Patterns
import android.widget.Toast
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.android.sample.resources.C.Tag.BORDER_STROKE_SM
import com.android.sample.resources.C.Tag.EXTRA_LARGE_PADDING
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.MIN_PASSWORD_LENGTH
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.ui.camera.CameraScreen
import com.android.sample.ui.camera.DefaultImageCarousel
import com.android.sample.ui.camera.GalleryScreen
import com.android.sample.ui.camera.ProfileImage
import com.android.sample.ui.components.EmailTextField
import com.android.sample.ui.components.PasswordTextField
import com.android.sample.ui.components.TextFieldWithErrorState
import com.android.sample.ui.dialogs.AddImageDialog
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.profile.ManageInterests
import com.android.sample.ui.profile.ModifyPictureButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// helper to check if the email is in the right format
fun isValidEmail(email: String): Boolean {
  return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun SignUpScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    imageViewModel: ImageViewModel
) {
  val context = LocalContext.current
  // Mutable states for user inputs, errors, and UI visibility
  val emailState = remember { mutableStateOf("") }
  val passwordState = remember { mutableStateOf("") }
  val emailErrorState = remember { mutableStateOf<String?>(null) }
  val passwordErrorState = remember { mutableStateOf<String?>(null) }
  val isPasswordVisible = remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("") }
  val nameErrorState = remember { mutableStateOf<String?>(null) }
  var surname by remember { mutableStateOf("") }
  val surnameErrorState = remember { mutableStateOf<String?>(null) }
  var interests by remember { mutableStateOf(listOf<Interest>()) }
  var photo by remember { mutableStateOf("") }
  var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  // States to manage the visibility of dialogs and actions for image selection
  var showDialogImage by remember { mutableStateOf(false) }
  var isGalleryOpen by remember { mutableStateOf(false) }
  var isCamOpen by remember { mutableStateOf(false) }
  var isDefaultImageOpen by remember { mutableStateOf(false) }

  if (showDialogImage) {
      // Dialog to let the user choose an image source (Gallery, Camera, or Default)
      AddImageDialog(
        onDismiss = { showDialogImage = false },
        onGalleryClick = {
          showDialogImage = false
          isGalleryOpen = true
        },
        onCameraClick = {
          showDialogImage = false
          isCamOpen = true
        },
        onSelectDefault = {
          showDialogImage = false
          isDefaultImageOpen = true
        },
        default = true)
  }
  if (isGalleryOpen) {
    GalleryScreen(
        isGalleryOpen = { isGalleryOpen = false },
        addImage = { bitmap -> selectedBitmap = bitmap },
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
        addElem = { bitmap -> selectedBitmap = bitmap })
  } else {
    LazyColumn(
        modifier =
            Modifier.fillMaxSize()
                .padding(horizontal = MEDIUM_PADDING.dp)
                .background(Color(MAIN_BACKGROUND))
                .testTag("SignUpColumn"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING.dp),
    ) {

      // Profile Picture
      item {
        Spacer(modifier = Modifier.height(EXTRA_LARGE_PADDING.dp))
        ProfileImage(
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            modifier = Modifier.size(150.dp).clip(CircleShape).testTag("profilePicture"),
            imageViewModel,
            bitmap = selectedBitmap,
            editing = true)
        if (isDefaultImageOpen) {
          DefaultImageCarousel(
              onImageSelected = { bitmap ->
                selectedBitmap = bitmap
                isDefaultImageOpen = false
              },
              context = context,
              onDismiss = { isDefaultImageOpen = false })
        }
        ModifyPictureButton(showDialogImage = { showDialogImage = true })
      }

      // Email Field
      item {
        EmailTextField(
            email = emailState.value,
            onEmailChange = {
              emailState.value = it
              emailErrorState.value = if (it.isBlank()) "Email cannot be empty" else null
            },
            emailError = emailErrorState.value)
      }

      // Password Field
      item {
        PasswordTextField(
            password = passwordState.value,
            onPasswordChange = { passwordState.value = it },
            isPasswordVisible = isPasswordVisible.value,
            onPasswordVisibilityChange = { isPasswordVisible.value = !isPasswordVisible.value },
            passwordError = passwordErrorState.value)
      }

      // Name and Surname Fields
      item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MEDIUM_PADDING.dp)) {
              TextFieldWithErrorState(
                  value = name,
                  onValueChange = { name = it },
                  label = "Name",
                  validation = { input -> if (input.isBlank()) "Name cannot be empty" else null },
                  externalError = nameErrorState.value,
                  errorTestTag = "nameError",
                  testTag = "nameTextField",
                  modifier = Modifier.weight(1f))
              TextFieldWithErrorState(
                  value = surname,
                  onValueChange = { surname = it },
                  label = "Surname",
                  validation = { input ->
                    if (input.isBlank()) "Surname cannot be empty" else null
                  },
                  externalError = surnameErrorState.value,
                  errorTestTag = "surnameError",
                  testTag = "surnameTextField",
                  modifier = Modifier.weight(1f))
            }
      }

      // Interests Section
      item { ManageInterests(initialInterests = interests, onUpdateInterests = { interests = it }) }

      // Sign Up Button
      item {
        Button(
            onClick = {
              // Validate email, password, name, and surname
              emailErrorState.value =
                  if (!isValidEmail(emailState.value)) "Please enter a valid email address"
                  else null
              passwordErrorState.value =
                  if (passwordState.value.isBlank()) "Password cannot be empty"
                  else if (passwordState.value.length < MIN_PASSWORD_LENGTH)
                      "Password must be at least $MIN_PASSWORD_LENGTH characters long"
                  else null
              nameErrorState.value = if (name.isBlank()) "Name cannot be empty" else null
              surnameErrorState.value = if (surname.isBlank()) "Surname cannot be empty" else null

              // Proceed if no errors
              if (emailErrorState.value == null &&
                  passwordErrorState.value == null &&
                  nameErrorState.value == null &&
                  surnameErrorState.value == null) {
                createUserWithEmailAndPassword(
                    emailState.value,
                    passwordState.value,
                    context,
                    onSuccess = {
                      selectedBitmap?.let { bitmap ->
                        imageViewModel.uploadProfilePicture(
                            FirebaseAuth.getInstance().currentUser?.uid ?: "",
                            bitmap,
                            onSuccess = {}, // the photo field of user is not used anymore
                            onFailure = { error -> errorMessage = error.message })
                      }
                      val userProfile =
                          User(
                              id = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                              name = name,
                              surname = surname,
                              interests = interests,
                              activities = emptyList(),
                              photo = photo,
                              likedActivities = emptyList())
                      profileViewModel.createUserProfile(
                          userProfile = userProfile,
                          onSuccess = {
                            profileViewModel.fetchUserData(userProfile.id)
                            navigationActions.navigateTo(Screen.OVERVIEW)
                          },
                          onError = { error -> errorMessage = error.message })
                    })
              }
            },
            modifier =
                Modifier.fillMaxWidth().height(AUTH_BUTTON_HEIGHT.dp).testTag("SignUpButton"),
            shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
            border = BorderStroke(BORDER_STROKE_SM.dp, Color.Transparent),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color(MAIN_COLOR_DARK), contentColor = Color.White)) {
              Text("SIGN UP", fontSize = SUBTITLE_FONTSIZE.sp)
            }
      }

      // Already Have an Account Button
      item {
        TextButton(
            onClick = { navigationActions.navigateTo(Screen.AUTH) },
            modifier = Modifier.testTag("GoToSignInButton"),
            colors = ButtonDefaults.textButtonColors(contentColor = Color(MAIN_COLOR_DARK))) {
              Text("Already an account? Sign-in")
            }
      }
    }
  }
}
/**
 * Creates a new user account using email and password with Firebase Authentication.
 *
 * This function interacts with Firebase's Authentication API to create a new user account.
 *
 * @param email The email address of the user to create the account for.
 * @param password The password for the new account.
 * @param context The Android `Context` used to display `Toast` messages.
 * @param onSuccess Callback invoked when the account is successfully created.
 */
fun createUserWithEmailAndPassword(
    email: String,
    password: String,
    context: Context,
    onSuccess: () -> Unit
) {
  Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { createTask
    ->
    if (createTask.isSuccessful) {
      // User successfully created
      Toast.makeText(context, "Account created successfully!", Toast.LENGTH_LONG).show()
      onSuccess()
    } else {
      // User creation failed, display an error message
      Toast.makeText(context, "Account creation failed! Please try again.", Toast.LENGTH_LONG)
          .show()
    }
  }
}
