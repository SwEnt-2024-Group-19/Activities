package com.android.sample.model.profile

interface ProfilesRepository {
  fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit)

  fun addActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun updateProfile(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun addProfileToDatabase(userProfile: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteActivityFromProfile(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
