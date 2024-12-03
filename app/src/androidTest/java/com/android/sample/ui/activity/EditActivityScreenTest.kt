package com.android.sample.ui.activity

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
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
import com.android.sample.model.activity.categories
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationPermissionChecker
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.LocationViewModel
import com.android.sample.resources.dummydata.activity
import com.android.sample.resources.dummydata.locationList
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class EditActivityScreenTest {
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var mockLocationRepository: LocationRepository
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel
  private lateinit var mockLocationViewModel: LocationViewModel
  private lateinit var mockPermissionChecker: LocationPermissionChecker

  private lateinit var mockImageViewModel: ImageViewModel
  private lateinit var mockImageRepository: ImageRepositoryFirestore

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    activitiesRepository = mock(ActivitiesRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    listActivitiesViewModel = mock(ListActivitiesViewModel::class.java)
    mockLocationRepository = mock(LocationRepository::class.java)
    mockPermissionChecker = LocationPermissionChecker(context)
    mockLocationViewModel = LocationViewModel(mockLocationRepository, mockPermissionChecker)

    `when`(listActivitiesViewModel.selectedActivity).thenReturn(MutableStateFlow(activity))
    `when`(navigationActions.currentRoute()).thenReturn(Screen.EDIT_ACTIVITY)
    `when`(mockLocationRepository.search(any(), any(), any())).then { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Location>) -> Unit
      onSuccess(locationList)
    }
    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputTitleEdit"))
    composeTestRule.onNodeWithTag("inputTitleEdit").assertIsDisplayed()

    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDescriptionEdit"))
    composeTestRule.onNodeWithTag("inputDescriptionEdit").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputLocationEdit"))
    composeTestRule.onNodeWithTag("inputLocationEdit").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputPriceEdit"))
    composeTestRule.onNodeWithTag("inputPriceEdit").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputPlacesLeftEdit"))
    composeTestRule.onNodeWithTag("inputPlacesLeftEdit").assertIsDisplayed()
  }

  @Test
  fun inputsHaveInitialValue() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }

    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputTitleEdit"))
    composeTestRule.onNodeWithTag("inputTitleEdit").assertTextContains(activity.title)
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDescriptionEdit"))
    composeTestRule.onNodeWithTag("inputDescriptionEdit").assertTextContains(activity.description)
  }

  @Test
  fun saveButtonSavesActivity() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputTitleEdit"))
    composeTestRule.onNodeWithTag("inputTitleEdit").performTextInput("Updated Title")
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDescriptionEdit"))
    composeTestRule.onNodeWithTag("inputDescriptionEdit").performTextInput("Updated Description")
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("editButton"))
    composeTestRule.onNodeWithTag("editButton").performClick()
  }

  @Test
  fun goBackButtonNavigatesBack() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun inputFieldsUpdateViewModel() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputTitleEdit"))
    composeTestRule.onNodeWithTag("inputTitleEdit").performTextInput("Updated Title")
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDescriptionEdit"))
    composeTestRule.onNodeWithTag("inputDescriptionEdit").performTextInput("Updated Description")
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputLocationEdit"))
    composeTestRule.onNodeWithTag("inputLocationEdit").performTextInput("Updated Location")
  }

  @Test
  fun addAttendeeButton_opensAddUserDialog() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("addAttendeeButton"))
    composeTestRule.onNodeWithTag("addAttendeeButton").performClick()
    composeTestRule.onNodeWithTag("addUserDialog").assertExists()
  }

  @Test
  fun simpleUserIsDisplayed() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("addAttendeeButton"))
    composeTestRule.onNodeWithTag("addAttendeeButton").performClick()
    composeTestRule.onNodeWithTag("addUserDialog").assertExists()
    composeTestRule.onNodeWithTag("nameTextFieldUser").assertIsDisplayed()
    composeTestRule.onNodeWithTag("surnameTextFieldUser").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addUserButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nameTextFieldUser").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextFieldUser").performTextInput("Doe")
    composeTestRule.onNodeWithTag("addUserButton").performClick()
    composeTestRule.onNodeWithTag("attendeeName0", useUnmergedTree = true).assertTextEquals("John Doe")
  }

  @Test
  fun buttonDateAndTimeAreDisplayed() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("changeDateButton"))
    composeTestRule.onNodeWithTag("changeDateButton").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("changeTimeButton"))
    composeTestRule.onNodeWithTag("changeTimeButton").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("changeEndingTimeButton"))
    composeTestRule.onNodeWithTag("changeEndingTimeButton").assertIsDisplayed()
  }

  @Test
  fun buttonDateShowDialog() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("changeDateButton"))
    composeTestRule.onNodeWithTag("changeDateButton").performClick()
    composeTestRule.onNodeWithText("Select a date").assertIsDisplayed()
  }

  @Test
  fun startTimeButtonSHowDialog() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("changeTimeButton"))
    composeTestRule.onNodeWithTag("changeTimeButton").performClick()
    composeTestRule.onNodeWithText("Pick a time").assertIsDisplayed()
  }

  @Test
  fun endTimeButtonShowDialog() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("changeEndingTimeButton"))
    composeTestRule.onNodeWithTag("changeEndingTimeButton").performClick()
    composeTestRule.onNodeWithText("Pick a time").assertIsDisplayed()
  }

  @Test
  fun editActivityScreen_dropdownCategoryOpensAndDisplaysOptions() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("chooseCategoryMenu"))
    composeTestRule.onNodeWithText(activity.category.toString()).assertIsDisplayed()

    composeTestRule.onNodeWithTag("chooseCategoryMenu").performClick()

    composeTestRule.onNodeWithTag("chooseTypeMenu").assertIsDisplayed()
    composeTestRule.onNodeWithText(categories[1].name).assertIsDisplayed()
    composeTestRule.onNodeWithText(categories[2].name).assertIsDisplayed()
  }

  @Test
  fun editActivityScreen_selectsCategoryDropdownOption1() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel, navigationActions, mockLocationViewModel, mockImageViewModel)
    }

    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("chooseCategoryMenu"))

    composeTestRule.onNodeWithTag("categoryTextField").assertIsDisplayed()
    composeTestRule.onNodeWithText("Activity Category").assertIsDisplayed()

    composeTestRule.onNodeWithTag("chooseCategoryMenu").performClick()

    composeTestRule.onNodeWithText(categories[1].name).performClick()

    composeTestRule.onNodeWithText(categories[1].name).assertIsDisplayed()

    composeTestRule.onNodeWithTag("chooseCategoryMenu").performClick()
    composeTestRule.onNodeWithText(categories[0].name).performClick()

    composeTestRule.onNodeWithText(categories[0].name).assertIsDisplayed()

    composeTestRule.onNodeWithTag("chooseCategoryMenu").performClick()

    composeTestRule.onNodeWithText(categories[2].name).performClick()

    composeTestRule.onNodeWithText(categories[2].name).assertIsDisplayed()
  }
}
