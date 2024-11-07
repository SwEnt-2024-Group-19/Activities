package com.android.sample.ui.activityDetails

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
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.Comment
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.ui.activitydetails.ActivityDetailsScreen
import com.android.sample.ui.activitydetails.LikeButton
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class ActivityDetailsScreenAndroidTest {
  private lateinit var mockNavigationActions: NavigationActions

  private lateinit var mockViewModel: ListActivitiesViewModel
  private lateinit var activity: Activity

  private lateinit var mockProfileViewModel: ProfileViewModel
  private lateinit var testUser: User
  private lateinit var mockFirebaseRepository: ActivitiesRepositoryFirestore
  private lateinit var mockRepository: ProfilesRepository

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockFirebaseRepository = mock(ActivitiesRepositoryFirestore::class.java)
    mockRepository = mock(ProfilesRepository::class.java)

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
            creator = "Creator",
            status = ActivityStatus.ACTIVE,
            location = Location(46.519962, 6.633597, "EPFL"),
            images = listOf("1"),
            participants = listOf(),
            duration = "02:00",
            startTime = "10:00",
            type = ActivityType.INDIVIDUAL,
            comments = listOf())
    val activityStateFlow = MutableStateFlow(activity)
    `when`(mockViewModel.selectedActivity).thenReturn(activityStateFlow)
  }

  @Test
  fun activityComponents_areDisplayed() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
    }

    composeTestRule.onNodeWithTag("image").assertIsDisplayed()
    composeTestRule.onNodeWithTag("title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description").assertIsDisplayed()
    composeTestRule.onNodeWithTag("price&&location").assertIsDisplayed()
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
          profileViewModel = mockProfileViewModel)
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
    activity = activity.copy(status = ActivityStatus.FINISHED)
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activity))
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
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
          profileViewModel = mockProfileViewModel)
    }

    composeTestRule.onNodeWithTag("titleText").assertTextContains("Sample Activity")
    composeTestRule.onNodeWithTag("descriptionText").assertTextContains("Sample Description")
    composeTestRule.onNodeWithTag("priceText").assertTextContains("10.0 CHF")
    composeTestRule.onNodeWithTag("locationText").assertTextContains("EPFL")
    composeTestRule.onNodeWithTag("scheduleText").assertTextContains("3/11/2025 at 10:00")
    composeTestRule.onNodeWithTag("durationText").assertTextContains("02:00")
  }

  @Test
  fun goBackButtonNavigatesBack() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
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
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
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
    composeTestRule.onNodeWithText("Enroll").assertIsDisplayed()
  }

  @Test
  fun editButton_displaysForActiveActivity_whenUserIsTheCreator() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    val activity1 = activity.copy(creator = "123")
    `when`(mockViewModel.selectedActivity).thenReturn(MutableStateFlow(activity1))

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
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

    // Set the user state to null to simulate the user not being logged in
    val userStateFlow = MutableStateFlow<User?>(null)
    `when`(mockProfileViewModel.userState).thenReturn(userStateFlow)

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          profileViewModel = mockProfileViewModel,
          navigationActions = mockNavigationActions)
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
    activity = activity.copy(placesLeft = activity.maxPlaces)
    val activityStateFlow = MutableStateFlow(activity)
    `when`(mockViewModel.selectedActivity).thenReturn(activityStateFlow)

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          profileViewModel = mockProfileViewModel,
          navigationActions = mockNavigationActions)
    }
    composeTestRule
        .onNodeWithTag("activityDetailsScreen")
        .performScrollToNode(hasTestTag("enrollButton"))

    composeTestRule.onNodeWithTag("enrollButton").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Enroll failed, limit of places reached").isDisplayed()
  }

  @Test
  fun deleteMainComment_removesMainCommentSuccessfully() {
    mockProfileViewModel = mock(ProfileViewModel::class.java)
    `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))
    // Set initial comments with a main comment
    val comments =
        listOf(
            activity.comments
                .firstOrNull()
                ?.copy(uid = "main-comment-uid", content = "Main comment") ?: return)

    // Set up the ActivityDetailsScreen with mock data
    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
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
            activity.comments
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
          profileViewModel = mockProfileViewModel)
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
          profileViewModel = mockProfileViewModel)
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
            activity.comments
                .firstOrNull()
                ?.copy(uid = "main-comment-uid", content = "Main comment", replies = listOf())
                ?: return)

    composeTestRule.setContent {
      ActivityDetailsScreen(
          listActivityViewModel = mockViewModel,
          navigationActions = mockNavigationActions,
          profileViewModel = mockProfileViewModel)
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
  fun changeIconWhenActivityIsLiked() {
    mockProfileViewModel = ProfileViewModel(mockRepository)

    composeTestRule.setContent { LikeButton(testUser, activity, mockProfileViewModel) }

    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()

    // Click on the like button
    composeTestRule.onNodeWithTag("likeButtonfalse").performClick()

    // Verify that the like button is toggled
    composeTestRule.onNodeWithTag("likeButtontrue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("likeButtontrue").performClick()

    // Verify that the like button is toggled
    composeTestRule.onNodeWithTag("likeButtonfalse").assertIsDisplayed()
  }
}
