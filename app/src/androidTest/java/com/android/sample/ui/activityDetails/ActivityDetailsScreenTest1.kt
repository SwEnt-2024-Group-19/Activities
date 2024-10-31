package com.android.sample.ui.activityDetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.activity.ActivitiesRepositoryFirestore
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.activitydetails.ActivityDetailsScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class ActivityDetailsScreenAndroidTest {
  private lateinit var mockNavigationActions: NavigationActions

  private lateinit var mockViewModel: ListActivitiesViewModel
  private lateinit var activity: Activity

  private lateinit var mockProfileViewModel: ProfileViewModel
  private lateinit var testUser: User
  private lateinit var mockFirebaseRepository: ActivitiesRepositoryFirestore

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockFirebaseRepository = mock(ActivitiesRepositoryFirestore::class.java)

    mockProfileViewModel = mock(ProfileViewModel::class.java)

    mockNavigationActions = mock(NavigationActions::class.java)
    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACTIVITY_DETAILS)

    testUser =
        User(
            id = "123",
            name = "Amine",
            surname = "A",
            photo = "",
            interests = listOf("Cycling", "Reading"),
            activities = listOf("Football"))

    val userStateFlow = MutableStateFlow(testUser)
    `when`(mockProfileViewModel.userState).thenReturn(userStateFlow)

    mockViewModel = mock(ListActivitiesViewModel::class.java)
    activity =
        Activity(
            uid = "123",
            title = "Sample Activity",
            description = "Sample Description",
            date = Timestamp(GregorianCalendar(2025, Calendar.NOVEMBER, 3).time),
            price = 10.0,
            placesLeft = 5,
            maxPlaces = 10,
            creator = "someone",
            status = ActivityStatus.ACTIVE,
            location = "Sample Location",
            images = listOf("1"),
            participants = listOf(),
            duration = "2 hours",
            startTime = "10:00",
            type = ActivityType.INDIVIDUAL)
    val activityStateFlow = MutableStateFlow(activity)
    `when`(mockViewModel.selectedActivity).thenReturn(activityStateFlow)
  }

  @Test
  fun activityComponents_areDisplayed() {
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
    }

    composeTestRule.onNodeWithTag("image").assertIsDisplayed()
    composeTestRule.onNodeWithTag("title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description").assertIsDisplayed()
    composeTestRule.onNodeWithTag("price").assertIsDisplayed()
    composeTestRule.onNodeWithTag("schedule").assertIsDisplayed()
  }

  @Test
  fun enrollButtonIsDisplayedWhenActivityIsActive() {
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("enrollButton"))
    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed()
  }

  @Test
  fun noButtonIsNotDisplayedWhenActivityIsFinished() {
    activity = activity.copy(status = ActivityStatus.FINISHED)
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activity))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
    }

    composeTestRule.onNodeWithTag("enrollButton").assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("notActiveText")
        .assertIsDisplayed()
        .assertTextContains("Activity is not active")
    composeTestRule.onNodeWithText("Activity is not active").assertIsDisplayed()
  }

  @Test
  fun activityDetailsAreDisplayedCorrectly() {
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
    }

    composeTestRule.onNodeWithTag("titleText").assertTextContains("Sample Activity")
    composeTestRule.onNodeWithTag("descriptionText").assertTextContains("Sample Description")
    composeTestRule.onNodeWithTag("priceText").assertTextContains("10.0 CHF")
    composeTestRule.onNodeWithTag("scheduleText").assertTextContains("3/11/2025")
  }

  @Test
  fun goBackButtonNavigatesBack() {
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    verify(mockNavigationActions).goBack()
  }

  @Test
  fun enrollButton_displays_whenUserLoggedIn() {
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
    }

    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("Enroll").assertIsDisplayed()
  }

  @Test
  fun enrollButton_displaysForActiveActivity_whenUserIsNotCreator() {
    val activity1 = activity.copy(creator = "123")
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activity1))

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
    }
    composeTestRule.onNodeWithText("Edit").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").assertIsDisplayed().performClick()
    verify(mockNavigationActions).navigateTo(Screen.EDIT_ACTIVITY)
  }

  @Test
  fun loginRegisterButton_displays_whenUserIsNotLoggedIn() {
    // Set the user state to null to simulate the user not being logged in
    val userStateFlow = MutableStateFlow<User?>(null)
    `when`(mockProfileViewModel.userState).thenReturn(userStateFlow)

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          profileViewModel = mockProfileViewModel,
          navigationActions = mockNavigationActions)
    }
    composeTestRule.onNodeWithText("Login/Register").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed().performClick()

    verify(mockNavigationActions).navigateTo(Screen.AUTH)
  }

  @Test
  fun enrollFailureToast_displays_whenPlacesAreFull() {
    // Set placesLeft equal to maxPlaces to simulate full capacity
    activity = activity.copy(placesLeft = activity.maxPlaces)
    val activityStateFlow = MutableStateFlow(activity)
    `when`(mockViewModel.selectedActivity).thenReturn(activityStateFlow)

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          profileViewModel = mockProfileViewModel,
          navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Enroll failed, limit of places reached").isDisplayed()
  }
}
