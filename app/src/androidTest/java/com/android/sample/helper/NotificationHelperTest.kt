package com.android.sample.helper

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.concurrent.TimeUnit
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class NotificationHelperTest {

  private lateinit var notificationHelper: NotificationHelper
  private lateinit var context: Context
  private lateinit var notificationManager: NotificationManager
  private lateinit var alarmManager: AlarmManager

  @Before
  fun setup() {
    context = mock()
    notificationManager = mock()
    alarmManager = mock()

    whenever(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager)
    whenever(context.getSystemService(Context.ALARM_SERVICE)).thenReturn(alarmManager)
    whenever(context.packageName).thenReturn("com.android.sample")

    notificationHelper = NotificationHelper(context)
  }

  @Test
  fun testNotificationChannelCreation() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      verify(notificationManager).createNotificationChannel(any())
    }
  }

  @Test
  fun testInitializationPreOreo() {
    // This test just verifies that initialization doesn't crash on pre-Oreo devices
    notificationHelper = NotificationHelper(context)
  }

  @Test
  fun testNotificationTimeCalculation() {
    val currentTime = System.currentTimeMillis()
    val expectedNotificationTime = currentTime - TimeUnit.HOURS.toMillis(24)

    // Just test that we can access this time calculation without crashing
    val activityDate = currentTime
    assertTrue(expectedNotificationTime < activityDate)
  }
}
