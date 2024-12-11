package com.android.sample.helper

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.android.sample.model.profile.User
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
            val channel = NotificationChannel(
                channelId, "Activity Reminders", NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Reminders for upcoming activities" }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(
        activityId: String, activityName: String, activityDate: Long, notificationTitle: String
    ) {
        // cancel notification in case it already exists and just got updated
        cancelNotification(activityId)
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("activityId", activityId)
            putExtra("activityName", activityName)
            putExtra("notificationTitle", notificationTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            activityId.toIntOrNull() ?: activityId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationTime = activityDate - TimeUnit.HOURS.toMillis(24) // 24h before

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent
                    )
                } else {
                    Log.e("NotificationHelper", "Cannot schedule exact alarms")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "SecurityException: ${e.message}")
        }
    }

    fun cancelNotification(activityId: String) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            activityId.toIntOrNull() ?: activityId.hashCode(),
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }


    fun sendDeletionNotification(
        activityId: String,
        activityName: String,
        notificationTitle: String,
        participants: List<User>

    ) {
        participants.forEach { participant ->
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("activityId", activityId)
                putExtra("activityName", activityName)
                putExtra("notificationTitle", notificationTitle)
                //we set this flag to true to differentiate deletion notifications from schedule ones (that depend on time)
                putExtra("isDeletionNotification", true)
                putExtra("userId", participant.id)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                "${participant.id}_${activityId}".hashCode(),
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                pendingIntent.send()
            } catch (e: PendingIntent.CanceledException) {
                Log.e("NotificationHelper", "Failed to send deletion notification", e)
            }
        }

        // Cancel any existing scheduled notifications
        cancelNotification(activityId)

    }
}
