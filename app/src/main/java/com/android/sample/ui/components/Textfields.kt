package com.android.sample.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import com.android.sample.resources.C.Tag.ERROR_TEXTFIELD_FONT_SIZE
import com.android.sample.resources.C.Tag.ERROR_TEXTFIELD_PADDING_START
import com.android.sample.resources.C.Tag.ERROR_TEXTFIELD_PADDING_TOP
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.WIDTH_FRACTION

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    passwordError: String?
) {
  Column(modifier = Modifier.fillMaxWidth(WIDTH_FRACTION)) {
    OutlinedTextField(
        value = password,
        modifier = Modifier.fillMaxWidth().testTag("PasswordTextField"),
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        visualTransformation =
            if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = passwordError != null,
        trailingIcon = {
          IconButton(onClick = onPasswordVisibilityChange) {
            Icon(
                imageVector =
                    if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = if (isPasswordVisible) "Hide password" else "Show password")
          }
        },
    )
    passwordError?.let {
      Text(
          text = it,
          color = Color.Red,
          fontSize = SUBTITLE_FONTSIZE.sp,
          modifier =
              Modifier.align(Alignment.Start)
                  .padding(start = MEDIUM_PADDING.dp, top = SMALL_PADDING.dp)
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
  Column(modifier = Modifier.fillMaxWidth(WIDTH_FRACTION)) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        isError = emailError != null,
        modifier = Modifier.fillMaxWidth().testTag("EmailTextField"))

    // Display the error message if there is an error
    emailError?.let {
      Text(
          text = it,
          color = Color.Red,
          fontSize = SUBTITLE_FONTSIZE.sp,
          modifier =
              Modifier.align(Alignment.Start)
                  .padding(start = MEDIUM_PADDING.dp, top = SMALL_PADDING.dp)
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
