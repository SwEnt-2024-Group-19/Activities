package com.android.sample.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test

class DatePickerTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun datePicker_disallowsPastDates() {
    composeTestRule.setContent {
      MyDatePicker(onDateSelected = {}, isOpen = true, initialDate = null, onCloseRequest = {})
    }

    val pastDate = LocalDate.now().minusDays(2)
    composeTestRule.onNodeWithText(pastDate.toString()).assertDoesNotExist()
  }

  @Test
  fun datePicker_isDisplayed() {
    composeTestRule.setContent {
      MyDatePicker(onDateSelected = {}, isOpen = true, initialDate = null, onCloseRequest = {})
    }
    composeTestRule.onNodeWithText("Select a date").assertExists()
  }

  @Test
  fun datePicker_isNotDisplayed() {
    composeTestRule.setContent {
      MyDatePicker(onDateSelected = {}, isOpen = false, initialDate = null, onCloseRequest = {})
    }
    composeTestRule.onNodeWithText("Select a date").assertDoesNotExist()
  }
}
