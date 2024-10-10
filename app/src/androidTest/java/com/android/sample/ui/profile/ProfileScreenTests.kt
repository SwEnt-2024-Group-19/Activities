package com.android.sample.ui.profile


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.User
import com.android.sample.model.UserProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.Mock
import org.mockito.kotlin.whenever

class ProfileScreenTest {

    @Mock
    private lateinit var userProfileViewModel: UserProfileViewModel

    private lateinit var testUser: User

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        testUser = User(
            id = "123",
            name = "Amine",
            surname = "A",
            photo = "",
            interests = listOf("Cycling", "Reading"),
            activities = listOf("Football")
        )

        val userStateFlow = MutableStateFlow(testUser)

        whenever(userProfileViewModel.userState).thenReturn(userStateFlow)
    }

    @Test
    fun displayAllProfileComponents() {
        composeTestRule.setContent {
            ProfileScreen(userProfileViewModel = userProfileViewModel)
        }

        composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
        composeTestRule.onNodeWithTag("userName").assertIsDisplayed()
        composeTestRule.onNodeWithTag("userName").assertTextEquals("John Doe")
        composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
        composeTestRule.onNodeWithTag("interestsSection").assertTextEquals("Interests: Cycling, Reading")
        composeTestRule.onNodeWithTag("activitiesSection").assertIsDisplayed()
        composeTestRule.onNodeWithTag("activitiesList").assertIsDisplayed()
    }

    @Test
    fun activitiesAreDisplayedCorrectly() {
        composeTestRule.setContent {
            ProfileScreen(userProfileViewModel = userProfileViewModel)
        }

        composeTestRule.onNodeWithTag("activitiesList").assertIsDisplayed()
        testUser.activities?.forEachIndexed { index, activity ->
            composeTestRule.onNodeWithTag("activity_$index").assertIsDisplayed()
        }
    }

    @Test
    fun handleInvalidProfileData() {
        val nullStateFlow = MutableStateFlow<User?>(null)

        whenever(userProfileViewModel.userState).thenReturn(nullStateFlow)

        composeTestRule.setContent {
            ProfileScreen(userProfileViewModel = userProfileViewModel)
        }

        composeTestRule.onNodeWithTag("loadingScreen").assertIsDisplayed()
    }
}
