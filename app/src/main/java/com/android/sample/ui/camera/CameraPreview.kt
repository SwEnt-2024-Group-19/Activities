package com.android.sample.ui.camera

import android.graphics.Bitmap
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.sample.model.activity.Category
import com.android.sample.model.image.resize
import com.android.sample.resources.C.Tag.IMAGE_SIZE
import com.android.sample.resources.C.Tag.LARGE_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.LARGE_IMAGE_SIZE
import com.android.sample.resources.C.Tag.MEDIUM_IMAGE_SIZE
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.WHITE_COLOR

/**
 * Composable function to display the camera preview.
 *
 * @param controller The LifecycleCameraController to control the camera.
 * @param modifier The Modifier to be applied to the camera preview.
 */
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
      modifier = modifier.testTag("cameraPreview"),
  )
}

/**
 * Composable function to display a carousel of images with an option to add and delete images.
 *
 * @param openDialog Callback to open the dialog for adding a new image.
 * @param itemsList List of Bitmap images to be displayed in the carousel.
 * @param deleteImage Callback to delete an image from the carousel.
 */
@Composable
fun Carousel(openDialog: () -> Unit, itemsList: List<Bitmap>, deleteImage: (Bitmap) -> Unit) {
  Row(modifier = Modifier.padding(STANDARD_PADDING.dp).height(120.dp)) {
    FloatingActionButton(
        content = {
          Icon(imageVector = Icons.Outlined.AddCircle, contentDescription = "Add a new image")
        },
        onClick = openDialog,
        modifier =
            Modifier.size(LARGE_BUTTON_HEIGHT.dp)
                .background(Color(WHITE_COLOR))
                .testTag("addImageButton"),
    )
    LazyRow {
      items(itemsList.size) { bitmap ->
        Card(modifier = Modifier.padding(SMALL_PADDING.dp)) {
          Image(
              bitmap = itemsList[bitmap].resize(IMAGE_SIZE).asImageBitmap(),
              contentDescription = "Selected Image",
              modifier = Modifier.size(IMAGE_SIZE.dp))
          IconButton(
              onClick = { deleteImage(itemsList[bitmap]) },
              modifier =
                  Modifier.width(MEDIUM_IMAGE_SIZE.dp)
                      .height(MEDIUM_IMAGE_SIZE.dp)
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

/**
 * Composable function to display a carousel of images with no option to add and delete images.
 *
 * @param openDialog Callback to open the dialog for adding a new image.
 * @param itemsList List of Bitmap images to be displayed in the carousel.
 * @param deleteImage Callback to delete an image from the carousel.
 */
@Composable
fun CarouselNoModif(itemsList: List<Bitmap>, category: Category) {
  Row(modifier = Modifier.padding(STANDARD_PADDING.dp)) {
    LazyRow {
      if (itemsList.isNotEmpty()) {
        items(itemsList.size) { index ->
          Card(modifier = Modifier.padding(SMALL_PADDING.dp)) {
            Image(
                bitmap = itemsList[index].resize(LARGE_IMAGE_SIZE).asImageBitmap(),
                contentDescription = "Selected Image",
                modifier = Modifier.size(LARGE_IMAGE_SIZE.dp).fillMaxSize())
          }
        }
      } else {
        item {
          Card(modifier = Modifier.padding(SMALL_PADDING.dp)) {
            Image(
                painter = painterResource(id = getImageResourceIdForCategory(category)),
                contentDescription = "Default Category Image",
                modifier = Modifier.size(LARGE_IMAGE_SIZE.dp),
                contentScale = ContentScale.Crop)
          }
        }
      }
    }
  }
}
