package com.android.sample.model.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class NavigationActionsTest {

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
  }

  @Test
  fun navigateToCallsController() {
    navigationActions.navigateTo(TopLevelDestinations.OVERVIEW)
    verify(navHostController).navigate(eq(Route.OVERVIEW), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(Screen.ADD_ACTIVITY)
    verify(navHostController).navigate(Screen.ADD_ACTIVITY)
  }

  @Test
  fun navigateToWithDestinationCallsController() {
    navigationActions.navigateTo(TopLevelDestinations.OVERVIEW)
    navigationDestination.route?.let { verify(navHostController).navigate(it) }
  }

  @Test
  fun goBackCallsController() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.OVERVIEW)

    assertThat(navigationActions.currentRoute(), `is`(Route.OVERVIEW))
  }

  @Test
  fun navigateToOverviewClearsBackStack() {
    navigationActions.navigateTo(TopLevelDestinations.OVERVIEW)
    verify(navHostController).navigate(eq(Route.OVERVIEW), any<NavOptionsBuilder.() -> Unit>())
  }

  @Test
  fun navigateToProfileRestoresState() {
    navigationActions.navigateTo(TopLevelDestinations.PROFILE)
    verify(navHostController).navigate(eq(Route.PROFILE), any<NavOptionsBuilder.() -> Unit>())
  }

  @Test
  fun navigateToAuthDoesNotRestoreState() {
    navigationActions.navigateTo(TopLevelDestinations.OVERVIEW)
    verify(navHostController).navigate(eq(Route.OVERVIEW), any<NavOptionsBuilder.() -> Unit>())
  }

  @Test
  fun navigateToLaunchesSingleTop() {
    navigationActions.navigateTo(TopLevelDestinations.ADD_ACTIVITY)
    verify(navHostController).navigate(eq(Route.ADD_ACTIVITY), any<NavOptionsBuilder.() -> Unit>())
  }

  @Test
  fun navigateToPopUpToStartDestination() {
    navigationActions.navigateTo(TopLevelDestinations.LIKED_ACTIVITIES)
    verify(navHostController)
        .navigate(eq(Route.LIKED_ACTIVITIES), any<NavOptionsBuilder.() -> Unit>())
  }

  @Test
  fun getPreviousRouteReturnsNullWhenNoPreviousBackStackEntry() {
    `when`(navHostController.previousBackStackEntry).thenReturn(null)
    val result = navigationActions.getPreviousRoute()
    assertThat(result, `is`(nullValue()))
  }
}
