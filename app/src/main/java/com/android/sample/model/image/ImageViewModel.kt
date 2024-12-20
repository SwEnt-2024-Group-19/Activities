package com.android.sample.model.image

import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel
@Inject
constructor(
    private val repository: ImageRepositoryFirestore,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

  /**
   * Uploads a user's profile picture and caches the resulting URL.
   *
   * This function uploads a Bitmap image as a user's profile picture using the repository layer.
   * Upon successful upload, it caches the URL of the uploaded image using SharedPreferences for
   * quick access and then triggers the onSuccess callback with the URL. If the upload fails, it
   * triggers the onFailure callback.
   *
   * @param userId The unique identifier of the user.
   * @param bitmap The Bitmap image to upload as the profile picture.
   * @param onSuccess Callback that receives the URL of the successfully uploaded image.
   * @param onFailure Callback that handles any errors encountered during the upload process.
   */
  fun uploadProfilePicture(
      userId: String,
      bitmap: Bitmap,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.uploadProfilePicture(
        userId,
        bitmap,
        { url ->
          cacheProfilePicture(userId, url)
          onSuccess(url)
        },
        onFailure)
  }

  private fun cacheProfilePicture(userId: String, url: String) {
    sharedPreferences.edit().putString(userId, url).apply()
  }

  /**
   * Uploads multiple images for a specific activity.
   *
   * This function handles the uploading of a list of Bitmap images for a given activity. It
   * delegates the uploading process to the repository layer and passes along the onSuccess and
   * onFailure callbacks to handle the response.
   *
   * @param activityId The identifier for the activity whose images are being uploaded.
   * @param bitmaps A list of Bitmap images to upload.
   * @param onSuccess Callback invoked with a list of URLs for the successfully uploaded images.
   * @param onFailure Callback invoked upon failure to upload the images.
   */
  fun uploadActivityImages(
      activityId: String,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.uploadActivityImages(activityId, bitmaps, onSuccess, onFailure)
  }
  /**
   * Fetches the URL for a user's profile image.
   *
   * Retrieves the URL of a user's profile image from the repository layer. If the fetch is
   * successful, it triggers the onSuccess callback with the URL; if it fails, it triggers the
   * onFailure callback.
   *
   * @param userId The unique identifier of the user whose profile image URL is being fetched.
   * @param onSuccess Callback that receives the URL of the profile image.
   * @param onFailure Callback that handles any errors encountered during the fetch.
   */
  fun fetchProfileImageUrl(
      userId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.fetchProfileImageUrl(userId, onSuccess, onFailure)
  }
  /**
   * Fetches and converts all images related to an activity into Bitmaps.
   *
   * This function retrieves image URLs for a specific activity and converts each URL into a Bitmap.
   * It handles both successful conversion, triggering onSuccess with a list of Bitmaps, and failure
   * by invoking onFailure.
   *
   * @param activityId The identifier for the activity whose images are being fetched.
   * @param onSuccess Callback invoked with a list of Bitmaps once all images are successfully
   *   converted.
   * @param onFailure Callback invoked upon failure to fetch or convert the images.
   */
  fun fetchActivityImagesAsBitmaps(
      activityId: String,
      onSuccess: (List<Bitmap>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.fetchActivityImagesAsBitmaps(activityId, onSuccess, onFailure)
  }
  /**
   * Deletes all images associated with a specific activity.
   *
   * This function initiates the deletion of all images stored in Firebase for a given activity. If
   * the deletion process is successful, it triggers the onSuccess callback; if there are any
   * errors, it triggers the onFailure callback.
   *
   * @param activityId The identifier for the activity whose images are to be deleted.
   * @param onSuccess Callback invoked upon successful deletion of all images.
   * @param onFailure Callback invoked if there is a failure during the deletion process.
   */
  fun removeAllActivityImages(
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.removeAllActivityImages(activityId, onSuccess, onFailure)
  }
}
