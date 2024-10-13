package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class SignInScreenTest {
  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions
  private lateinit var auth: FirebaseAuth

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
    auth = mock(FirebaseAuth::class.java)
  }

  @Test
  fun testEmailTextFieldExists() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(
          navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
    }

    // Check if the email text field exists and is displayed
    composeTestRule.onNodeWithTag("EmailTextField").assertIsDisplayed()
  }

  @Test
  fun testPasswordTextFieldExists() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(
          navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
    }

    // Check if the password text field exists and is displayed
    composeTestRule.onNodeWithTag("PasswordTextField").assertIsDisplayed()
  }

  @Test
  fun testSignInButtonExists() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(
          navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
    }

    // Check if the sign-in button exists and is displayed
    composeTestRule.onNodeWithTag("SignInButton").assertIsDisplayed()
  }

  @Test
  fun testGoogleSignInButtonExists() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(
          navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
    }

    // Check if the Google sign-in button exists and is displayed
    composeTestRule.onNodeWithTag("GoogleSignInButton").assertIsDisplayed()
  }

  @Test
  fun testAppLogoExists() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(
          navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
    }

    // Check if the app logo exists and is displayed
    composeTestRule.onNodeWithTag("AppLogo").assertIsDisplayed()
  }

  @Test
  fun testContinueAsGuestButtonExists() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(
          navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
    }

    // Check if the continue as guest button exists and is displayed
    composeTestRule.onNodeWithTag("ContinueAsGuestButton").assertIsDisplayed()
  }

  @Test
  fun testGoToSignUpButtonExists() {
    // Set the SignInScreen as the content to test
    composeTestRule.setContent {
      SignInScreen(
          navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
    }
    // Check if the go to sign up button exists and is displayed
    composeTestRule.onNodeWithTag("GoToSignUpButton").assertIsDisplayed()
  }
}
