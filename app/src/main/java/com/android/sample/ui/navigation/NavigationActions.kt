package com.android.sample.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
  const val AUTH = "Auth"
  const val OVERVIEW = "Overview"
  const val PROFILE = "Profile"
  const val ADD_ACTIVITY = "AddActivity"
  const val LIKED_ACTIVITIES = "LikedActivities"
  const val MAP = "Map"
  const val CHOOSE_ACCOUNT = "ChooseAccount"
}

object Screen {
  const val PROFILE = "Profile Screen"
  const val AUTH = "Auth Screen"
  const val OVERVIEW = "Overview Screen"
  const val SIGN_UP = "SignUp Screen"
  const val ADD_ACTIVITY = "AddActivity Screen"
  const val EDIT_ACTIVITY = "EditActivity Screen"
  const val ACTIVITY_DETAILS = "ActivityDetails Screen"
  const val EDIT_PROFILE = "EditProfile Screen"
  const val CREATE_PROFILE = "CreateProfile Screen"
  const val LIKED_ACTIVITIES = "LikedActivities Screen"
  const val MAP = "Map Screen"
  const val CHOOSE_ACCOUNT = "ChooseAccount Screen"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
  val OVERVIEW =
      TopLevelDestination(route = Route.OVERVIEW, icon = Icons.Outlined.Menu, textId = "Overview")
  val PROFILE =
      TopLevelDestination(
          route = Route.PROFILE, icon = Icons.Outlined.AccountCircle, textId = "Profile")
  val ADD_ACTIVITY =
      TopLevelDestination(
          route = Route.ADD_ACTIVITY,
          icon = Icons.Outlined.AddCircleOutline,
          textId = "Add Activity")
  val LIKED_ACTIVITIES =
      TopLevelDestination(
          route = Route.LIKED_ACTIVITIES, icon = Icons.Outlined.FavoriteBorder, textId = "Liked")
  val MAP = TopLevelDestination(route = Route.MAP, icon = Icons.Outlined.Map, textId = "Map")
}

val LIST_TOP_LEVEL_DESTINATION =
    listOf(
        TopLevelDestinations.OVERVIEW,
        TopLevelDestinations.MAP,
        TopLevelDestinations.ADD_ACTIVITY,
        TopLevelDestinations.LIKED_ACTIVITIES,
        TopLevelDestinations.PROFILE)

open class NavigationActions(
    private val navController: NavHostController,
) {
  /**
   * Navigate to the specified [TopLevelDestination]
   *
   * @param destination The top level destination to navigate to Clear the back stack when
   *   navigating to a new destination This is useful when navigating to a new screen from the
   *   bottom navigation bar as we don't want to keep the previous screen in the back stack
   */
  open fun navigateTo(destination: TopLevelDestination) {

    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
        inclusive = true
      }

      // Avoid multiple copies of the same destination when reselecting same item
      launchSingleTop = true

      // Restore state when reselecting a previously selected item
      if (destination.route != Route.AUTH) {
        restoreState = true
      }
    }
  }

  /**
   * Navigate to the specified screen.
   *
   * @param screen The screen to navigate to
   */
  open fun navigateTo(screen: String) {
    navController.navigate(screen)
  }

  /** Navigate back to the previous screen. */
  open fun goBack() {
    navController.popBackStack()
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route ?: ""
  }
}
