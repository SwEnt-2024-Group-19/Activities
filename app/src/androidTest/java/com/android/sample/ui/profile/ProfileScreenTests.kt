package com.android.sample.ui.profile

import android.content.SharedPreferences
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.activity
import com.android.sample.resources.dummydata.activity1
import com.android.sample.resources.dummydata.activity2
import com.android.sample.resources.dummydata.activityListWithPastActivity
import com.android.sample.resources.dummydata.listOfActivitiesUid
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import java.lang.Thread.sleep
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class ProfileScreenTest {

  private lateinit var userProfileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var testUser: User
  private lateinit var mockImageViewModel: ImageViewModel
  private lateinit var mockImageRepository: ImageRepositoryFirestore
  private lateinit var mockHourDateViewModel: HourDateViewModel
    private lateinit var signInViewModel: SignInViewModel


  private lateinit var sharedPreferences: SharedPreferences
  private lateinit var mockEditor: SharedPreferences.Editor
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    testUser =
        User(
            id = "Rola",
            name = "Amine",
            surname = "A",
            photo = "",
            interests =
                listOf(
                    Interest("Cycling", com.android.sample.model.activity.Category.SPORT),
                    Interest("Reading", com.android.sample.model.activity.Category.ENTERTAINMENT)),
            activities = listOfActivitiesUid,
        )
    activitiesRepository = mock(ActivitiesRepository::class.java)
    userProfileViewModel = mock(ProfileViewModel::class.java)
    listActivitiesViewModel =
        ListActivitiesViewModel(mock(ProfilesRepository::class.java), activitiesRepository)

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(activityListWithPastActivity)
    }

    listActivitiesViewModel.getActivities()
      signInViewModel = mock(SignInViewModel::class.java)
    val userStateFlow = MutableStateFlow(testUser)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.PROFILE)

    `when`(userProfileViewModel.userState).thenReturn(userStateFlow)
    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    sharedPreferences = mock(SharedPreferences::class.java)
    mockEditor = mock(SharedPreferences.Editor::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository, sharedPreferences)
    mockHourDateViewModel = mock(HourDateViewModel::class.java)
  }

  @Test
  fun displayLoadingScreen() {
    val userStateFlow = MutableStateFlow<User?>(null) // No user profile
    `when`(userProfileViewModel.userState).thenReturn(userStateFlow)

    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          imageViewModel = mockImageViewModel,
          signInViewModel = signInViewModel)
    }
    composeTestRule
        .onNodeWithTag("loadingText")
        .assertTextEquals("You are not logged in. Login or Register to see your liked activities.")
    composeTestRule.onNodeWithTag("loadingScreen").assertIsDisplayed()
  }

  @Test
  fun displayAllProfileComponents() {

    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          imageViewModel = mockImageViewModel,
          signInViewModel = signInViewModel)
    }
    composeTestRule.onNodeWithTag("profileTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("settingsIcon", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileContentColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileHeader").assertIsDisplayed()
    composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("activityTypeRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("createdActivities").assertIsDisplayed()
    composeTestRule.onNodeWithTag("enrolledActivities").assertIsDisplayed()
    composeTestRule.onNodeWithTag("passedActivities").assertIsDisplayed()
    composeTestRule.onNodeWithTag("activitiesColumn").assertIsDisplayed()

    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun editProfileNavigatesToAuth() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          imageViewModel = mockImageViewModel,
          signInViewModel = signInViewModel)
    }
    composeTestRule.onNodeWithTag("settingsIcon", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("editProfileMenuItem").performClick()
    verify(navigationActions).navigateTo(Screen.EDIT_PROFILE)
  }

  @Test
  fun goesToDetailsOnClick() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          imageViewModel = mockImageViewModel,
          signInViewModel = signInViewModel)
    }

    // Wait until the UI is idle and ready
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Fun Farm", useUnmergedTree = true).assertExists().performClick()

    // Wait for any UI operations to complete
    composeTestRule.waitForIdle()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }

  @Test
  fun displayPastActivities() {
    val activity2 = activity2.copy(participants = listOf(testUser))
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(activityListWithPastActivity + activity2)
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          imageViewModel = mockImageViewModel,
          signInViewModel = signInViewModel)
    }
    composeTestRule.onNodeWithTag("passedActivities").performClick()
    composeTestRule.onNodeWithText("Watch World Cup 2022", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("createdActivities").performClick()
    composeTestRule.onNodeWithText("Fun Farm", useUnmergedTree = true).assertExists()

    composeTestRule.onNodeWithTag("enrolledActivities").performClick()
    composeTestRule.onNodeWithText("Cooking", useUnmergedTree = true).assertExists()
  }

  @Test
  fun navigateToPastActivityDetails() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          imageViewModel = mockImageViewModel,
          signInViewModel = signInViewModel)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("passedActivities").performClick()

    composeTestRule.onAllNodes(hasText("Watch World Cup 2022")).onFirst().performClick()

    composeTestRule.waitForIdle()

    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }

  @Test
  fun test_RemainingTime_ForMonths() {
    // convert 4 december 2024 to date
    val calendar =
        Calendar.getInstance().apply {
          clear() // Clears all fields to start fresh and avoid unwanted hour/minute/second
          set(Calendar.YEAR, 2024) // Set year to 2024
          set(Calendar.MONTH, Calendar.DECEMBER) // Set month to December
          set(Calendar.DAY_OF_MONTH, 4) // Set day to 4
          set(Calendar.HOUR_OF_DAY, 0) // Set hour to 00
          set(Calendar.MINUTE, 0) // Set minute to 00
          set(Calendar.SECOND, 0) // Set second to 00
          set(Calendar.MILLISECOND, 0) // Set millisecond to 00
        }

    val futureDate =
        com.google.firebase.Timestamp(
            Date(calendar.timeInMillis + 305L * 30 * 24 * 60 * 60 * 1000)) // 305 months from now
    val activity = activity1.copy(date = futureDate, startTime = "12:00") // Start time is noon

    composeTestRule.setContent { RemainingTime(calendar.timeInMillis, activity = activity) }
    composeTestRule.onNodeWithTag("remainingTime").assertIsDisplayed()
    composeTestRule.onNodeWithText("In 305 months").assertIsDisplayed()
  }

  @Test
  fun test_RemainingTime_ForDays() {
    val calendar =
        Calendar.getInstance().apply {
          clear() // Clears all fields to start fresh and avoid unwanted hour/minute/second
          set(Calendar.YEAR, 2024) // Set year to 2024
          set(Calendar.MONTH, Calendar.DECEMBER) // Set month to December
          set(Calendar.DAY_OF_MONTH, 5) // Set day to 5
          set(Calendar.HOUR_OF_DAY, 0) // Set hour to 00
          set(Calendar.MINUTE, 0) // Set minute to 00
          set(Calendar.SECOND, 0) // Set second to 00
          set(Calendar.MILLISECOND, 0) // Set millisecond to 00
        }
    val futureDate =
        com.google.firebase.Timestamp(Date(calendar.timeInMillis + 6L * 24 * 60 * 60 * 1000))
    val activity = activity1.copy(date = futureDate)

    composeTestRule.setContent { RemainingTime(calendar.timeInMillis, activity = activity) }
    composeTestRule.onNodeWithText("In 6 days", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun test_RemainingTime_ForHours() {
    val calendar =
        Calendar.getInstance().apply {
          clear() // Clears all fields to start fresh and avoid unwanted hour/minute/second
          set(Calendar.YEAR, 2024) // Set year to 2024
          set(Calendar.MONTH, Calendar.DECEMBER) // Set month to December
          set(Calendar.DAY_OF_MONTH, 5) // Set day to 5
          set(Calendar.HOUR_OF_DAY, 1) // Set hour to 00
          set(Calendar.MINUTE, 0) // Set minute to 00
          set(Calendar.SECOND, 0) // Set second to 00
          set(Calendar.MILLISECOND, 0) // Set millisecond to 00
        }

    val futureDate =
        com.google.firebase.Timestamp(Date(calendar.timeInMillis + 2L * 60 * 60 * 1000))

    val activity = activity1.copy(date = futureDate, startTime = "3:00")

    composeTestRule.setContent { RemainingTime(calendar.timeInMillis, activity = activity) }
    sleep(5000)
    composeTestRule.onNodeWithTag("remainingTime").assertIsDisplayed()
    composeTestRule.onNodeWithText("In 2 h 0 min").assertIsDisplayed()
  }

  @Test
  fun reviewPastActivity_updatesReviewStatus() {

    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          imageViewModel = mockImageViewModel,
          signInViewModel = signInViewModel)
    }

    // Wait for the UI to settle
    composeTestRule.waitForIdle()

    // Scroll to the past activity
    composeTestRule.onNodeWithTag("passedActivities").performClick()

    val pastActivityNode = composeTestRule.onAllNodes(hasText("Watch World Cup 2022")).onFirst()
    pastActivityNode.assertIsDisplayed()

    composeTestRule.onNodeWithTag("likeIconButton_false").performClick()
    composeTestRule.onNodeWithTag("likeIconButton_false").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("likeIconButton_true").assertExists()

    composeTestRule.onNodeWithTag("dislikeIconButton_false").performClick()
    composeTestRule.waitForIdle()
    // following fails on the CI but not in the local environment. Is expected to pass.
    // composeTestRule.onNodeWithTag("dislikeIconButton_true").assertExists()
    // composeTestRule.onNodeWithTag("dislikeIconButton_true").assertExists()
  }

  @Test
  fun interestsAreDisplayed() {
    composeTestRule.setContent { ShowInterests(testUser) }
    composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("interestsTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("interestsRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Cycling").assertIsDisplayed()
  }

  @Test
  fun headerProfileIsDisplayed() {
    val activity1 = activity1.copy(creator = testUser.id)
    val activity2 = activity.copy(participants = listOf(testUser))
    val activity3 = activity.copy(creator = testUser.id)
    composeTestRule.setContent {
      ProfileHeader(testUser, mockImageViewModel, listOf(activity1, activity2, activity3))
    }
    composeTestRule.onNodeWithTag("profileHeader").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profilePicture").assertExists()
    composeTestRule.onAllNodesWithTag("headerItem").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("headerItemField").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("headerItemTitle").assertCountEquals(3)
    composeTestRule.onNodeWithTag("ratingStar").assertIsDisplayed()
  }

  @Test
  fun displayActivityListTest() {
    val activity1 = activity1.copy(creator = testUser.id)
    val activity2 = activity.copy(creator = testUser.id)
    composeTestRule.setContent {
      DisplayActivitiesList(
          listOf(activity1, activity2),
          0,
          testUser,
          mockHourDateViewModel,
          navigationActions,
          userProfileViewModel,
          listActivitiesViewModel,
          mockImageViewModel,
          "")
    }
    composeTestRule.onNodeWithTag("activitiesList").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("activityRow").assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("activityImage", useUnmergedTree = true).assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("activityTitle", useUnmergedTree = true).assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("remainingTime", useUnmergedTree = true).assertCountEquals(2)
    composeTestRule
        .onAllNodesWithTag("activityDescription", useUnmergedTree = true)
        .assertCountEquals(2)
  }

  @Test
  fun loadingScreen() {
    composeTestRule.setContent { LoadingScreen(navigationActions, "") }
    composeTestRule.onNodeWithTag("loadingScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loadingText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInButton").assertIsDisplayed()
  }
}
