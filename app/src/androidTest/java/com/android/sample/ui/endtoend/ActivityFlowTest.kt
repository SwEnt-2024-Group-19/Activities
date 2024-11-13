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
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          android.Manifest.permission.CAMERA)

  @Before
  fun setUp() {
    hiltRule.inject()
  }
}
