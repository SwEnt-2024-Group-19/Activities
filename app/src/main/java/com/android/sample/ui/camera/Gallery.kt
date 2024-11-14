package com.android.sample.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.android.sample.model.camera.fetchActivityImageUrls
import com.android.sample.model.camera.fetchProfileImageUrl
import com.android.sample.model.camera.uriToBitmap
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

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
fun ProfileImage(userId: String, modifier: Modifier = Modifier) {
  var imageUrl by remember { mutableStateOf<String?>(null) }
  val context = LocalContext.current

  // Fetch the profile image URL from Firebase Storage
  LaunchedEffect(userId) {
    fetchProfileImageUrl(
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

@Composable
fun ActivityImageCarousel(activityId: String, onFailure: (Exception) -> Unit) {
  var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

  // Fetch the image URLs from Firestore
  LaunchedEffect(activityId) {
    fetchActivityImageUrls(
        activityId = activityId,
        onSuccess = { urls ->
          imageUrls = urls // Update the state with the list of URLs
        },
        onFailure = { exception ->
          onFailure(exception) // Handle the error (e.g., show a message)
        })
  }

  // Display the carousel if image URLs are available
  if (imageUrls.isNotEmpty()) {
    LazyRow {
      items(imageUrls.size) { index ->
        val imageUrl = imageUrls[index]
        Card(modifier = Modifier.padding(SMALL_PADDING.dp)) {
          Image(
              painter =
                  rememberAsyncImagePainter(
                      model =
                          ImageRequest.Builder(LocalContext.current)
                              .data(imageUrl)
                              .size(Size(IMAGE_SIZE, IMAGE_SIZE)) // Restrict Coil to load a 100x100 image
                              .build()),
              contentDescription = "Selected Image",
              modifier = Modifier.size(IMAGE_SIZE.dp))
        }
      }
    }
  }
}

