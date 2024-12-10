package com.android.sample.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.android.sample.R

class NotificationReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (!intent.hasExtra("activityId")) {
      return
    }

    val activityId = intent.getStringExtra("activityId") ?: return
    // Convert string ID to int for notification ID
    val notificationId = activityId.toIntOrNull() ?: activityId.hashCode()

    // Early return if required notification data is missing, preventing incomplete notification
    val activityName = intent.getStringExtra("activityName") ?: return
    val title = intent.getStringExtra("notificationTitle") ?: return

    val notification =
        NotificationCompat.Builder(context, "activity_reminders")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText("Reminder: $activityName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, notification)
  }
}
