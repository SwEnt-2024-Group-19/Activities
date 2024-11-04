package com.android.sample.ui.dialogs

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AddImageDialogTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun addImageDialogDisplaysCorrectly() {
    composeTestRule.setContent {
      AddImageDialog(onDismiss = {}, onGalleryClick = {}, onCameraClick = {})
    }

    composeTestRule.onNodeWithText("Add an image").assertExists()
    composeTestRule.onNodeWithText("Choose from gallery").assertExists()
    composeTestRule.onNodeWithText("Take pictures with camera").assertExists()
  }

  @Test
  fun addImageDialogCallsOnGalleryClick() {
    var galleryClicked = false
    composeTestRule.setContent {
      AddImageDialog(onDismiss = {}, onGalleryClick = { galleryClicked = true }, onCameraClick = {})
    }

    composeTestRule.onNodeWithText("Choose from gallery").performClick()
    assert(galleryClicked)
  }

  @Test
  fun addImageDialogCallsOnCameraClick() {
    var cameraClicked = false
    composeTestRule.setContent {
      AddImageDialog(onDismiss = {}, onGalleryClick = {}, onCameraClick = { cameraClicked = true })
    }

    composeTestRule.onNodeWithText("Take pictures with camera").performClick()
    assert(cameraClicked)
  }
}
