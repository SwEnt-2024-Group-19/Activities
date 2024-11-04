package com.android.sample.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AddImageDialog(onDismiss: () -> Unit, onGalleryClick: () -> Unit, onCameraClick: () -> Unit) {
  Dialog(
      onDismissRequest = onDismiss,
      properties =
          DialogProperties(
              dismissOnBackPress = true,
              dismissOnClickOutside = true,
          )) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .height(200.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(size = 16.dp))
                    .testTag("addUserDialog"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
              text = "Add an image",
              style =
                  TextStyle(color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold),
          )

          Button(onClick = onGalleryClick, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color.Transparent)) {
                  Text(
                      text = "Choose from gallery",
                      style = TextStyle(color = Color.Black, fontSize = 16.sp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Icon(Icons.Filled.PhotoLibrary, contentDescription = "Choose from gallery")
                }
          }
          Spacer(modifier = Modifier.height(8.dp))

          Button(onClick = onCameraClick, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color.Transparent)) {
                  Text(
                      text = "Take pictures with camera",
                      style = TextStyle(color = Color.Black, fontSize = 16.sp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Icon(Icons.Filled.PhotoCamera, contentDescription = "Choose from gallery")
                }
          }
        }
      }
}
