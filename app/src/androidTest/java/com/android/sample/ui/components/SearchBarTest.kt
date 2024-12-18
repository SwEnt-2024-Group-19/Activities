package com.android.sample.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun searchBar_updatesValueOnInput() {
    var value = ""
    composeTestRule.setContent { SearchBar(onValueChange = { value = it }, value = value,{}) }

    val newValue = "New Value"
    composeTestRule.onNodeWithTag("searchBar").performTextInput(newValue)
    assertEquals(newValue, value)
  }

  @Test
  fun searchBar_hasSearchIcon() {
    composeTestRule.setContent { SearchBar(onValueChange = {}, value = "",{}) }

    composeTestRule.onNodeWithContentDescription("Search").assertExists()
  }

  @Test
  fun searchBar_clipsWithRoundedCorners() {
    composeTestRule.setContent { SearchBar(onValueChange = {}, value = "",{}) }

    composeTestRule.onNodeWithTag("searchBar").assertExists()
  }
}
