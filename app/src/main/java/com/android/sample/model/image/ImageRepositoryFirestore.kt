package com.android.sample.model.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

  override fun uploadActivityImages(
      activityId: String,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val activityFolderRef = storageRef.child("activities/$activityId")
    activityFolderRef
        .listAll()
        .addOnSuccessListener { listResult ->
          val deletionTasks = listResult.items.map { it.delete() }
          Tasks.whenAllSuccess<Void>(deletionTasks)
              .addOnSuccessListener {
                val uploadedImageUrls = mutableListOf<String>()
                var uploadCount = 0

                bitmaps.forEach { bitmap ->
                  val fileRef = activityFolderRef.child("image_${System.currentTimeMillis()}.jpg")
                  val baos = ByteArrayOutputStream()
                  bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, baos)

                  fileRef
                      .putBytes(baos.toByteArray())
                      .addOnSuccessListener {
                        fileRef.downloadUrl.addOnSuccessListener { uri ->
                          uploadedImageUrls.add(uri.toString())
                          uploadCount++
                          if (uploadCount == bitmaps.size) {
                            firestore
                                .collection("activities")
                                .document(activityId)
                                .update("images", uploadedImageUrls)
                                .addOnSuccessListener { onSuccess(uploadedImageUrls) }
                                .addOnFailureListener { onFailure(it) }
                          }
                        }
                      }
                      .addOnFailureListener { onFailure(it) }
                }
              }
              .addOnFailureListener { onFailure(it) }
        }
        .addOnFailureListener { onFailure(it) }
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
          // Handle failure to list files
          onFailure(exception)
        }
  }
}
