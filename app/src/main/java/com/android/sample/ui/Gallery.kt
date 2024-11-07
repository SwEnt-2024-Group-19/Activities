package com.android.sample.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun ImagePicker(onImagePicked: (Uri?) -> Unit, buttonText: String = "Select Image") {
  // Launches the gallery picker
  val pickImageLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onImagePicked(uri) // Pass the selected URI back
      }
  Button(onClick = { pickImageLauncher.launch("image/*") }, Modifier.testTag("uploadPicture")) {
    Text(buttonText)
  }
}
