package com.android.sample

import android.app.Application
import com.android.sample.helper.NotificationHelper
import com.android.sample.model.activity.Activity
import dagger.hilt.android.HiltAndroidApp
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class App : Application() {
  lateinit var notificationHelper: NotificationHelper

  companion object {
    private lateinit var instance: App

    fun getInstance(): App {
      return instance
    }
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    notificationHelper = NotificationHelper(this)
  }

  fun scheduleNotification(activity: Activity, isCreator: Boolean) {
    val title = if (isCreator) "Your Activity Tomorrow" else "Joined Activity Tomorrow"

    val activityDate = activity.date.toDate().time
    val activityTime = LocalTime.parse(activity.startTime)

    val activityDateTime = activityDate +
            TimeUnit.HOURS.toMillis(activityTime.hour.toLong()) +
            TimeUnit.MINUTES.toMillis(activityTime.minute.toLong())

    notificationHelper.scheduleNotification(
      activityId = activity.uid.toIntOrNull() ?: 0,
      activityName = activity.title,
      activityDate = activityDateTime,
      notificationTitle = title
    )
  }

}

