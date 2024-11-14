package com.android.sample.ui.authentication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.WIDTH_FRACTION
import com.android.sample.ui.components.EmailTextField
import com.android.sample.ui.components.PasswordTextField
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navigationActions: NavigationActions, viewModel: SignInViewModel) {
  val context = LocalContext.current
  val emailState = remember { mutableStateOf("") }
  val passwordState = remember { mutableStateOf("") }
  val passwordErrorState = remember { mutableStateOf<String?>(null) }
  val token = stringResource(R.string.default_web_client_id)
  val isPasswordVisible = remember { mutableStateOf(false) }
  val onAuthSuccess = { Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show() }
  val emailErrorState = remember { mutableStateOf<String?>(null) }
  val onAuthError = { errorMessage: String ->
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
  }

  // Google Sign-In Launcher
  val googleSignInLauncher =
      rememberGoogleSignInLauncher(viewModel, navigationActions, onAuthSuccess, onAuthError)

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("SignInScreen"),
      content = { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).testTag("SignInScreenColumn"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          item {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.aptivity_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(IMAGE_SIZE.dp).testTag("AppLogo"))
            Spacer(modifier = Modifier.height((2 * LARGE_PADDING).dp))
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
            PasswordTextField(
                password = passwordState.value,
                onPasswordChange = { passwordState.value = it },
                isPasswordVisible = isPasswordVisible.value,
                onPasswordVisibilityChange = { isPasswordVisible.value = !isPasswordVisible.value },
                passwordError = passwordErrorState.value)
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }

          item {
            // Sign-In Button
            Button(
                onClick = {
                  when {
                    !isValidEmail(emailState.value) -> {
                      emailErrorState.value = "Please enter a valid address: example@mail.xx"
                    }
                    passwordState.value.isEmpty() -> {
                      passwordErrorState.value = "Password cannot be empty" // Set external error
                    }
                    else -> {
                      passwordErrorState.value = null // Clear external error if password is valid
                      viewModel.signInWithEmailAndPassword(
                          emailState.value,
                          passwordState.value,
                          onAuthSuccess,
                          onAuthError,
                          navigationActions)
                    }
                  }
                  Log.d("SignInScreen", "Sign in with email/password")
                },
                modifier = Modifier.fillMaxWidth(WIDTH_FRACTION).height(BUTTON_HEIGHT.dp).testTag("SignInButton")) {
                  Text("Sign in with Email", fontSize = SUBTITLE_FONTSIZE.sp)
                }
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }

          item {
            // Google Sign-In Button
            GoogleSignInButton(
                onSignInClick = {
                  googleSignInLauncher.launch(rememberGoogleSignInIntent(context, token))
                })
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }

          item {
            // If user already has an account, navigate to the sign-in screen
            TextButton(
                onClick = { navigationActions.navigateTo(Screen.SIGN_UP) },
                modifier = Modifier.fillMaxWidth(WIDTH_FRACTION).height(BUTTON_HEIGHT.dp).testTag("GoToSignUpButton")) {
                  Text("No account yet?", fontSize = SUBTITLE_FONTSIZE.sp)
                }
          }

          item {
            // Continue as guest
            TextButton(
                onClick = { navigationActions.navigateTo(Screen.OVERVIEW) },
                modifier = Modifier.testTag("ContinueAsGuestButton")) {
                  Text("Continue as Guest")
                }
          }
        }
      })
}

@Composable
fun rememberGoogleSignInLauncher(
    viewModel: SignInViewModel,
    navigationActions: NavigationActions,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
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
          viewModel.handleGoogleSignInResult(idToken, onAuthSuccess, onAuthError, navigationActions)
        } else {
          onAuthError("Google Sign-in failed! Token is null.")
        }
      } catch (e: ApiException) {
        onAuthError("Google Sign-in failed! ${e.message}")
      }
    }
  }
}

fun rememberGoogleSignInIntent(context: Context, token: String): Intent {
  val gso =
      GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(token)
          .requestEmail()
          .build()
  return GoogleSignIn.getClient(context, gso).signInIntent
}

@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {
  Button(
      onClick = onSignInClick,
      colors = ButtonDefaults.buttonColors(containerColor = Color.White),
      shape = RoundedCornerShape(50),
      border = BorderStroke(1.dp, Color.LightGray),
      modifier =
          Modifier.padding(STANDARD_PADDING.dp)
              .height(BUTTON_HEIGHT.dp)
              .testTag("GoogleSignInButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.width(BUTTON_WIDTH.dp)) {
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = "Google Logo",
                  modifier = Modifier.size(IMAGE_SIZE.dp).padding(end = STANDARD_PADDING.dp))
              Text(
                  text = "Sign in with Google",
                  color = Color.Gray,
                  fontSize = MEDIUM_PADDING.sp,
                  fontWeight = FontWeight.Medium)
            }
      }
}
