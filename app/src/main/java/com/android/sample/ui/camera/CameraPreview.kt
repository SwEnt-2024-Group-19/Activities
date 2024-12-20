package com.android.sample.ui.camera

import android.graphics.Bitmap
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND_BUTTON
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.android.sample.resources.C.Tag.MEDIUM_FONTSIZE
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
          Icon(
              imageVector = Icons.Filled.AddAPhoto, // Using a more specific icon for adding photos
              contentDescription = "Add a new image", // Providing an accessible description
              modifier = Modifier.size(MEDIUM_FONTSIZE.dp) // Icon size can be adjusted as needed
              )
        },
        onClick = openDialog,
        modifier =
            Modifier.size(LARGE_BUTTON_HEIGHT.dp)
                .background(Color(WHITE_COLOR))
                .testTag("addImageButton"),
        contentColor = Color(MAIN_COLOR_DARK),
        containerColor = Color(MAIN_BACKGROUND_BUTTON))
    LazyRow {
      items(itemsList.size) { bitmap ->
        Box() {
          Card(
              modifier = Modifier.padding(SMALL_PADDING.dp),
              colors =
                  CardDefaults.cardColors(
                      containerColor = Color(MAIN_BACKGROUND_BUTTON),
                      contentColor = Color(MAIN_COLOR_DARK)),
              border = BorderStroke(3.dp, Color(MAIN_BACKGROUND_BUTTON))) {
                Image(
                    bitmap = itemsList[bitmap].resize(IMAGE_SIZE).asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(IMAGE_SIZE.dp),
                    contentScale = ContentScale.FillBounds)
              }
          IconButton(
              onClick = { deleteImage(itemsList[bitmap]) },
              modifier =
                  Modifier.size(48.dp) // Appropriate size for touch target
                      .align(Alignment.TopEnd) // Align to the top end of the Box, not the Card
                      .background(
                          color = Color.White.copy(alpha = 0.0F), // White background for contrast
                          shape = CircleShape // Circular background for the delete button
                          ), // Optional: Add a background for better visibility
          ) {
            Icon(
                imageVector = Icons.Filled.DeleteForever, // Clear delete icon
                contentDescription = "Remove Image",
                tint = Color.Black, // White icon for contrast
            )
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
