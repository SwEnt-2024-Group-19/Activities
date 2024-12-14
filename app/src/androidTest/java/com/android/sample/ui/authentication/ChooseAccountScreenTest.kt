package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.resources.dummydata.userProfile
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

  private lateinit var mockImageViewModel: ImageViewModel
  private lateinit var mockImageRepository: ImageRepositoryFirestore
  lateinit var signInRepository: SignInRepository

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    signInViewModel = mock(SignInViewModel::class.java)
    profileViewModel = mock(ProfileViewModel::class.java)
    signInRepository = mock(SignInRepository::class.java) // Mock the repository

    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository)
    doNothing().whenever(signInRepository).signOut()
  }

  @Test
  fun allElementsAreDisplayed() {
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(userProfile))

    composeTestRule.setContent {
      ChooseAccountScreen(navigationActions, signInViewModel, profileViewModel, mockImageViewModel)
    }

    // Check that the greeting text is displayed
    composeTestRule.onNodeWithTag("greetingText").assertIsDisplayed()

    // Check that the buttons are displayed
    composeTestRule.onNodeWithTag("continueText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("switchAccountButton").assertIsDisplayed()
  }

  @Test
  fun displaysGreetingText_whenUserProfileIsNotNull() {
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(userProfile))

    composeTestRule.setContent {
      ChooseAccountScreen(navigationActions, signInViewModel, profileViewModel, mockImageViewModel)
    }

    // Check the personalized greeting text
    composeTestRule.onNodeWithText("Hello, you are already signed in!").assertIsDisplayed()
  }

  @Test
  fun displaysDefaultGreeting_whenUserProfileIsNull() {
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(null))

    composeTestRule.setContent {
      ChooseAccountScreen(navigationActions, signInViewModel, profileViewModel, mockImageViewModel)
    }

    // Check the default greeting text
    composeTestRule.onNodeWithText("Hello, you are already signed in!").assertIsDisplayed()
  }

  @Test
  fun clickableContinueText_navigatesToOverview() {
    whenever(profileViewModel.userState).thenReturn(MutableStateFlow(userProfile))

    composeTestRule.setContent {
      ChooseAccountScreen(navigationActions, signInViewModel, profileViewModel, mockImageViewModel)
    }

    // Perform click on the "Continue" button
    composeTestRule.onNodeWithTag("continueText").assertIsDisplayed().performClick()

    // Verify navigation to the Overview screen
    verify(navigationActions).navigateTo(Screen.OVERVIEW)
  }
}
