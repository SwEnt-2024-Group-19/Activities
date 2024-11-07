package com.android.sample.ui.activity

import androidx.compose.ui.test.assertIsDisplayed
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
import com.android.sample.ui.listActivities.ActivityCard2
import com.android.sample.ui.listActivities.LikedActivitiesScreen
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
import org.mockito.kotlin.whenever

class LikedActivitiesTest {

  @get:Rule private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var navigationActions: NavigationActions
  private lateinit var viewModel: ListActivitiesViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var testUser: User

  private val testActivity =
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

    activitiesRepository = mock(ActivitiesRepository::class.java)
    profilesRepository = mock(ProfilesRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    viewModel = ListActivitiesViewModel(activitiesRepository)

    testUser =
        User(
            id = "Rola",
            name = "Amine",
            surname = "A",
            photo = "",
            interests = listOf("Cycling", "Reading"),
            activities = listOf(),
            likedActivities = listOf(testActivity.uid))
    // val userStateFlow = MutableStateFlow(testUser)
    navigationActions = mock(NavigationActions::class.java)

    // `when`(userProfileViewModel.userState).thenReturn(userStateFlow)

    `when`(navigationActions.currentRoute()).thenReturn(Route.LIKED_ACTIVITIES)
    // composeTestRule.setContent { ListActivitiesScreen(listActivitiesViewModel, navigationActions,
    // userProfileViewModel) }
  }

  @Test
  fun whenUserNotLoggedIn_thenDisplaysSignInPrompt() {
    profileViewModel = mock(ProfileViewModel::class.java)
    // Set user profile to null to simulate a user not logged in
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(null))

    composeTestRule.setContent {
      LikedActivitiesScreen(viewModel, navigationActions, profileViewModel)
    }

    // Verify sign-in prompt is displayed
    composeTestRule.onNodeWithTag("notConnectedPrompt").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInButton").performClick()
    verify(navigationActions).navigateTo(Screen.SIGN_UP)
  }

  @Test
  fun whenNoLikedActivities_thenDisplaysEmptyMessage() {
    profileViewModel = mock(ProfileViewModel::class.java)
    // Set liked activities to empty to simulate no liked activities
    testUser = testUser.copy(likedActivities = emptyList())
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(testUser))

    composeTestRule.setContent {
      LikedActivitiesScreen(viewModel, navigationActions, profileViewModel)
    }

    // Verify empty liked activity message is displayed
    composeTestRule.onNodeWithText("There is no liked activity yet.").assertIsDisplayed()
  }

  @Test
  fun whenLikedActivitiesExist_thenDisplaysActivityCards() {
    profileViewModel = mock(ProfileViewModel::class.java)
    composeTestRule.setContent {
      ActivityCard2(
          activityId = testActivity.uid,
          navigationActions = navigationActions,
          listActivitiesViewModel = viewModel,
          profileViewModel = profileViewModel,
          profile = testUser,
          allActivities = listOf(testActivity))
    }
    composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()
    composeTestRule.onNodeWithText("Mountain Biking").assertIsDisplayed()
    composeTestRule.onNodeWithText("Exciting mountain biking experience.").assertIsDisplayed()
    composeTestRule.onNodeWithText("EPFL").assertIsDisplayed()
    composeTestRule.onNodeWithText("8/15").assertIsDisplayed()
  }

  @Test
  fun activityListIsDisplayed() {
    profileViewModel = mock(ProfileViewModel::class.java)
    composeTestRule.setContent {
      ActivityCard2(
          activityId = testActivity.uid,
          navigationActions = navigationActions,
          listActivitiesViewModel = viewModel,
          profileViewModel = profileViewModel,
          profile = testUser,
          allActivities = listOf(testActivity))
    }
    composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()
    composeTestRule.onNodeWithText("Mountain Biking").assertIsDisplayed()
    composeTestRule.onNodeWithText("Exciting mountain biking experience.").assertIsDisplayed()
    composeTestRule.onNodeWithText("EPFL").assertIsDisplayed()
    composeTestRule.onNodeWithText("8/15").assertIsDisplayed()
  }

  @Test
  fun likeButtonTogglesCorrectly_whenClicked() {
    // Initially set activity as liked
    profileViewModel = ProfileViewModel(profilesRepository)
    composeTestRule.setContent {
      ActivityCard2(
          activityId = testActivity.uid,
          navigationActions = navigationActions,
          listActivitiesViewModel = viewModel,
          profileViewModel = profileViewModel,
          profile = testUser,
          allActivities = listOf(testActivity))
    }

    // Verify initial state is liked
    composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("favoriteIcontrue").assertIsDisplayed()

    // Click on the like button
    composeTestRule.onNodeWithTag("favoriteIcontrue").performClick()

    // Verify that the like button is toggled
    composeTestRule.onNodeWithTag("favoriteIconfalse").assertIsDisplayed()
    composeTestRule.onNodeWithTag("favoriteIconfalse").performClick()

    // Verify that the like button is toggled
    composeTestRule.onNodeWithTag("favoriteIcontrue").assertIsDisplayed()
  }

  @Test
  fun navigateToActivityDetails() {
    // Initially set activity as liked
    profileViewModel = ProfileViewModel(profilesRepository)
    composeTestRule.setContent {
      ActivityCard2(
          activityId = testActivity.uid,
          navigationActions = navigationActions,
          listActivitiesViewModel = viewModel,
          profileViewModel = profileViewModel,
          profile = testUser,
          allActivities = listOf(testActivity))
    }
    composeTestRule.onNodeWithTag("activityCard").performClick()
    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }
}