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
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.resources.dummydata.activity
import com.android.sample.resources.dummydata.locationList
import com.android.sample.resources.dummydata.testUser
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
  private lateinit var profileViewModel: ProfileViewModel

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
    profileViewModel = mock(ProfileViewModel::class.java)
    `when`(profileViewModel.userState).thenReturn(MutableStateFlow(testUser))
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
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputTitleCreate"))
    composeTestRule.onNodeWithTag("inputTitleCreate").assertIsDisplayed()

    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDescriptionCreate"))
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputLocationCreate"))
    composeTestRule.onNodeWithTag("inputLocationCreate").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputPriceCreate"))
    composeTestRule.onNodeWithTag("inputPriceCreate").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputPlacesCreate"))
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertIsDisplayed()
  }

  @Test
  fun inputsHaveInitialValue() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
    }

    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputTitleCreate"))
    composeTestRule.onNodeWithTag("inputTitleCreate").assertTextContains(activity.title)
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDescriptionCreate"))
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertTextContains(activity.description)
  }

  @Test
  fun saveButtonSavesActivity() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputTitleCreate"))
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Updated Title")
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDescriptionCreate"))
    composeTestRule.onNodeWithTag("inputDescriptionCreate").performTextInput("Updated Description")
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("editButton"))
    composeTestRule.onNodeWithTag("editButton").performClick()
  }

  @Test
  fun goBackButtonNavigatesBack() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
    }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun inputFieldsUpdateViewModel() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputTitleCreate"))
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Updated Title")
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDescriptionCreate"))
    composeTestRule.onNodeWithTag("inputDescriptionCreate").performTextInput("Updated Description")
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputLocationCreate"))
    composeTestRule.onNodeWithTag("inputLocationCreate").performTextInput("Updated Location")
  }

  @Test
  fun addAttendeeButton_opensAddUserDialog() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
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
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
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
    composeTestRule
        .onNodeWithTag("attendeeName0", useUnmergedTree = true)
        .assertTextEquals("John Doe")
  }

  @Test
  fun buttonDateAndTimeAreDisplayed() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
    }
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDateCreate"))
    composeTestRule.onNodeWithTag("inputDateCreate").assertIsDisplayed()

    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputStartTimeCreate"))
    composeTestRule.onNodeWithTag("inputStartTimeCreate").assertIsDisplayed()

    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputEndTimeCreate"))
    composeTestRule.onNodeWithTag("inputEndTimeCreate").assertIsDisplayed()
  }

  @Test
  fun buttonDateShowDialog() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDateCreate"))
    composeTestRule.onNodeWithTag("inputDateCreate").performClick()
    composeTestRule.onNodeWithText("Select a date").assertIsDisplayed()
  }

  @Test
  fun startTimeButtonSHowDialog() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputStartTimeCreate"))
    composeTestRule.onNodeWithTag("inputStartTimeCreate").performClick()
    composeTestRule.onNodeWithText("Pick a time").assertIsDisplayed()
  }

  @Test
  fun endTimeButtonShowDialog() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputEndTimeCreate"))
    composeTestRule.onNodeWithTag("inputEndTimeCreate").performClick()
    composeTestRule.onNodeWithText("Pick a time").assertIsDisplayed()
  }

  @Test
  fun editActivityScreen_dropdownCategoryOpensAndDisplaysOptions() {
    composeTestRule.setContent {
      EditActivityScreen(
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
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
          listActivitiesViewModel,
          navigationActions,
          mockLocationViewModel,
          mockImageViewModel,
          profileViewModel)
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
