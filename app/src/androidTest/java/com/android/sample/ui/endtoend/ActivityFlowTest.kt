package com.android.sample.ui.endtoend

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import com.android.sample.MainActivity
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.image.ImageRepository
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.PermissionChecker
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.resources.dummydata.defaultUserCredentials
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

  @Inject lateinit var imageRepository: ImageRepository

  @Inject lateinit var imageRepositoryFirestore: ImageRepositoryFirestore

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          android.Manifest.permission.CAMERA,
          android.Manifest.permission.POST_NOTIFICATIONS)

  @Before
  fun setUp() {
    hiltRule.inject()
  }

  // @Test // Will be fixed in a future PR
  fun guestCanSeeCorrectOverviewAndNavigateToActivityDetails() {
    // Opens the app as a guest
    composeTestRule.onNodeWithTag("ContinueAsGuestButton").performClick()
    composeTestRule.waitForIdle()

    // Goes back to the main screen and tries to filter activities
    composeTestRule.onNodeWithTag("Overview").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("listActivitiesScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()
    composeTestRule.onNodeWithText("Activity 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("likeButtontrue").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("segmentedButtonRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonCULTURE").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("emptyActivityPrompt").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonSPORT").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()

    // Opens the activity details
    composeTestRule.onNodeWithTag("activityCard").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("activityDetailsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topAppBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("image").assertIsDisplayed()
    composeTestRule.onNodeWithTag("title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("titleText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("price").assertIsDisplayed()
    composeTestRule.onNodeWithTag("priceText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("location").assertIsDisplayed()
    composeTestRule.onNodeWithTag("locationText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("schedule").assertIsDisplayed()
    composeTestRule.onNodeWithTag("scheduleText").assertIsDisplayed()

    composeTestRule.onNodeWithTag("notLoggedInText").assertExists()
    composeTestRule.onNodeWithTag("goBackButton").assertExists()
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    composeTestRule.onNodeWithTag("listActivitiesScreen").assertIsDisplayed()
  }

  // @Test // Will be fixed in a future PR
  fun guestShouldSignUpForOtherFunctionalities() {
    composeTestRule.onNodeWithTag("ContinueAsGuestButton").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("Liked").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("notConnectedPrompt").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInButton").assertExists()
    composeTestRule.onNodeWithTag("signInButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("SignUpScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoToSignInButton").assertExists()
    composeTestRule.onNodeWithTag("GoToSignInButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("ContinueAsGuestButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("Profile").performClick()
    composeTestRule.waitForIdle()

    // Tries to create a new activity and is prompted to sign in
    composeTestRule.onNodeWithTag("Add Activity").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("inputTitleCreate").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertExists()
    composeTestRule.onNodeWithTag("inputDateCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPriceCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertExists()
    composeTestRule.onNodeWithTag("inputLocationCreate").assertExists()
    composeTestRule.onNodeWithTag("chooseTypeMenu").assertExists()
    composeTestRule.onNodeWithTag("addAttendeeButton").assertExists()
    composeTestRule.onNodeWithTag("createButton").assertExists()
    composeTestRule.onNodeWithTag("Map").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Map").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("centerOnCurrentLocation").assertIsDisplayed()
  }

  @Test
  fun aUserSignsInAndLooksAtTheirProfile() {
    val email =
        defaultUserCredentials[
            "email"]!! // use of !! is allowed because this is a test environment and we know the
    // key exists. if it doesn't, it is up to the developer to fix the test
    val password = defaultUserCredentials["password"]!!

    // Tries to log in but fails because of wrong credentials
    composeTestRule.onNodeWithTag("SignInButton").performClick()
    composeTestRule.waitForIdle()

    //  Enters credentials then connects
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput(email)
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput(password)
    composeTestRule.onNodeWithTag("SignInButton").performClick()
    composeTestRule.waitForIdle()

    // Checks in the profile that the user is connected
    composeTestRule.onNodeWithTag("Profile").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText(defaultUserCredentials["full name"]!!).assertIsDisplayed()
  }

  /*
   @Test
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
     composeTestRule.onNodeWithTag("Description").performTextInput("Activity Description")
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

     // Filters for specific activity types
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

     // Verifies user-specific options (enrollment, edit, or activity details)
     composeTestRule.onNodeWithTag("enrollButton").assertExists()
     composeTestRule.onNodeWithTag("activityDescription").assertIsDisplayed()
   }

  */
}
