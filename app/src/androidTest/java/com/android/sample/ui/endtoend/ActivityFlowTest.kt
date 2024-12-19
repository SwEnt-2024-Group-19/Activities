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
import com.android.sample.resources.dummydata.defaultUserCredentials1
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

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
    composeTestRule.waitForIdle()
  }

  @Test
  fun guestCanSeeCorrectOverviewAndNavigateToActivityDetails() {
    // Auth screen > Sign in screen
    hlp.scroll(
        parentTag = Auth.SignIn.SIGN_IN_COLUMN,
        nodeTag = Auth.SignIn.GUEST_BUTTON) // @TODO: This should not need scrolling
    hlp.click(Auth.SignIn.GUEST_BUTTON)

    // Overview screen
    hlp.see(Overview.SCREEN)
    hlp.see(BottomNavigation.OVERVIEW, selected = true)
    hlp.see(Overview.ACTIVITY_CARD, any = true)

    // Filter for specific activity types
    hlp.click(Overview.SEGMENTED_BUTTON_(Category.CULTURE))
    hlp.see(Overview.EMPTY_ACTIVITY)
    hlp.click(Overview.SEGMENTED_BUTTON_(Category.CULTURE))
    // assert is unselected

    hlp.click(Overview.SEGMENTED_BUTTON_(Category.SPORT))
    hlp.see(Overview.ACTIVITY_CARD)


    // Filter for specific criteria

    hlp.see("filterDialog")
    hlp.click("filterDialog")
    hlp.see("FilterDialog")
    hlp.scroll(
        parentTag = "FilterDialog",
        nodeTag = "onlyPROCheckboxRow")
    hlp.see("onlyPROCheckbox")
    hlp.click("onlyPROCheckbox")
    hlp.scroll(
        parentTag = "FilterDialog",
        nodeTag = "filterButton")
    hlp.click("filterButton")

    hlp.notSee(Overview.ACTIVITY_CARD) // After filtering by PRO and SPORT, no activity is displayed
    hlp.click(Overview.SEGMENTED_BUTTON_(Category.SKILLS)) // Switch to SKILLS
    hlp.see(Overview.ACTIVITY_CARD)

    //Search bar
    hlp.click("searchBar")
    hlp.write("searchBar","DANCE",)
    hlp.notSee(Overview.ACTIVITY_CARD)
    hlp.click("searchBar")
    hlp.write("searchBar","Sample", replace = true)
    hlp.see(Overview.ACTIVITY_CARD)

    // Open the activity details
    hlp.click(Overview.ACTIVITY_CARD)

    //Activity details screen
    hlp.see("activityDetailsScreen")
    listOf(
      "topAppBar", "goBackButton", "image", "title", "titleText", "descriptionText",
      "price", "priceText", "location", "locationText", "schedule", "scheduleText"
    ).forEach { hlp.see(it) }


    // Check that the user is not logged in and can't enroll
    hlp.scroll(
        Overview.ActivityDetails.SCREEN,
        Overview.ActivityDetails
            .NOT_LOGGED_IN_TEXT) // @TODO: The need for a scroll here is debatable
    hlp.see(Overview.ActivityDetails.NOT_LOGGED_IN_TEXT)
    hlp.notSee(Overview.ActivityDetails.ENROLL_BUTTON)
    hlp.click(Overview.ActivityDetails.GO_BACK_BUTTON)
    hlp.see(Overview.SCREEN)

    //Check that the user do not have a profile
    hlp.click(BottomNavigation.PROFILE, bottomNavItem = true)
    hlp.see(Profile.NotLoggedIn.PROMPT)
    hlp.see(Profile.NotLoggedIn.SIGN_IN_BUTTON)

    // Check that the user is not logged in and has no liked activities
    hlp.click(BottomNavigation.Liked, bottomNavItem = true)
    hlp.notSee("activityCard")
    hlp.see("notConnectedPrompt")
    hlp.see("signInButton")


    // Go To signIn

    hlp.click("signInButton")
    hlp.see(Auth.SignUp.SCREEN)
    hlp.scroll(
      Auth.SignUp.SIGN_UP_COLUMN,
      Auth.SignUp.GO_TO_SIGN_IN_BUTTON)
    hlp.click(Auth.SignUp.GO_TO_SIGN_IN_BUTTON)

    // Auth screen > Sign in
    hlp.see(Auth.SignIn.SCREEN)
    hlp.scroll(
      Auth.SignIn.SIGN_IN_COLUMN,
      Auth.SignIn.GUEST_BUTTON)
    hlp.click(Auth.SignIn.GUEST_BUTTON)


  }

  @Test
  fun guestShouldSignUpForOtherFunctionalities() {
    // Auth screen > Sign in screen
    hlp.scroll(
        Auth.SignIn.SIGN_IN_COLUMN,
        Auth.SignIn.GUEST_BUTTON) // @TODO: This should not need scrolling
    hlp.click(Auth.SignIn.GUEST_BUTTON)

    // Overview screen
    hlp.see(Overview.SCREEN)
    hlp.click(BottomNavigation.PROFILE, bottomNavItem = true)

    // Profile screen
    hlp.see(Profile.NotLoggedIn.PROMPT)
    hlp.click(Profile.NotLoggedIn.SIGN_IN_BUTTON)

    // Auth screen > Sign up screen
    hlp.see(Auth.SignUp.SCREEN)
    hlp.scroll(
        Auth.SignUp.SIGN_UP_COLUMN,
        Auth.SignUp.GO_TO_SIGN_IN_BUTTON) // @TODO: This should not need scrolling
    hlp.click(Auth.SignUp.GO_TO_SIGN_IN_BUTTON)

    // Auth screen > Sign in
    hlp.see(Auth.SignIn.SCREEN)
    hlp.scroll(
        Auth.SignIn.SIGN_IN_COLUMN,
        Auth.SignIn.GUEST_BUTTON) // @TODO: This should not need scrolling
    hlp.click(Auth.SignIn.GUEST_BUTTON)

    // Tries to create a new activity and is prompted to sign in
    hlp.see(Overview.SCREEN)
    hlp.click(BottomNavigation.CREATE_ACTIVITY, bottomNavItem = true)

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
    val email = defaultUserCredentials1["email"]!!
    val password = defaultUserCredentials1["password"]!!
    val name = defaultUserCredentials1["first name"]!! // @TODO: Should this change to full name?

    // Auth screen > Sign in screen
    hlp.click(Auth.SignIn.SIGN_IN_BUTTON)
    hlp.notSee(Overview.SCREEN)
    hlp.see(Auth.SignIn.TEXT_INVALID_EMAIL, text = true)

    //  Enters credentials then connects
    hlp.write(Auth.SignIn.EMAIL_INPUT, email)
    hlp.write(Auth.SignIn.PASSWORD_INPUT, password)
    hlp.click(Auth.SignIn.SIGN_IN_BUTTON)

    // Overview screen
    hlp.see(Overview.SCREEN)
    hlp.see(BottomNavigation.OVERVIEW)
    hlp.click(BottomNavigation.PROFILE, bottomNavItem = true)

    // Profile screen
    hlp.see(Profile.SCREEN)
    hlp.see(name, text = true)
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
