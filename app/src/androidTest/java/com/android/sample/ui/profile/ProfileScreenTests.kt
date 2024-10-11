package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.profile.User
import com.android.sample.model.profile.ProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ProfileScreenTest {
  private lateinit var userProfileViewModel: ProfileViewModel
  private lateinit var testUser: User

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    userProfileViewModel = mock(ProfileViewModel::class.java)
    testUser =
        User(
            id = "123",
            name = "Amine",
            surname = "A",
            photo = "",
            interests = listOf("Cycling", "Reading"),
            activities = listOf("Football"))
    val userStateFlow = MutableStateFlow(testUser)
    `when`(userProfileViewModel.userState).thenReturn(userStateFlow)
  }

  @Test
  fun displayAllProfileComponents() {
    composeTestRule.setContent { ProfileScreen(userProfileViewModel = userProfileViewModel) }

    composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userName").assertTextEquals("Amine A")
    composeTestRule.onNodeWithTag("interestsSection").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("interestsSection")
        .assertTextEquals("Interests: Cycling, Reading")
    composeTestRule.onNodeWithTag("activitiesSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("activitiesList").assertIsDisplayed()
  }
}
