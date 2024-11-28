package com.android.sample.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import org.junit.Rule
import org.junit.Test

class NoInternetScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun noInternetScreen_displaysCorrectTexts() {
    composeTestRule.setContent {
      NoInternetScreen(paddingValues = PaddingValues(MEDIUM_PADDING.dp))
    }

    composeTestRule.onNodeWithText("No internet connection").assertIsDisplayed()
    composeTestRule.onNodeWithText("Please check your connection and try again").assertIsDisplayed()
  }

  @Test
  fun noInternetScreen_displaysImage() {
    composeTestRule.setContent {
      NoInternetScreen(paddingValues = PaddingValues(MEDIUM_PADDING.dp))
    }
    composeTestRule.onNodeWithContentDescription("No internet connection").assertIsDisplayed()
  }
}
