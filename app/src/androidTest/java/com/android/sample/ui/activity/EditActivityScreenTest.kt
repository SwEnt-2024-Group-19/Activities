package com.android.sample.ui.activity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.util.GregorianCalendar
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class EditActivityScreenTest {
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel

  @get:Rule val composeTestRule = createComposeRule()

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
    activitiesRepository = mock(ActivitiesRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    listActivitiesViewModel = mock(ListActivitiesViewModel::class.java)
    `when`(listActivitiesViewModel.selectedActivity).thenReturn(MutableStateFlow(activity))
    `when`(navigationActions.currentRoute()).thenReturn(Screen.EDIT_ACTIVITY)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }
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
        .performScrollToNode(hasTestTag("inputDateEdit"))
    composeTestRule.onNodeWithTag("inputDateEdit").assertIsDisplayed()
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
    composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }

    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputTitleEdit"))
    composeTestRule.onNodeWithTag("inputTitleEdit").assertTextContains(activity.title)
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDescriptionEdit"))
    composeTestRule.onNodeWithTag("inputDescriptionEdit").assertTextContains(activity.description)
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDateEdit"))
    composeTestRule.onNodeWithTag("inputDateEdit").assertTextContains("5/9/2024")
  }

  @Test
  fun saveButtonSavesActivity() {
    composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }
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
    composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun inputFieldsUpdateViewModel() {
    composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }
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
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("inputDateEdit"))
    composeTestRule.onNodeWithTag("inputDateEdit").performTextInput("5/10/2024")
  }

  @Test
  fun addAttendeeButton_opensAddUserDialog() {
    composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
        .performScrollToNode(hasTestTag("addAttendeeButton"))
    composeTestRule.onNodeWithTag("addAttendeeButton").performClick()
    composeTestRule.onNodeWithTag("addUserDialog").assertExists()
  }

  @Test
  fun simpleUserIsDisplayed() {
    composeTestRule.setContent { EditActivityScreen(listActivitiesViewModel, navigationActions) }
    composeTestRule
        .onNodeWithTag("activityEditScreen")
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
