package com.android.sample.ui.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class CameraScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockController: LifecycleCameraController
  private val mockIsCamOpen = mock<() -> Unit>()
  private val mockAddElem = mock<(Bitmap) -> Unit>()

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val lifecycleOwner = mock<LifecycleOwner>()
    val lifecycle = LifecycleRegistry(lifecycleOwner)

    // Ensure the lifecycle state change and bindToLifecycle happen on the main thread
    composeTestRule.runOnUiThread {
      lifecycle.currentState = Lifecycle.State.RESUMED

      mockController = LifecycleCameraController(context).apply { bindToLifecycle(lifecycleOwner) }
    }
  }

  @Test
  fun cameraScreen_displaysAllComponentsCorrectly() {
    // Arrange
    composeTestRule.setContent {
      val context = LocalContext.current
      val paddingValues = PaddingValues()
      CameraScreen(paddingValues, mockController, context, mockIsCamOpen, mockAddElem)
    }

    // Act & Assert
    composeTestRule.onNodeWithTag("cameraScreen").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Close camera").assertIsDisplayed()
    composeTestRule.onNodeWithTag("takePicture").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Switch camera").assertIsDisplayed()
  }

  @Test
  fun cameraScreen_closeButtonClick_triggersIsCamOpen() {
    // Arrange
    composeTestRule.setContent {
      val context = LocalContext.current
      val paddingValues = PaddingValues()
      CameraScreen(paddingValues, mockController, context, mockIsCamOpen, mockAddElem)
    }

    // Act
    composeTestRule.onNodeWithContentDescription("Close camera").performClick()

    // Assert
    verify(mockIsCamOpen).invoke()
  }
}
