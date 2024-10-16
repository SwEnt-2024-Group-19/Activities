package com.android.sample.ui.dialogs

import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Button
import androidx.core.app.ActivityCompat.startActivityForResult


@Composable
fun AddImageDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Add an image")
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onGalleryClick) {
                Text(text = "Choose from gallery")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onCameraClick) {
                Text(text = "Take a photo")
            }
        }
    }
}
