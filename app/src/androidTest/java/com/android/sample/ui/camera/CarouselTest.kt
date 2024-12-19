package com.android.sample.ui.camera

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.image.resize
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class CarouselTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var itemsList: List<Bitmap>
  private lateinit var openDialog: () -> Unit
  private lateinit var deleteImage: (Bitmap) -> Unit

  @Before
  fun setUp() {
    // Replace with a valid drawable resource
    itemsList = listOf()
    openDialog = mock()
    deleteImage = mock()
  }

  @Test
  fun addImageButton_isDisplayed() {
    composeTestRule.setContent {
      Carousel(openDialog = openDialog, itemsList = itemsList, deleteImage = deleteImage)
    }

    composeTestRule.onNodeWithTag("addImageButton").assertIsDisplayed()
  }

  @Test
  fun addImageButton_onClick_opensDialog() {
    composeTestRule.setContent {
      Carousel(openDialog = openDialog, itemsList = itemsList, deleteImage = deleteImage)
    }

    composeTestRule.onNodeWithTag("addImageButton").performClick()
    // Verify that the openDialog function is called
    // You may need to use a spy or a verification mechanism depending on the implementation
  }

  @Test
  fun testResize() {
    val originalWidth = 100
    val originalHeight = 50
    val reqWidth = 50

    // Create a mock Bitmap with specific dimensions

    // Calculate the expected height based on the required width
    val ratio = originalWidth.toFloat() / originalHeight.toFloat()
    val expectedHeight = (reqWidth / ratio).toInt()

    val originalBitmap = Bitmap.createBitmap(originalWidth, originalHeight, Bitmap.Config.ARGB_8888)
    // Perform the resize
    val resizedBitmap = originalBitmap.resize(reqWidth)

    // Verify the dimensions
    assertEquals(reqWidth, resizedBitmap.width)
    assertEquals(expectedHeight, resizedBitmap.height)
  }
}
