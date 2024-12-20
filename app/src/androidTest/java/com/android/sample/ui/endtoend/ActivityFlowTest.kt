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
import com.android.sample.resources.dummydata.defaultUserCredentials2
import com.android.sample.ui.endtoend.Overview.ActivityDetails.ENROLL_BUTTON
import com.android.sample.ui.endtoend.Profile.ACTIVITY_ROW
import com.android.sample.ui.endtoend.Profile.ENROLLED_BUTTON
import com.android.sample.ui.endtoend.Profile.PLUS_BUTTON_TO_CREATE
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
    composeTestRule.waitForIdle()
  }

  @Test
  fun guestCanSeeCorrectOverviewAndNavigateToActivityDetails() {
    // Auth screen > Sign in screen

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

    hlp.see(Overview.FILTER_DIALOG_BUTTON)
    hlp.click(Overview.FILTER_DIALOG_BUTTON)
    hlp.see(Overview.FILTER_DIALOG)
    hlp.scroll(parentTag = Overview.FILTER_DIALOG, nodeTag = Overview.Filters.ONLY_PRO_CHECKBOX_ROW)
    hlp.see(Overview.Filters.ONLY_PRO_CHECKBOX_ROW)
    hlp.click(Overview.Filters.ONLY_PRO_CHECKBOX)
    hlp.scroll(parentTag = Overview.FILTER_DIALOG, nodeTag = Overview.Filters.FILTER_BUTTON)
    hlp.click(Overview.Filters.FILTER_BUTTON)

    hlp.notSee(Overview.ACTIVITY_CARD) // After filtering by PRO and SPORT, no activity is displayed
    hlp.click(Overview.SEGMENTED_BUTTON_(Category.SKILLS)) // Switch to SKILLS
    hlp.see(Overview.ACTIVITY_CARD)

    // Search bar
    hlp.click(Overview.SearchBar.SEARCH_BAR)
    hlp.write(
        Overview.SearchBar.SEARCH_BAR,
        "DANCE",
    )
    hlp.notSee(Overview.ACTIVITY_CARD)
    hlp.click(Overview.SearchBar.SEARCH_BAR)
    hlp.write(Overview.SearchBar.SEARCH_BAR, "Sample", replace = true)
    hlp.see(Overview.ACTIVITY_CARD)

    // Open the activity details
    hlp.click(Overview.ACTIVITY_CARD)

    // Activity details screen
    hlp.see(ActivityDetails.SCREEN)

    hlp.see(ActivityDetails.TopAppBar)
    hlp.see(ActivityDetails.GoBackButton)
    hlp.see(ActivityDetails.Image)
    hlp.see(ActivityDetails.Title)
    hlp.see(ActivityDetails.TitleText)
    hlp.click(ActivityDetails.detailsIcon)

    hlp.see(ActivityDetails.DescriptionText)
    hlp.see(ActivityDetails.Price)
    hlp.see(ActivityDetails.PriceText)

    // Check that the user is not logged in and can't enroll
    // @TODO: The need for a scroll here is debatable
    hlp.see(Overview.ActivityDetails.NOT_LOGGED_IN_TEXT)
    hlp.notSee(Overview.ActivityDetails.ENROLL_BUTTON)
    hlp.click(Overview.ActivityDetails.GO_BACK_BUTTON)
    hlp.see(Overview.SCREEN)

    // Check that the user do not have a profile
    hlp.click(BottomNavigation.PROFILE, bottomNavItem = true)
    hlp.see(Profile.NotLoggedIn.PROMPT)
    hlp.see(Profile.NotLoggedIn.SIGN_IN_BUTTON)

    // Check that the user is not logged in and has no liked activities
    hlp.click(BottomNavigation.Liked, bottomNavItem = true)
    hlp.notSee(Overview.ACTIVITY_CARD)
    hlp.see(Prompts.NOT_CONNECTED)
    hlp.see(Prompts.SignInButton)

    // Go To signIn
    hlp.click(Prompts.SignInButton)
    hlp.see(Auth.SignUp.SCREEN)
    hlp.scroll(Auth.SignUp.SIGN_UP_COLUMN, Auth.SignUp.GO_TO_SIGN_IN_BUTTON)
    hlp.click(Auth.SignUp.GO_TO_SIGN_IN_BUTTON)

    // Auth screen > Sign in
    hlp.see(Auth.SignIn.SCREEN)
    hlp.scroll(Auth.SignIn.SIGN_IN_COLUMN, Auth.SignIn.GUEST_BUTTON)
    hlp.click(Auth.SignIn.GUEST_BUTTON)
  }

  @Test
  fun aUserSwitchesProfile() {
    // use of !! is allowed because this is a test environment and we know the
    // key exists. if it doesn't, it is up to the developer to fix the test
    val email1 = defaultUserCredentials1["email"]!!
    val password1 = defaultUserCredentials1["password"]!!
    val name1 = defaultUserCredentials1["first name"]!! // @TODO: Should this change to full name?
    val email2 = defaultUserCredentials2["email"]!!
    val password2 = defaultUserCredentials2["password"]!!
    val name2 = defaultUserCredentials2["first name"]!!

    // Auth screen > Sign in screen
    hlp.click(Auth.SignIn.SIGN_IN_BUTTON)
    hlp.notSee(Overview.SCREEN)
    hlp.see(Auth.SignIn.TEXT_INVALID_EMAIL, text = true)

    //  Enters credentials then connects
    hlp.write(Auth.SignIn.EMAIL_INPUT, email1)
    hlp.write(Auth.SignIn.PASSWORD_INPUT, password1)
    hlp.click(Auth.SignIn.SIGN_IN_BUTTON)

    // Overview screen
    hlp.see(Overview.SCREEN)
    hlp.see(BottomNavigation.OVERVIEW)
    hlp.click(BottomNavigation.PROFILE, bottomNavItem = true)

    // Profile screen of user 1
    hlp.see(Profile.SCREEN)
    hlp.see(name1, text = true)

    // proceed to logout
    hlp.see(Profile.MORE_OPTIONS_BUTTON)
    hlp.click(Profile.MORE_OPTIONS_BUTTON)
    hlp.see(Profile.LOGOUT_BUTTON)
    hlp.click(Profile.LOGOUT_BUTTON)

    // login with new credentials for user 2
    hlp.see(Auth.SignIn.SCREEN)
    hlp.write(Auth.SignIn.EMAIL_INPUT, email2)
    hlp.write(Auth.SignIn.PASSWORD_INPUT, password2)
    hlp.click(Auth.SignIn.SIGN_IN_BUTTON)

    // Overview screen
    hlp.see(Overview.SCREEN)
    hlp.see(BottomNavigation.OVERVIEW)

    // Profile screen of user 2
    hlp.click(BottomNavigation.PROFILE, bottomNavItem = true)
    hlp.see(Profile.SCREEN)
    hlp.see(name2, text = true)
  }

  @Test
  fun EditProfileAndJoinActivity() {
    //    val email = "anna1@gmail.com"
    //    val password= "anna123"
    //    val name = "anna" // @TODO: Should this change to full name?
    //    val surname="ronnie"

    // Auth screen > Sign up screen
    //    hlp.click(Auth.SignIn.GO_TO_SIGN_UP_BUTTON)
    //    hlp.notSee(Overview.SCREEN)
    //    hlp.see(Auth.SignUp.SCREEN)
    //    hlp.write(Auth.SignUp.EMAIL_TEXT_FIELDS, email)
    //    hlp.write(Auth.SignUp.PASSWORD_TEXT_FIELDS, password)
    //    hlp.write(Auth.SignUp.NAME_TEXT_FIELDS, name)
    //    hlp.write(Auth.SignUp.SURNAME_TEXT_FIELDS, surname)
    //    hlp.click(Auth.SignUp.SIGN_UP_BUTTON)
    //
    //
    val email1 = defaultUserCredentials2["email"]!!
    val password1 = defaultUserCredentials2["password"]!!
    val name1 = defaultUserCredentials2["first name"]!! // @TODO: Should this change to full name?

    // Auth screen > Sign in screen
    hlp.click(Auth.SignIn.SIGN_IN_BUTTON)
    hlp.notSee(Overview.SCREEN)
    hlp.see(Auth.SignIn.TEXT_INVALID_EMAIL, text = true)

    //  Enters credentials then connects
    hlp.write(Auth.SignIn.EMAIL_INPUT, email1)
    hlp.write(Auth.SignIn.PASSWORD_INPUT, password1)
    hlp.click(Auth.SignIn.SIGN_IN_BUTTON)

    // Overview screen
    hlp.see(Overview.SCREEN)
    hlp.see(BottomNavigation.OVERVIEW)
    hlp.click(BottomNavigation.PROFILE, bottomNavItem = true)

    // Profile screen of user
    hlp.see(Profile.SCREEN)
    hlp.see(name1, text = true)

    // proceed to edit profile
    hlp.see(Profile.MORE_OPTIONS_BUTTON)
    hlp.click(Profile.MORE_OPTIONS_BUTTON)
    hlp.see(Profile.EDIT_PROFILE_BUTTON)
    hlp.click(Profile.EDIT_PROFILE_BUTTON)

    // changing The profile name
    hlp.see(Profile.EditProfile.EDIT_PROFILE_SCREEN)
    hlp.click(Profile.EditProfile.INPUT_NAME)
    hlp.write(Profile.EditProfile.INPUT_NAME, "Mary", true)
    hlp.scroll("editProfileContent", "ProfileCreationButtonCard")
    hlp.click(Profile.EditProfile.SAVE_BUTTON)
    hlp.see(Profile.SCREEN)

    // make sure the name is really updated and displayed
    hlp.see("Mary", text = true)

    // make sure the user is not enrolled in any activity
    hlp.click(ENROLLED_BUTTON)
    hlp.notSee(ACTIVITY_ROW)
    hlp.see(PLUS_BUTTON_TO_CREATE)

    // enroll in a new activity
    hlp.click(BottomNavigation.OVERVIEW, bottomNavItem = true)
    hlp.see(Overview.SCREEN)
    hlp.click(Overview.SEGMENTED_BUTTON_(Category.SPORT))
    hlp.click(Overview.ACTIVITY_CARD)
    hlp.see(ActivityDetails.SCREEN)
    hlp.click(ENROLL_BUTTON)

    // make sure the user is now enrolled in an activity
    hlp.click(BottomNavigation.PROFILE, bottomNavItem = true)
    hlp.click(ENROLLED_BUTTON)
    hlp.see(ACTIVITY_ROW)
    hlp.notSee(PLUS_BUTTON_TO_CREATE)
  }
}
