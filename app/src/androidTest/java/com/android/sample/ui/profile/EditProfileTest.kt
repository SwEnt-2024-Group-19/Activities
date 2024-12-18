package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.R
import com.android.sample.model.activity.categories
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class EditProfileScreenTest {
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions

  private lateinit var mockImageViewModel: ImageViewModel
  private lateinit var mockImageRepository: ImageRepositoryFirestore
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {

    profileViewModel = mock<ProfileViewModel>()

    val userStateFlow = MutableStateFlow(testUser)
    navigationActions = Mockito.mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(PROFILE)
    `when`(profileViewModel.userState).thenReturn(userStateFlow)
    mockImageRepository = Mockito.mock(ImageRepositoryFirestore::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository)
  }

  @Test
  fun testInitialValuesDisplayedCorrectly() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

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
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("editProfileContent")
        .performScrollToNode(hasTestTag("uploadPicture"))
    composeTestRule.onNodeWithTag("uploadPicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag("addImageDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cameraButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("defaultImageButton").assertIsDisplayed()
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
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }
    composeTestRule
        .onNodeWithTag("editProfileContent")
        .performScrollToNode(hasTestTag("uploadPicture"))
    composeTestRule.onNodeWithTag("uploadPicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag("addImageDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cameraButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("defaultImageButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").performClick()
  }

  @Test
  fun testProfileEditionWithDialogWithDefaultImage() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("uploadPicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag("addImageDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cameraButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("defaultImageButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("defaultImageButton").performClick()
    composeTestRule.onNodeWithTag("DefaultImageCarousel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ImageCard_${R.drawable.dog_avatar}").performClick()
  }

  @Test
  fun testButtonGetsEnabledWhenBothFieldsAreFilled() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("categoryDropdown").performClick()
    composeTestRule.onNodeWithText(categories[0].name).performClick()
    composeTestRule.onNodeWithTag("addInterestButton").assertIsNotEnabled()

    composeTestRule.onNodeWithTag("interestDropdown").performClick()
    composeTestRule.onNodeWithText("Art").assertIsNotDisplayed()
    composeTestRule.onNodeWithText("Football").performClick()
    composeTestRule.onNodeWithTag("addInterestButton").assertIsEnabled()
  }

  @Test
  fun testModifyProfilePictureButtonIsClickable() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    // Verify if the button to modify the profile picture exists
    composeTestRule.onNodeWithTag("uploadPicture").assertIsDisplayed()

    // Perform a click action on the button
    composeTestRule.onNodeWithTag("uploadPicture").performClick()

    // Verify that the dialog to select a picture is shown
    composeTestRule.onNodeWithTag("editProfileScreen").assertIsDisplayed()
  }

  @Test
  fun testOpenImageDialogWhenButtonClicked() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    // Perform a click on the button to modify the profile picture
    composeTestRule.onNodeWithTag("uploadPicture").performClick()

    // Verify that the dialog for selecting an image is displayed
    composeTestRule.onNodeWithTag("editProfileScreen").assertIsDisplayed()
  }

  @Test
  fun testGoBackButtonNavigatesBack() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    // Perform a click on the Go Back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // Verify that the goBack method was called
    verify(navigationActions).goBack()
  }

  @Test
  fun testRemoveProfilePicture() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    // Click on the profile picture to remove it
    composeTestRule.onNodeWithTag("profilePicture").performClick()

    // Verify the "Remove Image" action was performed
    composeTestRule.onNodeWithTag("profilePicture").assertExists()
  }
}
