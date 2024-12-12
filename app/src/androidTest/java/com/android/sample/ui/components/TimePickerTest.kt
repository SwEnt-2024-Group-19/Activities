package com.android.sample.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class TimePickerTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testMyTimePicker_isDisplayed() {
    composeTestRule.setContent {
      MyTimePicker(onTimeSelected = {}, isOpen = true, onCloseRequest = {})
    }
    // Check if dialog opens with correct title
    composeTestRule.onNodeWithText("Pick a time").assertExists()
  }

  @Test
  fun testMyPicker_isNotDisplayed() {
    composeTestRule.setContent {
      MyTimePicker(onTimeSelected = {}, isOpen = false, onCloseRequest = {})
    }
    // Check if dialog is not displayed
    composeTestRule.onNodeWithText("Pick a time").assertDoesNotExist()
  }
}
