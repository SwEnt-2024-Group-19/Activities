package com.android.sample.model.notifications

import android.app.Application
import androidx.lifecycle.ViewModel
import com.android.sample.App
import com.android.sample.model.activity.Activity
import com.android.sample.model.profile.ProfileViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val context: Application,
    private val profileViewModel: ProfileViewModel
) : ViewModel() {

    fun scheduleNotification(activity: Activity) {
        val isOwner = activity.creator == profileViewModel.userState.value?.id
        val title = if (isOwner) "Your Activity Tomorrow" else "Joined Activity Tomorrow"

        val app = context as App
        app.notificationHelper.scheduleNotification(
            activityId = activity.uid.toIntOrNull() ?: 0,
            activityName = activity.title,
            activityDate = activity.date.toDate().time,
            notificationTitle = title
        )
    }
}