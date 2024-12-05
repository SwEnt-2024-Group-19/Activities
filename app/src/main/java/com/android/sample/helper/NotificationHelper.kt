package com.android.sample.helper

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.android.sample.receivers.NotificationReceiver
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) {
  private val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  private val channelId = "activity_reminders"

  init {
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel =
          NotificationChannel(
                  channelId, "Activity Reminders", NotificationManager.IMPORTANCE_DEFAULT)
              .apply { description = "Reminders for upcoming activities" }
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun scheduleNotification(
      activityId: Int,
      activityName: String,
      activityDate: Long,
      notificationTitle: String
  ) {
    val intent =
        Intent(context, NotificationReceiver::class.java).apply {
          putExtra("activityId", activityId)
          putExtra("activityName", activityName)
          putExtra("notificationTitle", notificationTitle)
        }

    val pendingIntent =
        PendingIntent.getBroadcast(
            context,
            activityId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val notificationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1) // 1 mn before activty
    //val notificationTime = activityDate - TimeUnit.HOURS.toMillis(24) // 24h before

    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
          alarmManager.setExactAndAllowWhileIdle(
              AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
        } else {
          Log.e("NotificationHelper", "Cannot schedule exact alarms")
        }
      } else {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
      }
    } catch (e: SecurityException) {
      Log.e("NotificationHelper", "SecurityException: ${e.message}")
    }
  }
}
