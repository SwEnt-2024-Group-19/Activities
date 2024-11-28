package com.android.sample.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.image.uriToBitmap

@Composable
fun GalleryScreen(isGalleryOpen: () -> Unit, addImage: (Bitmap) -> Unit, context: Context) {
  val launcher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri?
        ->
        uri?.let {
          val bitmap = uriToBitmap(it, context)
          bitmap?.let { bmp ->
            addImage(bmp)
            isGalleryOpen()
          }
        }
      }

  LaunchedEffect(key1 = true) { launcher.launch("image/*") }
  DisposableEffect(Unit) {
    onDispose {
      isGalleryOpen() // Ensure the screen closes even if no image is selected
    }
  }
}

@Composable
fun ProfileImage(userId: String, modifier: Modifier = Modifier, imageViewModel: ImageViewModel) {
  var imageUrl by remember { mutableStateOf<String?>(null) }
  val context = LocalContext.current

  // Fetch the profile image URL from Firebase Storage
  LaunchedEffect(userId) {
    imageViewModel.fetchProfileImageUrl(
        userId = userId,
        onSuccess = { url -> imageUrl = url },
        onFailure = { error ->
          Log.e("ProfileImage", "Failed to fetch image URL: ${error.message}")
        })
  }

  val painter =
      rememberAsyncImagePainter(
          model =
              ImageRequest.Builder(context)
                  .apply {
                    // Only parse the URI if the URL is not null and not empty
                    if (!imageUrl.isNullOrEmpty()) {
                      data(Uri.parse(imageUrl))
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
