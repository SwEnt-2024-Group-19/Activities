package com.android.sample.model.profile

class MockProfilesRepository : ProfilesRepository {

  private val userProfiles =
      mutableMapOf<String, User>(
          "u1" to
              User(
                  id = "u1",
                  name = "Alice",
                  surname = "Smith",
                  interests = listOf(Interest("Sport", "Hiking"), Interest("Sport", "Cycling")),
                  activities = listOf("a1", "a2"),
                  photo = null,
                  likedActivities = listOf("a1")))
  private val activitiesList =
      mutableMapOf<String, MutableList<String>>("u1" to mutableListOf("a1", "a2"))

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
        activitiesList.computeIfAbsent(userId) { mutableListOf() }.add(activityId)
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

  override fun removeJoinedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (userProfiles.containsKey(userId)) {
      activitiesList[userId]?.remove(activityId)
      onSuccess()
    } else {
      onFailure(Exception("User not found"))
    }
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
