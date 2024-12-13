package com.android.sample.model.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import javax.inject.Inject

open class ImageRepositoryFirestore
@Inject
constructor(private val firestore: FirebaseFirestore, private val storage: FirebaseStorage) :
    ImageRepository {
  private val storageRef = storage.reference
  private val compressionQuality = 50

  override fun uploadProfilePicture(
      userId: String,
      bitmap: Bitmap,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val profilePicRef = storageRef.child("users/$userId/profile_picture.jpg")
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, baos)

    profilePicRef
        .putBytes(baos.toByteArray())
        .addOnSuccessListener {
          profilePicRef.downloadUrl.addOnSuccessListener { uri ->
              firestore
              .collection("users")
              .document(userId)
              .update("photo", uri.toString())
              .addOnSuccessListener { onSuccess(uri.toString()) }
              .addOnFailureListener { onFailure(it) }
          }
        }
        .addOnFailureListener { onFailure(it) }
  }

  /*fun updateFirestoreUserPhoto(
      userId: String,
      photoUrl: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    firestore
        .collection("users")
        .document(userId)
        .update("photo", photoUrl)
        .addOnSuccessListener { onSuccess(photoUrl) }
        .addOnFailureListener { onFailure(it) }
  }*/

  override fun uploadActivityImages(
      activityId: String,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val activityFolderRef = storageRef.child("activities/$activityId")
    deleteExistingImages(activityFolderRef, bitmaps, onSuccess, onFailure)
  }

  fun deleteExistingImages(
      activityFolderRef: StorageReference,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    activityFolderRef
        .listAll()
        .addOnSuccessListener { listResult ->
          handleDeletionSuccess(listResult, activityFolderRef, bitmaps, onSuccess, onFailure)
        }
        .addOnFailureListener(onFailure)
  }

  fun handleDeletionSuccess(
      listResult: ListResult,
      activityFolderRef: StorageReference,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val deletionTasks = listResult.items.map { it.delete() }
    Tasks.whenAllSuccess<Void>(deletionTasks)
        .addOnSuccessListener {
          uploadImagesAndCollectUrls(activityFolderRef, bitmaps, onSuccess, onFailure)
        }
        .addOnFailureListener(onFailure)
  }

  fun uploadImagesAndCollectUrls(
      activityFolderRef: StorageReference,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val uploadedImageUrls = mutableListOf<String>()
    val uploadCount = intArrayOf(0) // Using an array to hold mutable integer

    bitmaps.forEach { bitmap ->
      val fileRef = activityFolderRef.child("image_${System.currentTimeMillis()}.jpg")
      val baos = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, baos)

      fileRef
          .putBytes(baos.toByteArray())
          .addOnSuccessListener {
            handleImageUploadSuccess(
                fileRef,
                uploadedImageUrls,
                uploadCount,
                bitmaps.size,
                onSuccess,
                onFailure,
                activityFolderRef.parent!!.path)
          }
          .addOnFailureListener(onFailure)
    }
  }

  fun handleImageUploadSuccess(
      fileRef: StorageReference,
      uploadedImageUrls: MutableList<String>,
      uploadCount: IntArray,
      totalImages: Int,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit,
      activityId: String
  ) {
    fileRef.downloadUrl.addOnSuccessListener { uri ->
      processSuccessfulUpload(
          uri.toString(),
          uploadedImageUrls,
          uploadCount,
          totalImages,
          onSuccess,
          onFailure,
          activityId)
    }
  }

  fun processSuccessfulUpload(
      uri: String,
      uploadedImageUrls: MutableList<String>,
      uploadCount: IntArray,
      totalImages: Int,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit,
      activityId: String
  ) {
    uploadedImageUrls.add(uri)
    uploadCount[0]++ // Increment the mutable integer in the array
    if (uploadCount[0] == totalImages) {
      firestore
          .collection("activities")
          .document(activityId)
          .update("images", uploadedImageUrls)
          .addOnSuccessListener { onSuccess(uploadedImageUrls) }
          .addOnFailureListener(onFailure)
    }
  }

  override fun fetchProfileImageUrl(
      userId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val profilePicRef = storageRef.child("users/$userId/profile_picture.jpg")
    profilePicRef.downloadUrl
        .addOnSuccessListener { uri -> onSuccess(uri.toString()) }
        .addOnFailureListener { onFailure(it) }
  }

  override fun fetchActivityImageUrls(
      activityId: String,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val firestore = firestore
    val collection = firestore.collection("activities")

    collection
        .document(activityId)
        .get()
        .addOnSuccessListener { document ->
          val imageUrls = document["images"] as? List<String> ?: emptyList()
          onSuccess(imageUrls)
        }
        .addOnFailureListener { onFailure(it) }
  }

  override fun fetchActivityImagesAsBitmaps(
      activityId: String,
      onSuccess: (List<Bitmap>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    fetchActivityImageUrls(
        activityId,
        { urls ->
          val bitmaps = mutableListOf<Bitmap>()
          var successCount = 0

          urls.forEach { url ->
            val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
            imageRef
                .getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                  val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                  bitmaps.add(bitmap)
                  successCount++
                  if (successCount == urls.size) onSuccess(bitmaps)
                }
                .addOnFailureListener { onFailure(it) }
          }
        },
        onFailure)
  }

  override fun removeAllActivityImages(
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val activityFolderRef = storageRef.child("activities/$activityId")

    // List all files in the activity's folder
    activityFolderRef
        .listAll()
        .addOnSuccessListener { listResult ->

          // Create deletion tasks for each file
          val deletionTasks = listResult.items.map { it.delete() }

          // Execute all deletion tasks
          Tasks.whenAll(deletionTasks)
              .addOnSuccessListener {
                // Once all files are successfully deleted
                onSuccess()
              }
              .addOnFailureListener { exception ->
                // Handle any failure in the deletion process
                onFailure(exception)
              }
        }
        .addOnFailureListener { exception ->
          Log.e(
              "ImageRepositoryFirestore",
              "Failed to delete images for activity: $activityId",
              exception)
          // Handle failure to list files
          onFailure(exception)
        }
  }

  override fun deleteProfilePicture(
      userId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val profilePicRef = storageRef.child("users/$userId/profile_picture.jpg")

    profilePicRef
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }
}
