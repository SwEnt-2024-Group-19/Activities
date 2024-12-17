package com.android.sample.model.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
            updateFirestoreUserPhoto(userId, uri.toString(), onSuccess, onFailure)
          }
        }
        .addOnFailureListener { onFailure(it) }
  }

  fun updateFirestoreUserPhoto(
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
  }

  override fun uploadActivityImages(
      activityId: String,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val activityFolderRef = storageRef.child("activities/$activityId")
    deleteExistingImagesThenUploadNewImages(activityFolderRef, bitmaps, onSuccess, onFailure, activityId)
  }

  fun deleteExistingImages(
      activityFolderRef: StorageReference,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit,
      activityId: String
  ) {
    activityFolderRef
        .listAll()
        .addOnSuccessListener { listResult ->
          handleDeletionSuccess(
              listResult, activityFolderRef, bitmaps, onSuccess, onFailure, activityId)
        }
        .addOnFailureListener {
          // Even if listing or deletion fails, try to upload new images
          uploadImagesAndCollectUrls(activityFolderRef, bitmaps, onSuccess, onFailure, activityId)
        }
  }

  fun handleDeletionSuccess(
      listResult: ListResult,
      activityFolderRef: StorageReference,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit,
      activityId: String
  ) {
    val deletionTasks = listResult.items.map { it.delete() }
    Tasks.whenAllSuccess<Void>(deletionTasks).addOnCompleteListener {
      uploadImagesAndCollectUrls(activityFolderRef, bitmaps, onSuccess, onFailure, activityId)
    }
  }

  fun uploadImagesAndCollectUrls(
      activityFolderRef: StorageReference,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit,
      activityId: String
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
                activityId)
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
          .addOnFailureListener { onFailure(it) }
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

  // Converts image URLs to Bitmaps
  fun convertUrlsToBitmaps(
      urls: List<String>,
      onSuccess: (List<Bitmap>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val bitmaps = mutableListOf<Bitmap>()
    var successCount = 0

    urls.forEach { url ->
      val imageRef = storage.getReferenceFromUrl(url)
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
          .addOnFailureListener { onFailure(it) }
    }
  }

  override fun fetchActivityImagesAsBitmaps(
      activityId: String,
      onSuccess: (List<Bitmap>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {

    val collection = firestore.collection("activities")
    collection
        .document(activityId)
        .get()
        .addOnSuccessListener { document ->
          val imageUrls = document["images"] as? List<String> ?: emptyList()
          convertUrlsToBitmaps(imageUrls, onSuccess, onFailure)
        }
        .addOnFailureListener { onFailure(it) }
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
              .addOnSuccessListener { onSuccess() }
              .addOnFailureListener { onFailure(it) }
        }
        .addOnFailureListener { onFailure(it) }
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
