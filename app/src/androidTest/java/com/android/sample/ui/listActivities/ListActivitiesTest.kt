package com.android.sample.ui.listActivities

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
/*
class OverviewScreenTest {
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel

  private val activity =
      Activity(
          uid = "1",
          title = "Mountain Biking",
          description = "Exciting mountain biking experience.",
          date = Timestamp.now(),
          location = "Hills",
          creator = "Chris",
          images = listOf(),
          price = 10.0,
          status = ActivityStatus.ACTIVE,
          type = ActivityType.PRO,
          placesLeft = 8,
          maxPlaces = 15,
          participants = listOf(),
          duration = "2 hours",
          startTime = "10:00")

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    activitiesRepository = mock(ActivitiesRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    listActivitiesViewModel = ListActivitiesViewModel(activitiesRepository)

    `when`(navigationActions.currentRoute()).thenReturn(Route.OVERVIEW)
    composeTestRule.setContent { ListActivitiesScreen(listActivitiesViewModel, navigationActions) }
  }

  @Test
  fun displayTextWhenEmpty() {
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf())
    }
    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("emptyActivityPrompt").assertIsDisplayed()
  }

  @Test
  fun displaysActivity() {

    // Mock the activities repository to return an activity
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }

    listActivitiesViewModel.getActivities()

    // Simulate click on the activity card
    composeTestRule.onNodeWithTag("activityCard").assertExists()
    composeTestRule.onNodeWithText("Mountain Biking").assertIsDisplayed()
    composeTestRule.onNodeWithText("Exciting mountain biking experience.").assertIsDisplayed()
    composeTestRule.onNodeWithText("Hills").assertIsDisplayed()

    composeTestRule.onNodeWithTag("activityCard").performClick()

    // Verify navigation to details screen was triggered
    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }

  @Test
  fun segmentedButtonRowIsDisplayed() {
    // Ensure segmented buttons are displayed
    composeTestRule.onNodeWithTag("segmentedButtonRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonPRO").performClick().assertIsSelected()
    composeTestRule.onNodeWithText("PRO").performClick().assertIsSelected()
    composeTestRule.onNodeWithTag("segmentedButtonINDIVIDUAL").performClick().assertIsSelected()
    composeTestRule.onNodeWithText("INDIVIDUAL").performClick().assertIsSelected()
    composeTestRule.onNodeWithTag("segmentedButtonSOLO").performClick().assertIsSelected()
    composeTestRule.onNodeWithText("SOLO").performClick().assertIsSelected()
    composeTestRule.onNodeWithText("ALL").performClick().assertIsSelected()
  }

  @Test
  fun displayTextWhenNoSolo() {
    composeTestRule.onNodeWithText("SOLO").performClick()
    composeTestRule.onNodeWithText("There is no activity of this type yet.").assertIsDisplayed()
  }

  @Test
  fun bottomNavigationMenuIsDisplayed() {
    // Ensure bottom navigation menu is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun goesToDetailsOnClick() {

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("activityCard").performClick()
    composeTestRule.waitForIdle()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }

  @Test
  fun filteringWorks() {
    val activity1 = activity.copy(title = "cooking", type = ActivityType.INDIVIDUAL)
    val activity2 = activity.copy(title = "dance", type = ActivityType.SOLO)
    val activity3 = activity.copy(title = "football", type = ActivityType.INDIVIDUAL)

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity, activity1, activity2, activity3))
    }

    listActivitiesViewModel.getActivities()
    composeTestRule.onNodeWithTag("segmentedButtonINDIVIDUAL").performClick()
    composeTestRule.onNodeWithText("cooking").assertIsDisplayed()
    composeTestRule.onNodeWithText("football").assertIsDisplayed()

    composeTestRule.onNodeWithTag("segmentedButtonSOLO").performClick()
    composeTestRule.onNodeWithText("dance").assertIsDisplayed()

    composeTestRule.onNodeWithTag("segmentedButtonPRO").performClick()
    composeTestRule.onNodeWithText("Mountain Biking").assertIsDisplayed()
  }
}

 */
