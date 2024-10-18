package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen.PROFILE
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class EditProfileScreenTest {
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var profile: User
  private lateinit var navigationActions: NavigationActions
  private lateinit var listActivitiesViewModel: ListActivitiesViewModel
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {

    profileViewModel = mock<ProfileViewModel>()
    profile =
        User(
            id = "123",
            name = "Amine",
            surname = "A",
            photo = "",
            interests = listOf("Cycling", "Reading"),
            activities = listOf("Football"))
    val userStateFlow = MutableStateFlow(profile)
    navigationActions = Mockito.mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(PROFILE)
    `when`(profileViewModel.userState).thenReturn(userStateFlow)
  }

  @Test
  fun testInitialValuesDisplayedCorrectly() {
    composeTestRule.setContent { EditProfileScreen(profileViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("editProfileTitle").assertTextEquals("Edit Profile")
    composeTestRule.onNodeWithTag("inputProfileName").assertIsDisplayed()
    composeTestRule.onNodeWithText("Amine").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputProfileSurname").assertIsDisplayed()
    composeTestRule.onNodeWithText("A")
  }

  @Test
  fun testAddInterestButtonFunctionality() {
    composeTestRule.setContent { EditProfileScreen(profileViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("newInterestInput").performTextInput("New Interest")
    composeTestRule.onNodeWithTag("addInterestButton").performClick()
    composeTestRule.onNodeWithTag("interestsList").assertIsDisplayed()
    composeTestRule.onNodeWithText("New Interest").assertIsDisplayed()
  }

  /*@Test
  fun testSaveButtonFunctionality() {
      composeTestRule.setContent {
          EditProfileScreen(profileViewModel, navigationActions)
      }
      composeTestRule.onNodeWithTag("profileSaveButton").performClick()
      verify(navigationActions).navigateTo(PROFILE)
  }*/
}
