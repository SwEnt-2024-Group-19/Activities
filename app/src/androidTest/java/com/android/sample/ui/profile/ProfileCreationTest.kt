package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.testUserId
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class ProfileCreationTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var mockProfilesRepository: ProfilesRepository
  private lateinit var profileViewModel: ProfileViewModel

  private lateinit var mockImageViewModel: ImageViewModel
  private lateinit var mockImageRepository: ImageRepositoryFirestore

  // Mock or create a fake ProfilesRepository
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_PROFILE)

    mockProfilesRepository = mock(ProfilesRepository::class.java)

    val mockFirebaseAuth = mock(FirebaseAuth::class.java)
    val mockFirebaseUser = mock(FirebaseUser::class.java)
    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn(testUserId)

    profileViewModel = ProfileViewModel(repository = mockProfilesRepository, mock())

    `when`(mockProfilesRepository.getUser(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(User?) -> Unit>(1)
      onSuccess.invoke(null) // Simulate no existing user found
      null
    }
    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository)
    composeTestRule.setContent {
      ProfileCreationScreen(
          viewModel = profileViewModel, navigationActions = navigationActions, mockImageViewModel)
    }
  }

  @Test
  fun testProfileCreationWithDialogWithCamera() {
    composeTestRule
        .onNodeWithTag("profileCreationScrollColumn")
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
    composeTestRule.onNodeWithTag("profileCreationScrollColumn").assertIsDisplayed()
  }

  @Test
  fun testProfileCreationWithDialogWithGallery() {
    composeTestRule
        .onNodeWithTag("profileCreationScrollColumn")
        .performScrollToNode(hasTestTag("uploadPicture"))
    composeTestRule.onNodeWithTag("uploadPicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag("addImageDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cameraButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").performClick()
  }

  @Test
  fun testProfileCreationScreenElementsDisplayed() {
    composeTestRule.onNodeWithTag("profileCreationTitle").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nameTextField").assertIsDisplayed()

    composeTestRule.onNodeWithTag("surnameTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nameTextField").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("interestsLists")
    composeTestRule.onNodeWithTag("categoryDropdown").performClick()
    composeTestRule.onNodeWithText("SPORT").performClick()
    composeTestRule.onNodeWithTag("interestDropdown").performClick()
    composeTestRule.onNodeWithText("Football").performClick()

    composeTestRule.onNodeWithTag("addInterestButton").assertIsEnabled()
    composeTestRule.onNodeWithTag("addInterestButton").performClick()

    composeTestRule
        .onNodeWithTag("profileCreationScrollColumn")
        .performScrollToNode(hasTestTag("profilePicture"))

    composeTestRule
        .onNodeWithTag("profileCreationScrollColumn")
        .performScrollToNode(hasTestTag("uploadPicture"))
    composeTestRule.onNodeWithTag("uploadPicture").isDisplayed()

    composeTestRule
        .onNodeWithTag("profileCreationScrollColumn")
        .performScrollToNode(hasTestTag("createProfileButton"))

    composeTestRule.onNodeWithTag("createProfileButton").performClick()
  }

  @Test
  fun testNavigateToProfileScreen() {
    `when`(mockProfilesRepository.addProfileToDatabase(any(), any(), any())).thenAnswer { invocation
      ->
      // Get the onSuccess callback (second argument)
      val onSuccess = invocation.getArgument<() -> Unit>(1)
      // Simulate successful profile creation by invoking the onSuccess callback
      onSuccess.invoke()
      null
    }
    // Set the content for the test
    // Perform an action that triggers navigation to the Profile screen
    // Verify that the navigation action was called with the correct route
    // Simulate successful profile creation when called
    // composeTestRule.onNodeWithTag("createProfileButton").performScrollTo()
    composeTestRule.onNodeWithTag("nameTextField").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextField").performTextInput("Doe")
    composeTestRule
        .onNodeWithTag("profileCreationScrollColumn")
        .performScrollToNode(hasTestTag("createProfileButton"))

    composeTestRule.onNodeWithTag("createProfileButton").performClick()
    verify(navigationActions).navigateTo(Screen.OVERVIEW)
  }

  @Test
  fun testProfileCreationFailureDisplaysErrorMessage() {
    // Simulate failure in profile creation
    `when`(mockProfilesRepository.addProfileToDatabase(any(), any(), any())).thenAnswer { invocation
      ->
      val onError = invocation.getArgument<(Exception) -> Unit>(2)
      onError.invoke(Exception("Profile creation failed"))
      null
    }

    // Perform the click action
    composeTestRule.onNodeWithTag("nameTextField").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextField").performTextInput("Doe")
    composeTestRule
        .onNodeWithTag("profileCreationScrollColumn")
        .performScrollToNode(hasTestTag("createProfileButton"))
    composeTestRule.onNodeWithTag("createProfileButton").performClick()

    // Check if the error message is displayed
    composeTestRule
        .onNodeWithTag("profileCreationScrollColumn")
        .performScrollToNode(hasTestTag("errorMessage"))

    composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
  }
}
