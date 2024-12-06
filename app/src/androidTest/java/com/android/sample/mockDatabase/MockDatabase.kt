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
  fun getUser(userId: String): User? {
    return users.find { it.id == userId }
  }
}

/**
 * Mock database for activities.
 *
 * @param activities: List of activities
 */
data class MockActivitiesDatabase(private val activities: List<Activity> = e2e_Activities) {
  fun getActivity(activityId: String): Activity? {
    return activities.find { it.uid == activityId }
  }
}
