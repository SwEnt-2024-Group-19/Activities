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
  /**
   * Uploads a profile picture to Firebase Storage and updates the user's profile URL in Firestore.
   *
   * This function takes a user's ID and a bitmap image, compresses the image to JPEG format, and
   * uploads it to Firebase Storage under a specified path. Upon successful upload, it retrieves the
   * publicly accessible URL of the image and updates the user's profile in Firestore to include
   * this new image URL.
   *
   * @param userId The unique identifier for the user whose profile picture is being updated.
   * @param bitmap The bitmap image to be uploaded as the user's profile picture.
   * @param onSuccess A callback function that is invoked with the URL of the uploaded image upon
   *   successful upload and Firestore update.
   * @param onFailure A callback function that is invoked if any part of the upload or update
   *   process fails.
   */
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
  /**
   * Updates the user's profile photo URL in Firestore.
   *
   * This function is responsible for updating the Firestore document of a specific user with a new
   * profile photo URL. It is typically called after successfully uploading a new profile picture to
   * Firebase Storage and obtaining the public URL for that image. The function also updates the
   * user's Firestore document to reflect the new image URL, ensuring that any part of the
   * application relying on this data is synchronized with the latest user profile image.
   *
   * @param userId The unique identifier for the user whose profile picture URL needs updating.
   * @param photoUrl The new URL pointing to the user's updated profile picture in Firebase Storage.
   * @param onSuccess A callback function that is invoked upon successful update of the user's
   *   profile. The function receives the new photo URL as a parameter.
   * @param onFailure A callback function that is invoked if the update fails. It receives an
   *   Exception detailing the cause of the failure.
   */
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

  /**
   * Initiates the process to upload new activity images by first deleting existing images.
   *
   * This function manages the complete process of updating images for a specific activity by
   * starting with the deletion of any existing images in Firebase Storage, followed by uploading
   * new ones. It utilizes Firebase Storage to handle the images associated with an activity
   * identified by `activityId`.
   *
   * @param activityId The unique identifier of the activity whose images are being managed.
   * @param bitmaps A list of Bitmap objects that are to be uploaded as new activity images.
   * @param onSuccess A callback function invoked with a list of URLs of successfully uploaded
   *   images.
   * @param onFailure A callback function invoked if the process encounters an error at any stage.
   */
  override fun uploadActivityImages(
      activityId: String,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val activityFolderRef = storageRef.child("activities/$activityId")
    deleteExistingImagesThenUploadNewImages(
        activityFolderRef, bitmaps, onSuccess, onFailure, activityId)
  }
  /**
   * Deletes existing images for an activity from Firebase Storage, then uploads new images.
   *
   * This function first attempts to list and delete all existing images in an activity's specific
   * folder. Upon successful deletion or if listing fails, it proceeds to upload new images to
   * ensure the activity's image content is updated.
   *
   * @param activityFolderRef A reference to the Firebase Storage folder where the activity's images
   *   are stored.
   * @param bitmaps A list of new Bitmap images to upload.
   * @param onSuccess Callback invoked with the URLs of the uploaded images upon successful update.
   * @param onFailure Callback invoked upon failure to delete or upload images.
   * @param activityId The ID of the activity being updated, used for logging and error handling.
   */
  fun deleteExistingImagesThenUploadNewImages(
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
  /**
   * Handles the successful listing of existing images by deleting them and then uploading new ones.
   *
   * This function is called after successfully listing existing images in an activity's folder. It
   * schedules deletion of all found images and, upon successful deletion, begins the upload of new
   * images.
   *
   * @param listResult The result from listing images, containing references to existing image
   *   files.
   * @param activityFolderRef Reference to the storage location for activity images.
   * @param bitmaps List of new images to upload.
   * @param onSuccess Callback to invoke with URLs of newly uploaded images.
   * @param onFailure Callback to invoke on failure during deletion or upload.
   * @param activityId ID of the activity for contextual logging and error handling.
   */
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
  /**
   * Uploads a list of images to Firebase Storage and collects their URLs.
   *
   * After deleting old images or when no deletions are needed, this function uploads new images to
   * Firebase Storage. It manages the upload process for each image, collects their URLs upon
   * successful upload, and calls the onSuccess callback with a list of these URLs.
   *
   * @param activityFolderRef Reference to the storage folder for the activity's images.
   * @param bitmaps List of Bitmaps to upload.
   * @param onSuccess Callback invoked with a list of image URLs upon successful uploads.
   * @param onFailure Callback invoked upon any upload failure.
   * @param activityId The ID of the activity for which images are being managed.
   */
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
  /**
   * Handles the successful upload of an individual image, collecting its URL and updating activity
   * status.
   *
   * This function is called for each successful image upload. It retrieves the image's URL, adds it
   * to the list of uploaded URLs, and checks if all images have been uploaded. If all images are
   * uploaded, it updates the Firestore document of the activity with these new image URLs.
   *
   * @param fileRef Reference to the uploaded image file in Firebase Storage.
   * @param uploadedImageUrls List that collects URLs of successfully uploaded images.
   * @param uploadCount Counter array tracking the number of successfully uploaded images.
   * @param totalImages Total number of images that need to be uploaded.
   * @param onSuccess Callback invoked with all image URLs upon successful upload of all images.
   * @param onFailure Callback invoked upon failure to retrieve any image URL.
   * @param activityId The ID of the activity being updated.
   */
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
  /**
   * Finalizes the image upload process by updating the Firestore document with new image URLs.
   *
   * Once all images for an activity have been successfully uploaded and their URLs collected, this
   * function updates the Firestore document of the activity with these URLs. It ensures that the
   * activity's data reflects the most current set of images available.
   *
   * @param uri The URL of the recently uploaded image.
   * @param uploadedImageUrls List of URLs for all successfully uploaded images.
   * @param uploadCount A counter tracking the number of images successfully uploaded.
   * @param totalImages The total number of images that were intended to be uploaded.
   * @param onSuccess Callback to be invoked with all uploaded image URLs upon successful update of
   *   Firestore.
   * @param onFailure Callback to be invoked upon failure to update Firestore.
   * @param activityId Identifier for the activity whose images are being updated.
   */
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
  /**
   * Retrieves the download URL for a user's profile picture from Firebase Storage.
   *
   * This function fetches the URL of the stored profile picture for a given user by accessing
   * Firebase Storage. It ensures the application can dynamically update user interfaces with the
   * user's current profile picture.
   *
   * @param userId The unique identifier for the user whose profile picture URL is being retrieved.
   * @param onSuccess A callback function that is invoked with the URL of the profile picture upon
   *   successful retrieval.
   * @param onFailure A callback function that is invoked if the retrieval process fails.
   */
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
  /**
   * Converts a list of image URLs to Bitmaps while maintaining their order.
   *
   * This function takes a list of URLs pointing to images stored externally, retrieves each image,
   * and converts them into Bitmap format. The Bitmaps are placed in a list maintaining the order
   * they appear in the provided URL list, ensuring consistent positioning for display purposes.
   *
   * @param urls List of URLs to images that need to be converted into Bitmaps.
   * @param onSuccess Callback function called with the list of Bitmaps once all images are
   *   successfully converted.
   * @param onFailure Callback function called if any image conversion fails.
   */

  // Converts image URLs to Bitmaps
  fun convertUrlsToBitmaps(
      urls: List<String>,
      onSuccess: (List<Bitmap>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Initialize a list of nulls with the size of urls to hold the Bitmaps in order
    val bitmaps = MutableList<Bitmap?>(urls.size) { null }
    var successCount = 0

    urls.forEachIndexed { index, url ->
      val imageRef = storage.getReferenceFromUrl(url)
      imageRef
          .getBytes(Long.MAX_VALUE)
          .addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            // Place the bitmap at the correct index
            bitmaps[index] = bitmap
            successCount++
            if (successCount == urls.size) {
              // Filter out any null values and return the list of bitmaps
              onSuccess(bitmaps.filterNotNull())
            }
          }
          .addOnFailureListener { onFailure(it) }
    }
  }
  /**
   * Retrieves and converts all images related to an activity from Firebase Storage into Bitmaps.
   *
   * This function fetches the list of image URLs from a Firestore document related to an activity,
   * orders them by the timestamp embedded in the URL, and converts each URL into a Bitmap. This
   * allows displaying these images in the order they were uploaded.
   *
   * @param activityId The unique identifier of the activity whose images are being fetched.
   * @param onSuccess Callback function called with the list of Bitmaps once all images are
   *   successfully converted.
   * @param onFailure Callback function called if the fetch or conversion fails.
   */
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
          val sortedImageUrls = sortUrlsByTimestamp(imageUrls)
          convertUrlsToBitmaps(sortedImageUrls, onSuccess, onFailure)
        }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Sorts a list of image URLs by extracting and comparing timestamps embedded in the URLs.
   *
   * This utility function orders a list of URLs based on timestamps, which are part of the URL
   * string. This is useful for maintaining a chronological order of images, especially when they
   * are displayed to the user.
   *
   * @param urls A list of image URLs to be sorted.
   * @return A list of URLs sorted by the embedded timestamps.
   */
  fun sortUrlsByTimestamp(urls: List<String>): List<String> {
    return urls.sortedBy { url ->
      // Extract the timestamp part from the URL
      url.substringAfterLast("image_").substringBefore('.').toLongOrNull() ?: 0L
    }
  }
  /**
   * Deletes all images associated with a specific activity from Firebase Storage.
   *
   * This function lists all files in an activity's specific folder in Firebase Storage and deletes
   * them. This is typically used when an activity is being deleted or its images are being
   * completely refreshed.
   *
   * @param activityId The unique identifier of the activity whose images are to be deleted.
   * @param onSuccess Callback function called once all images are successfully deleted.
   * @param onFailure Callback function called if the deletion process encounters an error.
   */
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
}
