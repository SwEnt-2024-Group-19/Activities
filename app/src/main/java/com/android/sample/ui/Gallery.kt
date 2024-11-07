package com.android.sample.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun ImagePicker(onImagePicked: (Uri?) -> Unit, buttonText: String = "Select Image") {
  // Launches the gallery picker
  val pickImageLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onImagePicked(uri) // Pass the selected URI back
      }
  Button(onClick = { pickImageLauncher.launch("image/*") }, Modifier.testTag("uploadPicture")) {
    Text(buttonText)
  }
}

@Composable
fun ProfileImage(url: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val painter =
        rememberAsyncImagePainter(
            model =
            ImageRequest.Builder(context)
                .apply {
                    // Only parse the URI and set it if the URL is not null and not empty
                    if (!url.isNullOrEmpty()) {
                        data(Uri.parse(url))
                    } else {
                        data(null) // Handle null or empty string by not attempting to load anything
                    }
                    crossfade(true)
                }
                .build())

    Image(
        painter = painter,
        contentDescription = "Profile Image",
        modifier = modifier,
        contentScale = ContentScale.Crop)
}
/*

@Composable
fun ImagePicker(onImagePicked: (String?) -> Unit, buttonText: String = "Select Image") {
    val context = LocalContext.current
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val imageStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(imageStream)
                val base64String = bitmap?.toBase64String()  // Convert Bitmap to Base64
                onImagePicked(base64String) // Pass the Base64 string back
            }
        }
    Button(onClick = { pickImageLauncher.launch("image/*") }) {
        Text(buttonText)
    }
}

@Composable
fun ProfileImage(url: String?, modifier: Modifier = Modifier) {
    val bitmap = url?.base64ToBitmap() // Decode Base64 string to Bitmap
    val imagePainter = bitmap?.let {
        BitmapPainter(it.asImageBitmap())
    }


    Image(
        painter = imagePainter ?: rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(null) // No image to display if bitmap is null
                .build()
        ),
        contentDescription = "Profile Image",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
*/




fun String.base64ToBitmap(): Bitmap? {
    return try {
        val byteArray = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    } catch (e: Exception) {
        // Exception caught here includes any decoding issues
        null  // Silently ignore the error and return null
    }
}




fun Bitmap.toBase64String(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
*/
