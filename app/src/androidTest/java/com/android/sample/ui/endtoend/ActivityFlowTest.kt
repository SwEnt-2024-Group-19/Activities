package com.android.sample.ui.endtoend

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.MockActivitiesRepository
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.map.MockLocationPermissionChecker
import com.android.sample.model.profile.MockProfilesRepository
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
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
import com.google.firebase.Timestamp
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class YourFlowTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    val profilesRepository: ProfilesRepository = MockProfilesRepository()
    val profileViewModel = ProfileViewModel(profilesRepository)

    val activitiesRepository: ActivitiesRepository = MockActivitiesRepository()
    val listActivitiesViewModel = ListActivitiesViewModel(activitiesRepository)

    val signInRepository: SignInRepository = mock(SignInRepository::class.java)
    val authViewModel = SignInViewModel(signInRepository)

    val locationRepository: LocationRepository = mock(LocationRepository::class.java)
    val locationViewModel =
        LocationViewModel(locationRepository, MockLocationPermissionChecker(true))

    private val sampleActivity =
        Activity(
            uid = "1",
            title = "Sample Activity",
            description = "This is a sample activity",
            date = Timestamp.now(),
            location = Location(0.0, 0.0, "origin"),
            creator = "TestCreator",
            price = 1.0,
            images = listOf(),
            placesLeft = 10,
            maxPlaces = 20,
            status = ActivityStatus.ACTIVE,
            type = ActivityType.PRO,
            participants = listOf(),
            startTime = "12:00",
            duration = "1 hour"
        )

    @Before
    fun setUp() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            val navigationActions = NavigationActions(navController)
            val startDestination = Route.AUTH

            NavHost(navController = navController, startDestination = startDestination) {
                composable(Route.CHOOSE_ACCOUNT) {
                    ChooseAccountScreen(
                        navigationActions,
                        authViewModel
                    )
                }
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
                            listActivitiesViewModel,
                            navigationActions,
                            profileViewModel
                        )
                    }
                    composable(Screen.EDIT_ACTIVITY) {
                        EditActivityScreen(
                            listActivitiesViewModel,
                            navigationActions,
                            locationViewModel
                        )
                    }
                    composable(Screen.ACTIVITY_DETAILS) {
                        ActivityDetailsScreen(
                            listActivitiesViewModel,
                            navigationActions,
                            profileViewModel
                        )
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
                            listActivitiesViewModel,
                            navigationActions,
                            profileViewModel,
                            locationViewModel
                        )
                    }
                }

                navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
                    composable(Screen.PROFILE) {
                        ProfileScreen(profileViewModel, navigationActions, listActivitiesViewModel)
                    }
                    composable(Screen.EDIT_PROFILE) {
                        EditProfileScreen(
                            profileViewModel,
                            navigationActions
                        )
                    }
                }

                navigation(
                    startDestination = Screen.LIKED_ACTIVITIES,
                    route = Route.LIKED_ACTIVITIES
                ) {
                    composable(Screen.LIKED_ACTIVITIES) {
                        LikedActivitiesScreen(
                            listActivitiesViewModel,
                            navigationActions,
                            profileViewModel
                        )
                    }
                }
            }
        }

        activitiesRepository.addActivity(sampleActivity, {}, {})
        activitiesRepository.getActivities({}, {})
    }
}
