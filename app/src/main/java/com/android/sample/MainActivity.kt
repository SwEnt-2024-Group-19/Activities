package com.android.sample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.resources.C
import com.android.sample.ui.activity.CreateActivityScreen
import com.android.sample.ui.activity.EditActivityScreen
import com.android.sample.ui.activitydetails.ActivityDetailsScreen
import com.android.sample.ui.authentication.ChooseAccountScreen
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.authentication.SignUpScreen
import com.android.sample.ui.listActivities.LikedActivitiesScreen
import com.android.sample.ui.listActivities.ListActivitiesScreen
import com.android.sample.ui.map.MapScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.profile.EditProfileScreen
import com.android.sample.ui.profile.ProfileCreationScreen
import com.android.sample.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val CAMERA_PERMISSION_REQUEST_CODE = 0
  private val LOCATION_PERMISSION_REQUEST_CODE = 1

  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (!hasCameraPermissions(applicationContext)) {
      ActivityCompat.requestPermissions(this, CAMERAX_PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE)
    }

    auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    if (currentUser != null && currentUser.isAnonymous) {
      auth.signOut()
    }
    val startDestination = if (auth.currentUser != null) Route.CHOOSE_ACCOUNT else Route.AUTH
    // log current user
    Log.d("MainActivity", "Current user: ${auth.currentUser?.uid}")

    setContent {
      Surface(
          modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
          color = MaterialTheme.colorScheme.background) {
            NavGraph(startDestination)
          }
    }
  }

  private fun hasCameraPermissions(context: Context): Boolean {
    return CAMERAX_PERMISSIONS.all {
      ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
  }

  private val CAMERAX_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
}

@Composable
fun NavGraph(
    startDestination: String,
    navController: NavHostController = rememberNavController(),
    navigationActions: NavigationActions = NavigationActions(navController),
    authViewModel: SignInViewModel = hiltViewModel<SignInViewModel>(),
    profileViewModel: ProfileViewModel = hiltViewModel<ProfileViewModel>(),
    listActivitiesViewModel: ListActivitiesViewModel = hiltViewModel<ListActivitiesViewModel>(),
    locationViewModel: LocationViewModel = hiltViewModel<LocationViewModel>(),
) {

  NavHost(navController = navController, startDestination = startDestination) {
    composable(Route.CHOOSE_ACCOUNT) { ChooseAccountScreen(navigationActions, authViewModel) }
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignInScreen(navigationActions, authViewModel) }
      composable(Screen.SIGN_UP) { SignUpScreen(navigationActions) }
      composable(Screen.CREATE_PROFILE) {
        ProfileCreationScreen(profileViewModel, navigationActions)
      }
    }

    navigation(
        startDestination = Screen.OVERVIEW,
        route = Route.OVERVIEW,
    ) {
      composable(Screen.OVERVIEW) {
        ListActivitiesScreen(
            listActivitiesViewModel, navigationActions, profileViewModel, locationViewModel)
      }
      composable(Screen.EDIT_ACTIVITY) {
        EditActivityScreen(listActivitiesViewModel, navigationActions, locationViewModel)
      }
      composable(Screen.ACTIVITY_DETAILS) {
        ActivityDetailsScreen(
            listActivitiesViewModel, navigationActions, profileViewModel, locationViewModel)
      }
    }

    navigation(startDestination = Screen.MAP, route = Route.MAP) {
      composable(Screen.MAP) {
        MapScreen(navigationActions, locationViewModel, listActivitiesViewModel)
      }
    }

    navigation(startDestination = Screen.ADD_ACTIVITY, route = Route.ADD_ACTIVITY) {
      composable(Screen.ADD_ACTIVITY) {
        CreateActivityScreen(
            listActivitiesViewModel, navigationActions, profileViewModel, locationViewModel)
      }
    }

    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.PROFILE) {
        ProfileScreen(profileViewModel, navigationActions, listActivitiesViewModel)
      }
      composable(Screen.EDIT_PROFILE) { EditProfileScreen(profileViewModel, navigationActions) }
    }

    navigation(startDestination = Screen.LIKED_ACTIVITIES, route = Route.LIKED_ACTIVITIES) {
      composable(Screen.LIKED_ACTIVITIES) {
        LikedActivitiesScreen(listActivitiesViewModel, navigationActions, profileViewModel)
      }
    }
  }
}
