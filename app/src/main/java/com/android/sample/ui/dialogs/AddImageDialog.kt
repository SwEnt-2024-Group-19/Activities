package com.android.sample.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.sample.resources.C.Tag.BORDER_STROKE_SM
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.SUBTITLE_FONTSIZE
import com.android.sample.resources.C.Tag.TITLE_FONTSIZE

/**
 * Composable function to display the dialog to add an image, either from the gallery or the camera.
 *
 * @param onDismiss The callback to dismiss the dialog.
 * @param onGalleryClick The callback to handle the click on the gallery button.
 * @param onCameraClick The callback to handle the click on the camera button.
 */
@Composable
fun AddImageDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSelectDefault: () -> Unit,
    default: Boolean = false
) {
  Dialog(
      onDismissRequest = onDismiss,
      properties =
          DialogProperties(
              dismissOnBackPress = true,
              dismissOnClickOutside = true,
          )) {
        Column(
            modifier =
                Modifier.background(
                        color = Color.White,
                        shape = RoundedCornerShape(size = ROUNDED_CORNER_SHAPE_DEFAULT.dp))
                    .testTag("addImageDialog"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
              text = "Add an image",
              style = TextStyle(color = Color.Black, fontSize = TITLE_FONTSIZE.sp),
          )
          TextButton(
              onClick = onGalleryClick,
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(STANDARD_PADDING.dp)
                      .testTag("galleryButton")
                      .border(
                          width = BORDER_STROKE_SM.dp,
                          color = Color.Cyan,
                          shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp))) {
                Text(
                    text = "Choose from gallery",
                    style = TextStyle(color = Color.Black, fontSize = SUBTITLE_FONTSIZE.sp))
                Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
                Icon(
                    Icons.Filled.PhotoLibrary,
                    contentDescription = "Choose from gallery",
                    tint = Color.Gray)
              }

          TextButton(
              onClick = onCameraClick,
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(STANDARD_PADDING.dp)
                      .testTag("cameraButton")
                      .border(
                          width = BORDER_STROKE_SM.dp,
                          color = Color.Cyan,
                          shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp))) {
                Text(
                    text = "Take pictures with camera",
                    style = TextStyle(color = Color.Black, fontSize = SUBTITLE_FONTSIZE.sp))
                Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = "Choose from gallery",
                    tint = Color.Gray)
              }

          if (default) {
            TextButton(
                onClick = onSelectDefault,
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(STANDARD_PADDING.dp)
                        .testTag("defaultImageButton")
                        .border(
                            width = BORDER_STROKE_SM.dp,
                            color = Color.Cyan,
                            shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp))) {
                  Text(
                      text = "Select default picture",
                      style = TextStyle(color = Color.Black, fontSize = SUBTITLE_FONTSIZE.sp))
                  Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
                  Icon(Icons.Filled.AddToPhotos, contentDescription = "Default", tint = Color.Gray)
                }
          }
        }
      }
}
