package com.android.sample.model.activity

import com.android.sample.mockDatabase.MockActivitiesDatabase
import java.util.UUID

class MockActivitiesRepository(
    private val database: MockActivitiesDatabase = MockActivitiesDatabase()
) : ActivitiesRepository {

  override fun getNewUid(): String {
    return UUID.randomUUID().toString()
  }

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getActivities(onSuccess: (List<Activity>) -> Unit, onFailure: (Exception) -> Unit) {
    try {
      onSuccess(database.getActivities())
    } catch (e: Exception) {
      onFailure(e)
    }
  }

  override fun addActivity(
      activity: Activity,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    throw NotImplementedError("Not needed for mock repository")
    /*try {
      // Simulate adding an activity
      database.getActivities().add(activity)
      onSuccess()
    } catch (e: Exception) {
      onFailure(e)
    }*/
  }

  override fun updateActivity(
      activity: Activity,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      database.updateActivity(activity)
      onSuccess()
    } catch (e: Exception) {
      onFailure(e)
    }
  }

  override fun deleteActivityById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    throw NotImplementedError("Not needed for mock repository")
    /*try {
      // Simulate deleting an activity by id
      val activityToRemove = activities.find { it.uid == id }
      if (activityToRemove != null) {
        activities.remove(activityToRemove)
        onSuccess()
      } else {
        throw Exception("Activity not found")
      }
    } catch (e: Exception) {
      onFailure(e)
    }*/
  }
}
