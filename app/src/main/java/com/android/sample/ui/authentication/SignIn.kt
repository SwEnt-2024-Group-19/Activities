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
  val coroutineScope = rememberCoroutineScope()
  val token = stringResource(R.string.default_web_client_id)

  val onAuthSuccess = { Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show() }

  val onAuthError = { errorMessage: String ->
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
  }

  // Google Sign-In Launcher
  val googleSignInLauncher =
      rememberGoogleSignInLauncher(viewModel, navigationActions, onAuthSuccess, onAuthError)

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              // App Logo
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = "App Logo",
                  modifier = Modifier.size(110.dp).testTag("AppLogo"))
              Spacer(modifier = Modifier.height(48.dp))

              // Email Input
              OutlinedTextField(
                  value = emailState.value,
                  onValueChange = { emailState.value = it },
                  label = { Text("Email") },
                  modifier = Modifier.fillMaxWidth(0.8f).testTag("EmailTextField"))
              Spacer(modifier = Modifier.height(16.dp))

              // Password Input
              OutlinedTextField(
                  value = passwordState.value,
                  onValueChange = { passwordState.value = it },
                  label = { Text("Password") },
                  isError = passwordErrorState.value != null,
                  modifier = Modifier.fillMaxWidth(0.8f).testTag("PasswordTextField"))

              // Password Error
              passwordErrorState.value?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier =
                        Modifier.align(Alignment.Start)
                            .padding(start = 40.dp)
                            .testTag("PasswordErrorText"))
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Sign-In Button
              Button(
                  onClick = {
                    when {
                      passwordState.value.isEmpty() ->
                          passwordErrorState.value = "Password cannot be empty"
                      else ->
                          viewModel.signInWithEmailAndPassword(
                              emailState.value,
                              passwordState.value,
                              onAuthSuccess,
                              onAuthError,
                              navigationActions)
                    }
                      Log.d("SignInScreen", "Sign in with email/password")

                  },
                  modifier = Modifier.fillMaxWidth(0.8f).height(48.dp).testTag("SignInButton")) {
                    Text("Sign in with Email", fontSize = 16.sp)
                  }

              Spacer(modifier = Modifier.height(16.dp))

              // Google Sign-In Button
              GoogleSignInButton(
                  onSignInClick = {
                    googleSignInLauncher.launch(rememberGoogleSignInIntent(context, token))
                  })

              Spacer(modifier = Modifier.height(16.dp))

              // If user already has an account, navigate to the sign in screen
              TextButton(
                  onClick = { navigationActions.navigateTo(Screen.SIGN_UP) },
                  modifier =
                      Modifier.fillMaxWidth(0.8f).height(36.dp).testTag("GoToSignUpButton")) {
                    Text("No account yet?", fontSize = 16.sp)
                  }

              // Continue as guest
              TextButton(
                  onClick = { navigationActions.navigateTo(Screen.OVERVIEW) },
                  modifier = Modifier.testTag("ContinueAsGuestButton")) {
                    Text("Continue as Guest")
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
      modifier = Modifier.padding(8.dp).height(48.dp).testTag("GoogleSignInButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.8f)) {
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = "Google Logo",
                  modifier = Modifier.size(30.dp).padding(end = 8.dp))
              Text(
                  text = "Sign in with Google",
                  color = Color.Gray,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Medium)
            }
      }
}
