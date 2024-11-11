package com.android.sample.ui.authentication

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
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
                    painterResource(id = R.drawable.google_logo), // Ensure this drawable exists
                contentDescription = "App Logo",
                modifier = Modifier.size(110.dp))
          }
          item { Spacer(modifier = Modifier.height(48.dp)) }
          item {
            // Email field
            EmailTextField(
                email = emailState.value,
                onEmailChange = {
                  emailState.value = it
                  emailErrorState.value = null // Clear error when user starts typing
                },
                emailError = emailErrorState.value)
          }
          // Display email error message below the email field
          if (emailErrorState.value != null) {
            item {
              Text(
                  text = emailErrorState.value ?: "",
                  color = Color.Red,
                  fontSize = 12.sp,
                  modifier =
                      Modifier.align(Alignment.Start)
                          .padding(start = 40.dp, top = 4.dp)
                          .testTag("EmailErrorText"))
            }
          }
          item { Spacer(modifier = Modifier.height(16.dp)) }
          item {
            // Password field
            PasswordTextField(
                password = passwordState.value,
                onPasswordChange = {
                  passwordState.value = it
                  passwordErrorState.value = null // Clear error when user starts typing
                },
                isPasswordVisible = isPasswordVisible.value,
                onPasswordVisibilityChange = { isPasswordVisible.value = !isPasswordVisible.value })
          }
          // Display password error message below the password field
          if (passwordErrorState.value != null) {
            item {
              Text(
                  text = passwordErrorState.value ?: "",
                  color = Color.Red,
                  fontSize = 12.sp,
                  modifier =
                      Modifier.align(Alignment.Start)
                          .padding(start = 40.dp, top = 4.dp)
                          .testTag("PasswordErrorText"))
            }
          }
          item { Spacer(modifier = Modifier.height(16.dp)) }
          item {
            // Sign up button
            Button(
                onClick = {
                  when {
                    !isValidEmail(emailState.value) -> {
                      emailErrorState.value =
                          "Please enter a valid email address" // Set the error message if email is
                      // invalid
                    }
                    passwordState.value.isEmpty() -> {
                      passwordErrorState.value =
                          "Password cannot be empty" // Set the error message if password is empty
                    }
                    passwordState.value.length < 6 -> {
                      passwordErrorState.value =
                          "Password must be at least 6 characters long" // Set the error message for
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
                modifier = Modifier.fillMaxWidth(0.8f).height(48.dp).testTag("SignUpButton")) {
                  Text("Sign up with Email", fontSize = 16.sp)
                }
          }
          item { Spacer(modifier = Modifier.height(16.dp)) }
          item {
            // If user already has an account, navigate to the sign in screen
            TextButton(
                onClick = { navigationActions.navigateTo(Screen.AUTH) },
                modifier = Modifier.fillMaxWidth(0.8f).height(36.dp).testTag("GoToSignInButton")) {
                  Text("Already an account?", fontSize = 16.sp)
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
