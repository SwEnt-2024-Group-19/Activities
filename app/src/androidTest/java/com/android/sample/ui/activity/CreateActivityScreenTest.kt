package com.android.sample.ui.activity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.types
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import io.mockk.mockk
import java.util.GregorianCalendar
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

class CreateActivityScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockRepository: ActivitiesRepository
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockViewModel: ListActivitiesViewModel
  private val mockProfileViewModel = mockk<ProfileViewModel>()
  private val activity =
      Activity(
          "1",
          "First Activity",
          "Do something",
          creator = "John Doe",
          date = Timestamp(GregorianCalendar(2024, 8, 5).time),
          location = "EPFL",
          status = ActivityStatus.ACTIVE,
          participants = listOf(),
          price = 10.0,
          placesLeft = 10,
          maxPlaces = 20,
          images = listOf("image1", "image2"),
          type = ActivityType.PRO,
          startTime = "09:30",
          duration = "00:30")

  @Before
  fun setUp() {
    mockRepository = Mockito.mock(ActivitiesRepository::class.java)
    mockNavigationActions = Mockito.mock(NavigationActions::class.java)
    mockViewModel = Mockito.mock(ListActivitiesViewModel::class.java)
    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ADD_ACTIVITY)
  }

  @Test
  fun createActivityScreen_displaysTitleField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputTitleCreate"))
    composeTestRule.onNodeWithTag("inputTitleCreate").assertExists()
    composeTestRule.onNodeWithTag("inputTitleCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysDescriptionField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputDescriptionCreate"))
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertExists()
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysDateField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputDateCreate"))
    composeTestRule.onNodeWithTag("inputDateCreate").assertExists()
    composeTestRule.onNodeWithTag("inputDateCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysPriceField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputPriceCreate"))
    composeTestRule.onNodeWithTag("inputPriceCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPriceCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysPlacesLeftField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputPlacesCreate"))
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysLocationField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputLocationCreate"))
    composeTestRule.onNodeWithTag("inputLocationCreate").assertExists()
    composeTestRule.onNodeWithTag("inputLocationCreate").assertIsDisplayed()
  }

  @Test
  fun createButton_isDisabledInitially() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("createButton"))
    composeTestRule.onNodeWithTag("createButton").assertIsNotEnabled()
  }

  @Test
  fun createButton_isEnabledWhenAllFieldsAreFilled() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputTitleCreate"))
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Title")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputDescriptionCreate"))
    composeTestRule.onNodeWithTag("inputDescriptionCreate").performTextInput("Description")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputDateCreate"))
    composeTestRule.onNodeWithTag("inputDateCreate").performTextInput("01/01/2022")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputPriceCreate"))
    composeTestRule.onNodeWithTag("inputPriceCreate").performTextInput("100")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputPlacesCreate"))
    composeTestRule.onNodeWithTag("inputPlacesCreate").performTextInput("10/20")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputLocationCreate"))
    composeTestRule.onNodeWithTag("inputLocationCreate").performTextInput("Location")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("createButton"))
    composeTestRule.onNodeWithTag("createButton").assertIsEnabled()
  }

  @Test
  fun createButton_isDisabledWhenFieldsAreCleared() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputTitleCreate"))
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("createButton"))
    composeTestRule.onNodeWithTag("createButton").assertIsNotEnabled()
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputTitleCreate"))
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Title")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputDescriptionCreate"))
    composeTestRule.onNodeWithTag("inputDescriptionCreate").performTextInput("Description")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputDateCreate"))
    composeTestRule.onNodeWithTag("inputDateCreate").performTextInput("01/01/2022")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputPriceCreate"))
    composeTestRule.onNodeWithTag("inputPriceCreate").performTextInput("100")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputPlacesCreate"))
    composeTestRule.onNodeWithTag("inputPlacesCreate").performTextInput("10/20")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputLocationCreate"))
    composeTestRule.onNodeWithTag("inputLocationCreate").performTextInput("Location")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("createButton"))
    composeTestRule.onNodeWithTag("createButton").assertIsEnabled()
  }

  @Test
  fun createButton_isDisabledWhenPartialFieldsAreFilled() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputTitleCreate"))
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Title")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputDateCreate"))
    composeTestRule.onNodeWithTag("inputDateCreate").performTextInput("01/01/2022")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("inputPriceCreate"))
    composeTestRule.onNodeWithTag("inputPriceCreate").performTextInput("100")
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("createButton"))
    composeTestRule.onNodeWithTag("createButton").assertIsNotEnabled()
  }

  @Test
  fun createActivityScreen_dropdownOpensAndDisplaysOptions() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("chooseTypeMenu"))
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
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }

    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("chooseTypeMenu"))
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

  @Test
  fun simpleUserIsDisplayed() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("addAttendeeButton"))
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
    // composeTestRule.onNodeWithTag("attendeeRow0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("attendeeName0").assertTextEquals("John")
    composeTestRule.onNodeWithTag("attendeeSurname0").assertTextEquals("Doe")
    composeTestRule.onNodeWithTag("attendeeAge0").assertTextEquals("25")
  }
}
