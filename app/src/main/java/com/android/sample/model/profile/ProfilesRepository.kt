package com.android.sample.model.profile

interface ProfilesRepository {
  fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit)

  fun addCreatedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

    fun addJoinedActivity(
        userId: String,
        activityId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

  fun addLikedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun removeLikedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun removeJoinedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun updateProfile(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun addProfileToDatabase(userProfile: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
