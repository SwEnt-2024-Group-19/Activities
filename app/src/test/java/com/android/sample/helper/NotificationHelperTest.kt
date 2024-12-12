package com.android.sample.helper

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.junit.Before
import org.junit.Test
import org.junit.After
import org.mockito.Mockito
import org.mockito.MockedStatic

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
        Mockito.`when`(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager)

        // Setup static mock for PendingIntent
        pendingIntentStatic = Mockito.mockStatic(PendingIntent::class.java)
        pendingIntentStatic.`when`<PendingIntent> {
            PendingIntent.getBroadcast(
                Mockito.any(),
                Mockito.anyInt(),
                Mockito.any(Intent::class.java),
                Mockito.anyInt()
            )
        }.thenReturn(pendingIntent)
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
            activityId,
            activityName,
            activityDate,
            notificationTitle
        )

        // Then
        Mockito.verify(alarmManager, Mockito.times(2)).setExactAndAllowWhileIdle(
            Mockito.eq(AlarmManager.RTC_WAKEUP),
            Mockito.anyLong(),
            Mockito.eq(pendingIntent)
        )
    }
}