package com.android.sample.ui.authentication

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.resources.C.Tag.AUTH_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.BORDER_STROKE_SM
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT_SM
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.CARD_ELEVATION_DEFAULT
import com.android.sample.resources.C.Tag.EXTRA_LARGE_PADDING
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.MIN_PASSWORD_LENGTH
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_SM
import com.android.sample.ui.components.EmailTextField
import com.android.sample.ui.components.PasswordTextField
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun isValidEmail(email: String): Boolean {
  return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun SignUpScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val emailState = remember { mutableStateOf("") }
  val passwordState = remember { mutableStateOf("") }
  val emailErrorState = remember {
    mutableStateOf<String?>(null)
  } // State for email validation error
  val passwordErrorState = remember {
    mutableStateOf<String?>(null)
  } // State for password validation error
  val isPasswordVisible = remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).testTag("SignUpScreenColumn"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          // App Logo Image
          item {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.aptivity_logo_with_text), // Ensure this drawable exists
                contentDescription = "App Logo",
                modifier = Modifier.size((3 * IMAGE_SIZE).dp))
            Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
          }
          item {
            // Email field
            EmailTextField(
                email = emailState.value,
                onEmailChange = {
                  emailState.value = it
                  emailErrorState.value = if (it.isBlank()) "Email cannot be empty" else null
                },
                emailError = emailErrorState.value)
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }
          item {
            // Password field
            PasswordTextField(
                password = passwordState.value,
                onPasswordChange = { passwordState.value = it },
                isPasswordVisible = isPasswordVisible.value,
                onPasswordVisibilityChange = { isPasswordVisible.value = !isPasswordVisible.value },
                passwordError = passwordErrorState.value,
            )
            Spacer(modifier = Modifier.height(EXTRA_LARGE_PADDING.dp))
          }
          item {
            // Sign up button
            Card(
                modifier = Modifier.fillMaxWidth(WIDTH_FRACTION_SM).testTag("SignUpCard"),
                colors =
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                elevation =
                    CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION_DEFAULT.dp),
                shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
            ) {
              OutlinedButton(
                  onClick = {
                    when {
                      !isValidEmail(emailState.value) -> {
                        emailErrorState.value =
                            "Please enter a valid email address" // Set the error message if email
                        // is
                        // invalid
                      }
                      passwordState.value.isEmpty() -> {
                        passwordErrorState.value =
                            "Password cannot be empty" // Set the error message if password is empty
                      }
                      passwordState.value.length < MIN_PASSWORD_LENGTH -> {
                        passwordErrorState.value =
                            "Password must be at least 6 characters long" // Set the error message
                        // for
                        // short passwords
                      }
                      else -> {
                        createUserWithEmailAndPassword(
                            emailState.value,
                            passwordState.value,
                            context,
                            onSuccess = { navigationActions.navigateTo(Screen.CREATE_PROFILE) })
                      }
                    }
                  },
                  shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
                  border =
                      BorderStroke(BORDER_STROKE_SM.dp, Color.Transparent), // Transparent indicator
                  modifier =
                      Modifier.fillMaxWidth().height(AUTH_BUTTON_HEIGHT.dp).testTag("SignUpButton"),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.primary,
                          contentColor = Color.White)) {
                    Text("SIGN UP", fontSize = SUBTITLE_FONTSIZE.sp)
                  }
            }
            Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          }
          item {
            // If user already has an account, navigate to the sign in screen
            TextButton(
                onClick = { navigationActions.navigateTo(Screen.AUTH) },
                modifier =
                    Modifier.width(BUTTON_WIDTH.dp)
                        .height(BUTTON_HEIGHT_SM.dp)
                        .testTag("GoToSignInButton")) {
                  Text("Already an account?", fontSize = SUBTITLE_FONTSIZE.sp)
                }
          }
        }
      })
}

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
      Log.d("UserCreation", "createUserWithEmail:success")
      Toast.makeText(context, "Account created successfully!", Toast.LENGTH_LONG).show()
      onSuccess()
    } else {
      // User creation failed, display an error message
      Log.w("UserCreation", "createUserWithEmail:failure", createTask.exception)
      Toast.makeText(context, "Account creation failed! Please try again.", Toast.LENGTH_LONG)
          .show()
    }
  }
}
