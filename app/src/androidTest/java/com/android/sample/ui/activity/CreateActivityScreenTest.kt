
package com.android.sample.ui.activity
import androidx.compose.ui.test.onNodeWithText
import com.android.sample.model.activity.types
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.activity.CreateActivityScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock


class CreateActivityScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockViewModel = mockk<ListActivitiesViewModel>()
  private val mockProfileViewModel = mockk<ProfileViewModel>()
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

  @Test
  fun createActivityScreen_displaysTitleField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputTitleCreate").assertExists()
    composeTestRule.onNodeWithTag("inputTitleCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysDescriptionField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertExists()
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysDateField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputDateCreate").assertExists()
    composeTestRule.onNodeWithTag("inputDateCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysPriceField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputPriceCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPriceCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysPlacesLeftField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysLocationField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputLocationCreate").assertExists()
    composeTestRule.onNodeWithTag("inputLocationCreate").assertIsDisplayed()
  }


  }
}
