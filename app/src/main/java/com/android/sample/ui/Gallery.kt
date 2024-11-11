package com.android.sample.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

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

@Composable
fun ProfileImage(url: String?, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val painter =
      rememberAsyncImagePainter(
          model =
              ImageRequest.Builder(context)
                  .apply {
                    // Only parse the URI and set it if the URL is not null and not empty
                    if (!url.isNullOrEmpty()) {
                      data(Uri.parse(url))
                    } else {
                      data(null) // Handle null or empty string by not attempting to load anything
                    }
                    crossfade(true)
                  }
                  .build())

  Image(
      painter = painter,
      contentDescription = "Profile Image",
      modifier = modifier,
      contentScale = ContentScale.Crop)
}
