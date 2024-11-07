package com.android.sample.ui.endtoend

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.MockActivitiesRepository
import com.android.sample.model.profile.MockProfilesRepository
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.ui.navigation.NavigationActions
import com.google.firebase.Timestamp
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.junit4.MockKRule
import junit.framework.TestCase
import org.junit.Rule
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class YourFlowTests : TestCase() {
  @get:Rule var hiltRule = HiltAndroidRule(this)
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  private lateinit var navActions: NavigationActions

  private var userRepository: ProfilesRepository = MockProfilesRepository()
  private var activityRepository: ActivitiesRepository = MockActivitiesRepository()

  private val sampleActivity =
      Activity(
          uid = "1",
          title = "Sample Activity",
          description = "This is a sample activity",
          date = Timestamp.now(),
          location = "Sample Location",
          creator = "Sample Creator",
          price = 1.0,
          images = listOf(),
          placesLeft = 10,
          maxPlaces = 20,
          status = ActivityStatus.ACTIVE,
          type = ActivityType.PRO,
          participants = listOf(),
          startTime = "12:00",
          duration = "1 hour")
}
