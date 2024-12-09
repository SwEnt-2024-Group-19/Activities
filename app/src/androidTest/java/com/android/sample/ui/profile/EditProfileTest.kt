package com.android.sample.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.InterestCategories
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
    composeTestRule.onNodeWithTag("galleryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("galleryButton").performClick()
  }

  @Test
  fun testAddInterestButtonFunctionality() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("categoryDropdown").performClick()
    composeTestRule.onNodeWithText("Indoor Activity").performClick()
    composeTestRule.onNodeWithTag("newInterestInput").performTextInput("New Interest")
    composeTestRule.onNodeWithTag("addInterestButton").performClick()
    composeTestRule.onNodeWithTag("interestsList").performScrollTo()
    composeTestRule.onNodeWithTag("interestsList").assertIsDisplayed()
  }

  @Test
  fun testCannotWriteInterestBeforeChoosingCategory() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    // Check if the interest input is initially disabled
    composeTestRule.onNodeWithTag("newInterestInput").assertIsNotEnabled()

    // Try to interact with the interest input while it's disabled
    // Compose testing framework doesn't allow interaction with disabled components
    // So we verify the disabled state instead of attempting to interact with it

    // Now select a category
    composeTestRule.onNodeWithTag("categoryDropdown").performClick()
    composeTestRule.onNodeWithText("Sport").performClick()

    // The interest input should now be enabled
    composeTestRule.onNodeWithTag("newInterestInput").assertIsEnabled()

    // Try to enter text
    composeTestRule.onNodeWithTag("newInterestInput").performTextInput("Football")
    composeTestRule.onNodeWithText("Football").assertIsDisplayed() // Ensure input is now accepted
  }

  @Test
  fun testAddButtonEnabledOnlyWithBothFieldsFilled() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    // Initially, both fields are empty, and "Add" button should be disabled.
    composeTestRule.onNodeWithTag("addInterestButton").assertIsNotEnabled()

    // Select a category
    composeTestRule.onNodeWithTag("categoryDropdown").performClick()
    composeTestRule.onNodeWithText("Sport").performClick()

    // "Add" button should still be disabled because the interest field is empty.
    composeTestRule.onNodeWithTag("addInterestButton").assertIsNotEnabled()

    // Enter text in the interest input
    composeTestRule.onNodeWithTag("newInterestInput").performTextInput("Football")

    // Now, "Add" button should be enabled as both fields are correctly filled.
    composeTestRule.onNodeWithTag("addInterestButton").assertIsEnabled()
  }

  @Test
  fun testAddButtonDisabledWhenCategoryIsNone() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    // Set the category to "None"
    composeTestRule.onNodeWithTag("categoryDropdown").performClick()
    composeTestRule.onNodeWithText("None").performClick()

    // Verify the interest input field is disabled
    composeTestRule.onNodeWithTag("newInterestInput").assertIsNotEnabled()

    // Try to add the interest
    composeTestRule.onNodeWithTag("addInterestButton").assertIsNotEnabled()
  }

  @Test
  fun testAddButtonDisabledWhenCategoryIsNoneEvenIfThereIsAnInterest() {
    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("categoryDropdown").performClick()
    composeTestRule.onNodeWithText("Sport").performClick()

    // Input an interest and add it
    composeTestRule.onNodeWithTag("newInterestInput").performTextInput("Luge")
    // Set the category to "None"
    composeTestRule.onNodeWithTag("categoryDropdown").performClick()
    composeTestRule.onNodeWithText("None").performClick()

    // Verify the interest input field is disabled
    composeTestRule.onNodeWithTag("newInterestInput").assertIsNotEnabled()

    // Try to add the interest
    composeTestRule.onNodeWithTag("addInterestButton").assertIsNotEnabled()
  }

  @Test
  fun testAllCategoriesAppearOnClick() {

    composeTestRule.setContent {
      EditProfileScreen(profileViewModel, navigationActions, mockImageViewModel)
    }

    // Click the dropdown to expand it
    composeTestRule.onNodeWithText("Category").performClick()

    // Check each category is displayed in the dropdown
    InterestCategories.forEach { category ->
      composeTestRule.onNodeWithText(category).assertIsDisplayed()
    }
  }
}
