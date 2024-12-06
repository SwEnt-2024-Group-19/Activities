package com.android.sample.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.sample.R
import com.android.sample.model.activity.Category
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
fun ProfileImage(
    userId: String,
    modifier: Modifier = Modifier,
    imageViewModel: ImageViewModel,
    editing: Boolean = false,
    bitmap: Bitmap? = null,
    onRemoveImage: () -> Unit = {}
) {
  var imageUrl by remember { mutableStateOf<String?>(null) }
  var isImageRemoved by remember { mutableStateOf(false) }
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

  LaunchedEffect(bitmap) {
    if (bitmap != null && editing) {
      isImageRemoved = false
    } else if (bitmap == null && imageUrl == null && editing) {
      isImageRemoved = true
    }
  }
  LaunchedEffect(imageUrl) {
    if (imageUrl != null && editing) {
      isImageRemoved = false
    }
  }

  val defaultImageId = remember(userId) { randomDefaultProfileImage() }
  // Determine which painter to use
  val painter =
      when {
        editing && bitmap != null -> {
          // Use the bitmap provided when editing
          rememberAsyncImagePainter(model = ImageRequest.Builder(context).data(bitmap).build())
        }
        editing && isImageRemoved -> {
          // Use the default monkey picture when the image is marked as removed
          painterResource(id = defaultImageId)
        }
        !imageUrl.isNullOrEmpty() -> {
          // Use the profile image URL from Firebase Storage, with caching via Coil
          rememberAsyncImagePainter(
              model =
                  ImageRequest.Builder(context).data(Uri.parse(imageUrl)).crossfade(true).build())
        }
        else -> {
          // Fallback to the default image
          painterResource(id = defaultImageId)
        }
      }

  Box() {
    Image(
        painter = painter,
        contentDescription = "Profile Image",
        modifier = modifier,
        contentScale = ContentScale.Crop)

    // Show the "X" button to remove the image when editing
    if (editing && !isImageRemoved) {
      IconButton(
          onClick = {
            isImageRemoved = true // Mark the image as removed
            onRemoveImage() // Trigger external action if needed
          },
          modifier =
              Modifier.align(Alignment.TopCenter)
                  .size(24.dp)
                  .background(color = Color.Red, shape = CircleShape)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove Image",
                tint = Color.White)
          }
    }
  }
}

fun randomDefaultProfileImage(): Int {
  // List of drawable resource IDs
  val defaultImages =
      listOf(
          R.drawable.dog_avatar,
          R.drawable.cat_avatar,
          R.drawable.fox_avatar,
          R.drawable.bull_avatar,
          R.drawable.pig_avatar,
          R.drawable.chicken_avatar,
          R.drawable.panda_avatar,
          R.drawable.reindeer_avatar,
          R.drawable.monkey_avatar)

  // Remember a random default image ID when recomposed
  val defaultImageId = defaultImages.random()

  return defaultImageId
}

fun getImageResourceIdForCategory(category: Category): Int {
  return when (category) {
    Category.SPORT -> R.drawable.sports_image
    Category.CULTURE -> R.drawable.culture_image
    Category.ENTERTAINMENT -> R.drawable.entertainment_image
    Category.SKILLS -> R.drawable.skills_image // Fallback image
  }
}
