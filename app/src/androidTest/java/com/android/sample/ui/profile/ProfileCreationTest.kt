package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
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
import org.mockito.kotlin.argThat

class ProfileCreationTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var mockProfilesRepository: ProfilesRepository
  private lateinit var profileViewModel: ProfileViewModel

  // Mock or create a fake ProfilesRepository
  // private val mockProfilesRepository: ProfilesRepository = mock()
  @get:Rule val composeTestRule = createComposeRule()
  // Define a test user ID
  private val testUserId = "testUser123"

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_PROFILE)

    mockProfilesRepository = mock(ProfilesRepository::class.java)

    val mockFirebaseAuth = mock(FirebaseAuth::class.java)
    val mockFirebaseUser = mock(FirebaseUser::class.java)
    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUser123")


    profileViewModel = ProfileViewModel(repository = mockProfilesRepository, userId = testUserId)

    `when`(mockProfilesRepository.getUser(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(User?) -> Unit>(1)
      onSuccess.invoke(null)  // Simulate no existing user found
      null
    }

    composeTestRule.setContent {
      ProfileCreationScreen(viewModel = profileViewModel, navigationActions = navigationActions)
    }
  }

  @Test
  fun testProfileCreationScreenElementsDisplayed() {
    // Create a ProfileViewModel using the mocked repository and test user ID
    // Set the content for the test, passing the required parameters
    // Perform assertions to check if elements are displayed
    composeTestRule.onNodeWithTag("profileCreationTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nameTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("surnameTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("interestsTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("activitiesTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("photoTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("createProfileButton").assertIsDisplayed()
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
    composeTestRule.onNodeWithTag("createProfileButton").performClick()

    // Check if the error message is displayed
    composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
  }


  }



