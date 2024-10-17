package com.android.sample.ui.activity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.navigation.NavigationActions
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

  @Test
  fun createButton_isDisabledInitially() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("createButton").assertIsNotEnabled()
  }

  @Test
  fun createButton_isEnabledWhenAllFieldsAreFilled() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Title")
    composeTestRule.onNodeWithTag("inputDescriptionCreate").performTextInput("Description")
    composeTestRule.onNodeWithTag("inputDateCreate").performTextInput("01/01/2022")
    composeTestRule.onNodeWithTag("inputPriceCreate").performTextInput("100")
    composeTestRule.onNodeWithTag("inputPlacesCreate").performTextInput("10/20")
    composeTestRule.onNodeWithTag("inputLocationCreate").performTextInput("Location")
    composeTestRule.onNodeWithTag("createButton").assertIsEnabled()
  }

  @Test
  fun addAttendeeButton_opensAddUserDialog() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("addAttendeeButton").performClick()
    composeTestRule.onNodeWithTag("addUserDialog").assertExists()
  }

  @Test
  fun createButton_isDisabledWhenFieldsAreCleared() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("")
    composeTestRule.onNodeWithTag("createButton").assertIsNotEnabled()
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Title")
    composeTestRule.onNodeWithTag("inputDescriptionCreate").performTextInput("Description")
    composeTestRule.onNodeWithTag("inputDateCreate").performTextInput("01/01/2022")
    composeTestRule.onNodeWithTag("inputPriceCreate").performTextInput("100")
    composeTestRule.onNodeWithTag("inputPlacesCreate").performTextInput("10/20")
    composeTestRule.onNodeWithTag("inputLocationCreate").performTextInput("Location")
    composeTestRule.onNodeWithTag("createButton").assertIsEnabled()
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("")
  }

  @Test
  fun createButton_isDisabledWhenPartialFieldsAreFilled() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Title")
    composeTestRule.onNodeWithTag("inputDateCreate").performTextInput("01/01/2022")
    composeTestRule.onNodeWithTag("inputPriceCreate").performTextInput("100")
    composeTestRule.onNodeWithTag("createButton").assertIsNotEnabled()
  }

  @Test
  fun SimpleUserIsDisplayed() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("addAttendeeButton").performClick()
    composeTestRule.onNodeWithTag("addUserDialog").assertExists()
    composeTestRule.onNodeWithTag("nameTextFieldUser").assertIsDisplayed()
    composeTestRule.onNodeWithTag("surnameTextFieldUser").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ageTextFieldUser").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addUserButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nameTextFieldUser").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextFieldUser").performTextInput("Doe")
    composeTestRule.onNodeWithTag("ageTextFieldUser").performTextInput("25")
    composeTestRule.onNodeWithTag("addUserButton").performClick()
    composeTestRule.onNodeWithTag("addUserDialog").assertDoesNotExist()
    composeTestRule.onNodeWithTag("attendeeRow0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("attendeeName0").assertTextEquals("John")
    composeTestRule.onNodeWithTag("attendeeSurname0").assertTextEquals("Doe")
    composeTestRule.onNodeWithTag("attendeeAge0").assertTextEquals("25")
  }
}
