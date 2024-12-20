package com.android.sample.ui.authentication

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.network.NetworkManager
import com.android.sample.resources.C.Tag.AUTH_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT_SM
import com.android.sample.resources.C.Tag.CARD_ELEVATION_DEFAULT
import com.android.sample.resources.C.Tag.EXTRA_LARGE_PADDING
import com.android.sample.resources.C.Tag.IMAGE_IN_BUTTON_DEFAULT
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.LINE_STROKE
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND_BUTTON
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_SM
import com.android.sample.ui.components.EmailTextField
import com.android.sample.ui.components.PasswordTextField
import com.android.sample.ui.components.performOfflineAwareAction
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navigationActions: NavigationActions, viewModel: SignInViewModel) {
  val context = LocalContext.current
  val networkManager = NetworkManager(context)
  // Mutable states for user inputs, errors, and UI visibility
  val emailState = remember { mutableStateOf("") }
  val passwordState = remember { mutableStateOf("") }
  val passwordErrorState = remember { mutableStateOf<String?>(null) }
  val emailErrorState = remember { mutableStateOf<String?>(null) }

  val token = stringResource(R.string.default_web_client_id)
  val isPasswordVisible = remember { mutableStateOf(false) }
  val onProfileExists = { navigationActions.navigateTo(Screen.OVERVIEW) }
  val onProfileMissing = { navigationActions.navigateTo(Screen.CREATE_PROFILE) }
  val onSignInFailure = { errorMessage: String ->
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
  }

  // Google Sign-In Launcher
  val googleSignInLauncher =
      rememberGoogleSignInLauncher(viewModel, onProfileExists, onProfileMissing, onSignInFailure)

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("SignInScreen"),
      content = { padding ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .background(Color(MAIN_BACKGROUND))
                    .testTag("SignInScreenColumn"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          item {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.aptivity_logo_with_text),
                contentDescription = "App Logo",
                modifier = Modifier.size((2 * IMAGE_SIZE).dp).testTag("AppLogo"))
            Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
          }

          item {
            // Email Input
            EmailTextField(
                email = emailState.value,
                onEmailChange = {
                  emailState.value = it
                  emailErrorState.value =
                      if (!isValidEmail(it)) "Please enter a valid address: example@mail.xx "
                      else null
                },
                emailError = emailErrorState.value)
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }

          item {
            // Password input
            PasswordTextField(
                password = passwordState.value,
                onPasswordChange = {
                  passwordState.value = it
                  passwordErrorState.value = if (it.isEmpty()) "Password cannot be empty" else null
                },
                isPasswordVisible = isPasswordVisible.value,
                onPasswordVisibilityChange = { isPasswordVisible.value = !isPasswordVisible.value },
                passwordError = passwordErrorState.value)
            Spacer(modifier = Modifier.height(EXTRA_LARGE_PADDING.dp))
          }

          item {
            // Sign-in Button
            Card(
                modifier = Modifier.fillMaxWidth(WIDTH_FRACTION_SM).testTag("SignInCard"),
                colors =
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                elevation =
                    CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION_DEFAULT.dp),
                shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp)) {
                  Button(
                      onClick = {
                        performOfflineAwareAction(
                            context = context,
                            networkManager = networkManager,
                            onPerform = {
                              when {
                                !isValidEmail(emailState.value) -> {
                                  emailErrorState.value =
                                      "Please enter a valid address: example@mail.xx"
                                }
                                passwordState.value.isEmpty() -> {
                                  passwordErrorState.value =
                                      "Password cannot be empty" // Set external error
                                }
                                else -> {
                                  passwordErrorState.value =
                                      null // Clear external error if password is valid
                                  viewModel.signInWithEmailAndPassword(
                                      emailState.value,
                                      passwordState.value,
                                      onProfileExists,
                                      onProfileMissing,
                                      onSignInFailure)
                                }
                              }
                            })
                      },
                      modifier =
                          Modifier.fillMaxWidth()
                              .height(AUTH_BUTTON_HEIGHT.dp)
                              .testTag("SignInButton"),
                      shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
                      colors =
                          ButtonDefaults.buttonColors(
                              containerColor = Color(MAIN_COLOR_DARK),
                              contentColor = Color.White)) {
                        Text("SIGN IN", fontSize = SUBTITLE_FONTSIZE.sp)
                      }
                }
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }

          // text that separates the buttons "OR"
          item {
            Text("OR", fontSize = SUBTITLE_FONTSIZE.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }

          item {
            // Google Sign-In Button
            GoogleSignInButton(
                onSignInClick = {
                  performOfflineAwareAction(
                      context = context,
                      networkManager = networkManager,
                      onPerform = {
                        googleSignInLauncher.launch(rememberGoogleSignInIntent(context, token))
                      })
                })
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }

          item {
            // Continue as guest
            TextButton(
                onClick = {
                  performOfflineAwareAction(
                      context = context,
                      networkManager = networkManager,
                      onPerform = { navigationActions.navigateTo(Screen.OVERVIEW) })
                },
                modifier = Modifier.testTag("ContinueAsGuestButton")) {
                  Text(
                      "Continue as a guest",
                      fontSize = SUBTITLE_FONTSIZE.sp,
                      color = Color(MAIN_COLOR_DARK))
                }
            Spacer(modifier = Modifier.height(EXTRA_LARGE_PADDING.dp))
          }
          item {
            // If user already has an account, navigate to the sign-in screen
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Text("Don't have an account? ", fontSize = SUBTITLE_FONTSIZE.sp)
              Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
              TextButton(
                  onClick = {
                    performOfflineAwareAction(
                        context = context,
                        networkManager = networkManager,
                        onPerform = { navigationActions.navigateTo(Screen.SIGN_UP) })
                  },
                  modifier = Modifier.testTag("GoToSignUpButton")) {
                    Text("Sign Up", fontSize = SUBTITLE_FONTSIZE.sp, color = Color(MAIN_COLOR_DARK))
                  }
            }
          }
        }
      })
}
/**
 * Creates a `ManagedActivityResultLauncher` to handle Google Sign-In intents.
 *
 * This composable function returns a `ManagedActivityResultLauncher` that allows launching a Google
 * Sign-In activity and processes its result. The function integrates with a `SignInViewModel` to
 * manage authentication results and navigate users based on their profile status.
 *
 * @param viewModel The `SignInViewModel` responsible for handling the Google Sign-In result.
 * @param onProfileExists Callback invoked when the user's profile already exists in the system.
 * @param onProfileMissing Callback invoked when the user's profile is missing and needs creation.
 * @param onFailure Callback invoked when the Google Sign-In process fails with an error message.
 * @return A `ManagedActivityResultLauncher` instance to be used for launching the Google Sign-In
 *   intent.
 */
