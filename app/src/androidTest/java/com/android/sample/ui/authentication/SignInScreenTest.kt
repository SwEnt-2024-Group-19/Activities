package com.android.sample.ui.authentication

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doAnswer
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
    // Set the SignInScreen as the content to test
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

    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("achraf@hakimi.ma")
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("")
    composeTestRule.onNodeWithTag("SignInButton").performClick()
    composeTestRule.onNodeWithTag("PasswordErrorText").assertIsDisplayed()
  }

  @Test
  fun testContinueAsGuestButton() {

    composeTestRule.onNodeWithTag("ContinueAsGuestButton").performClick()
    verify(navigationActions).navigateTo(Screen.OVERVIEW)
  }

  @Test
  fun testGoToSignUpButton() {

    composeTestRule.onNodeWithTag("GoToSignUpButton").performClick()
    verify(navigationActions).navigateTo(Screen.SIGN_UP)
  }

  /*@Test
  fun testSignInWithEmailAndPasswordIsCalledWhenValidEmailAndPassword() {
    // Given
    val email = "achraf@hakimi.ma"
    val password = "correct_password"
    val onAuthSuccess = mock<() -> Unit>()
    val onAuthError = mock<(String) -> Unit>()
    val navigationActions = mock(NavigationActions::class.java)
    val viewModel = mock(SignInViewModel::class.java)

    // When: Enter valid email and password, then click the sign-in button
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput(email)
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput(password)
    composeTestRule.onNodeWithTag("SignInButton").performClick()

    // Then: Verify that signInWithEmailAndPassword was called with the correct arguments
    verify(viewModel)
        .signInWithEmailAndPassword(
            email,
            password,
            onAuthSuccess, // onAuthSuccess
            onAuthError, // onAuthError
            navigationActions)
  }*/

  @Test
  fun googleSignInReturnsValidActivityResult() {
    Intents.init()
    composeTestRule.onNodeWithTag("GoogleSignInButton").performClick()

    // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
    intended(toPackage("com.google.android.gms"))
  }
}
