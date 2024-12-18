package com.android.sample.ui.listActivities

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.Category
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.map.PermissionChecker
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class OverviewScreenTest {
  private lateinit var activitiesRepository: ActivitiesRepository
  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel
  private lateinit var userProfileViewModel: ProfileViewModel
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var testUser: User

  private val activity =
      Activity(
          uid = "1",
          title = "Mountain Biking",
          description = "Exciting mountain biking experience.",
          date = Timestamp(GregorianCalendar(2050, Calendar.JANUARY, 1).time),
          location =
              Location(46.519962, 6.633597, "EPFL", "Ecole Polytechnique Fédérale de Lausanne"),
          creator = "Chris",
          images = listOf(),
          price = 10.0,
          status = ActivityStatus.ACTIVE,
          type = ActivityType.PRO,
          placesLeft = 8,
          maxPlaces = 15,
          participants =
              listOf(
                  User(
                      id = "1",
                      name = "Amine",
                      surname = "A",
                      interests = listOf(Interest("Football", Category.SPORT)),
                      activities = listOf(),
                      photo = "",
                      likedActivities = listOf("1")),
                  User(
                      id = "2",
                      name = "John",
                      surname = "Doe",
                      interests = listOf(Interest("Movies", Category.ENTERTAINMENT)),
                      activities = listOf(),
                      photo = "",
                      likedActivities = listOf("1"))),
          duration = "2 hours",
          startTime = "10:00")

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          android.Manifest.permission.CAMERA)

  @Before
  fun setUp() {
    profilesRepository = mock(ProfilesRepository::class.java)
    activitiesRepository = mock(ActivitiesRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    listActivitiesViewModel = ListActivitiesViewModel(profilesRepository, activitiesRepository)

    testUser =
        User(
            id = "Rola",
            name = "Amine",
            surname = "A",
            photo = "",
            interests =
                listOf(
                    Interest("Football", Category.SPORT),
                    Interest("Movies", Category.ENTERTAINMENT)),
            activities = listOf(activity.uid),
        )

    navigationActions = mock(NavigationActions::class.java)

    // `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))

    `when`(navigationActions.currentRoute()).thenReturn(Route.OVERVIEW)
    locationViewModel =
        LocationViewModel(mock(LocationRepository::class.java), mock(PermissionChecker::class.java))
  }

  @Test
  fun displayTextWhenEmpty() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf())
    }
    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("emptyActivityPrompt").assertIsDisplayed()
  }

  @Test
  fun displaysActivity() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    // Mock the activities repository to return an activity
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }

    listActivitiesViewModel.getActivities()

    // Simulate click on the activity card
    composeTestRule.onNodeWithTag("activityCard").assertExists()
    composeTestRule.onNodeWithText("Mountain Biking").assertIsDisplayed()
    composeTestRule.onNodeWithText("Exciting mountain biking experience.").assertIsDisplayed()
    composeTestRule.onNodeWithText("EPFL").assertIsDisplayed()
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()
    composeTestRule.onNodeWithTag("activityCard").performClick()

    // Verify navigation to details screen was triggered
    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }

  @Test
  fun segmentedButtonRowIsDisplayed() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    // Ensure segmented buttons are displayed
    composeTestRule.onNodeWithTag("segmentedButtonRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonSPORT").performClick().assertIsOn()

    composeTestRule.onNodeWithTag("segmentedButtonCULTURE").performClick().assertIsOn()
    composeTestRule.onNodeWithTag("segmentedButtonSPORT").performClick().assertIsOff()
    composeTestRule.onNodeWithTag("segmentedButtonSKILLS").performClick().assertIsOn()

    composeTestRule.onNodeWithTag("segmentedButtonENTERTAINMENT").performClick().assertIsOn()
    composeTestRule.onNodeWithTag("segmentedButtonCULTURE").performClick().assertIsOff()
  }

  @Test
  fun displayTextWhenNoCulture() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    composeTestRule.onNodeWithText("CULTURE").performClick()
    composeTestRule
        .onNodeWithText("There is no activity of these categories yet.")
        .assertIsDisplayed()
  }

  @Test
  fun activityNotDisplayedWhenFull() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    val fullActivity = activity.copy(maxPlaces = 2, placesLeft = 2)
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(fullActivity))
    }
    listActivitiesViewModel.getActivities()

    // Verify that the full activity is not displayed
    composeTestRule.onNodeWithText("Mountain Biking").assertDoesNotExist()
  }

  @Test
  fun bottomNavigationMenuIsDisplayed() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    // Ensure bottom navigation menu is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    // composeTestRule.onNodeWithTag("Liked").performClick()
    // verify(navigationActions).navigateTo(Screen.LIKED_ACTIVITIES)
  }

  @Test
  fun goesToDetailsOnClick() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("activityCard").performClick()
    composeTestRule.waitForIdle()

    // Verify that the navigation action was triggered
    verify(navigationActions).navigateTo(Screen.ACTIVITY_DETAILS)
  }

  @Test
  fun filteringCategoryWorks() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    val activity1 = activity.copy(title = "Italian cooking", category = Category.CULTURE)
    val activity2 = activity.copy(title = "dance", category = Category.ENTERTAINMENT)
    val activity3 = activity.copy(title = "football", category = Category.SPORT)
    val activity4 = activity.copy(title = "networking", category = Category.SKILLS)

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(
          listOf(activity, activity1, activity2, activity3, activity4))
    }

    listActivitiesViewModel.getActivities()
    composeTestRule.onNodeWithTag("segmentedButtonSPORT").performClick()
    composeTestRule.onNodeWithText("football").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonSPORT").performClick()

    composeTestRule.onNodeWithTag("segmentedButtonENTERTAINMENT").performClick()
    composeTestRule.onNodeWithText("dance").assertIsDisplayed()
    composeTestRule.onNodeWithText("football").assertIsNotDisplayed()
    composeTestRule.onNodeWithText("Italian cooking").assertIsNotDisplayed()
    composeTestRule.onNodeWithText("networking").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonENTERTAINMENT").performClick()

    composeTestRule.onNodeWithTag("segmentedButtonCULTURE").performClick()
    composeTestRule.onNodeWithText("Italian cooking").assertIsDisplayed()
    composeTestRule.onNodeWithTag("segmentedButtonCULTURE").performClick()

    composeTestRule.onNodeWithTag("segmentedButtonSKILLS").performClick()
    composeTestRule.onNodeWithText("networking").assertIsDisplayed()
  }
    @Test
    fun iconsAreDisplayed(){
        userProfileViewModel = mock(ProfileViewModel::class.java)
        composeTestRule.setContent {
            ActivityCard(
                navigationActions = navigationActions,
                listActivitiesViewModel = listActivitiesViewModel,
                profileViewModel = userProfileViewModel,
                profile = testUser,
                activity = activity)
        }
        composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("iconparticipants", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("iconlocation", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("iconcalendar", useUnmergedTree = true).assertIsDisplayed()
    }
    @Test
    fun interestsAreCorrectlyDisplayed(){
        userProfileViewModel = mock(ProfileViewModel::class.java)
        val activity1 = activity.copy(subcategory = "Basketball")
        composeTestRule.setContent {
            ActivityCard(
                navigationActions = navigationActions,
                listActivitiesViewModel = listActivitiesViewModel,
                profileViewModel = userProfileViewModel,
                profile = testUser,
                activity = activity1)
        }
        composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("interestPresent", useUnmergedTree = true)
        composeTestRule.onNodeWithText("Basketball", useUnmergedTree = true).assertIsDisplayed()
    }
  @Test
  fun changeIconWhenActivityNotLiked() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }
    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()
  }

  @Test
  fun changeIconWhenActivityIsLiked() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }
    listActivitiesViewModel.getActivities()

    val newUser = testUser.copy(likedActivities = listOf(activity.uid))
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(newUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }

    composeTestRule.onNodeWithTag("likeButtontrue").assertIsDisplayed()
  }

  @Test
  fun distanceIsCorrectlyDisplayedInMeters() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    composeTestRule.setContent {
      ActivityCard(
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          profileViewModel = userProfileViewModel,
          profile = testUser,
          activity = activity,
          distance = 0.5503f)
    }
    composeTestRule
        .onNodeWithTag("locationAndDistance", useUnmergedTree = true)
        .assertTextContains("550m", substring = true)
  }

  @Test
  fun distanceIsCorrectlyDisplayedInKilometers() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    composeTestRule.setContent {
      ActivityCard(
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          profileViewModel = userProfileViewModel,
          profile = testUser,
          activity = activity,
          distance = 12.354f)
    }
    composeTestRule
        .onNodeWithTag("locationAndDistance", useUnmergedTree = true)
        .assertTextContains("12.4km", substring = true)
  }

  @Test
  fun distanceIsNotDisplayedWhenNullDistance() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    composeTestRule.setContent {
      ActivityCard(
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          profileViewModel = userProfileViewModel,
          profile = testUser,
          activity = activity,
          distance = null)
    }
    composeTestRule.onNodeWithTag("locationAndDistance", useUnmergedTree = true).assertTextEquals("EPFL") //there is only the location, not the distance
  }

  @Test
  fun changeIconWhenActivityIsOnclick() {
    userProfileViewModel = ProfileViewModel(profilesRepository, mock())

    composeTestRule.setContent {
      ActivityCard(
          navigationActions = navigationActions,
          listActivitiesViewModel = listActivitiesViewModel,
          profileViewModel = userProfileViewModel,
          profile = testUser,
          activity = activity)
    }

    // Verify initial state is liked
    composeTestRule.onNodeWithTag("activityCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()

    // Click on the like button
    composeTestRule.onNodeWithTag("likeButtonfalse").performClick()

    // Verify that the like button is toggled
    composeTestRule.onNodeWithTag("likeButtontrue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("likeButtontrue").performClick()

    // Verify that the like button is toggled
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()
  }

  @Test
  fun searchBarFiltersActivities() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    val activity1 = activity.copy(title = "cooking", type = ActivityType.INDIVIDUAL)
    val activity2 = activity.copy(title = "dance", type = ActivityType.PRO)
    val activity3 = activity.copy(title = "football", type = ActivityType.INDIVIDUAL)

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity, activity1, activity2, activity3))
    }

    listActivitiesViewModel.getActivities()
    composeTestRule.onNodeWithTag("searchBar").performClick()
    composeTestRule.onNodeWithTag("searchBar").performTextInput("cook")
    composeTestRule.onNodeWithText("cooking").assertIsDisplayed()
    composeTestRule.onNodeWithText("dance").assertDoesNotExist()
    composeTestRule.onNodeWithText("football").assertDoesNotExist()
  }

  @Test
  fun searchBarClearsFilter() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }
    val activity1 = activity.copy(title = "cooking", type = ActivityType.INDIVIDUAL)
    val activity2 = activity.copy(title = "dance", type = ActivityType.PRO)
    val activity3 = activity.copy(title = "football", type = ActivityType.INDIVIDUAL)

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity1, activity2, activity3))
    }

    listActivitiesViewModel.getActivities()
    composeTestRule.onNodeWithTag("searchBar").performClick()
    composeTestRule.onNodeWithTag("searchBar").performTextInput("cook")
    composeTestRule.onNodeWithText("cooking").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBar").performTextClearance()
    composeTestRule.onNodeWithTag("lazyColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lazyColumn").performScrollToNode(hasText("cooking"))
    composeTestRule.onNodeWithText("cooking").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lazyColumn").performScrollToNode(hasText("dance"))
    composeTestRule.onNodeWithText("dance").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lazyColumn").performScrollToNode(hasText("football"))
    composeTestRule.onNodeWithText("football").assertIsDisplayed()
  }

  @Test
  fun filterDialogFiltersByPlacesAvailable() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }

    val activity1 = activity.copy(title = "Few spots", maxPlaces = 5)
    val activity2 = activity.copy(title = "Many spots", maxPlaces = 20)

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity1, activity2))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("filterDialog").performClick()
    composeTestRule
        .onNodeWithTag("membersAvailableTextField")
        .performTextInput("5") // Set available places to 5
    composeTestRule.onNodeWithTag("FilterDialog").performScrollToNode(hasTestTag("cancelButton"))
    composeTestRule.onNodeWithTag("filterButton").performClick()

    composeTestRule.onNodeWithText("Many spots").assertIsDisplayed()
    composeTestRule.onNodeWithText("Few spots").assertDoesNotExist()
  }

  @Test
  fun filterDialogFiltersByOnlyPRO() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }

    val activity1 = activity.copy(title = "PRO activity", type = ActivityType.PRO)
    val activity2 = activity.copy(title = "INDIVIDUAL activity", type = ActivityType.INDIVIDUAL)

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity1, activity2))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("filterDialog").performClick()
    composeTestRule.onNodeWithTag("FilterDialog").performScrollToNode(hasTestTag("cancelButton"))
    composeTestRule.onNodeWithTag("onlyPROCheckboxRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("onlyPROCheckbox").performClick()
    composeTestRule.onNodeWithTag("filterButton").performClick()

    composeTestRule.onNodeWithText("PRO activity").assertIsDisplayed()
    composeTestRule.onNodeWithText("INDIVIDUAL activity").assertDoesNotExist()
  }

  @Test
  fun pastActivitiesRemoved() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }

    val activity1 =
        activity.copy(
            title = "Past activity",
            date = Timestamp(GregorianCalendar(2020, Calendar.JANUARY, 1).time))
    val activity2 =
        activity.copy(
            title = "Future activity",
            date = Timestamp(GregorianCalendar(2050, Calendar.JANUARY, 1).time))

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity1, activity2))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithText("Past activity").assertDoesNotExist()
    composeTestRule.onNodeWithText("Future activity").assertIsDisplayed()
  }

  @Test
  fun displaysYourActivityBox() {

    val creatorProfile = testUser.copy(id = activity.creator)
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(creatorProfile))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("yourActivityStatus", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("activityStatus", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("activityStatusPresent", useUnmergedTree = true).assertExists()

    composeTestRule.onNodeWithText("YOUR ACTIVITY").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityStatusEnrolledBox", useUnmergedTree = true)
        .assertDoesNotExist()

    composeTestRule.onNodeWithTag("enrolledText", useUnmergedTree = true).assertDoesNotExist()

    composeTestRule.onNodeWithText("ENROLLED").assertIsNotDisplayed()
  }

  @Test
  fun displaysEnrolledBox() {

    val activity1 = activity.copy(participants = listOf(testUser))
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity1))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule
        .onNodeWithTag("activityStatusEnrolledBox", useUnmergedTree = true)
        .assertExists()
    composeTestRule.onNodeWithTag("activityStatus", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("enrolledText", useUnmergedTree = true).assertExists()

    composeTestRule.onNodeWithText("ENROLLED").assertIsDisplayed()
  }

  @Test
  fun enrolledAndYourActivityBoxNotDisplayed() {

    val user = testUser.copy(activities = listOf())
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(user))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("yourActivityStatus", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("activityStatus", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("activityStatusPresent", useUnmergedTree = true)
        .assertDoesNotExist()

    composeTestRule.onNodeWithText("YOUR ACTIVITY").assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag("activityStatusEnrolledBox", useUnmergedTree = true)
        .assertDoesNotExist()
    composeTestRule.onNodeWithTag("activityStatus", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("enrolledText", useUnmergedTree = true).assertDoesNotExist()

    composeTestRule.onNodeWithText("ENROLLED").assertIsNotDisplayed()
  }

  @Test
  fun displaysYourActivityBoxAndEnrolledBox() {
    val creatorProfile = testUser.copy(id = activity.creator)
    val activity1 = activity.copy(participants = listOf(creatorProfile))
    userProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(userProfileViewModel.userState).thenReturn(MutableStateFlow(creatorProfile))
    composeTestRule.setContent {
      ListActivitiesScreen(
          listActivitiesViewModel, navigationActions, userProfileViewModel, locationViewModel)
    }

    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(listOf(activity1))
    }

    listActivitiesViewModel.getActivities()

    composeTestRule.onNodeWithTag("yourActivityStatus", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("activityStatus", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("activityStatusPresent", useUnmergedTree = true).assertExists()

    composeTestRule.onNodeWithText("YOUR ACTIVITY").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("activityStatusEnrolledBox", useUnmergedTree = true)
        .assertExists()
    composeTestRule.onNodeWithTag("activityStatus", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("enrolledText", useUnmergedTree = true).assertExists()

    composeTestRule.onNodeWithText("ENROLLED").assertIsDisplayed()
  }
}
