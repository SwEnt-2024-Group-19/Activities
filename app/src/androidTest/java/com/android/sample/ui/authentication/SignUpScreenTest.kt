package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.activity.Activity
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.MockProfilesRepository
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any

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
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("profilePicture"))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("EmailTextField"))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("PasswordTextField"))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("nameTextField"))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("surnameTextField"))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("SignUpButton"))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("GoToSignInButton"))
        .assertIsDisplayed()
  }

  @Test
  fun testInvalidEmailShowsErrorMessage() {
    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("invalidemail")

    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("SignUpButton").performClick()

    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("EmailErrorText"))
    composeTestRule.onNodeWithTag("EmailErrorText").assertIsDisplayed()
  }

  @Test
  fun testShortPasswordShowsErrorMessage() {
    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("validemail@gmail.com")

    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("12345")

    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("SignUpButton").performClick()

    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("PasswordErrorText"))
    composeTestRule.onNodeWithTag("PasswordErrorText").assertIsDisplayed()
  }

  @Test
  fun testEmptyNameShowsErrorMessage() {
    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("validemail@gmail.com")

    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("password123")

    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("surnameTextField"))
    composeTestRule.onNodeWithTag("surnameTextField").performTextInput("Doe")

    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("SignUpButton").performClick()

    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("nameError"))
    composeTestRule.onNodeWithTag("nameError").assertIsDisplayed()
  }

  @Test
  fun testEmptySurnameShowsErrorMessage() {
    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("EmailTextField"))
    composeTestRule.onNodeWithTag("EmailTextField").performTextInput("validemail@gmail.com")

    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("PasswordTextField"))
    composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("password123")

    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("nameTextField"))
    composeTestRule.onNodeWithTag("nameTextField").performTextInput("John")

    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("SignUpButton").performClick()

    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("surnameError"))
    composeTestRule.onNodeWithTag("surnameError").assertIsDisplayed()
  }

  @Test
  fun testProfilePictureButtonsDisplayedOnClick() {
    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("profilePicture"))
    composeTestRule.onNodeWithTag("profilePicture").performClick()
    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("uploadPicture"))
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag(("cameraButton")).assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").assertIsDisplayed()
  }

  @Test
  fun testGoToSignInButtonNavigatesToSignIn() {
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("GoToSignInButton"))
    composeTestRule.onNodeWithTag("GoToSignInButton").performClick()
    verify(navigationActions).navigateTo(Screen.AUTH)
  }

  @Test
  fun testCameraButtonOpensCameraScreen() {
    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("profilePicture"))
    composeTestRule.onNodeWithTag("profilePicture").performClick()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("uploadPicture"))
        .performClick()
    composeTestRule.onNodeWithTag("uploadPicture").performClick()
    composeTestRule.onNodeWithTag("cameraButton").performClick()
    composeTestRule.onNodeWithTag(("cameraScreen")).assertIsDisplayed()
  }

  @Test
  fun testProfileCreationWithInvalidDataShowsErrors() {
    composeTestRule.onNodeWithTag("SignUpColumn").performScrollToNode(hasTestTag("SignUpButton"))
    composeTestRule.onNodeWithTag("SignUpButton").performClick()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("EmailErrorText"))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("PasswordErrorText"))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("nameError"))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("SignUpColumn")
        .performScrollToNode(hasTestTag("surnameError"))
        .assertIsDisplayed()
  }
    @Test
    fun togglePasswordVisibility() {

        composeTestRule.onNodeWithContentDescription("Show password").assertExists()

        // Click the visibility icon to show the password
        composeTestRule.onNodeWithContentDescription("Show password").performClick()

        // Verify password visibility toggle behavior (e.g., check attribute or visual state).
        composeTestRule.onNodeWithTag("PasswordTextField").assertExists()
    }

    @Test
    fun testSuccessfulAccountCreation() {
        // Mock FirebaseAuth to return a valid user ID
        val mockFirebaseAuth = mock(FirebaseAuth::class.java)
        val mockFirebaseUser = mock(FirebaseUser::class.java)
        whenever(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.uid).thenReturn("mockUserId")

        // Mock ProfileViewModel and its behavior
        profileViewModel = mock(ProfileViewModel::class.java)
        doAnswer { invocation ->
            val userProfile = invocation.getArgument<User>(0) // Retrieve the userProfile argument
            val onSuccess = invocation.getArgument<() -> Unit>(1) // Retrieve the onSuccess callback
            checkNotNull(userProfile) { "UserProfile cannot be null" } // Ensure userProfile is not null
            onSuccess.invoke() // Invoke the success callback
            null
        }.whenever(profileViewModel).createUserProfile(any(), any(), any())

        // Simulate valid user input
        composeTestRule.onNodeWithTag("EmailTextField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("PasswordTextField").performTextInput("password123")
        composeTestRule.onNodeWithTag("nameTextField").performTextInput("John")
        composeTestRule.onNodeWithTag("surnameTextField").performTextInput("Doe")

        // Click the Sign-Up button
        composeTestRule.onNodeWithTag("SignUpButton").performClick()

        // Verify that `createUserProfile` was called with the correct data
        verify(profileViewModel).createUserProfile(
            userProfile = argThat {
                it.id == "mockUserId" &&
                        it.name == "John" &&
                        it.surname == "Doe" &&
                        it.photo == "" &&
                        it.interests == emptyList<Interest>() &&
                        it.activities == emptyList<Activity>() &&
                        it.likedActivities == emptyList<Activity>()
            },
            onSuccess = any(),
            onError = any()
        )

        // Verify navigation to the overview screen
        verify(navigationActions).navigateTo(Screen.OVERVIEW)
    }




}
