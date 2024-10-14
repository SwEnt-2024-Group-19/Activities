package com.android.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.sample.model.activity.ActivitiesRepositoryFirestore
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.resources.C
import com.android.sample.ui.activity.CreateActivityScreen
import com.android.sample.ui.activity.EditActivityScreen
import com.android.sample.ui.activitydetails.ActivityDetailsScreen
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.authentication.SignUpScreen
import com.android.sample.ui.listActivities.ListActivitiesScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.profile.ProfileCreationScreen
import com.android.sample.ui.profile.ProfileScreen
import com.android.sample.ui.theme.SampleAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
      Log.d("MainActivity", "onCreate called") // <-- Log here

    auth = FirebaseAuth.getInstance()
    auth.currentUser?.let {
        Log.d("MainActivity", "User signed out") // <-- Log for sign out
        auth.signOut() }


    setContent {
      SampleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
            Log.d("MainActivity", "ActivitiesApp Composable launched") // <-- Log before composable
            ActivitiesApp(auth.currentUser?.uid ?: "")
            }
      }
    }
  }
}

@Composable
fun ActivitiesApp(uid: String) {
    Log.d("ActivitiesApp", "ActivitiesApp Composable called with uid: $uid") // <-- Log for uid
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val listActivitiesViewModel: ListActivitiesViewModel =
      viewModel(factory = ListActivitiesViewModel.Factory)
  val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(uid))

    Log.d("ActivitiesApp", "NavHost initialized") // <-- Log for NavHost initialization

    NavHost(navController = navController, startDestination = Route.AUTH) {
        Log.d("ActivitiesApp", "Start destination: ${Route.AUTH}")
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) {
          Log.d("ActivitiesApp", "SignInScreen launched") // <-- Log for SignInScreen
          SignInScreen(navigationActions) }
      composable(Screen.SIGN_UP) {
          Log.d("ActivitiesApp", "SignUpScreen launched") // <-- Log for SignUpScreen
          SignUpScreen(navigationActions) }
      composable(Screen.CREATE_PROFILE) {
          Log.d("ActivitiesApp", "ProfileCreationScreen launched") // <-- Log for ProfileCreationScreen
        ProfileCreationScreen(profileViewModel, navigationActions)
      }
    }

    navigation(
        startDestination = Screen.OVERVIEW,
        route = Route.OVERVIEW,
    ) {
      composable(Screen.OVERVIEW) {
          Log.d("ActivitiesApp", "ListActivitiesScreen launched") // <-- Log for ListActivitiesScreen
        ListActivitiesScreen(listActivitiesViewModel, navigationActions)
      }
      composable(Screen.EDIT_ACTIVITY) {
          Log.d("ActivitiesApp", "EditActivityScreen launched") // <-- Log for EditActivityScreen
          EditActivityScreen(listActivitiesViewModel, navigationActions)
      }


      composable(Screen.ACTIVITY_DETAILS) {
          Log.d("ActivitiesApp", "ActivityDetailsScreen launched") // <-- Log for ActivityDetailsScreen
        ActivityDetailsScreen(listActivitiesViewModel, navigationActions)
      }
    }

    navigation(startDestination = Screen.ADD_ACTIVITY, route = Route.ADD_ACTIVITY) {
      composable(Screen.ADD_ACTIVITY) {
          Log.d("ActivitiesApp", "CreateActivityScreen launched") // <-- Log for CreateActivityScreen
        CreateActivityScreen(listActivitiesViewModel, navigationActions)
      }
    }

    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.PROFILE) {
          Log.d("ActivitiesApp", "ProfileScreen launched") // <-- Log for ProfileScreen
          ProfileScreen(profileViewModel) }
      composable(Screen.EDIT_PROFILE) {
          Log.d("ActivitiesApp", "BlankScreen launched") // <-- Log for BlankScreen
          BlankScreen() }
    }
  }
}

@Composable
fun BlankScreen() {
  Surface(
      modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.blank_screen },
      color = MaterialTheme.colorScheme.background) {
        Text(text = "Blank Screen")
      }
}
