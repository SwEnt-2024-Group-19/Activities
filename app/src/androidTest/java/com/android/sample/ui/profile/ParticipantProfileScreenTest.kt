package com.android.sample.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
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

    private lateinit var mockListActivitiesViewModel: ListActivitiesViewModel
    private lateinit var mockNavigationActions: NavigationActions
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
        mockListActivitiesViewModel = mock(ListActivitiesViewModel::class.java)
        mockNavigationActions = mock(NavigationActions::class.java)
    }

    @Test
    fun testParticipantProfileScreen_showsUserProfile() {
        val selectedUserFlow = MutableStateFlow(sampleUser)
        `when`(mockListActivitiesViewModel.selectedUser).thenReturn(selectedUserFlow)

        composeTestRule.setContent {
            ParticipantProfileScreen(
                listActivitiesViewModel = mockListActivitiesViewModel,
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
        `when`(mockListActivitiesViewModel.selectedUser).thenReturn(selectedUserFlow)

        composeTestRule.setContent {
            ParticipantProfileScreen(
                listActivitiesViewModel = mockListActivitiesViewModel,
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
                listActivitiesViewModel = mockListActivitiesViewModel
            )
        }

        composeTestRule.onNodeWithTag("goBackButton").performClick()

        verify(mockNavigationActions).goBack()
    }
}

