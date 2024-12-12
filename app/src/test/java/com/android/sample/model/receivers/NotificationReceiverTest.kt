package com.android.sample.receivers

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class NotificationReceiverTest {

  private lateinit var receiver: NotificationReceiver
  private lateinit var context: Context
  private lateinit var notificationManager: NotificationManager
  private lateinit var resources: Resources

  @Before
  fun setup() {
    context = mock()
    notificationManager = mock()
    resources = mock()

    whenever(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager)
    whenever(context.resources).thenReturn(resources)
    whenever(resources.getBoolean(any())).thenReturn(true)

    receiver = NotificationReceiver()
  }

  @Test
  fun testNotificationNotCreatedWithMissingActivityId() {
    val intent = Intent()
    receiver.onReceive(context, intent)
    verify(notificationManager, never()).notify(any(), any())
  }

  @Test
  fun testNotificationNotCreatedWithMissingActivityName() {
    val intent =
        Intent().apply {
          putExtra("activityId", "123")
          // Missing activityName
          putExtra("notificationTitle", "Test Title")
        }
    receiver.onReceive(context, intent)
    verify(notificationManager, never()).notify(any(), any())
  }

  @Test
  fun testNotificationNotCreatedWithMissingTitle() {
    val intent =
        Intent().apply {
          putExtra("activityId", "123")
          putExtra("activityName", "Test Activity")
          // Missing title
        }
    receiver.onReceive(context, intent)
    verify(notificationManager, never()).notify(any(), any())
  }

  @Test
  fun testNotificationWithNullActivityId() {
    val intent =
        Intent().apply {
          putExtra("activityId", null as String?)
          putExtra("activityName", "Test Activity")
          putExtra("notificationTitle", "Test Title")
        }
    receiver.onReceive(context, intent)
    verify(notificationManager, never()).notify(any(), any())
  }

  @Test
  fun testNotificationWithNullActivityName() {
    val intent =
        Intent().apply {
          putExtra("activityId", "123")
          putExtra("activityName", null as String?)
          putExtra("notificationTitle", "Test Title")
        }
    receiver.onReceive(context, intent)
    verify(notificationManager, never()).notify(any(), any())
  }
}
