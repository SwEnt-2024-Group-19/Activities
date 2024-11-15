package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.resources.dummydata.testUser
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen.PROFILE
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class EditProfileScreenTest {
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {

    profileViewModel = mock<ProfileViewModel>()

    val userStateFlow = MutableStateFlow(testUser)
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
    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
  }

  @Test
  fun testProfileEditionWithDialogWithCamera() {
    composeTestRule.setContent { EditProfileScreen(profileViewModel, navigationActions) }
    composeTestRule
        .onNodeWithTag("editProfileContent")
        .performScrollToNode(hasTestTag("uploadPicture"))
    composeTestRule.onNodeWithTag("uploadPicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag("addImageDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cameraButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cameraButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cameraScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("closeCamera").assertIsDisplayed()
    composeTestRule.onNodeWithTag("takePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("switchCamera").assertIsDisplayed()
    composeTestRule.onNodeWithTag("closeCamera").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("editProfileContent").assertIsDisplayed()
  }

  @Test
  fun testProfileEditionWithDialogWithGallery() {
    composeTestRule.setContent { EditProfileScreen(profileViewModel, navigationActions) }
    composeTestRule
        .onNodeWithTag("editProfileContent")
        .performScrollToNode(hasTestTag("uploadPicture"))
    composeTestRule.onNodeWithTag("uploadPicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag("addImageDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cameraButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").performClick()
  }

  @Test
  fun testAddInterestButtonFunctionality() {
    composeTestRule.setContent { EditProfileScreen(profileViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("newInterestInput").performTextInput("New Interest")
    composeTestRule.onNodeWithTag("addInterestButton").performClick()
    composeTestRule.onNodeWithTag("interestsList").assertIsDisplayed()
  }
}
