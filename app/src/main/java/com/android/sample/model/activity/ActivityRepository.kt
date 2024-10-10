package com.android.sample.model.activity

interface ActivityRepository {
  fun getNewUid(): String

  fun init(onSuccess: () -> Unit)

  fun getActivities(onSuccess: (List<Activity>) -> Unit, onFailure: (Exception) -> Unit)

  fun addActivity(activity: Activity, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateActivity(activity: Activity, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteActivityById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
