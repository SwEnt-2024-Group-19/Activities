package com.android.sample.ui.endtoend

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.rule.GrantPermissionRule
import com.android.sample.MainActivity
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Category
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

  @Inject lateinit var imageRepository: ImageRepository

  @Inject lateinit var imageRepositoryFirestore: ImageRepositoryFirestore

  /** Helper class to interact with the UI. `hlp` stands for helper. */
  private lateinit var hlp: ComposeTestHelper

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
    hlp = ComposeTestHelper(composeTestRule)
  }

  @Test
  fun guestCanSeeCorrectOverviewAndNavigateToActivityDetails() {

    // Auth screen > Sign in screen
    hlp.click(Auth.SignIn.GUEST_BUTTON)

    // Overview screen
    hlp.assertIsDisplayed(Overview.SCREEN)
    hlp.assertIsSelected(BottomNavigation.OVERVIEW)
    hlp.assertAnyIsDisplayed(Overview.ACTIVITY_CARD)

    // Filter for specific activity types
    hlp.click(Overview.SEGMENTED_BUTTON_(Category.CULTURE))
    hlp.assertIsDisplayed(Overview.EMPTY_ACTIVITY)
    hlp.click(Overview.SEGMENTED_BUTTON_(Category.CULTURE))
    // assert is unselected

    hlp.click(Overview.SEGMENTED_BUTTON_(Category.SPORT))
    hlp.assertIsDisplayed(Overview.ACTIVITY_CARD)

    // Filter for specific criteria
    // TODO: Implement this feature

    // Open the activity details
    hlp.click(Overview.ACTIVITY_CARD)

    // Activity details screen
    /*hlp.assertIsDisplayed("activityDetailsScreen")
    listOf(
      "topAppBar", "goBackButton", "image", "title", "titleText", "descriptionText",
      "price", "priceText", "location", "locationText", "schedule", "scheduleText"
    ).forEach { hlp.assertIsDisplayed(it) }*/
    // TODO: Implement this feature

    // Check that the user is not logged in and can't enroll
    hlp.assertExists(Overview.ActivityDetails.NOT_LOGGED_IN_TEXT)
    hlp.assertDoesNotExist(Overview.ActivityDetails.ENROLL_BUTTON)
    hlp.click(Overview.ActivityDetails.GO_BACK_BUTTON)
    hlp.assertIsDisplayed(Overview.SCREEN)
  }

  @Test
  fun guestShouldSignUpForOtherFunctionalities() {
    // Auth screen > Sign in screen
    hlp.click(Auth.SignIn.GUEST_BUTTON)

    // Overview screen
    hlp.assertIsDisplayed(Overview.SCREEN)
    hlp.clickBottomNavigationItem(BottomNavigation.PROFILE)

    // Profile screen
    hlp.assertIsDisplayed(Profile.NotLoggedIn.PROMPT)
    hlp.click(Profile.NotLoggedIn.SIGN_IN_BUTTON)

    // Auth screen > Sign up screen
    hlp.assertIsDisplayed(Auth.SignUp.SCREEN)
    hlp.click(Auth.SignUp.GO_TO_SIGN_IN_BUTTON)

    // Auth screen > Sign in
    hlp.assertIsDisplayed(Auth.SignIn.SCREEN)
    hlp.click(Auth.SignIn.GUEST_BUTTON)

    // Tries to create a new activity and is prompted to sign in
    hlp.assertIsDisplayed(Overview.SCREEN)
    hlp.clickBottomNavigationItem(BottomNavigation.CREATE_ACTIVITY)

    // Add activity screen
    /*hlp.assertIsDisplayed(CreateActivity.SCREEN)
    hlp.write(CreateActivity.TITLE_INPUT, "Activity Title")
    hlp.write(CreateActivity.DESCRIPTION_INPUT, "Activity Description")
    hlp.write(CreateActivity.PRICE_INPUT, "13")
    hlp.write(CreateActivity.PLACES_INPUT, "7")
    hlp.write(CreateActivity.LOCATION_INPUT, "Activity Location")


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
    composeTestRule.onNodeWithTag("centerOnCurrentLocation").assertIsDisplayed()*/
  }

  @Test
  fun aUserSignsInAndLooksAtTheirProfile() {
    // use of !! is allowed because this is a test environment and we know the
    // key exists. if it doesn't, it is up to the developer to fix the test
    val email = defaultUserCredentials["email"]!!
    val password = defaultUserCredentials["password"]!!
    val name = defaultUserCredentials["first name"]!! // @TODO: Should this change to full name?

    // Auth screen > Sign in screen
    hlp.click(Auth.SignIn.SIGN_IN_BUTTON) // try to sign in without credentials
    hlp.assertIsNotDisplayed(Overview.SCREEN)
    hlp.assertTextIsDisplayed(Auth.SignIn.TEXT_INVALID_EMAIL)

    //  Enters credentials then connects
    hlp.write(Auth.SignIn.EMAIL_INPUT, email)
    hlp.write(Auth.SignIn.PASSWORD_INPUT, password)
    hlp.click(Auth.SignIn.SIGN_IN_BUTTON)

    // Overview screen
    hlp.assertIsDisplayed(Overview.SCREEN)
    hlp.assertIsSelected(BottomNavigation.OVERVIEW)
    hlp.clickBottomNavigationItem(BottomNavigation.PROFILE)

    // Profile screen
    hlp.assertIsDisplayed(Profile.SCREEN)
    hlp.assertTextIsDisplayed(name)
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
