package com.android.sample.model.image

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val repository: ImageRepositoryFirestore) :
    ViewModel() {

  fun uploadProfilePicture(
      userId: String,
      bitmap: Bitmap,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.uploadProfilePicture(userId, bitmap, onSuccess, onFailure)
  }

  fun uploadActivityImages(
      activityId: String,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.uploadActivityImages(activityId, bitmaps, onSuccess, onFailure)
  }

  fun fetchProfileImageUrl(
      userId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.fetchProfileImageUrl(userId, onSuccess, onFailure)
  }

  fun fetchActivityImageUrls(
      activityId: String,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.fetchActivityImageUrls(activityId, onSuccess, onFailure)
  }

  fun fetchActivityImagesAsBitmaps(
      activityId: String,
      onSuccess: (List<Bitmap>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.fetchActivityImagesAsBitmaps(activityId, onSuccess, onFailure)
  }

  fun removeAllActivityImages(
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.removeAllActivityImages(activityId, onSuccess, onFailure)
  }

  fun deleteProfilePicture(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.deleteProfilePicture(userId, onSuccess, onFailure)
  }
}
