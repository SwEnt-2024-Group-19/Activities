package com.android.sample.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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

class MapScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var mockRepository: LocationRepository
  private lateinit var mockPermissionChecker: LocationPermissionChecker

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)
    mockRepository = mock(LocationRepository::class.java)
    mockPermissionChecker = mock(LocationPermissionChecker::class.java)
    locationViewModel = LocationViewModel(mockRepository, mockPermissionChecker)
  }

  @Test
  fun testMapScreenIsDisplayed() {

    composeTestRule.setContent {
      MapScreen(navigationActions = navigationActions, locationViewModel)
    }

    // Verify that the Google Map is displayed
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }
}
