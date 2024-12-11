package com.android.sample

import android.app.Application
import com.android.sample.helper.NotificationHelper
import com.android.sample.model.activity.Activity
import com.android.sample.model.hour_date.HourDateViewModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    lateinit var notificationHelper: NotificationHelper

    companion object {
        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        notificationHelper = NotificationHelper(this)
    }

    fun scheduleNotification(activity: Activity, isCreator: Boolean) {
        val title =
            if (isCreator) "Your Activity is in less than 24h!"
            else "The Activity you joined is in less than 24h!"

        val hourDateViewModel = HourDateViewModel()
        val activityTimestamps =
            hourDateViewModel.combineDateAndTime(activity.date, activity.startTime)
        val activityDate = activityTimestamps.toInstant().toEpochMilli()

        notificationHelper.scheduleNotification(
            activityId = activity.uid,
            activityName = activity.title,
            activityDate = activityDate,
            notificationTitle = title
        )
    }

    fun sendDeletionNotification(activity: Activity) {
        // No need for isCreator parameter since here we don't make distinction between participant or creator
        val title = "Activity '${activity.title}' has been cancelled :("

        notificationHelper.sendDeletionNotification(
            activityId = activity.uid,
            activityName = activity.title,
            notificationTitle = title,
            participants = activity.participants
        )
    }
}
