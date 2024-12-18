package com.android.sample.ui.authentication

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Mockito.mock

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
    composeTestRule.setContent {
      SignInScreen(navigationActions = navigationActions, viewModel) // Passing null for simplicity
    }
  }

  @Test
  fun allElementsAreDisplayed() {
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("SignInButton"))
    composeTestRule.onNodeWithTag("SignInButton").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("GoogleSignInButton"))
    composeTestRule.onNodeWithTag("GoogleSignInButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SignInScreenColumn").performScrollToNode(hasTestTag("AppLogo"))
    composeTestRule.onNodeWithTag("AppLogo").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("ContinueAsGuestButton"))
    composeTestRule.onNodeWithTag("ContinueAsGuestButton").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("GoToSignUpButton"))
    composeTestRule.onNodeWithTag("GoToSignUpButton").assertIsDisplayed()
  }

  @Test
  fun wrongPassword() {
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("achraf@hakimi.ma")
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("")
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("SignInButton"))
    composeTestRule.onNodeWithTag("SignInButton").performClick()
    composeTestRule.onNodeWithTag("PasswordErrorText").assertIsDisplayed()
  }

  @Test
  fun testContinueAsGuestButton() {
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("ContinueAsGuestButton"))
    composeTestRule.onNodeWithTag("ContinueAsGuestButton").performClick()
    verify(navigationActions).navigateTo(Screen.OVERVIEW)
  }

  @Test
  fun testGoToSignUpButton() {
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("GoToSignUpButton"))
    composeTestRule.onNodeWithTag("GoToSignUpButton").performClick()
    verify(navigationActions).navigateTo(Screen.SIGN_UP)
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    Intents.init()
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("GoogleSignInButton"))
    composeTestRule.onNodeWithTag("GoogleSignInButton").performClick()

    // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
    intended(toPackage("com.google.android.gms"))
  }

  @Test
  fun invalidEmailShowsError() {
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("invalid_email")
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("SignInButton"))
    composeTestRule.onNodeWithTag("SignInButton").performClick()
    composeTestRule.onNodeWithTag("EmailErrorText").assertIsDisplayed()
  }

  @Test
  fun validPasswordClearsError() {
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("validPassword123")
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("SignInButton"))
    composeTestRule.onNodeWithTag("SignInButton").performClick()
    composeTestRule.onNodeWithTag("PasswordErrorText").assertDoesNotExist()
  }

  @Test
  fun togglePasswordVisibility() {
    composeTestRule
        .onNodeWithTag("SignInScreenColumn")
        .performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithContentDescription("Show password").assertExists()

    // Click the visibility icon to show the password
    composeTestRule.onNodeWithContentDescription("Show password").performClick()

    // Verify password visibility toggle behavior (e.g., check attribute or visual state).
    composeTestRule.onNodeWithTag("PasswordTextField").assertExists()
  }
}
