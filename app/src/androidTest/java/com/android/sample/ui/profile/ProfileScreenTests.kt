package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
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
  private lateinit var activity1: Activity
  private lateinit var activity2: Activity
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel
  private lateinit var activitiesRepository: ActivitiesRepository

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    activitiesRepository = mock(ActivitiesRepository::class.java)
    userProfileViewModel = mock(ProfileViewModel::class.java)
    listActivitiesViewModel = ListActivitiesViewModel(activitiesRepository)
    activity1 =
        Activity(
            uid = "3",
            title = "Fun Farm",
            description = "Come discover the new farm and enjoy with your family!",
            date = Timestamp.now(),
            location = Location(46.5, 6.6, "Lausanne"),
            creator = "Rola",
            price = 1.0,
            images = listOf(),
            placesLeft = 10,
            maxPlaces = 20,
            status = ActivityStatus.ACTIVE,
            type = ActivityType.PRO,
            participants = listOf(),
            duration = "2 hours",
            startTime = "10:00",
        )
    activity2 =
        Activity(
            uid = "2",
            title = "Cooking",
            description = "Great cooking class",
            date = Timestamp.now(),
            location = Location(46.519962, 6.633597, "EPFL"),
            creator = "123",
            price = 1.0,
            images = listOf(),
            placesLeft = 10,
            maxPlaces = 20,
            status = ActivityStatus.ACTIVE,
            type = ActivityType.PRO,
            participants = listOf(),
            duration = "2 hours",
            startTime = "10:00",
        )
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity1, activity2))
    }

    listActivitiesViewModel.getActivities()
    testUser =
        User(
            id = "Rola",
            name = "Amine",
            surname = "A",
            photo = "",
            interests = listOf("Cycling", "Reading"),
            activities = listOf(activity1.uid, activity2.uid),
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
}
