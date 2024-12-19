package com.android.sample.ui.camera

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.R
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class GalleryTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockAddImage: (Bitmap) -> Unit
  private val mockIsGalleryOpen = mock<() -> Unit>()

  private lateinit var mockImageViewModel: ImageViewModel
  private lateinit var mockImageRepository: ImageRepositoryFirestore

  private lateinit var sharedPreferences: SharedPreferences
  private lateinit var mockEditor: SharedPreferences.Editor
  private val context = ApplicationProvider.getApplicationContext<Context>()

  @Before
  fun setUp() {
    mockAddImage = mock()
    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    sharedPreferences = mock(SharedPreferences::class.java)
    mockEditor = mock(SharedPreferences.Editor::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository, sharedPreferences)
  }

  @Test
  fun profileImage_displaysCorrectlyWhenUrlIsPresent() {
    // Arrange
    val mockUserId = "testUserId"

    composeTestRule.setContent {
      ProfileImage(userId = mockUserId, modifier = androidx.compose.ui.Modifier, mockImageViewModel)
    }

    // Act
    composeTestRule.waitForIdle() // Wait for the image to load

    // Assert
    composeTestRule.onNodeWithContentDescription("Profile Image").assertIsDisplayed()
  }

  @Test
  fun testDefaultImageCarousel() {
    val mockOnImageSelected = mock<(Bitmap) -> Unit>()
    val mockOnDismiss = mock<() -> Unit>()
    composeTestRule.setContent {
      DefaultImageCarousel(
          onImageSelected = mockOnImageSelected, context = context, onDismiss = mockOnDismiss)
    }

    composeTestRule.onNodeWithTag("DefaultImageCarousel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ImageCard_${R.drawable.dog_avatar}").performClick()
    composeTestRule.onNodeWithText("Select an Image").assertIsDisplayed()
  }
}
