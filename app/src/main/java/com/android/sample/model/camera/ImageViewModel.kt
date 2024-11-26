package com.android.sample.model.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Tasks
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
  val activityFolderRef = storageRef.child("activities/$activityId")

  // First, list all existing images in the activity folder
  activityFolderRef
      .listAll()
      .addOnSuccessListener { listResult ->
        // Create a list of tasks for deleting each file
        val deletionTasks = listResult.items.map { it.delete() }

        // Wait for all deletions to complete
        Tasks.whenAllSuccess<Void>(deletionTasks)
            .addOnSuccessListener {
              // After all files are deleted, start uploading new images
              val uploadedImageUrls = mutableListOf<String>()
              var uploadCount = 0

              bitmaps.forEach { bitmap ->
                val timestamp = System.currentTimeMillis()
                val fileRef = storageRef.child("activities/$activityId/image_$timestamp.jpg")
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                fileRef
                    .putBytes(data)
                    .addOnSuccessListener {
                      fileRef.downloadUrl
                          .addOnSuccessListener { uri ->
                            uploadedImageUrls.add(uri.toString())
                            uploadCount++
                            // Check if all images have been uploaded
                            if (uploadCount == bitmaps.size) {
                              FirebaseFirestore.getInstance()
                                  .collection("activities")
                                  .document(activityId)
                                  .update("images", uploadedImageUrls)
                                  .addOnSuccessListener { onSuccess(uploadedImageUrls) }
                                  .addOnFailureListener { onFailure(it) }
                            }
                          }
                          .addOnFailureListener { onFailure(it) }
                    }
                    .addOnFailureListener { onFailure(it) }
              }
            }
            .addOnFailureListener { onFailure(it) }
      }
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
        val imageUrls = document["images"] as? List<String> ?: emptyList()
        onSuccess(imageUrls)
      }
      .addOnFailureListener { exception -> onFailure(exception) }
}

fun fetchActivityImagesAsBitmaps(
    activityId: String,
    onSuccess: (List<Bitmap>) -> Unit,
    onFailure: (Exception) -> Unit
) {
  fetchActivityImageUrls(
      activityId, { urls -> fetchBitmapsFromUrls(urls, onSuccess, onFailure) }, onFailure)
}

private fun fetchBitmapsFromUrls(
    urls: List<String>,
    onSuccess: (List<Bitmap>) -> Unit,
    onFailure: (Exception) -> Unit
) {
  val bitmaps = mutableListOf<Bitmap>()
  var successCount = 0

  if (urls.isEmpty()) {
    onSuccess(emptyList())
    return
  }

  urls.forEach { url ->
    val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
    imageRef
        .getBytes(Long.MAX_VALUE)
        .addOnSuccessListener { bytes ->
          val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
          bitmaps.add(bitmap)
          successCount++

          if (successCount == urls.size) {
            onSuccess(bitmaps)
          }
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }
}

fun Bitmap.resize(reqWidth: Int, reqHeight: Int): Bitmap {
  val ratio: Float = this.width.toFloat() / this.height.toFloat()
  val height = (reqWidth / ratio).toInt()
  return Bitmap.createScaledBitmap(this, reqWidth, height, true)
}
