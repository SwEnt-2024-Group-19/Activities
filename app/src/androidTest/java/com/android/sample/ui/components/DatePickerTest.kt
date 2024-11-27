package com.android.sample.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.resources.dummydata.initialDate
import com.google.firebase.Timestamp
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class DatePickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun datePicker_disallowsPastDates() {
        composeTestRule.setContent {
            MyDatePicker(onDateSelected = {}, isOpen = true, initialDate = null)
        }

        val pastDate = LocalDate.now().minusDays(2)
        composeTestRule.onNodeWithText(pastDate.toString()).assertDoesNotExist()
    }

    @Test
    fun datePicker_isDisplayed() {
        composeTestRule.setContent {
            MyDatePicker(onDateSelected = {}, isOpen = true, initialDate = null)
        }
        composeTestRule.onNodeWithText("Select a date").assertExists()
    }

    @Test
    fun datePicker_isNotDisplayed() {
        composeTestRule.setContent {
            MyDatePicker(onDateSelected = {}, isOpen = false, initialDate = null)
        }
        composeTestRule.onNodeWithText("Select a date").assertDoesNotExist()
    }
}