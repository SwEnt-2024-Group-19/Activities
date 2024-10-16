package com.android.sample.ui.authentication

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Mockito.mock
import org.mockito.kotlin.eq
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var auth: FirebaseAuth
  private lateinit var context: Context
  private lateinit var user: FirebaseUser
  private lateinit var viewModel: SignInViewModel
  private lateinit var repository: ProfilesRepository

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    auth = mock(FirebaseAuth::class.java)
    repository = mock(ProfilesRepository::class.java)
    context = mock(Context::class.java)
    user = mock(FirebaseUser::class.java)
    viewModel = mock(SignInViewModel::class.java) // Mocking the ViewModel
  }

  @Test
  fun testEmailTextFieldExists() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(navigationActions = navigationActions, viewModel) // Passing null for simplicity
    }
    composeTestRule.onNodeWithTag("EmailTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PasswordTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SignInButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoogleSignInButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AppLogo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ContinueAsGuestButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoToSignUpButton").assertIsDisplayed()
  }

  @Test
  fun wrongPassword() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(navigationActions = navigationActions, viewModel) // Passing null for simplicity
    }
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("achraf@hakimi.ma")
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("")
    composeTestRule.onNodeWithTag("SignInButton").performClick()
    composeTestRule.onNodeWithTag("PasswordErrorText").assertIsDisplayed()
  }

  @Test
  fun testContinueAsGuestButton() {
    // Set the SignInScreen as the content to test
    `when`(navigationActions.currentRoute()).thenReturn(Route.AUTH)

    composeTestRule.setContent {
      SignInScreen(navigationActions = navigationActions, viewModel) // Passing null for simplicity
    }
    composeTestRule.onNodeWithTag("ContinueAsGuestButton").performClick()
    verify(navigationActions).navigateTo(Screen.OVERVIEW)
  }

  @Test
  fun testGoToSignUpButton() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(navigationActions = navigationActions, viewModel) // Passing null for simplicity
    }
    composeTestRule.onNodeWithTag("GoToSignUpButton").performClick()
    verify(navigationActions).navigateTo(Screen.SIGN_UP)
  }

  @Test
  fun testSuccessfulSignInNavigatesToOverview() {
    // Ensure the ViewModel is a mock
    viewModel = mock(SignInViewModel::class.java)

    // Set up mock behavior for successful sign-in
    doAnswer { invocation ->
      val onAuthSuccess = invocation.arguments[2] as () -> Unit
      onAuthSuccess() // Simulate successful sign-in
      null
    }.`when`(viewModel).signInWithEmailAndPassword(
      anyString(),
      anyString(),
      any(),  // non-null lambda for onAuthSuccess
      any(),  // non-null lambda for onAuthError
      eq(navigationActions)
    )

    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(navigationActions = navigationActions, viewModel)
    }

    // Simulate user input and click the sign-in button
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("user@example.com")
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("password123")
    composeTestRule.onNodeWithTag("SignInButton").performClick()

    // Verify that the navigation to the Overview screen occurred
    verify(navigationActions).navigateTo(Screen.OVERVIEW)
  }


}
