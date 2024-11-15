package com.android.sample.ui.activity

import android.content.Context
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
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.types
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationPermissionChecker
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.resources.dummydata.testUser
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class CreateActivityScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockRepository: ActivitiesRepository
  private lateinit var mockLocationRepository: LocationRepository
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockViewModel: ListActivitiesViewModel
  private lateinit var mockLocationViewModel: LocationViewModel
  private lateinit var mockPermissionChecker: LocationPermissionChecker

  private lateinit var profileViewModel: ProfileViewModel

  private val location = Location(46.519962, 6.633597, "EPFL")
  private val location2 = Location(46.5, 6.6, "Lausanne")
  private val locationList = listOf(location, location2)
  private val locationListFlow = MutableStateFlow(listOf(location, location2))

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    mockRepository = Mockito.mock(ActivitiesRepository::class.java)
    mockNavigationActions = Mockito.mock(NavigationActions::class.java)
    mockViewModel = Mockito.mock(ListActivitiesViewModel::class.java)
    mockLocationRepository = Mockito.mock(LocationRepository::class.java)
    mockPermissionChecker = LocationPermissionChecker(context)
    `when`(mockLocationRepository.search(any(), any(), any())).then { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Location>) -> Unit
      onSuccess(locationList)
    }

    mockLocationViewModel = LocationViewModel(mockLocationRepository, mockPermissionChecker)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ADD_ACTIVITY)
    profileViewModel = mock(ProfileViewModel::class.java)
    `when`(profileViewModel.userState).thenReturn(MutableStateFlow(testUser))
  }

  private fun setUpComposeContent() {
    composeTestRule.setContent {
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
    }
  }

  @Test
  fun createActivityScreen_displaysTitleField() {

    composeTestRule.setContent {
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
    }

    // Wait for the UI to finish rendering
    composeTestRule.waitForIdle()

    // Ensure the button is displayed
    // composeTestRule.onNodeWithTag("createButton").assertIsDisplayed()

    // Scroll to the button to ensure it's in view
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("createButton"))

    // Assert that the button is not enabled
    composeTestRule.onNodeWithTag("createButton").assertIsNotEnabled()
  }

  @Test
  fun createButton_isEnabledWhenAllFieldsAreFilled() {
    composeTestRule.setContent {
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
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
      CreateActivityScreen(
          mockViewModel, mockNavigationActions, profileViewModel, mockLocationViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityCreateScreen")
        .performScrollToNode(hasTestTag("addAttendeeButton"))
    composeTestRule.onNodeWithTag("addAttendeeButton").performClick()
    composeTestRule.onNodeWithTag("addUserDialog").assertExists()
    composeTestRule.onNodeWithTag("nameTextFieldUser").assertIsDisplayed()
    composeTestRule.onNodeWithTag("surnameTextFieldUser").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addUserButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nameTextFieldUser").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextFieldUser").performTextInput("Doe")
    composeTestRule.onNodeWithTag("addUserButton").performClick()
    composeTestRule.onNodeWithTag("attendeeName0").assertTextEquals("John Doe")
  }
}
