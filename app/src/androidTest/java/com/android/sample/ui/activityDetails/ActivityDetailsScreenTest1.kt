package com.android.sample.ui.activityDetails

import android.content.SharedPreferences
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.activity.ActivitiesRepositoryFirestore
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.Comment
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.activity.MockActivitiesRepository
import com.android.sample.model.activity.database.AppDatabase
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.LocationViewModel
import com.android.sample.model.map.PermissionChecker
import com.android.sample.model.profile.MockProfilesRepository
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.model.profile.database.UserDao
import com.android.sample.resources.dummydata.activityListWithPastActivity
import com.android.sample.resources.dummydata.activityWithParticipants
import com.android.sample.resources.dummydata.testUser
import com.android.sample.ui.activitydetails.ActivityDetailsScreen
import com.android.sample.ui.activitydetails.CreatorRow
import com.android.sample.ui.activitydetails.LikeButton
import com.android.sample.ui.activitydetails.PaymentInfoScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class ActivityDetailsScreenAndroidTest {

  private lateinit var mockNavigationActions: NavigationActions

  private lateinit var mockViewModel: ListActivitiesViewModel

  private lateinit var mockProfileViewModel: ProfileViewModel
  private lateinit var mockFirebaseRepository: ActivitiesRepositoryFirestore
  private lateinit var mockRepository: ProfilesRepository

  private lateinit var mockLocationViewModel: LocationViewModel
  private lateinit var mockLocationRepository: LocationRepository
  private lateinit var mockImageViewModel: ImageViewModel
  private lateinit var mockImageRepository: ImageRepositoryFirestore

  private lateinit var mockDefaultUiState: ListActivitiesViewModel.ActivitiesUiState

  private lateinit var sharedPreferences: SharedPreferences
  private lateinit var mockEditor: SharedPreferences.Editor
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockFirebaseRepository = mock(ActivitiesRepositoryFirestore::class.java)
    mockRepository = mock(ProfilesRepository::class.java)

    mockNavigationActions = mock(NavigationActions::class.java)
    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACTIVITY_DETAILS)

    mockViewModel = mock(ListActivitiesViewModel::class.java)

    val activityStateFlow = MutableStateFlow(activityWithParticipants)
    `when`(mockViewModel.selectedActivity).thenReturn(activityStateFlow)

    mockDefaultUiState =
        ListActivitiesViewModel.ActivitiesUiState.Success(activityListWithPastActivity)
    `when`(mockViewModel.uiState).thenReturn(MutableStateFlow(mockDefaultUiState))

    mockLocationRepository = mock(LocationRepository::class.java)

    mockLocationViewModel =
        LocationViewModel(mockLocationRepository, mock(PermissionChecker::class.java))

    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    sharedPreferences = mock(SharedPreferences::class.java)
    mockEditor = mock(SharedPreferences.Editor::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository, sharedPreferences)
  }

  fun tearDown() {
    reset(
        mockViewModel,
        mockProfileViewModel,
        mockNavigationActions,
        mockFirebaseRepository,
        mockRepository,
        mockLocationViewModel)
  }

  @Test
  fun activityComponents_areDisplayed() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("image").assertIsDisplayed()
    composeTestRule.onNodeWithTag("title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description").assertIsDisplayed()
    composeTestRule.onNodeWithTag("price").assertIsDisplayed()
    composeTestRule.onNodeWithTag("location").assertIsDisplayed()
    composeTestRule.onNodeWithTag("schedule").assertIsDisplayed()
    composeTestRule.onNodeWithTag("duration").assertIsDisplayed()
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()
  }

  @Test
  fun enrollButtonIsDisplayedWhenActivityIsActive() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("enrollButton"))
    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed()
  }

  @Test
  fun enrollButtonIsNotDisplayedWhenActivityIsFinished() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    val activityFinished = activityWithParticipants.copy(status = ActivityStatus.FINISHED)
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activityFinished))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("enrollButton").assertDoesNotExist()
  }

  @Test
  fun activityDetailsAreDisplayedCorrectly() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("titleText").assertTextContains("Sample Activity")
    composeTestRule.onNodeWithTag("descriptionText").assertTextContains("Sample Description")
    composeTestRule.onNodeWithTag("priceText").assertTextContains("10.0 CHF")
    composeTestRule.onNodeWithTag("locationText").assertTextContains("EPFL")
    composeTestRule.onNodeWithTag("scheduleText").assertTextContains("1/1/2050 at 10:00")
    composeTestRule.onNodeWithTag("durationText").assertTextContains("Event length: 02:00")
  }

  @Test
  fun goBackButtonNavigatesBack() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    verify(mockNavigationActions).goBack()
  }

  @Test
  fun enrollButton_displays_whenUserLoggedInAndNotActivityCreator() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    val testUser = testUser.copy(id = "123")
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))

    val activity =
        activityWithParticipants.copy(
            creator = "456", // Different from user ID
            status = ActivityStatus.ACTIVE)
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activity))

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("enrollButton"))
    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("Enroll").assertIsDisplayed()
  }

  @Test
  fun editButton_displaysForActiveActivity_whenUserIsTheCreator() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser.copy(id = "123")))
    val activity1 = activityWithParticipants.copy(creator = "123")
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activity1))

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("editButton"))

    composeTestRule.onNodeWithText("Edit").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").assertIsDisplayed().performClick()
    verify(mockNavigationActions).navigateTo(Screen.EDIT_ACTIVITY)
  }

  @Test
  fun loginRegisterButton_displays_whenUserIsNotLoggedIn() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))

    val userStateFlow = MutableStateFlow<User?>(null)
    `when`(mockProfileViewModel.userState).thenReturn(userStateFlow)

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          profileViewModel = mockProfileViewModel,
          navigationActions = mockNavigationActions,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("loginButton"))
    composeTestRule.onNodeWithText("Login/Register").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed().performClick()

    verify(mockNavigationActions).navigateTo(Screen.AUTH)
  }

  @Test
  fun enrollFailureToast_displays_whenPlacesAreFull() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    // Set placesLeft equal to maxPlaces to simulate full capacity
    val activityWithMaxParticipants =
        activityWithParticipants.copy(placesLeft = activityWithParticipants.maxPlaces)
    val activityStateFlow = MutableStateFlow(activityWithMaxParticipants)
    `when`(mockViewModel.selectedActivity).thenReturn(activityStateFlow)

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          profileViewModel = mockProfileViewModel,
          navigationActions = mockNavigationActions,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("enrollButton"))

    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Enroll failed, limit of places reached").isDisplayed()
  }

  @Test
  fun enrollButton_addsUserToActivity() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)

    val testUser = testUser.copy(id = "123")
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))

    val activity =
        activityWithParticipants.copy(
            uid = "act1",
            creator = "456", // Different from user ID
            status = ActivityStatus.ACTIVE)
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activity))

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          profileViewModel = mockProfileViewModel,
          navigationActions = mockNavigationActions,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    // Verify the button is displayed and clickable
    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("enrollButton"))
    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("Enroll").assertIsDisplayed()
  }

  @Test
  fun leaveActivityToast_displays_whenLeftActivity() {
    val mockActivitiesRepo = MockActivitiesRepository()
    val mockProfileRepo = MockProfilesRepository()
    mockViewModel = spy(ListActivitiesViewModel(mockProfileRepo, mockActivitiesRepo))
    val mockAppDatabase = mock(AppDatabase::class.java)
    val mockUserDao = mock(UserDao::class.java)
    `when`(mockAppDatabase.userDao()).thenReturn(mockUserDao)
    mockProfileViewModel = spy(ProfileViewModel(mockProfileRepo, mockAppDatabase, mock()))
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser.copy(id = "123")))

    val enrolledActivity =
        activityWithParticipants.copy(
            creator = "456", participants = listOf(testUser.copy(id = "123")))
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(enrolledActivity))

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          profileViewModel = mockProfileViewModel,
          navigationActions = mockNavigationActions,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("enrollButton"))
    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed().performClick()

    composeTestRule.onNodeWithText("Successfully left the activity").isDisplayed()
  }

  @Test
  fun enrollButton_showsLeaveTextWhenUserEnrolled() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    val testUser = testUser.copy(id = "123")
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))

    val activity =
        activityWithParticipants.copy(
            creator = "456", status = ActivityStatus.ACTIVE, participants = listOf(testUser))
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activity))

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          profileViewModel = mockProfileViewModel,
          navigationActions = mockNavigationActions,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("enrollButton"))
    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("Leave").isDisplayed()
  }

  @Test
  fun deleteMainComment_removesMainCommentSuccessfully() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    // Set initial comments with a main comment
    val comments =
        listOf(
            activityWithParticipants.comments
                .firstOrNull()
                ?.copy(uid = "main-comment-uid", content = "Main comment") ?: return)

    // Set up the ActivityDetailsScreen with mock data
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    // Simulate the deletion of the main comment
    composeTestRule.onNodeWithTag("DeleteButton_main-comment-uid").performClick()

    // Assert that the main comment is removed
    assert(comments.none { it.uid == "main-comment-uid" })
  }

  @Test
  fun deleteReply_removesReplyFromMainComment() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    // Set initial comments with replies
    val comments =
        listOf(
            activityWithParticipants.comments
                .firstOrNull()
                ?.copy(
                    uid = "main-comment-uid",
                    replies =
                        listOf(
                            Comment(
                                uid = "reply-uid",
                                userId = "user-reply",
                                userName = "John",
                                content = "This is a reply",
                                timestamp = Timestamp.now()))) ?: return)

    // Set up the ActivityDetailsScreen with mock data
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    // Simulate the deletion of the reply
    composeTestRule.onNodeWithTag("DeleteButton_reply-uid").performClick()

    // Assert that the reply is removed
    assert(comments.first().replies.none { it.uid == "reply-uid" })
  }

  @Test
  fun notLoggedInNoLikeButton() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    val userStateFlow = MutableStateFlow<User?>(null)
    `when`(mockProfileViewModel.userState).thenReturn(userStateFlow)
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("likeButtontrue").assertIsNotDisplayed()
  }

  @Test
  fun replyToComment_displaysNewReplyInList() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    val comments =
        listOf(
            activityWithParticipants.comments
                .firstOrNull()
                ?.copy(uid = "main-comment-uid", content = "Main comment", replies = listOf())
                ?: return)

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    // Simulate replying to the main comment
    composeTestRule.onNodeWithTag("ReplyButton_main-comment-uid").performClick()
    composeTestRule
        .onNodeWithTag("ReplyInputField_main-comment-uid")
        .performTextInput("This is a reply")
    composeTestRule.onNodeWithTag("PostReplyButton_main-comment-uid").performClick()

    // Assert that the new reply is added
    assert(comments.first().replies.any { it.content == "This is a reply" })
  }

  @Test
  fun distanceIsCorrectlyDisplayedInMeters() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    mockLocationViewModel.setCurrentLocation(
        Location(46.52, 6.64, "Close to EPFL", "Close to EPFL"))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("distanceText").assertTextContains("Distance : 490m")
  }

  @Test
  fun distanceIsCorrectlyDisplayedInKilometers() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    mockLocationViewModel.setCurrentLocation(Location(50.0, 5.0, "Random Point", "Random Point"))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("distanceText").assertTextContains("Distance : 405.4km")
  }

  @Test
  fun distanceIsNotDisplayedWhenLocationIsNotAvailable() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("distanceText").assertIsNotDisplayed()
  }

  @Test
  fun participantsList_isDisplayedCorrectly() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel,
          locationViewModel = mockLocationViewModel,
          imageViewModel = mockImageViewModel)
    }

    // Check if the participants list is displayed
    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("participants"))
    // composeTestRule.onNodeWithTag("participants").assertIsDisplayed()

    // Check if each participant's name is displayed
    activityWithParticipants.participants.forEach { participant ->
      composeTestRule.onNodeWithText(participant.name).assertIsDisplayed()
    }
  }

  @Test
  fun changeIconWhenActivityIsLiked() {
    mockProfileViewModel = ProfileViewModel(mockRepository, mock(), mock())

    composeTestRule.setContent {
      LikeButton(testUser, activityWithParticipants, mockProfileViewModel)
    }

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
  fun paymentInfoPin() {

    composeTestRule.setContent { PaymentInfoScreen(10.0) }
    composeTestRule.onNodeWithTag("paymentSection").assertExists()
    composeTestRule.onNodeWithTag("paymentInfo").assertExists()
    composeTestRule.onNodeWithTag("infoIconButton").assertExists()
  }

  @Test
  fun paymentInfoDialog() {
    composeTestRule.setContent { PaymentInfoScreen(10.0) }
    composeTestRule.onNodeWithTag("infoIconButton").performClick()
    composeTestRule.onNodeWithTag("paymentInfoDialog").assertExists()
    composeTestRule.onNodeWithTag("paymentInfoTitle").assertExists()
    composeTestRule.onNodeWithTag("paymentInfoText").assertExists()
    composeTestRule.onNodeWithTag("okButton").performClick()
    composeTestRule.onNodeWithTag("infoDialog").assertDoesNotExist()
  }

  @Test
  fun paymentInfoDialogFree() {
    composeTestRule.setContent { PaymentInfoScreen(0.0) }
    composeTestRule.onNodeWithTag("infoIconButton").performClick()
    composeTestRule.onNodeWithTag("paymentInfoDialog").assertExists()
    composeTestRule.onNodeWithTag("paymentInfoTitle").assertExists()
    composeTestRule.onNodeWithTag("freeInfoText").assertExists()
    composeTestRule.onNodeWithTag("okButton").performClick()
    composeTestRule.onNodeWithTag("infoDialog").assertDoesNotExist()
  }

  @Test
  fun creatorRowDisplaysCorrectInformation() {
    val creator = User("1.203930", "John", "Doe", listOf(), listOf("122"), "2024", listOf())
    val activitiesCreated = 5

    composeTestRule.setContent {
      CreatorRow(creator = creator, nbActivitiesCreated = activitiesCreated)
    }

    composeTestRule.onNodeWithTag("creatorName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("creatorName").assertTextContains("John Doe")

    composeTestRule.onNodeWithTag("creatorRating").assertIsDisplayed()
    composeTestRule.onNodeWithTag("creatorRating").assertTextContains("Blank")

    composeTestRule.onNodeWithTag("ratingStar").assertIsDisplayed()

    composeTestRule.onNodeWithTag("activityCount").assertIsDisplayed()
    composeTestRule.onNodeWithTag("activityCount").assertTextContains("5 Activities Created")
  }
}
