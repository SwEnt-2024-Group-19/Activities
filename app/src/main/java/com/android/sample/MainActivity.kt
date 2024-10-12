package com.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.sample.model.activity.ListActivityViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.resources.C
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.authentication.SignUpScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      SampleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              ActivitiesApp("Android")
            }
      }
    }
  }
}

@Composable
fun ActivitiesApp(name: String, modifier: Modifier = Modifier) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val listToDosViewModel: ListActivityViewModel = viewModel(factory = ListActivityViewModel.Factory)
  val locationViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)

  NavHost(navController = navController, startDestination = Route.AUTH) {
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignInScreen(navigationActions) }
      composable(Screen.SIGN_UP) { SignUpScreen(navigationActions) }
    }

    navigation(
        startDestination = Screen.OVERVIEW,
        route = Route.OVERVIEW,
    ) {
      composable(Screen.OVERVIEW) { BlankScreen() }
      composable(Screen.EDIT_ACTIVITY) { BlankScreen() }
      composable(Screen.ACTIVITY_DETAILS) { BlankScreen() }
    }

    navigation(startDestination = Screen.ADD_ACTIVITY, route = Route.ADD_ACTIVITY) {
      composable(Screen.ADD_ACTIVITY) { BlankScreen() }
    }

    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.PROFILE) { BlankScreen() }
      composable(Screen.EDIT_PROFILE) { BlankScreen() }
    }
  }
}

@Composable
fun BlankScreen() {
  Surface(
      modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.blank_screen },
      color = MaterialTheme.colorScheme.background) {}
}
