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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.android.sample.model.profile.ProfilesRepositoryFirestore
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SignInScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val emailState = remember { mutableStateOf("") }
  val passwordState = remember { mutableStateOf("") }
  val passwordErrorState = remember {
    mutableStateOf<String?>(null)
  } // State for password validation error
  val launcher =
      rememberFirebaseAuthLauncher(
          onAuthComplete = { result ->
            Log.d("SignInScreen", "User signed in: ${result.user?.displayName}")
            Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
            // Navigate to OverviewScreen upon successful login
            navigationActions.navigateTo(Screen.OVERVIEW)
          },
          onAuthError = {
            Log.e("SignInScreen", "Failed to sign in: ${it.statusCode}")
            Toast.makeText(context, "Login Failed!", Toast.LENGTH_LONG).show()
          },
          navigationActions)
  val token = stringResource(R.string.default_web_client_id)

  // The main container for the screen
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          // App Logo Image
          Image(
              painter = painterResource(id = R.drawable.google_logo), // Ensure this drawable exists
              contentDescription = "App Logo",
              modifier = Modifier.size(110.dp).testTag("AppLogo"))
          Spacer(modifier = Modifier.height(48.dp))

          // Email and Password fields
          OutlinedTextField(
              value = emailState.value,
              onValueChange = { emailState.value = it },
              label = { Text("Email") },
              modifier = Modifier.fillMaxWidth(0.8f).testTag("EmailTextField"))
          Spacer(modifier = Modifier.height(16.dp))
          OutlinedTextField(
              value = passwordState.value,
              onValueChange = { passwordState.value = it },
              label = { Text("Password") },
              isError =
                  passwordErrorState.value !=
                      null, // Highlight the text field in red if there's an error
              modifier = Modifier.fillMaxWidth(0.8f).testTag("PasswordTextField"))
          // Display password error message below the password field
          if (passwordErrorState.value != null) {
            Text(
                text = passwordErrorState.value ?: "",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 40.dp, top = 4.dp))
          }
          Spacer(modifier = Modifier.height(16.dp))

          // Sign In with Email/Password Button
          Button(
              onClick = {
                when {
                  passwordState.value.isEmpty() -> {
                    passwordErrorState.value =
                        "Password cannot be empty" // Set the error message if password is empty
                  }
                  else -> {
                    signInWithEmailAndPassword(
                        emailState.value, passwordState.value, context, navigationActions)
                  }
                }
              },
              modifier = Modifier.fillMaxWidth(0.8f).height(48.dp).testTag("SignInButton")) {
                Text("Sign in with Email", fontSize = 16.sp)
              }
          Spacer(modifier = Modifier.height(16.dp))

          // Authenticate With Google Button
          GoogleSignInButton(
              onSignInClick = {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
              })

          // If user already has an account, navigate to the sign in screen
          TextButton(
              onClick = { navigationActions.navigateTo(Screen.SIGN_UP) },
              modifier = Modifier.fillMaxWidth(0.8f).height(36.dp).testTag("GoToSignUpButton")) {
                Text("No account yet?", fontSize = 16.sp)
              }

          // Continue as a guest button
          TextButton(
              onClick = { navigationActions.navigateTo(Screen.OVERVIEW) },
              modifier =
                  Modifier.fillMaxWidth(0.8f).height(36.dp).testTag("ContinueAsGuestButton")) {
                Text("Continue as a guest", fontSize = 16.sp)
              }
        }
      })
}

fun signInWithEmailAndPassword(
    email: String,
    password: String,
    context: Context,
    navigationActions: NavigationActions
) {
  val auth = Firebase.auth
  auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
    if (task.isSuccessful) {
      Log.d("SignInScreen", "signInWithEmail:success")
      Toast.makeText(context, "Email login successful!", Toast.LENGTH_LONG).show()
      // Navigate to the next screen upon successful login
      checkUserProfile(auth.currentUser?.uid, navigationActions, context)
    } else {
      Log.w("SignInScreen", "signInWithEmail:failure", task.exception)
      Toast.makeText(context, "Email login failed!", Toast.LENGTH_LONG).show()
    }
  }
}

@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {
  Button(
      onClick = onSignInClick,
      colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Button color
      shape = RoundedCornerShape(50), // Circular edges for the button
      border = BorderStroke(1.dp, Color.LightGray),
      modifier =
          Modifier.padding(8.dp)
              .height(48.dp) // Adjust height as needed
              .testTag("GoogleSignInButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.8f)) {
              // Load the Google logo from resources
              Image(
                  painter =
                      painterResource(id = R.drawable.google_logo), // Ensure this drawable exists
                  contentDescription = "Google Logo",
                  modifier =
                      Modifier.size(30.dp) // Size of the Google logo
                          .padding(end = 8.dp))

              // Text for the button
              Text(
                  text = "Sign in with Google",
                  color = Color.Gray, // Text color
                  fontSize = 16.sp, // Font size
                  fontWeight = FontWeight.Medium)
            }
      }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit,
    navigationActions: NavigationActions
): ManagedActivityResultLauncher<Intent, ActivityResult> {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
      val account = task.getResult(ApiException::class.java)!!
      val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
      scope.launch {
        val authResult = Firebase.auth.signInWithCredential(credential).await()
        onAuthComplete(authResult)
        val uid = Firebase.auth.currentUser?.uid
        checkUserProfile(uid, navigationActions, context)
      }
    } catch (e: ApiException) {
      onAuthError(e)
    }
  }
}

private fun checkUserProfile(uid: String?, navigationActions: NavigationActions, context: Context) {
  if (uid == null) {
    Toast.makeText(context, "User ID is null, cannot proceed!", Toast.LENGTH_LONG).show()
    return
  }

  val db = Firebase.firestore
  val repository = ProfilesRepositoryFirestore(db)

  repository.getUser(
      uid,
      onSuccess = { userProfile ->
        if (userProfile == null) {
          // If no profile exists, navigate to ProfileCreationScreen
          navigationActions.navigateTo(Screen.CREATE_PROFILE)
        } else {
          // If the profile exists, navigate to the main screen
          navigationActions.navigateTo(Screen.OVERVIEW)
        }
      },
      onFailure = {
        Toast.makeText(context, "Error checking user profile!", Toast.LENGTH_LONG).show()
        Log.e("SignInScreen", "Error checking user profile: ${it.message}")
      })
}
