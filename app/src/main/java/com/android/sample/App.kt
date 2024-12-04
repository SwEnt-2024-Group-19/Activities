package com.android.sample

import android.app.Application
import com.android.sample.helper.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp class App : Application() {
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
    }

}

