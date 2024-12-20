package com.android.sample.helper

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.android.sample.model.activity.Category
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.User
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito

class NotificationHelperUnitTest {
  private lateinit var notificationHelper: NotificationHelper
  private lateinit var context: Context
  private lateinit var notificationManager: NotificationManager
  private lateinit var alarmManager: AlarmManager
  private lateinit var pendingIntent: PendingIntent
  private lateinit var pendingIntentStatic: MockedStatic<PendingIntent>

  @Before
  fun setup() {
    // Create basic mocks
    context = Mockito.mock(Context::class.java)
    alarmManager = Mockito.mock(AlarmManager::class.java)
    notificationManager = Mockito.mock(NotificationManager::class.java)
    pendingIntent = Mockito.mock(PendingIntent::class.java)

    // Setup system services
    Mockito.`when`(context.getSystemService(Context.ALARM_SERVICE)).thenReturn(alarmManager)
    Mockito.`when`(context.getSystemService(Context.NOTIFICATION_SERVICE))
        .thenReturn(notificationManager)

    // Setup static mock for PendingIntent
    pendingIntentStatic = Mockito.mockStatic(PendingIntent::class.java)
    pendingIntentStatic
        .`when`<PendingIntent> {
          PendingIntent.getBroadcast(
              Mockito.any(), Mockito.anyInt(), Mockito.any(Intent::class.java), Mockito.anyInt())
        }
        .thenReturn(pendingIntent)
  }

  @After
  fun tearDown() {
    pendingIntentStatic.close()
  }

  @Test
  fun testCancelNotification() {
    notificationHelper = NotificationHelper(context)
    notificationHelper.cancelNotification("123")
    Mockito.verify(alarmManager).cancel(pendingIntent)
  }

  @Test
  fun testBasicScheduleNotification() {
    // Given
    notificationHelper = NotificationHelper(context)
    val activityId = "123"
    val activityName = "Test Activity"
    val activityDate = System.currentTimeMillis()
    val notificationTitle = "Test Notification"

    Mockito.`when`(alarmManager.canScheduleExactAlarms()).thenReturn(true)

    // When
    notificationHelper.scheduleNotification(
        activityId, activityName, activityDate, notificationTitle)

    // Then
    Mockito.verify(alarmManager, Mockito.times(2))
        .setExactAndAllowWhileIdle(
            Mockito.eq(AlarmManager.RTC_WAKEUP), Mockito.anyLong(), Mockito.eq(pendingIntent))
  }

  @Test
  fun testSendDeletionNotification() {
    // Given
    notificationHelper = NotificationHelper(context)
    val activityId = "a1"
    val activityName = "Test Activity"
    val notificationTitle = "Activity Deleted"
    val participants =
        listOf(
            User(
                id = "u1",
                name = "Alice",
                surname = "Smith",
                interests =
                    listOf(
                        Interest("Cycling", Category.SPORT), Interest("Running", Category.SPORT)),
                activities = listOf("a1", "a2"),
                photo = null,
                likedActivities = listOf("a1")))

    // When
    notificationHelper.sendDeletionNotification(
        activityId, activityName, notificationTitle, participants)

    // Then
    // Verify that PendingIntent.send() was called for each participant
    Mockito.verify(pendingIntent, Mockito.times(participants.size)).send()

    // Verify that cancelNotification was called
    Mockito.verify(alarmManager).cancel(pendingIntent)
  }

  @Test
  fun testSendDeletionNotificationHandlesSendFailure() {
    // Given
    notificationHelper = NotificationHelper(context)
    val activityId = "a1"
    val activityName = "Test Activity"
    val notificationTitle = "Activity Deleted"
    val participants =
        listOf(
            User(
                id = "u1",
                name = "Alice",
                surname = "Smith",
                interests =
                    listOf(
                        Interest("Cycling", Category.SPORT), Interest("Running", Category.SPORT)),
                activities = listOf("a1", "a2"),
                photo = null,
                likedActivities = listOf("a1")))

    // Setup pendingIntent to throw exception
    Mockito.doThrow(PendingIntent.CanceledException::class.java).`when`(pendingIntent).send()

    // When
    notificationHelper.sendDeletionNotification(
        activityId, activityName, notificationTitle, participants)

    // Then
    // Verify that the code handles the exception
    Mockito.verify(alarmManager).cancel(pendingIntent)
  }
}
