package com.android.sample.model.profile

import com.android.sample.mockDatabase.MockUsersDatabase

class MockProfilesRepository(private val database: MockUsersDatabase = MockUsersDatabase()) :
    ProfilesRepository {

  override fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
    try {
      val user =
          database.getUser(userId)
              ?: throw Exception(
                  "User not found: if you are expecting this to pass, make sure you have the user in the e2e_IdToUser map")
      onSuccess(user)
    } catch (e: Exception) {
      onFailure(e)
    }
  }

  override fun addActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    throw NotImplementedError("Not implemented: addActivity")
  }

  override fun addLikedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    throw NotImplementedError("Not implemented: addLikedActivity")
  }

  override fun removeLikedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    throw NotImplementedError("Not implemented: removeLikedActivity")
  }

  override fun removeJoinedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    throw NotImplementedError("Not implemented: removeJoinedActivity")
  }

  override fun updateProfile(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    throw NotImplementedError("Not implemented: updateProfile")
  }

  override fun addProfileToDatabase(
      userProfile: User,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    throw NotImplementedError("Not implemented: addProfileToDatabase")
  }
}
