package com.android.sample.ui.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.core.app.ApplicationProvider
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

  @Before
  fun setUp() {
    mockAddImage = mock()
    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository)
  }

  @Test
  fun profileImage_displaysCorrectlyWhenUrlIsPresent() {
    // Arrange
    val mockUserId = "testUserId"
    val mockImageUrl = "https://test.com/image.jpg"
    val context = ApplicationProvider.getApplicationContext<Context>()

    composeTestRule.setContent {
      ProfileImage(userId = mockUserId, modifier = androidx.compose.ui.Modifier, mockImageViewModel)
    }

    // Act
    composeTestRule.waitForIdle() // Wait for the image to load

    // Assert
    composeTestRule.onNodeWithContentDescription("Profile Image").assertIsDisplayed()
  }
}
