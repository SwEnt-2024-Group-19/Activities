package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

class ChooseAccountScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var signInViewModel: SignInViewModel
  private lateinit var profileViewModel: ProfileViewModel
  lateinit var signInRepository: SignInRepository

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    signInViewModel = mock(SignInViewModel::class.java)
    profileViewModel = mock(ProfileViewModel::class.java)
    signInRepository = mock(SignInRepository::class.java) // Mock the repository

    doNothing().whenever(signInRepository).signOut()
  }

  @Test
  fun allElementsAreDisplayed() {
    // Setup a default mock user profile
    val userProfile =
        User(
            name = "John Doe",
            photo = "https://example.com/photo.jpg",
            interests = listOf(),
            surname = "Doe",
            id = "123",
            activities = listOf())
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(userProfile))

    // Set the content once for this test
    composeTestRule.setContent {
      ChooseAccountScreen(navigationActions, signInViewModel, profileViewModel)
    }

    // Check all elements are displayed
    composeTestRule.onNodeWithTag("chooseAccountScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("greetingText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("continueText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("switchAccountButton").assertIsDisplayed()
  }

  @Test
  fun displaysGreetingTextAndProfileImage_whenUserProfileIsNotNull() {
    // Setup the mock user profile with a non-null user
    val userProfile =
        User(
            name = "John Doe",
            photo = "https://example.com/photo.jpg",
            interests = listOf(),
            surname = "Doe",
            id = "123",
            activities = listOf())
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(userProfile))

    // Set the content once for this test
    composeTestRule.setContent {
      ChooseAccountScreen(navigationActions, signInViewModel, profileViewModel)
    }

    // Check greeting text and profile image
    composeTestRule.onNodeWithText("Hello John Doe, you are already signed in!").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
  }

  @Test
  fun displaysDefaultGreeting_whenUserProfileIsNull() {
    // Setup the mock user profile to return null
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(null))

    // Set the content once for this test
    composeTestRule.setContent {
      ChooseAccountScreen(navigationActions, signInViewModel, profileViewModel)
    }

    // Check default greeting text
    composeTestRule.onNodeWithText("Hello User, you are already signed in!").assertIsDisplayed()
  }

  @Test
  fun clickableContinueText_navigatesToOverview() {
    // Setup a default mock user profile
    val userProfile =
        User(
            name = "John Doe",
            photo = "https://example.com/photo.jpg",
            interests = listOf(),
            surname = "Doe",
            id = "123",
            activities = listOf())
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(userProfile))

    // Set the content once for this test
    composeTestRule.setContent {
      ChooseAccountScreen(navigationActions, signInViewModel, profileViewModel)
    }

    // Perform click on "Continue" text and verify navigation
    composeTestRule.onNodeWithText("Continue as John Doe").assertIsDisplayed().performClick()

    verify(navigationActions).navigateTo(Screen.OVERVIEW)
  }


    @Test
    fun displaysPlaceholder_whenProfileImageUrlIsEmpty() {
        // Setup the mock user profile with an empty photo URL
        val userProfile = User(
            name = "John Doe",
            photo = "",
            interests = listOf(),
            surname = "Doe",
            id = "123",
            activities = listOf()
        )
        whenever(profileViewModel.userState).thenReturn(MutableStateFlow(userProfile))

        // Set the content once for this test
        composeTestRule.setContent {
            ChooseAccountScreen(navigationActions, signInViewModel, profileViewModel)
        }

        // Check that the profile picture node exists, regardless of URL content
        composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
    }



}