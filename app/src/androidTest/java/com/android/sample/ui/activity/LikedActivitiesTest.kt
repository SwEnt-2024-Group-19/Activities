package com.android.sample.ui.activity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.resources.dummydata.activity
import com.android.sample.resources.dummydata.activityBiking
import com.android.sample.resources.dummydata.testUser
import com.android.sample.ui.listActivities.ActivityCard2
import com.android.sample.ui.listActivities.LikedActivitiesScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
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

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {

    activitiesRepository = mock(ActivitiesRepository::class.java)
    profilesRepository = mock(ProfilesRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    viewModel = ListActivitiesViewModel(profilesRepository, activitiesRepository)

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
    val emptyTestUser = testUser.copy(likedActivities = emptyList())
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(emptyTestUser))

    composeTestRule.setContent {
      LikedActivitiesScreen(viewModel, navigationActions, profileViewModel)
    }

    // Verify empty liked activity message is displayed
    composeTestRule.onNodeWithText("There is no liked activity yet.").assertIsDisplayed()
  }








}
