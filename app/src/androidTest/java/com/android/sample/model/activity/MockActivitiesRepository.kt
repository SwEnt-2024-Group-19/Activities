package com.android.sample.model.activity

import com.android.sample.model.map.Location
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar

class MockActivitiesRepository : ActivitiesRepository {

  private val activities =
      mutableListOf<Activity>(
          Activity(
              "uid0",
              "Activity 1",
              "Description 1",
              date = Timestamp(GregorianCalendar(2050, Calendar.JANUARY, 1).time),
              "10:00",
              "09:00",
              category = Category.SPORT,
              10.0,
              Location(0.0, 0.0, "origin"),
              "creator",
              images = listOf(),
              placesLeft = 5,
              maxPlaces = 10,
              status = ActivityStatus.ACTIVE,
              type = ActivityType.PRO,
              participants = listOf(),
              comments = listOf()))

  private var currentId = 0

  override fun getNewUid(): String {
    return "uid${currentId++}"
  }

  override fun init(onSuccess: () -> Unit) {
    // Simulate initialization (you could add mock data here if needed)
    // activities.clear() // Clear any existing data
    onSuccess()
  }

  override fun getActivities(onSuccess: (List<Activity>) -> Unit, onFailure: (Exception) -> Unit) {
    try {
      // Simulate fetching activities from an in-memory list
      onSuccess(activities)
    } catch (e: Exception) {
      onFailure(e)
    }
  }

  override fun addActivity(
      activity: Activity,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      // Simulate adding an activity
      activities.add(activity)
      onSuccess()
    } catch (e: Exception) {
      onFailure(e)
    }
  }

  override fun updateActivity(
      activity: Activity,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      // Simulate updating an activity
      val index = activities.indexOfFirst { it.uid == activity.uid }
      if (index != -1) {
        activities[index] = activity
        onSuccess()
      } else {
        throw Exception("Activity not found")
      }
    } catch (e: Exception) {
      onFailure(e)
    }
  }

  override fun deleteActivityById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
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
    }
  }
}
