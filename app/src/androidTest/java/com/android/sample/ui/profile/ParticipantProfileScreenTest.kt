 package com.android.sample.ui.profile

 import androidx.compose.ui.test.assertIsDisplayed
 import androidx.compose.ui.test.assertTextEquals
 import androidx.compose.ui.test.hasTestTag
 import androidx.compose.ui.test.junit4.createComposeRule
 import androidx.compose.ui.test.onNodeWithTag
 import androidx.compose.ui.test.onNodeWithText
 import androidx.compose.ui.test.performClick
 import androidx.compose.ui.test.performScrollToNode
 import com.android.sample.model.activity.ActivitiesRepositoryFirestore
 import com.android.sample.model.activity.Activity
 import com.android.sample.model.activity.ListActivitiesViewModel
 import com.android.sample.model.image.ImageRepositoryFirestore
 import com.android.sample.model.image.ImageViewModel
 import com.android.sample.model.profile.Interest
 import com.android.sample.model.profile.ProfileViewModel
 import com.android.sample.model.profile.ProfilesRepository
 import com.android.sample.model.profile.User
 import com.android.sample.resources.dummydata.listOfActivitiesUid
 import com.android.sample.resources.dummydata.testUser
 import com.android.sample.ui.navigation.NavigationActions
 import com.android.sample.ui.navigation.Screen
 import org.junit.Before
 import org.junit.Rule
 import org.junit.Test
 import org.mockito.Mockito.mock
 import org.mockito.Mockito.verify
 import org.mockito.Mockito.`when`
 import org.mockito.kotlin.any

 class ParticipantProfileScreenTest {

  private lateinit var userProfileViewModel: ProfileViewModel
  private lateinit var testUser: User
  private lateinit var activitiesRepository: ActivitiesRepositoryFirestore
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var mockImageViewModel: ImageViewModel
  private lateinit var mockImageRepository: ImageRepositoryFirestore

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock dependencies
    testUser =
        User(
            id = "Rola",
            name = "Amine",
            surname = "A",
            photo = "",
            interests =
                listOf(Interest("Sport", "Cycling"), Interest("Indoor Activity", "Reading")),
            activities = listOfActivitiesUid,
        )
    userProfileViewModel = mock(ProfileViewModel::class.java)
    navigationActions = mock(NavigationActions::class.java)
    activitiesRepository = mock(ActivitiesRepositoryFirestore::class.java)
    listActivitiesViewModel =
        ListActivitiesViewModel(mock(ProfilesRepository::class.java), activitiesRepository)
    `when`(activitiesRepository.getActivities(any(), any())).then {
      it.getArgument<(List<Activity>) -> Unit>(0)(
          com.android.sample.resources.dummydata.activityListWithPastActivity)
    }
    `when`(navigationActions.currentRoute()).thenReturn(Screen.PARTICIPANT_PROFILE)
    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository)
  }

  @Test
  fun displayLoadingScreenWhenNoParticipantSelected() {

    composeTestRule.setContent {
      ParticipantProfileScreen(
          listActivitiesViewModel, navigationActions, mockImageViewModel, userProfileViewModel)
    }

    composeTestRule.onNodeWithTag("loadingScreen").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("loadingText")
        .assertTextEquals("No information available for this participant")
  }
//
//  @Test
//  fun displayParticipantProfileComponents() {
//    // Set up a null selected user to simulate loading
//    listActivitiesViewModel.selectUser(testUser)
//    composeTestRule.setContent {
//      ParticipantProfileScreen(
//          listActivitiesViewModel, navigationActions, mockImageViewModel, userProfileViewModel)
//    }
//
//    composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("userName").assertTextEquals("Amine A")
//    composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
//    composeTestRule.onNodeWithText("Cycling").assertIsDisplayed()
//    composeTestRule.onNodeWithText("Reading").assertIsDisplayed()
//  }
//
//  @Test
//  fun displayPastActivities() {
//    listActivitiesViewModel.selectUser(testUser)
//    composeTestRule.setContent {
//      ParticipantProfileScreen(
//          listActivitiesViewModel, navigationActions, mockImageViewModel, userProfileViewModel)
//    }
//    composeTestRule
//        .onNodeWithTag("ParticipantProfileContentColumn")
//        .assertIsDisplayed()
//        .performScrollToNode(hasTestTag("pastActivitiesTitle"))
//    composeTestRule.onNodeWithTag("pastActivitiesTitle").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("pastActivitiesTitle").assertTextEquals("Past Activities")
//  }
//
//  @Test
//  fun displayParticipantActivitiesCreated() {
//    listActivitiesViewModel.selectUser(testUser)
//    composeTestRule.setContent {
//      ParticipantProfileScreen(
//          listActivitiesViewModel, navigationActions, mockImageViewModel, userProfileViewModel)
//    }
//
//    composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()
//    composeTestRule
//        .onNodeWithTag("createdActivitiesTitle")
//        .assertTextEquals("Activities Created")
//        .assertIsDisplayed()
//  }
//
//  @Test
//  fun displayParticipantActivitiesEnrolled() {
//    listActivitiesViewModel.selectUser(testUser)
//
//    composeTestRule.setContent {
//      ParticipantProfileScreen(
//          listActivitiesViewModel, navigationActions, mockImageViewModel, userProfileViewModel)
//    }
//
//    composeTestRule.onNodeWithTag("ParticipantProfileContentColumn").assertIsDisplayed()
//
//    composeTestRule
//        .onNodeWithTag("enrolledActivitiesTitle")
//        .assertIsDisplayed()
//        .assertTextEquals("Activities Enrolled in")
//  }
//
//  @Test
//  fun navigateBackOnClick() {
//    listActivitiesViewModel.selectUser(testUser)
//
//    composeTestRule.setContent {
//      ParticipantProfileScreen(
//          listActivitiesViewModel, navigationActions, mockImageViewModel, userProfileViewModel)
//    }
//
//    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("goBackButton").performClick()
//
//    verify(navigationActions).goBack()
//  }
 }
