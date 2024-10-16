package com.android.sample.ui.activity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.types
import com.android.sample.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class CreateActivityScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockViewModel = mockk<ListActivitiesViewModel>()
  private val mockNavigationActions = mock<NavigationActions>()


  @Test
  fun createActivityScreen_dropdownOpensAndDisplaysOptions() {
    composeTestRule.setContent { CreateActivityScreen(mockViewModel, mockNavigationActions) }

    // Simulate a click to open the dropdown
    composeTestRule.onNodeWithTag("chooseTypeMenu").performClick()

    // Verify dropdown is expanded and the first option is displayed
    composeTestRule.onNodeWithTag("chooseTypeMenu").assertIsDisplayed()
    composeTestRule.onNodeWithText(types[0].name).assertIsDisplayed()
    composeTestRule.onNodeWithText(types[1].name).assertIsDisplayed()
    composeTestRule.onNodeWithText(types[2].name).assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_selectsDropdownOption1() {
    composeTestRule.setContent { CreateActivityScreen(mockViewModel, mockNavigationActions) }

    // Open the dropdown
    composeTestRule.onNodeWithTag("chooseTypeMenu").performClick()

    // Click on the first item in the dropdown
    composeTestRule.onNodeWithText(types[0].name).performClick()

    // Verify that the selected option is now displayed in the TextField
    composeTestRule.onNodeWithText(types[0].name).assertIsDisplayed()

    composeTestRule.onNodeWithTag("chooseTypeMenu").performClick()

    // Click on the first item in the dropdown
    composeTestRule.onNodeWithText(types[1].name).performClick()

    // Verify that the selected option is now displayed in the TextField
    composeTestRule.onNodeWithText(types[1].name).assertIsDisplayed()

    composeTestRule.onNodeWithTag("chooseTypeMenu").performClick()

    // Click on the first item in the dropdown
    composeTestRule.onNodeWithText(types[2].name).performClick()

    // Verify that the selected option is now displayed in the TextField
    composeTestRule.onNodeWithText(types[2].name).assertIsDisplayed()
  }
}
