package com.android.sample.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.Location
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class MapScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel

  private val activity =
      Activity(
          uid = "1",
          title = "Mountain Biking",
          description = "Exciting mountain biking experience.",
          date = Timestamp.now(),
          location = Location(46.519962, 6.633597, "EPFL"),
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

  private val activity2 =
      Activity(
          uid = "2",
          title = "Mountain Biking",
          description = "Exciting mountain biking experience.",
          date = Timestamp.now(),
          location = Location(46.5, 6.6, "Lausanne"),
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
    activitiesRepository = Mockito.mock(ActivitiesRepository::class.java)
    listActivitiesViewModel = Mockito.spy(ListActivitiesViewModel(activitiesRepository))
    navigationActions = Mockito.mock(NavigationActions::class.java)

    val activities = listOf(activity)
    val uiState = ListActivitiesViewModel.ActivitiesUiState.Success(activities)
    val stateFlow = MutableStateFlow(uiState)

    // Stub the getUiState method to return the stateFlow
    Mockito.doReturn(stateFlow).`when`(listActivitiesViewModel).uiState

    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)

    composeTestRule.setContent { MapScreen(listActivitiesViewModel, navigationActions) }
  }

  @Test
  fun testMapScreenIsDisplayed() {

    // Verify that the Google Map is displayed
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }
}
