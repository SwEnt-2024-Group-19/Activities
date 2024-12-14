package com.android.sample.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class NoInternetScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun NoInternetScreen_displaysCorrectTexts() {
    composeTestRule.setContent { NoInternetScreen() }

    // Directly use the hardcoded expected string for the assertion
    composeTestRule.onNodeWithText("No internet connection").assertIsDisplayed()
  }
}
