package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
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
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.activityListWithPastActivity
import com.android.sample.resources.dummydata.listOfActivitiesUid
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
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
  private lateinit var testUser: User
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel
  private lateinit var activitiesRepository: ActivitiesRepository

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    activitiesRepository = mock(ActivitiesRepository::class.java)
    userProfileViewModel = mock(ProfileViewModel::class.java)
    listActivitiesViewModel =
        ListActivitiesViewModel(mock(ProfilesRepository::class.java), activitiesRepository)

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(activityListWithPastActivity)
    }

    listActivitiesViewModel.getActivities()
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
    val userStateFlow = MutableStateFlow(testUser)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.PROFILE)
    `when`(userProfileViewModel.userState).thenReturn(userStateFlow)
  }

  @Test
  fun displayLoadingScreen() {
    val userStateFlow = MutableStateFlow<User?>(null) // Represents loading state
    `when`(userProfileViewModel.userState).thenReturn(userStateFlow)

    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel, navigationActions, listActivitiesViewModel)
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
          listActivitiesViewModel)
    }

    composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userName").assertTextEquals("Amine A")
    composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Cycling").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Reading").assertIsDisplayed()

    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("goBackButton")
        .performClick() // test for if on click it goes back
  }

  @Test
  fun goesToEditOnClick() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel)
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
    verify(navigationActions).navigateTo(Screen.EDIT_ACTIVITY)
  }

  @Test
  fun displayPastActivities() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel)
    }
    composeTestRule
        .onNodeWithTag("profileContentColumn")
        .assertIsDisplayed()
        .performScrollToNode(hasTestTag("pastActivitiesTitle"))
    composeTestRule.onNodeWithTag("pastActivitiesTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("pastActivitiesTitle").assertTextEquals("Past Activities")
    composeTestRule.onNodeWithText("Watch World Cup 2022").assertIsDisplayed()
  }

  @Test
  fun navigateToPastActivityDetailsOrEdit() {
    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel)
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
    val pastActivity = activityListWithPastActivity.first { it.uid == listOfActivitiesUid.first() }
    verify(navigationActions).navigateTo(Screen.EDIT_ACTIVITY)
  }

  @Test
  fun reviewPastActivity_updatesReviewStatus() {

    composeTestRule.setContent {
      ProfileScreen(
          userProfileViewModel = userProfileViewModel,
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel)
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
    composeTestRule.onNodeWithTag("likeIconButton_false").assertDoesNotExist()
    composeTestRule.onNodeWithTag("likeIconButton_true").assertExists()

    composeTestRule.onNodeWithTag("dislikeIconButton_false").performClick()
    composeTestRule.onNodeWithTag("dislikeIconButton_false").assertDoesNotExist()
    composeTestRule.onNodeWithTag("dislikeIconButton_true").assertExists()
  }
}
