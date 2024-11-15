package com.android.sample.ui.endtoend

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import org.junit.Test

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

  @Test
  fun aGuestTriesToLookAtAnActivity() {
    // Opens the app as a guest
    composeTestRule.onNodeWithTag("ContinueAsGuestButton").performClick()
    composeTestRule.waitForIdle()

    // Checks that is not connected in profile
    composeTestRule.onNodeWithTag("Profile").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("loadingText").assertIsDisplayed()

    // Goes back to the main screen and tries to filter activities
    composeTestRule.onNodeWithTag("Overview").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("listActivitiesScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonSOLO").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("emptyActivityPrompt").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonPRO").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()

    // Opens the activity details
    composeTestRule.onNodeWithTag("activityCard").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("activityDetailsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("notLoggedInText").assertExists()
  }

  fun aUserTriesToLookAtAnActivity() {
    // Signs in
    composeTestRule.onNodeWithTag("SignInButton").performClick()
    composeTestRule.waitForIdle()

    // create an activity
    composeTestRule.onNodeWithTag("Overview").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("Add Activity").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Activity Title")
    composeTestRule.onNodeWithTag("inputDescriptionCreate").performTextInput("Activity Description")
    composeTestRule.onNodeWithTag("inputDateCreate").performTextInput("12/12/2025")
    composeTestRule.onNodeWithTag("inputStartTime").performTextInput("15:30")
    composeTestRule.onNodeWithTag("inputDurationCreate").performTextInput("00:30")
    composeTestRule.onNodeWithTag("inputPriceCreate").performTextInput("13")
    composeTestRule.onNodeWithTag("inputPlacesCreate").performTextInput("7")
    composeTestRule.onNodeWithTag("inputLocationCreate").performTextInput("Activity Location")
    composeTestRule.onNodeWithTag("createButton").performClick()
    composeTestRule.waitForIdle()

    // check in profile that the activity was added
    composeTestRule.onNodeWithTag("Profile").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("profileScreen").assertExists()
    composeTestRule.onNodeWithTag("activityCreated").assertExists()
    composeTestRule.onNodeWithTag("activityCreated").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("editActivityScreen").assertExists()
    composeTestRule.onNodeWithTag("inputTitleEdit").assertTextEquals("Activity Title")
    composeTestRule.onNodeWithTag("inputTitleEdit").performTextInput("edited Title")
    composeTestRule.onNodeWithTag("inputDescriptionEdit").assertTextEquals("Activity Description")
    composeTestRule.onNodeWithTag("inputDescriptionEdit").performTextInput("edited Description")
    composeTestRule.onNodeWithTag("inputDateEdit").assertTextEquals("12/12/2025")
    composeTestRule.onNodeWithTag("inputDateEdit").performTextInput("12/12/2026")
    composeTestRule.onNodeWithTag("inputStartTimeEdit").assertTextEquals("15:30")
    composeTestRule.onNodeWithTag("inputStartTimeEdit").performTextInput("16:30")
    composeTestRule.onNodeWithTag("inputDurationEdit").assertTextEquals("00:30")
    composeTestRule.onNodeWithTag("inputDurationEdit").performTextInput("01:30")
    composeTestRule.onNodeWithTag("inputPriceEdit").assertTextEquals("13")
    composeTestRule.onNodeWithTag("inputPriceEdit").performTextInput("15")
    composeTestRule.onNodeWithTag("inputPlacesEdit").assertTextEquals("7")
    composeTestRule.onNodeWithTag("inputPlacesEdit").performTextInput("10")
    composeTestRule.onNodeWithTag("inputLocationEdit").assertTextEquals("Activity Location")
    composeTestRule.onNodeWithTag("inputLocationEdit").performTextInput("edited Location")
    composeTestRule.onNodeWithTag("editButton").performClick()
    composeTestRule.waitForIdle()

    // Opens the activity details
    composeTestRule.onNodeWithTag("activityCard").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("activityDetailsScreen").assertExists()
    composeTestRule.onNodeWithTag("image").assertExists()
    composeTestRule.onNodeWithTag("titleText").assertTextEquals("edited Title")
    composeTestRule.onNodeWithTag("descriptionText").assertTextEquals("edited Description")
    composeTestRule.onNodeWithTag("priceText").assertTextEquals("15")
    composeTestRule.onNodeWithTag("locationText").assertTextEquals("edited Location")
    composeTestRule.onNodeWithTag("scheduleText").assertTextEquals("12/12/2026 at 16:30")
    composeTestRule.onNodeWithTag("durationText").assertTextEquals("01:30")
    composeTestRule.onNodeWithTag("numberParticipants").assertTextEquals("Participants: 1/10")
    composeTestRule.onNodeWithTag("participants").assertExists()
    composeTestRule.onNodeWithTag("profileImage").assertExists()

    // like the activity and check it in liked activities
    composeTestRule.onNodeWithTag("likeButton").assertExists()
    composeTestRule.onNodeWithTag("likeButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("goBackButton").assertExists()
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("LikedActivities").assertExists()
    composeTestRule.onNodeWithTag("LikedActivities").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("activityCard").assertExists()
    composeTestRule.onNodeWithTag("activityCard").performClick()
  }
}
