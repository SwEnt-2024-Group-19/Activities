package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.navigation.NavigationActions

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {
    private lateinit var navigationDestination: NavDestination
    private lateinit var navHostController: NavHostController
    private lateinit var navigationActions: NavigationActions

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        navigationDestination = mock(NavDestination::class.java)
        navHostController = mock(NavHostController::class.java)
        navigationActions = NavigationActions(navHostController)
    }

    @Test
    fun testEmailTextFieldExists() {
        // Set the SignUpScreen as the content to test
        composeTestRule.setContent {
            SignUpScreen(
                navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
        }

        // Check if the email text field exists and is displayed
        composeTestRule.onNodeWithTag("EmailTextField").assertIsDisplayed()
    }

    @Test
    fun testPasswordTextFieldExists() {
        // Set the SignUpScreen as the content to test
        composeTestRule.setContent {
            SignUpScreen(
                navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
        }

        // Check if the password text field exists and is displayed
        composeTestRule.onNodeWithTag("PasswordTextField").assertIsDisplayed()
    }

    @Test
    fun testSignUpButtonExists() {
        // Set the SignUpScreen as the content to test
        composeTestRule.setContent {
            SignUpScreen(
                navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
        }

        // Check if the sign-in button exists and is displayed
        composeTestRule.onNodeWithTag("SignUpButton").assertIsDisplayed()
    }

    @Test
    fun testGoToSignInButtonExists() {
        // Set the SignUpScreen as the content to test
        composeTestRule.setContent {
            SignUpScreen(
                navigationActions = NavigationActions(navHostController)) // Passing null for simplicity
        }

        // Check if the sign-in button exists and is displayed
        composeTestRule.onNodeWithTag("GoToSignInButton").assertIsDisplayed()
    }

    @Test
    fun testInvalidEmailShowsErrorMessage() {
        composeTestRule.setContent {
            SignUpScreen(navigationActions = NavigationActions(navHostController))
        }

        // Enter an invalid email
        composeTestRule.onNodeWithTag("EmailTextField").performTextInput("invalidemail")

        // Click on the Sign-In button
        composeTestRule.onNodeWithTag("SignUpButton").performClick()

        // Check if the error message is displayed
        composeTestRule.onNodeWithTag("EmailErrorText").assertIsDisplayed()
    }

    @Test
    fun testShortPasswordShowsErrorMessage() {
        // Set the SignUpScreen as the content to test
        composeTestRule.setContent {
            SignUpScreen(navigationActions = NavigationActions(navHostController))
        }

        composeTestRule.onNodeWithTag("EmailTextField").performTextInput("abcde@gmail.com")

        // Enter a short password (only 5 characters)
        composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("abcde")

        // Click on the Sign-Up button
        composeTestRule.onNodeWithTag("SignUpButton").performClick()

        // Check if the error message for a short password is displayed
        composeTestRule.onNodeWithTag("PasswordErrorText").assertIsDisplayed()
    }

    @Test
    fun testFalseEmailShowsErrorMessage() {
        // Set the SignUpScreen as the content to test
        composeTestRule.setContent {
            SignUpScreen(navigationActions = NavigationActions(navHostController))
        }

        composeTestRule.onNodeWithTag("EmailTextField").performTextInput("abcde")

        // Enter a short password (only 5 characters)
        composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("abcde")

        // Click on the Sign-Up button
        composeTestRule.onNodeWithTag("SignUpButton").performClick()

        // Check if the error message for a short password is displayed
        composeTestRule.onNodeWithTag("EmailErrorText").assertIsDisplayed()
    }
}
