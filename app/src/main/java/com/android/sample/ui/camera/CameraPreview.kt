package com.android.sample.ui.camera

import android.graphics.Bitmap
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.sample.resources.C.Tag.BLACK_COLOR
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.WHITE_COLOR

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
  Row(
      modifier = Modifier.fillMaxWidth().height(135.dp).padding(STANDARD_PADDING.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Card(
            modifier =
                Modifier.padding(STANDARD_PADDING.dp)
                    .background(Color(WHITE_COLOR))
                    .testTag("carouselItem"),
        ) {
          itemsList.forEach { items ->
            Image(
                bitmap = items.asImageBitmap(),
                contentDescription = "Image",
                modifier = Modifier.size(IMAGE_SIZE.dp))
            IconButton(
                onClick = { deleteImage(items) },
                modifier =
                    Modifier.width(BUTTON_WIDTH.dp)
                        .height(BUTTON_WIDTH.dp)
                        .align(Alignment.End)
                        .testTag("removeImageButton"),
            ) {
              Icon(
                  Icons.Filled.DeleteOutline,
                  contentDescription = "remove image",
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(MEDIUM_PADDING.dp))
        Column(
            modifier =
                Modifier.padding(MEDIUM_PADDING.dp)
                    .fillMaxHeight(), // Use size modifier for simplicity
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End // Center the icon horizontally
            ) {
              if (itemsList.isEmpty()) {
                FloatingActionButton(
                    content = {
                      Icon(
                          imageVector = Icons.Outlined.AddCircle,
                          contentDescription = "Add a new image")
                    },
                    onClick = openDialog,
                    modifier = Modifier.size((2 * LARGE_PADDING).dp).background(Color(WHITE_COLOR)),
                )
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                Text(
                    text = "Add Image",
                    color = Color(BLACK_COLOR),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = STANDARD_PADDING.sp),
                )
              } else {
                FloatingActionButton(
                    content = {
                      Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit image")
                    },
                    onClick = openDialog,
                    modifier = Modifier.size(BUTTON_WIDTH.dp).background(Color(WHITE_COLOR)),
                )
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                Text(
                    text = "Replace Image",
                    color = Color(BLACK_COLOR),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = STANDARD_PADDING.sp),
                )
              }
            }
      }
}
