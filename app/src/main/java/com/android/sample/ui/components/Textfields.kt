package com.android.sample.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Tag.ERROR_TEXTFIELD_FONT_SIZE
import com.android.sample.resources.C.Tag.ERROR_TEXTFIELD_PADDING_START
import com.android.sample.resources.C.Tag.ERROR_TEXTFIELD_PADDING_TOP
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_MD

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    passwordError: String?
) {
  Column(modifier = Modifier.fillMaxWidth(WIDTH_FRACTION_MD)) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("PasswordCard"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border =
            if (passwordError != null) {
              BorderStroke(1.dp, Color.Red) // Apply red border in case of error
            } else null) {
          // Box to remove padding discrepancies
          Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 0.dp)) {
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth().testTag("PasswordTextField"),
                shape = RoundedCornerShape(12.dp),
                visualTransformation =
                    if (isPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                isError = passwordError != null,
                trailingIcon = {
                  IconButton(onClick = onPasswordVisibilityChange) {
                    Icon(
                        imageVector =
                            if (isPasswordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                        contentDescription =
                            if (isPasswordVisible) "Hide password" else "Show password")
                  }
                },
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        errorContainerColor = Color.Transparent))
          }
        }
    passwordError?.let {
      Text(
          text = it,
          color = Color.Red,
          modifier =
              Modifier.align(Alignment.Start)
                  .padding(start = 16.dp, top = 8.dp)
                  .testTag("PasswordErrorText"))
    }
  }
}

@Composable
fun EmailTextField(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
) {
  Column(modifier = Modifier.fillMaxWidth(WIDTH_FRACTION_MD)) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("EmailCard"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border =
            if (emailError != null) {
              BorderStroke(1.dp, Color.Red) // Apply red border in case of error
            } else null) {
          // Box to ensure alignment and padding consistency
          Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 0.dp)) {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().testTag("EmailTextField"),
                shape = RoundedCornerShape(12.dp), // Matches the Card shape
                isError = emailError != null,
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        errorContainerColor = Color.Transparent))
          }
        }
    emailError?.let {
      Text(
          text = it,
          color = Color.Red,
          modifier =
              Modifier.align(Alignment.Start)
                  .padding(start = 16.dp, top = 8.dp)
                  .testTag("EmailErrorText"))
    }
  }
}

@Composable
fun TextFieldWithErrorState(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    validation: (String) -> String?,
    externalError: String? = null,
    errorTestTag: String
) {
  var internalError by remember { mutableStateOf<String?>(null) }

  // Show external error if present, otherwise use internal error
  val error = externalError ?: internalError

  Column() {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
          onValueChange(newValue)
          internalError = validation(newValue)
        },
        label = { Text(label) },
        isError = error != null,
        modifier = modifier)
    error?.let {
      Text(
          text = it,
          color = Color.Red,
          fontSize = ERROR_TEXTFIELD_FONT_SIZE,
          modifier =
              Modifier.align(Alignment.Start)
                  .padding(start = ERROR_TEXTFIELD_PADDING_START, top = ERROR_TEXTFIELD_PADDING_TOP)
                  .testTag(errorTestTag))
    }
  }
}
