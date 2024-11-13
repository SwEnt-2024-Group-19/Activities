package com.android.sample.ui

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

fun uploadActivityImages(
    activityId: String,
    bitmaps: List<Bitmap>,
    onSuccess: (List<String>) -> Unit,
    onFailure: (Exception) -> Unit
) {
  val storageRef = FirebaseStorage.getInstance().reference
  val uploadedImageUrls = mutableListOf<String>()
  var uploadCount = 0

  // Loop through each bitmap to upload them individually
  bitmaps.forEach { bitmap ->
    val timestamp = System.currentTimeMillis()
    val activityImageRef = storageRef.child("activities/$activityId/image_$timestamp.jpg")

    // Convert Bitmap to ByteArray
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
    val data = baos.toByteArray()

    // Upload the Bitmap data to Firebase Storage
    activityImageRef
        .putBytes(data)
        .addOnSuccessListener {
          // Get the download URL after successful upload
          activityImageRef.downloadUrl
              .addOnSuccessListener { uri ->
                uploadedImageUrls.add(uri.toString()) // Add URL to the list
                uploadCount++

                // If all images have been uploaded, save URLs to Firestore
                if (uploadCount == bitmaps.size) {
                  val activityDocRef =
                      FirebaseFirestore.getInstance().collection("activities").document(activityId)

                  // Store all URLs in the 'images' field of the activity document
                  activityDocRef
                      .update("images", FieldValue.arrayUnion(*uploadedImageUrls.toTypedArray()))
                      .addOnSuccessListener { onSuccess(uploadedImageUrls) }
                      .addOnFailureListener { e -> onFailure(e) }
                }
              }
              .addOnFailureListener { e ->
                onFailure(e)
                return@addOnFailureListener
              }
        }
        .addOnFailureListener { e ->
          onFailure(e)
          return@addOnFailureListener
        }
  }
}

fun updateActivityImages(
    activityId: String,
    existingImageUrls: List<String>,
    bitmaps: List<Bitmap>, // This now includes potentially new and old bitmaps
    onSuccess: (List<String>) -> Unit,
    onFailure: (Exception) -> Unit
) {
  val storageRef = FirebaseStorage.getInstance().reference
  val newImageUrls = mutableListOf<String>()
  var uploadCount = bitmaps.size // We assume all bitmaps could potentially be new uploads

  bitmaps.forEach { bitmap ->
    val timestamp = System.currentTimeMillis()
    val activityImageRef = storageRef.child("activities/$activityId/image_$timestamp.jpg")

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
    val data = baos.toByteArray()

    activityImageRef
        .putBytes(data)
        .addOnSuccessListener {
          activityImageRef.downloadUrl.addOnSuccessListener { uri ->
            newImageUrls.add(uri.toString())
            uploadCount--
            if (uploadCount == 0) {
              // Combine existing URLs with new URLs and remove duplicates
              val finalUrls = (existingImageUrls + newImageUrls).distinct()
              finalizeUpdate(activityId, finalUrls, onSuccess, onFailure)
            }
          }
        }
        .addOnFailureListener { onFailure(it) }
  }
}

private fun finalizeUpdate(
    activityId: String,
    imageUrls: List<String>,
    onSuccess: (List<String>) -> Unit,
    onFailure: (Exception) -> Unit
) {
  val activityDocRef = FirebaseFirestore.getInstance().collection("activities").document(activityId)
  activityDocRef
      .update("images", imageUrls)
      .addOnSuccessListener { onSuccess(imageUrls) }
      .addOnFailureListener { onFailure(it) }
}

fun uriToBitmap(uri: Uri, context: Context): Bitmap? {
  return try {
    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
  } catch (e: IOException) {
    e.printStackTrace()
    null
  }
}

fun fetchProfileImageUrl(
    userId: String,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
  val storageRef = FirebaseStorage.getInstance().reference
  val profilePicRef = storageRef.child("users/$userId/profile_picture.jpg")

  profilePicRef.downloadUrl
      .addOnSuccessListener { uri ->
        onSuccess(uri.toString()) // Return the URL as a string
      }
      .addOnFailureListener { exception -> onFailure(exception) }
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
        Card(modifier = Modifier.padding(4.dp)) {
          Image(
              painter =
                  rememberAsyncImagePainter(
                      model =
                          ImageRequest.Builder(LocalContext.current)
                              .data(imageUrl)
                              .size(Size(100, 100)) // Restrict Coil to load a 100x100 image
                              .build()),
              contentDescription = "Selected Image",
              modifier = Modifier.size(100.dp))
        }
      }
    }
  }
}

// Firestore function to fetch URLs (same as before)
fun fetchActivityImageUrls(
    activityId: String,
    onSuccess: (List<String>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    firestore
        .collection("activities")
        .document(activityId)
        .get()
        .addOnSuccessListener { document ->
            if (document != null && document.contains("images")) {
                val imageUrls = document["images"] as? List<String> ?: emptyList()
                onSuccess(imageUrls) // Return the list of URLs
            } else {
                onSuccess(emptyList()) // Return empty list if no images found
            }
        }
        .addOnFailureListener { exception -> onFailure(exception) }
}
