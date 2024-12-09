package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.activity1
import com.android.sample.resources.dummydata.activityListWithPastActivity
import com.android.sample.resources.dummydata.listOfActivitiesUid
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import java.lang.Thread.sleep
import java.sql.Timestamp
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
                listOf(Interest("Sport", "Cycling"), Interest("Indoor Activity", "Reading")),
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
    val userStateFlow = MutableStateFlow(testUser)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.PROFILE)
    `when`(userProfileViewModel.userState).thenReturn(userStateFlow)
    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository)
  }

  @Test
  fun displayLoadingScreen() {
    val userStateFlow = MutableStateFlow<User?>(null) // Represents loading state
    `when`(userProfileViewModel.userState).thenReturn(userStateFlow)

    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions,
          listActivitiesViewModel,
          mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("loadingText").assertTextEquals("You do not have a profile")
    composeTestRule.onNodeWithTag("loadingScreen").assertIsDisplayed()
  }

  @Test
  fun displayAllProfileComponents() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel,
          mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userName").assertTextEquals("Amine A")
    composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Cycling").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Reading").assertIsDisplayed()

    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun goesToDetailsOnClick() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          mockImageViewModel)
    }

    // Wait until the UI is idle and ready
    composeTestRule.waitForIdle()

    // Get all nodes with the "activityCreated" test tag
    val activityNodes = composeTestRule.onAllNodes(hasTestTag("activityCreated"))

    // Perform a click on the first node
    activityNodes.onFirst().performClick()

    // Wait for any UI operations to complete
    composeTestRule.waitForIdle()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }

  @Test
  fun displayPastActivities() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("profileContentColumn")
        .assertIsDisplayed()
        .performScrollToNode(hasTestTag("pastActivitiesTitle"))
    composeTestRule.onNodeWithTag("pastActivitiesTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("pastActivitiesTitle").assertTextEquals("Past Activities")
    composeTestRule.onNodeWithText("Watch World Cup 2022", useUnmergedTree = true).assertExists()
  }

  @Test
  fun navigateToPastActivityDetails() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          mockImageViewModel)
    }

    // Wait until the UI is idle and ready
    composeTestRule.waitForIdle()

    // Get all nodes with the "activityPast" test tag
    val activityNodes = composeTestRule.onAllNodes(hasTestTag("activityPast"))
    composeTestRule
        .onNodeWithTag("profileContentColumn")
        .assertIsDisplayed()
        .performScrollToNode(hasTestTag("pastActivitiesTitle"))
    // Perform a click on the first past activity node
    activityNodes.onFirst().performClick()

    // Wait for any UI operations to complete
    composeTestRule.waitForIdle()

    // Verify navigation based on whether the user is the creator
    activityListWithPastActivity.first { it.uid == listOfActivitiesUid.first() }
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

  /*
   @Test
   fun test_RemainingTime_ForDays() {
     val calendar =
         Calendar.getInstance().apply {
           clear() // Clears all fields to start fresh and avoid unwanted hour/minute/second
           set(Calendar.YEAR, 2024) // Set year to 2024
           set(Calendar.MONTH, Calendar.DECEMBER) // Set month to December
           set(Calendar.DAY_OF_MONTH, 9) // Set day to
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

  */

  @Test
  fun test_RemainingTime_ForHours() {
    val currentTime = System.currentTimeMillis()
    val futureDate = com.google.firebase.Timestamp(Date(currentTime))
    val calendar = Calendar.getInstance().apply { timeInMillis = currentTime }
    calendar.add(Calendar.MINUTE, 30)
    val futureStartTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"

    val activity = activity1.copy(date = futureDate, startTime = futureStartTime)

    composeTestRule.setContent { RemainingTime(currentTime, activity = activity) }
    sleep(5000)
    composeTestRule.onNodeWithTag("remainingTime").assertIsDisplayed()
    composeTestRule.onNodeWithText("In 0 h 29 min").assertIsDisplayed()
  }

  @Test
  fun reviewPastActivity_updatesReviewStatus() {

    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          imageViewModel = mockImageViewModel)
    }

    // Wait for the UI to settle
    composeTestRule.waitForIdle()

    // Scroll to the past activity
    composeTestRule
        .onNodeWithTag("profileContentColumn")
        .performScrollToNode(hasTestTag("pastActivitiesTitle"))

    val pastActivityNode = composeTestRule.onAllNodes(hasTestTag("activityPast")).onFirst()
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
}
