package com.android.sample.model.profile

class MockProfilesRepository : ProfilesRepository {

  private val userProfiles = mutableMapOf<String, User>()
  private val userActivities = mutableMapOf<String, MutableList<String>>()

  override fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
    try {
      // Retrieve the user profile by userId
      val user = userProfiles[userId]
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
      // Add activity to user's activity list
      if (userProfiles.containsKey(userId)) {
        userActivities.computeIfAbsent(userId) { mutableListOf() }.add(activityId)
        onSuccess()
      } else {
        throw Exception("User not found")
      }
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
    TODO("Not yet implemented")
  }

  override fun removeLikedActivity(
    userId: String,
    activityId: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun updateProfile(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    try {
      // Update the user profile in the in-memory map
      userProfiles[user.id] = user
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
    try {
      // Simulate adding a user profile to the database
      userProfiles[userProfile.id] = userProfile
      onSuccess()
    } catch (e: Exception) {
      onFailure(e)
    }
  }
}
