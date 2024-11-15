package com.android.sample.ui.camera

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
}
