package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.MockProfilesRepository
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class SignUpAndProfileCreationScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var mockImageViewModel: ImageViewModel
  private lateinit var mockImageRepository: ImageRepositoryFirestore
  private lateinit var mockProfilesRepository: MockProfilesRepository

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository)
    mockProfilesRepository = MockProfilesRepository()
    profileViewModel = ProfileViewModel(mockProfilesRepository, mock())
    composeTestRule.setContent {
      SignUpScreen(
          navigationActions = navigationActions,
          profileViewModel = profileViewModel,
          imageViewModel = mockImageViewModel)
    }
  }

  @Test
  fun allElementsAreDisplayed() {
    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("EmailTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PasswordTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nameTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("surnameTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SignUpButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoToSignInButton").assertIsDisplayed()
  }

  @Test
  fun testInvalidEmailShowsErrorMessage() {
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("invalidemail")
    composeTestRule.onNodeWithTag("SignUpButton").performClick()
    composeTestRule.onNodeWithTag("EmailErrorText").assertIsDisplayed()
  }

  @Test
  fun testShortPasswordShowsErrorMessage() {
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("validemail@gmail.com")
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("12345")
    composeTestRule.onNodeWithTag("SignUpButton").performClick()
    composeTestRule.onNodeWithTag("PasswordErrorText").assertIsDisplayed()
  }

  @Test
  fun testEmptyNameShowsErrorMessage() {
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("validemail@gmail.com")
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("password123")
    composeTestRule.onNodeWithTag("surnameTextField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("SignUpButton").performClick()
    composeTestRule.onNodeWithTag("nameError").assertIsDisplayed()
  }

  @Test
  fun testEmptySurnameShowsErrorMessage() {
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("validemail@gmail.com")
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("password123")
    composeTestRule.onNodeWithTag("nameTextField").performTextInput("John")
    composeTestRule.onNodeWithTag("SignUpButton").performClick()
    composeTestRule.onNodeWithTag("surnameError").assertIsDisplayed()
  }

  @Test
  fun testProfilePicturButtonsDisplayedOnClick() {
    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profilePicture").performClick()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag("cameraButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").assertIsDisplayed()
  }

  @Test
  fun testGoToSignInButtonNavigatesToSignIn() {
    composeTestRule.onNodeWithTag("GoToSignInButton").performClick()
    verify(navigationActions).navigateTo(Screen.AUTH)
  }

  @Test
  fun testCameraButtonOpensCameraScreen() {
    composeTestRule.onNodeWithTag("profilePicture").performClick()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag("cameraButton").performClick()
    // Verify that CameraScreen opens
    composeTestRule.onNodeWithTag("cameraScreen").assertIsDisplayed()
  }

  @Test
  fun testProfileCreationWithInvalidDataShowsErrors() {
    composeTestRule.onNodeWithTag("SignUpButton").performClick()
    composeTestRule.onNodeWithTag("EmailErrorText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PasswordErrorText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nameError").assertIsDisplayed()
    composeTestRule.onNodeWithTag("surnameError").assertIsDisplayed()
  }
}
