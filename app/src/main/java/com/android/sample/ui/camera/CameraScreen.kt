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
import com.android.sample.model.camera.flipCamera
import com.android.sample.model.camera.takePhoto

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
    IconButton(onClick = isCamOpen, modifier = Modifier.align(Alignment.TopEnd)) {
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
