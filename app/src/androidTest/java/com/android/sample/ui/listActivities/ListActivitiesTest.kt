package com.android.sample.ui.listActivities

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
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

class OverviewScreenTest {
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel
  private lateinit var userProfileViewModel: ProfileViewModel
  private lateinit var testUser: User

  private val activity =
      Activity(
          uid = "1",
          title = "Mountain Biking",
          description = "Exciting mountain biking experience.",
          date = Timestamp.now(),
          location = Location(46.519962, 6.633597, "EPFL"),
          creator = "Chris",
          images = listOf(),
          price = 10.0,
          status = ActivityStatus.ACTIVE,
          type = ActivityType.PRO,
          placesLeft = 8,
          maxPlaces = 15,
          participants = listOf(),
          duration = "2 hours",
          startTime = "10:00")

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    profilesRepository = mock(ProfilesRepository::class.java)
    activitiesRepository = mock(ActivitiesRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    listActivitiesViewModel = ListActivitiesViewModel(activitiesRepository)

    testUser =
        User(
            id = "Rola",
            name = "Amine",
            surname = "A",
            photo = "",
            interests = listOf("Cycling", "Reading"),
            activities = listOf(activity.uid),
        )

    navigationActions = mock(NavigationActions::class.java)

    // `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))

    `when`(navigationActions.currentRoute()).thenReturn(Route.OVERVIEW)
  }

  @Test
  fun displayTextWhenEmpty() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    }
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf())
    }
    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("emptyActivityPrompt").assertIsDisplayed()
  }

  @Test
  fun displaysActivity() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    }
    // Mock the activities repository to return an activity
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }

    listActivitiesViewModel.getActivities()

    // Simulate click on the activity card
    composeTestRule.onNodeWithTag("activityCard").assertExists()
    composeTestRule.onNodeWithText("Mountain Biking").assertIsDisplayed()
    composeTestRule.onNodeWithText("Exciting mountain biking experience.").assertIsDisplayed()
    composeTestRule.onNodeWithText("EPFL").assertIsDisplayed()
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()
    composeTestRule.onNodeWithTag("activityCard").performClick()

    // Verify navigation to details screen was triggered
    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }

  @Test
  fun segmentedButtonRowIsDisplayed() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    }
    // Ensure segmented buttons are displayed
    composeTestRule.onNodeWithTag("segmentedButtonRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonPRO").performClick().assertIsSelected()
    composeTestRule.onNodeWithText("PRO").performClick().assertIsSelected()
    composeTestRule.onNodeWithTag("segmentedButtonINDIVIDUAL").performClick().assertIsSelected()
    composeTestRule.onNodeWithText("INDIVIDUAL").performClick().assertIsSelected()
    composeTestRule.onNodeWithTag("segmentedButtonSOLO").performClick().assertIsSelected()
    composeTestRule.onNodeWithText("SOLO").performClick().assertIsSelected()
    composeTestRule.onNodeWithText("ALL").performClick().assertIsSelected()
  }

  @Test
  fun displayTextWhenNoSolo() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    }
    composeTestRule.onNodeWithText("SOLO").performClick()
    composeTestRule.onNodeWithText("There is no activity of this type yet.").assertIsDisplayed()
  }

  @Test
  fun bottomNavigationMenuIsDisplayed() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    }
    // Ensure bottom navigation menu is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    // composeTestRule.onNodeWithTag("Liked").performClick()
    // verify(navigationActions).navigateTo(Screen.LIKED_ACTIVITIES)
  }

  @Test
  fun goesToDetailsOnClick() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    }

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("activityCard").performClick()
    composeTestRule.waitForIdle()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }

  @Test
  fun filteringWorks() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    }
    val activity1 = activity.copy(title = "cooking", type = ActivityType.INDIVIDUAL)
    val activity2 = activity.copy(title = "dance", type = ActivityType.SOLO)
    val activity3 = activity.copy(title = "football", type = ActivityType.INDIVIDUAL)

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity, activity1, activity2, activity3))
    }

    listActivitiesViewModel.getActivities()
    composeTestRule.onNodeWithTag("segmentedButtonINDIVIDUAL").performClick()
    composeTestRule.onNodeWithText("cooking").assertIsDisplayed()
    composeTestRule.onNodeWithText("football").assertIsDisplayed()

    composeTestRule.onNodeWithTag("segmentedButtonSOLO").performClick()
    composeTestRule.onNodeWithText("dance").assertIsDisplayed()

    composeTestRule.onNodeWithTag("segmentedButtonPRO").performClick()
    composeTestRule.onNodeWithText("Mountain Biking").assertIsDisplayed()
  }

  @Test
  fun changeIconWhenActivityNotLiked() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    }
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }
    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()
  }

  @Test
  fun changeIconWhenActivityIsLiked() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }
    listActivitiesViewModel.getActivities()

    val newUser = testUser.copy(likedActivities = listOf(activity.uid))
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(newUser))
    composeTestRule.setContent {
      ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    }
    // composeTestRule.setContent {

    // ListActivitiesScreen(listActivitiesViewModel, navigationActions, userProfileViewModel)
    // }
    composeTestRule.onNodeWithTag("likeButtontrue").assertIsDisplayed()
  }

  @Test
  fun changeIconWhenActivityIsOnclick() {
    userProfileViewModel = ProfileViewModel(profilesRepository)

    composeTestRule.setContent {
      ActivityCard(
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          profileViewModel = userProfileViewModel,
          profile = testUser,
          activity = activity)
    }

    // Verify initial state is liked
    composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()

    // Click on the like button
    composeTestRule.onNodeWithTag("likeButtonfalse").performClick()

    // Verify that the like button is toggled
    composeTestRule.onNodeWithTag("likeButtontrue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("likeButtontrue").performClick()

    // Verify that the like button is toggled
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()
  }
}
