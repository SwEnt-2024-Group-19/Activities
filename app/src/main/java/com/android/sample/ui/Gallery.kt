package com.android.sample.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.sample.model.camera.ImageViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

@Composable
fun GalleryScreen(isGalleryOpen: () -> Unit, addImage: (Bitmap) -> Unit, context: Context) {
    val imageViewModel : ImageViewModel = hiltViewModel()
  val launcher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri?
        ->
        uri?.let {
          val bitmap = imageViewModel.uriToBitmap(it, context)
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
    val imageViewModel : ImageViewModel = hiltViewModel()
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

fun uploadProfilePicture(
    userId: String,
    bitmap: Bitmap,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
  val storageRef = FirebaseStorage.getInstance().reference
  val profilePicRef = storageRef.child("users/$userId/profile_picture.jpg")

  val baos = ByteArrayOutputStream()
  bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
  val data = baos.toByteArray()

  profilePicRef
      .putBytes(data)
      .addOnSuccessListener {
        profilePicRef.downloadUrl.addOnSuccessListener { uri ->
          // Save the URL to Firestore
          FirebaseFirestore.getInstance()
              .collection("users")
              .document(userId)
              .update("photo", uri.toString())
              .addOnSuccessListener { onSuccess(uri.toString()) }
              .addOnFailureListener { e -> onFailure(e) }
        }
      }
      .addOnFailureListener { e ->
        onFailure(e)
        Log.e("uploadProfilePicture", "Failed to upload profile picture: ${e.message}")
      }
}
