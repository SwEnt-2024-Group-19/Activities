package com.android.sample.ui.map

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationPermissionChecker
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.LocationViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class MapScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  private lateinit var navigationActions: NavigationActions
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var mockRepository: LocationRepository
  private lateinit var mockPermissionChecker: LocationPermissionChecker
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)

    mockRepository = mock(LocationRepository::class.java)
    mockPermissionChecker = LocationPermissionChecker(context)
    locationViewModel = LocationViewModel(mockRepository, mockPermissionChecker)
    activitiesRepository = mock(ActivitiesRepository::class.java)
    listActivitiesViewModel = Mockito.spy(ListActivitiesViewModel(activitiesRepository))
    val activities = listOf(activity)
    val uiState = ListActivitiesViewModel.ActivitiesUiState.Success(activities)
    val stateFlow = MutableStateFlow(uiState)
    Mockito.doReturn(stateFlow).`when`(listActivitiesViewModel).uiState
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)
    composeTestRule.setContent {
      MapScreen(navigationActions, locationViewModel, listActivitiesViewModel)
    }
  }

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

  @Test
  fun testMapScreenIsDisplayed() {

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("centerOnCurrentLocation").assertIsDisplayed()
  }

  @Test
  fun testCenterOnCurrentLocationTriggersGetCurrentLocation() {

    verify(mockRepository).getCurrentLocation(any(), any())
  }
}
