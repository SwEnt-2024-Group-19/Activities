package com.android.sample.model.image

import android.graphics.Bitmap

/**
 * Interface for an image repository that provides methods to upload and fetch images related to
 * users and activities.
 */
interface ImageRepository {
  /**
   * Uploads a profile picture for a specific user.
   *
   * @param userId The unique identifier for the user.
   * @param bitmap The Bitmap image to upload.
   * @param onSuccess Callback invoked with the URL of the successfully uploaded image.
   * @param onFailure Callback invoked if the upload fails.
   */
  fun uploadProfilePicture(
      userId: String,
      bitmap: Bitmap,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  )
  /**
   * Uploads multiple images for a specified activity.
   *
   * @param activityId The unique identifier for the activity.
   * @param bitmaps List of Bitmap images to upload.
   * @param onSuccess Callback invoked with a list of URLs for the successfully uploaded images.
   * @param onFailure Callback invoked if the upload fails.
   */
  fun uploadActivityImages(
      activityId: String,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  )
  /**
   * Fetches the URL of a user's profile image.
   *
   * @param userId The unique identifier of the user.
   * @param onSuccess Callback invoked with the URL of the profile image.
   * @param onFailure Callback invoked if fetching the image URL fails.
   */
  fun fetchProfileImageUrl(
      userId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  )
  /**
   * Fetches and converts all images related to an activity into Bitmaps.
   *
   * @param activityId The identifier of the activity.
   * @param onSuccess Callback invoked with a list of Bitmaps once all images are successfully
   *   converted.
   * @param onFailure Callback invoked if the fetch or conversion fails.
   */
  fun fetchActivityImagesAsBitmaps(
      activityId: String,
      onSuccess: (List<Bitmap>) -> Unit,
      onFailure: (Exception) -> Unit
  )
  /**
   * Removes all images associated with a specific activity.
   *
   * This method is responsible for deleting all images stored for a given activity, usually when
   * the activity is deleted or the images are to be refreshed.
   *
   * @param activityId The identifier for the activity whose images are to be deleted.
   * @param onSuccess Callback invoked once all images are successfully deleted.
   * @param onFailure Callback invoked if the deletion process encounters an error.
   */
  fun removeAllActivityImages(
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
