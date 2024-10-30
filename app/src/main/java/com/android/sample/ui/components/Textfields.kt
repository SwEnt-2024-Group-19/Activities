package com.android.sample.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    passwordError: String? = null,
) {
  OutlinedTextField(
      value = password,
      onValueChange = onPasswordChange,
      label = { Text("Password") },
      isError = passwordError != null,
      visualTransformation =
          if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      trailingIcon = {
        IconButton(onClick = onPasswordVisibilityChange) {
          Icon(
              imageVector =
                  if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
              contentDescription = if (isPasswordVisible) "Hide password" else "Show password")
        }
      },
      modifier = Modifier.fillMaxWidth(0.8f).testTag("PasswordTextField"))

  // Show password error if it exists
  passwordError?.let {
    Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
  }
}

@Composable
fun EmailTextField(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
) {
  OutlinedTextField(
      value = email,
      onValueChange = onEmailChange,
      label = { Text("Email") },
      isError = emailError != null,
      modifier = Modifier.fillMaxWidth(0.8f).testTag("EmailTextField"))
  emailError?.let {
    Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
  }
}
