package com.android.sample.helper

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.android.sample.receivers.NotificationReceiver
import java.util.concurrent.TimeUnit
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationHelperInstrumentedTest {
  private lateinit var notificationHelper: NotificationHelper
  private lateinit var context: Context
  private lateinit var notificationManager: NotificationManager
  private lateinit var alarmManager: AlarmManager

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    notificationHelper = NotificationHelper(context)
  }

  @Test
  fun testNotificationChannelCreation() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = notificationManager.getNotificationChannel("activity_reminders")
      assertTrue(channel != null)
      assertTrue(channel?.importance == NotificationManager.IMPORTANCE_DEFAULT)
    }
  }

  @Test
  @SdkSuppress(minSdkVersion = Build.VERSION_CODES.S)
  fun testScheduleNotification() {
    val activityId = "123"
    val activityName = "Test Activity"
    val activityDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)
    val notificationTitle = "Test Notification"

    notificationHelper.scheduleNotification(
        activityId, activityName, activityDate, notificationTitle)

    // Verify the scheduled PendingIntent
    val expectedRequestCode = activityId.toIntOrNull() ?: activityId.hashCode()
    val pendingIntent =
        PendingIntent.getBroadcast(
            context,
            expectedRequestCode,
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
    assertTrue(pendingIntent != null)
  }
}
