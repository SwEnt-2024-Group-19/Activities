package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    composeTestRule.setContent {
      SignUpScreen(navigationActions = navigationActions) // Passing null for simplicity
    }
  }

  fun allElementsAreDisplayed() {
    // Set the SignUpScreen as the content to test
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("signUpButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("GoToSignInButton"))
    composeTestRule.onNodeWithTag("GoToSignInButton").assertIsDisplayed()
  }

  @Test
  fun testInvalidEmailShowsErrorMessage() {
    // Enter an invalid email
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("invalidemail")

    // Click on the Sign-In button
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("SignUpButton").performClick()

    // Check if the error message is displayed
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("EmailErrorText"))
    composeTestRule.onNodeWithTag("EmailErrorText").assertIsDisplayed()
  }

  @Test
  fun testShortPasswordShowsErrorMessage() {
    // Set the SignUpScreen as the content to test
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("abcde@gmail.com")

    // Enter a short password (only 5 characters)
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("abcde")

    // Click on the Sign-Up button
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("SignUpButton").performClick()

    // Check if the error message for a short password is displayed
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("PasswordErrorText"))
    composeTestRule.onNodeWithTag("PasswordErrorText").assertIsDisplayed()
  }

  @Test
  fun testFalseEmailShowsErrorMessage() {
    // Set the SignUpScreen as the content to test
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("abcde")

    // Enter a short password (only 5 characters)
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("abcde")

    // Click on the Sign-Up button
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("SignUpButton").performClick()

    // Check if the error message for a short password is displayed
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("EmailErrorText"))
    composeTestRule.onNodeWithTag("EmailErrorText").assertIsDisplayed()
  }

  @Test
  fun testEmptyPasswordShowsErrorMessage() {

    // Enter a valid email
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("validemail@gmail.com")

    // Leave password field empty
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("")

    // Click on the Sign-Up button
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("SignUpButton").performClick()

    // Check if the error message for an empty password is displayed
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("PasswordErrorText"))
    composeTestRule.onNodeWithTag("PasswordErrorText").assertIsDisplayed()
  }

  @Test
  fun testGoToSignInButtonNavigatesToSignIn() {
    // Click on the "Go to Sign-In" button
    composeTestRule.onNodeWithTag("SignUpScreenColumn").performScrollToNode(hasTestTag("GoToSignInButton"))
    composeTestRule.onNodeWithTag("GoToSignInButton").performClick()

    // Check if the navigation action to the SignIn screen is called
    verify(navigationActions).navigateTo(Screen.AUTH)
  }
}
