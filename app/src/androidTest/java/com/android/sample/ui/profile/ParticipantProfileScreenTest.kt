package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.activity.ActivitiesRepository
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.activityList
import com.android.sample.resources.dummydata.listOfActivitiesUid
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
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
    private lateinit var navigationActions: NavigationActions
    private lateinit var listActivitiesViewModel: ListActivitiesViewModel
    private lateinit var activitiesRepository: ActivitiesRepository

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        activitiesRepository = mock(ActivitiesRepository::class.java)
        userProfileViewModel = mock(ProfileViewModel::class.java)
        listActivitiesViewModel = mock(ListActivitiesViewModel::class.java)

        `when`(activitiesRepository.getActivities(any(), any())).then {
            it.getArgument<(List<Activity>) -> Unit>(0)(activityList)
        }

        listActivitiesViewModel.getActivities()
        testUser =
            User(
                id = "Rola",
                name = "Amine",
                surname = "A",
                photo = "",
                interests = listOf("Cycling", "Reading"),
                activities = listOfActivitiesUid,
            )
        val userStateFlow = MutableStateFlow(testUser)
        navigationActions = mock(NavigationActions::class.java)
        `when`(navigationActions.currentRoute()).thenReturn(Screen.PROFILE)
        `when`(userProfileViewModel.userState).thenReturn(userStateFlow)
    }

    @Test
    fun testParticipantProfileScreen_noUserProfileShowsLoadingMessage() {
        val selectedUserFlow = MutableStateFlow<User?>(null)
        org.mockito.kotlin.doReturn(selectedUserFlow).`when`(listActivitiesViewModel).selectedUser

        composeTestRule.setContent {
            ParticipantProfileScreen(
                listActivitiesViewModel = listActivitiesViewModel,
                navigationActions = navigationActions
            )
        }

        composeTestRule.onNodeWithTag("loadingText")
            .assertIsDisplayed()
            .assertTextContains("No information available for this participant")
    }

    @Test
    fun displayAllProfileComponents() {
        composeTestRule.setContent {
            ParticipantProfileScreen(listActivitiesViewModel,
                navigationActions = navigationActions
                )
        }

        composeTestRule.onNodeWithTag("topAppBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("userName").assertTextContains("John Doe")
        composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
        composeTestRule.onNodeWithTag("activitiesCreatedTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("activitiesEnrolledTitle").assertIsDisplayed()
    }
}
