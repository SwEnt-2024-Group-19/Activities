package com.android.sample.model.profile

import android.util.Log
import com.android.sample.mockDatabase.MockUsersDatabase

class MockProfilesRepository(private val database: MockUsersDatabase = MockUsersDatabase()) :
    ProfilesRepository {

  override fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
    try {
      val user = database.getUser(userId)
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
    try {
      val user = database.getUser(userId)
      database.updateUser(user.copy(activities = user.activities?.plus(activityId)))
    } catch (e: Exception) {
      onFailure(e)
    }
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
    try {
      database.updateUser(user)


      onSuccess()
    } catch (e: Exception) {
      onFailure(e)
    }
  }

  override fun addProfileToDatabase(
      userProfile: User,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    throw NotImplementedError("Not implemented: addProfileToDatabase")
  }
}
