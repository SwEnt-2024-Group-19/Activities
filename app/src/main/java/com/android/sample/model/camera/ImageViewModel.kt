package com.android.sample.model.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    applicationContext: Context
) {
  controller.takePicture(
      ContextCompat.getMainExecutor(applicationContext),
      object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
          super.onCaptureSuccess(image)

          val matrix = Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
          val rotatedBitmap =
              Bitmap.createBitmap(image.toBitmap(), 0, 0, image.width, image.height, matrix, true)

          onPhotoTaken(rotatedBitmap)
        }

        override fun onError(exception: ImageCaptureException) {
          super.onError(exception)
          Toast.makeText(
                  applicationContext,
                  "Error taking picture: ${exception.message}",
                  Toast.LENGTH_SHORT)
              .show()
        }
      })
}

fun bitmapToBase64(bitmap: Bitmap): String {
  val byteArrayOutputStream = ByteArrayOutputStream()
  bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream)
  val byteArray = byteArrayOutputStream.toByteArray()
  return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun base64ToBitmap(encodedString: String): Bitmap? {
  return try {
    val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
  } catch (e: IllegalArgumentException) {
    e.printStackTrace()
    null
  }
}

fun flipCamera(cameraSelector: CameraSelector): CameraSelector {
  if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
      return CameraSelector.DEFAULT_FRONT_CAMERA
  else return CameraSelector.DEFAULT_BACK_CAMERA
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

fun Bitmap.resize(reqWidth: Int, reqHeight: Int): Bitmap {
  val ratio: Float = this.width.toFloat() / this.height.toFloat()
  val height = (reqWidth / ratio).toInt()
  return Bitmap.createScaledBitmap(this, reqWidth, height, true)
}
