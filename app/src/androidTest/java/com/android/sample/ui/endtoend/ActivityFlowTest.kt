package com.android.sample.ui.endtoend

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.rule.GrantPermissionRule
import com.android.sample.MainActivity
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.PermissionChecker
import com.android.sample.model.profile.ProfilesRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
class ActivityFlowTest {

  @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)

  @JvmField @Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Inject lateinit var authRepository: SignInRepository

  @Inject lateinit var profilesRepository: ProfilesRepository

  @Inject lateinit var activitiesRepository: ActivitiesRepository

  @Inject lateinit var locationRepository: LocationRepository

  @Inject lateinit var permissionChecker: PermissionChecker

    @get:Rule
    val composeTestRule = createComposeRule()

    val profilesRepository: ProfilesRepository = MockProfilesRepository()
    val profileViewModel = ProfileViewModel(profilesRepository)

    val activitiesRepository: ActivitiesRepository = MockActivitiesRepository()
    val listActivitiesViewModel = ListActivitiesViewModel(activitiesRepository)

    val signInRepository: SignInRepository = mock(SignInRepository::class.java)
    val authViewModel = SignInViewModel(signInRepository)

    val locationRepository: LocationRepository = mock(LocationRepository::class.java)
    val locationViewModel =
        LocationViewModel(locationRepository, MockLocationPermissionChecker(true))

    private val sampleActivity =
        Activity(
            uid = "1",
            title = "Sample Activity",
            description = "This is a sample activity",
            date = Timestamp.now(),
            location = Location(0.0, 0.0, "origin"),
            creator = "TestCreator",
            price = 1.0,
            images = listOf(),
            placesLeft = 10,
            maxPlaces = 20,
            status = ActivityStatus.ACTIVE,
            type = ActivityType.PRO,
            participants = listOf(),
            startTime = "12:00",
            duration = "1 hour"
        )

  @Before
  fun setUp() {
    hiltRule.inject()
  }
}
