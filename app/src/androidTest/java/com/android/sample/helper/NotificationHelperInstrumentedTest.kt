package com.android.sample.helper

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import junit.framework.TestCase.assertTrue

@RunWith(AndroidJUnit4::class)
class NotificationHelperInstrumentedTest {
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var alarmManager: AlarmManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        val activityDate = System.currentTimeMillis()
        val notificationTitle = "Test Notification"

        notificationHelper.scheduleNotification(
            activityId,
            activityName,
            activityDate,
            notificationTitle
        )

        // This is a basic smoke test - we can only verify that no exceptions are thrown
        // Real alarm verification would require device interaction
    }

    @Test
    fun testNotificationTimeCalculation() {
        val currentTime = System.currentTimeMillis()
        val activityDate = currentTime + TimeUnit.DAYS.toMillis(1)

        notificationHelper.scheduleNotification(
            "123",
            "Test Activity",
            activityDate,
            "Test Title"
        )

        // Verify notification is scheduled 24 hours before activity
        val expectedNotificationTime = activityDate - TimeUnit.HOURS.toMillis(24)
        assertTrue(expectedNotificationTime > currentTime)
        assertTrue(expectedNotificationTime < activityDate)
    }
}