@Composable
fun rememberGoogleSignInLauncher(
    viewModel: SignInViewModel,
    onProfileExists: () -> Unit,
    onProfileMissing: () -> Unit,
    onFailure: (String) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
  val scope = rememberCoroutineScope()
  return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    scope.launch {
      try {
        val account = task.getResult(ApiException::class.java)!!
        val idToken = account.idToken // Extract the actual ID token
        if (idToken != null) {
          viewModel.handleGoogleSignInResult(idToken, onProfileExists, onProfileMissing, onFailure)
        } else {
          onFailure("Google Sign-in failed! Token is null.")
        }
      } catch (e: ApiException) {
        onFailure("Google Sign-in failed! ${e.message}")
      }
    }
  }
}
/**
 * Creates an `Intent` for initiating Google Sign-In.
 *
 * It configures the Google Sign-In options to request the ID token and email address.
 *
 * @param context The `Context` used to create the Google Sign-In client.
 * @param token The client ID token required for authentication.
 * @return An `Intent` to launch the Google Sign-In activity.
 */
fun rememberGoogleSignInIntent(context: Context, token: String): Intent {
  val gso =
      GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(token)
          .requestEmail()
          .build()
  return GoogleSignIn.getClient(context, gso).signInIntent
}
// UI part of the Google button
@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {
  val context = LocalContext.current
  val networkManager = NetworkManager(context)
  Card(
      modifier = Modifier.fillMaxWidth(WIDTH_FRACTION_SM).testTag("GoogleCard"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
      elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION_DEFAULT.dp),
      shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
  ) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 0.dp)) {
      OutlinedButton(
          onClick = {
            performOfflineAwareAction(
                context = context, networkManager = networkManager, onPerform = onSignInClick)
          },
          shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
          modifier =
              Modifier.height(BUTTON_HEIGHT_SM.dp).fillMaxWidth().testTag("GoogleSignInButton"),
          border = BorderStroke(LINE_STROKE.dp, Color.Transparent), // Transparent indicator
          colors =
              ButtonColors(
                  containerColor = Color(MAIN_BACKGROUND_BUTTON),
                  contentColor = Color.White,
                  disabledContentColor = Color.Gray,
                  disabledContainerColor = Color.Gray)) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = "Google Sign-In",
                modifier = Modifier.size(IMAGE_IN_BUTTON_DEFAULT.dp))
            Spacer(Modifier.width(STANDARD_PADDING.dp))
            Text(
                "Login with Google",
                fontSize = SUBTITLE_FONTSIZE.sp,
                color = Color(MAIN_COLOR_DARK))
          }
    }
  }
}
