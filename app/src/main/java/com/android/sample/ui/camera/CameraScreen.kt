package com.android.sample.ui.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.model.image.flipCamera
import com.android.sample.model.image.takePhoto

/**
 * Composable function to display the Camera Screen.
 *
 * @param paddingValues The padding values to be applied to the screen.
 * @param controller The LifecycleCameraController to control the camera.
 * @param context The context in which the camera is used.
 * @param isCamOpen Callback to handle the camera open state.
 * @param addElem Callback to add the captured photo as a Bitmap.
 */
@Composable
fun CameraScreen(
    paddingValues: PaddingValues,
    controller: LifecycleCameraController,
    context: Context,
    isCamOpen: () -> Unit,
    addElem: (Bitmap) -> Unit
) {
  Box(modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("cameraScreen")) {
    CameraPreview(controller, Modifier.fillMaxSize())
    IconButton(
        onClick = isCamOpen, modifier = Modifier.align(Alignment.TopEnd).testTag("closeCamera")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Close camera")
        }
    IconButton(
        onClick = {
          takePhoto(
              controller,
              { bitmap ->
                isCamOpen()
                addElem(bitmap)
              },
              context)
        },
        modifier = Modifier.align(Alignment.BottomCenter).testTag("takePicture")) {
          Icon(Icons.Default.PhotoCamera, contentDescription = "Take picture")
        }
    IconButton(
        onClick = { controller.cameraSelector = flipCamera(controller.cameraSelector) },
        modifier = Modifier.align(Alignment.BottomEnd).testTag("switchCamera")) {
          Icon(Icons.Default.Cameraswitch, contentDescription = "Switch camera")
        }
  }
}
