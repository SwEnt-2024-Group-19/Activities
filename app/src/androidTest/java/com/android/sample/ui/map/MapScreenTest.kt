package com.android.sample.ui.map

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.android.sample.model.map.LocationPermissionChecker
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.LocationViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)

    mockRepository = mock(LocationRepository::class.java)
    mockPermissionChecker = LocationPermissionChecker(context)
    locationViewModel = LocationViewModel(mockRepository, mockPermissionChecker)
  }

  @Test
  fun testMapScreenIsDisplayed() {
    composeTestRule.setContent {
      MapScreen(navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    // Verify that the Google Map and bottom navigation are displayed
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("centerOnCurrentLocation").assertIsDisplayed()
  }

  @Test
  fun testCenterOnCurrentLocationTriggersFetchUserData() {
    composeTestRule.setContent {
      MapScreen(navigationActions = navigationActions, locationViewModel = locationViewModel)
    }
    verify(mockRepository).getCurrentLocation(any(), any())
  }
}
