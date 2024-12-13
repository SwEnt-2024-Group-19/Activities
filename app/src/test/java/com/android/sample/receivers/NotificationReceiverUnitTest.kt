package com.android.sample.receivers

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowNotificationManager

@RunWith(RobolectricTestRunner::class)
class NotificationReceiverUnitTest {

  private lateinit var context: Context
  private lateinit var notificationReceiver: NotificationReceiver
  private lateinit var notificationManager: NotificationManager
  private lateinit var shadowNotificationManager: ShadowNotificationManager

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    notificationReceiver = NotificationReceiver()
    notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    shadowNotificationManager = Shadows.shadowOf(notificationManager)
  }

  @Test
  fun `onReceive should create and send notification with valid intent`() {
    val activityId = "123"
    val activityName = "Test Activity"
    val notificationTitle = "Test Title"

    val intent =
        Intent().apply {
          putExtra("activityId", activityId)
          putExtra("activityName", activityName)
          putExtra("notificationTitle", notificationTitle)
        }

    notificationReceiver.onReceive(context, intent)

    // Verify that a notification was posted
    val notifications = shadowNotificationManager.allNotifications
    assert(notifications.isNotEmpty()) { "No notifications were posted" }

    val notification = notifications[0]

    val shadowNotification = Shadows.shadowOf(notification)
    assert(shadowNotification.contentTitle == notificationTitle) {
      "Notification title does not match"
    }
    assert(shadowNotification.contentText == "Reminder: $activityName") {
      "Notification content does not match"
    }
  }

  @Test
  fun `onReceive should create deletion notification with correct content`() {
    val activityId = "123"
    val activityName = "Test Activity"
    val notificationTitle = "Activity Deleted"

    val intent =
        Intent().apply {
          putExtra("activityId", activityId)
          putExtra("activityName", activityName)
          putExtra("notificationTitle", notificationTitle)
          putExtra("isDeletionNotification", true)
          putExtra("userId", "u1")
        }

    notificationReceiver.onReceive(context, intent)

    // Verify that a notification was posted
    val notifications = shadowNotificationManager.allNotifications
    assert(notifications.isNotEmpty()) { "No notifications were posted" }

    val notification = notifications[0]
    val shadowNotification = Shadows.shadowOf(notification)

    assert(shadowNotification.contentTitle == notificationTitle) {
      "Notification title does not match"
    }
    assert(shadowNotification.contentText == "Join or create another activity!") {
      "Deletion notification content does not match"
    }
    assert(notification.category == NotificationCompat.CATEGORY_EVENT) {
      "Notification category should be EVENT for deletion notifications"
    }
  }
}
