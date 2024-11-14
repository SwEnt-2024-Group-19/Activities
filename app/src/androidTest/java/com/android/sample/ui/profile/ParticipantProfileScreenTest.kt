package com.android.sample.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.activity.ActivitiesRepositoryFirestore
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.activityWithParticipants
import com.android.sample.resources.dummydata.testUser
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.profile.ParticipantProfileScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class ParticipantProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavigationActions: NavigationActions

    private lateinit var mockViewModel: ListActivitiesViewModel
    private lateinit var mockProfileViewModel: ProfileViewModel
    private lateinit var mockFirebaseRepository: ActivitiesRepositoryFirestore
    private lateinit var mockRepository: ProfilesRepository

    private val sampleUser = User(
        id = "1",
        name = "John",
        surname = "Doe",
        interests = listOf("Hiking", "Reading"),
        activities = listOf("Activity1", "Activity2"),
        photo = null
    )

    @Before
    fun setUp() {
        mockFirebaseRepository = mock(ActivitiesRepositoryFirestore::class.java)
        mockRepository = mock(ProfilesRepository::class.java)

        mockNavigationActions = mock(NavigationActions::class.java)
        `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACTIVITY_DETAILS)

        mockViewModel = mock(ListActivitiesViewModel::class.java)

        val activityStateFlow = MutableStateFlow(activityWithParticipants)
        `when`(mockViewModel.selectedActivity).thenReturn(activityStateFlow)

    }

    @Test
    fun testParticipantProfileScreen_showsUserProfile() {
        val selectedUserFlow = MutableStateFlow(sampleUser)
        `when`(mockViewModel.selectedUser).thenReturn(selectedUserFlow)
        mockProfileViewModel = mock(ProfileViewModel::class.java)
        `when`(mockProfileViewModel.userState).thenReturn(MutableStateFlow(testUser))

        composeTestRule.setContent {
            ParticipantProfileScreen(
                listActivitiesViewModel = mockViewModel,
                navigationActions = mockNavigationActions
            )
        }

        composeTestRule.onNodeWithTag("topAppBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("userName").assertTextContains("John Doe")
        composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
        composeTestRule.onNodeWithTag("activitiesCreatedTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("activitiesEnrolledTitle").assertIsDisplayed()
    }

    @Test
    fun testParticipantProfileScreen_noUserProfileShowsLoadingMessage() {
        val selectedUserFlow = MutableStateFlow<User?>(null)
        `when`(mockViewModel.selectedUser).thenReturn(selectedUserFlow)

        composeTestRule.setContent {
            ParticipantProfileScreen(
                listActivitiesViewModel = mockViewModel,
                navigationActions = mockNavigationActions
            )
        }

        composeTestRule.onNodeWithTag("loadingText")
            .assertIsDisplayed()
            .assertTextContains("No information available for this participant")
    }
    @Test
    fun testBackButtonNavigatesBack() {
        composeTestRule.setContent {
            ParticipantProfileContent(
                user = sampleUser,
                navigationActions = mockNavigationActions,
                listActivitiesViewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithTag("goBackButton").performClick()

        verify(mockNavigationActions).goBack()
    }
}

