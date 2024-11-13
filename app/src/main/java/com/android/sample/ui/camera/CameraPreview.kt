package com.android.sample.ui.camera

import android.graphics.Bitmap
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier,
) {
  val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
  AndroidView(
      factory = { context ->
        PreviewView(context).apply {
          this.controller = controller
          controller.bindToLifecycle(lifecycleOwner)
        }
      },
      modifier = modifier)
}

@Composable
fun Carousel(openDialog: () -> Unit, itemsList: List<Bitmap>, deleteImage: (Bitmap) -> Unit) {
  Row(modifier = Modifier.padding(8.dp).height(120.dp)) {
    FloatingActionButton(
        content = {
          Icon(imageVector = Icons.Outlined.AddCircle, contentDescription = "Add a new image")
        },
        onClick = openDialog,
        modifier = Modifier.size(50.dp).background(Color(0xFFFFFFFF)),
    )
    LazyRow {
      items(itemsList.size) { bitmap ->
        Card(modifier = Modifier.padding(4.dp)) {
          Image(
              bitmap = itemsList[bitmap].resize(100, 100).asImageBitmap(),
              contentDescription = "Selected Image",
              modifier = Modifier.size(100.dp))
          IconButton(
              onClick = { deleteImage(itemsList[bitmap]) },
              modifier =
                  Modifier.width(80.dp)
                      .height(80.dp)
                      .align(Alignment.End)
                      .testTag("removeImageButton")) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove Image",
                    tint = Color.Black)
              }
        }
      }
    }
  }
}

fun Bitmap.resize(reqWidth: Int, reqHeight: Int): Bitmap {
  val ratio: Float = this.width.toFloat() / this.height.toFloat()
  val height = (reqWidth / ratio).toInt()
  return Bitmap.createScaledBitmap(this, reqWidth, height, true)
}
