package com.android.sample.mockDatabase

import com.android.sample.model.activity.Activity
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.e2e_Activities
import com.android.sample.resources.dummydata.e2e_Users

/**
 * Mock database for users. Keep in mind that login credentials are static and are not stored in
 * this database
 *
 * @param users: List of users
 */
data class MockUsersDatabase(private val users: List<User> = e2e_Users) {
  fun getUser(userId: String): User {
    return users.find { it.id == userId }
        ?: throw IllegalArgumentException("User with id $userId does not exist")
  }

  fun updateUser(user: User) {
    val index = users.indexOfFirst { it.id == user.id }
    if (index == -1) {
      throw IllegalArgumentException("User with id ${user.id} does not exist")
    }
  }
}

/**
 * Mock database for activities.
 *
 * @param activities: List of activities
 */
data class MockActivitiesDatabase(private val activities: List<Activity> = e2e_Activities) {

  /** Get an activity by its ID */
  fun getActivity(activityId: String): Activity? {
    return activities.find { it.uid == activityId }
  }

  /** Get all activities */
  fun getActivities(): List<Activity> {
    return activities
  }

  /** Add an activity */
  fun addActivity(activity: Activity) {
    activities.toMutableList().add(activity)
  }

  /**
   * Update an activity
   *
   * @throws IllegalArgumentException if the activity does not exist
   */
  fun updateActivity(activity: Activity) {
    val index = activities.indexOfFirst { it.uid == activity.uid }
    if (index == -1) {
      throw IllegalArgumentException("Activity with id ${activity.uid} does not exist")
    }
  }
}
