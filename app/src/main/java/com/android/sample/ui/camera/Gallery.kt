package com.android.sample.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.sample.R
import com.android.sample.model.activity.Category
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.image.uriToBitmap
import com.android.sample.resources.C.Tag.CARD_IAMGES_SIZE
import com.android.sample.resources.C.Tag.MEDIUM_FONTSIZE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING

@Composable
fun GalleryScreen(isGalleryOpen: () -> Unit, addImage: (Bitmap) -> Unit, context: Context) {
  val launcher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri?
        ->
        uri?.let {
          val bitmap = uriToBitmap(it, context)
          bitmap?.let { bmp ->
            addImage(bmp)
            isGalleryOpen()
          }
        }
      }

  LaunchedEffect(key1 = true) { launcher.launch("image/*") }
  DisposableEffect(Unit) {
    onDispose {
      isGalleryOpen() // Ensure the screen closes even if no image is selected
    }
  }
}

@Composable
fun DefaultImageCarousel(
    onImageSelected: (Bitmap) -> Unit,
    context: Context,
    onDismiss: () -> Unit
) {
  val defaultImages =
      listOf(
          R.drawable.dog_avatar,
          R.drawable.cat_avatar,
          R.drawable.fox_avatar,
          R.drawable.bull_avatar,
          R.drawable.pig_avatar,
          R.drawable.chicken_avatar,
          R.drawable.panda_avatar,
          R.drawable.reindeer_avatar,
          R.drawable.monkey_avatar)

  Dialog(onDismissRequest = onDismiss) {
    Card(
        shape = RoundedCornerShape(MEDIUM_PADDING.dp),
        modifier = Modifier.padding(MEDIUM_PADDING.dp).testTag("DefaultImageCarousel")) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Select an Image",
                fontWeight = FontWeight.Bold,
                fontSize = MEDIUM_FONTSIZE.sp,
                modifier = Modifier.padding(MEDIUM_PADDING.dp))
            LazyRow(
                modifier = Modifier.padding(MEDIUM_PADDING.dp),
                horizontalArrangement = Arrangement.spacedBy(MEDIUM_PADDING.dp)) {
                  items(defaultImages) { imageRes ->
                    ImageCard(imageRes = imageRes) {
                      val bitmap = BitmapFactory.decodeResource(context.resources, imageRes)
                      onImageSelected(bitmap)
                      onDismiss()
                    }
                  }
                }
          }
        }
  }
}

@Composable
fun ImageCard(imageRes: Int, onClick: () -> Unit) {
  Card(
      modifier =
          Modifier.size(CARD_IAMGES_SIZE.dp)
              .clickable(onClick = onClick)
              .testTag("ImageCard_$imageRes"), // Dynamic tag based on image resource
      shape = CircleShape) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Default Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(CircleShape))
      }
}

@Composable
fun ProfileImage(
    userId: String,
    modifier: Modifier = Modifier,
    imageViewModel: ImageViewModel,
    editing: Boolean = false,
    bitmap: Bitmap? = null
) {
  var imageUrl by remember { mutableStateOf<String?>(null) }
  var isImageRemoved by remember { mutableStateOf(false) }
  val context = LocalContext.current
  // Fetch the profile image URL from Firebase Storage
  LaunchedEffect(userId) {
    imageViewModel.fetchProfileImageUrl(
        userId = userId, onSuccess = { url -> imageUrl = url }, onFailure = { _ -> })
  }

  LaunchedEffect(bitmap) {
    if (bitmap != null && editing) {
      isImageRemoved = false
    } else if (bitmap == null && imageUrl == null && editing) {
      isImageRemoved = true
    }
  }
  LaunchedEffect(imageUrl) {
    if (imageUrl != null && editing) {
      isImageRemoved = false
    }
  }
  // Determine which painter to use
  val painter =
      when {
        editing && bitmap != null -> {
          // Use the bitmap provided when editing
          rememberAsyncImagePainter(model = ImageRequest.Builder(context).data(bitmap).build())
        }
        !imageUrl.isNullOrEmpty() -> {
          // Use the profile image URL from Firebase Storage, with caching via Coil
          rememberAsyncImagePainter(
              model =
                  ImageRequest.Builder(context).data(Uri.parse(imageUrl)).crossfade(true).build())
        }
        else -> {
          // Use the default profile image
          rememberAsyncImagePainter(
              model = ImageRequest.Builder(context).data(R.drawable.default_profile_image).build())
        }
      }

  Box {
    Image(
        painter = painter,
        contentDescription = "Profile Image",
        modifier = modifier,
        contentScale = ContentScale.Crop)
  }
}

fun getImageResourceIdForCategory(category: Category): Int {
  return when (category) {
    Category.SPORT -> R.drawable.sports_image
    Category.CULTURE -> R.drawable.culture_image
    Category.ENTERTAINMENT -> R.drawable.entertainment_image
    Category.SKILLS -> R.drawable.skills_image // Fallback image
  }
}
