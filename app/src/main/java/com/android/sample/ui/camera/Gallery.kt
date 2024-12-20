package com.android.sample.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import kotlinx.coroutines.delay

/**
 * Displays a gallery screen that allows users to select an image from their device's media store.
 *
 * This composable function utilizes the `ActivityResultContracts.GetContent` contract to open the
 * system's file picker, specifically filtered for image types. Users can select an image, which is
 * then converted to a Bitmap and handled via the provided callback. If no image is selected, or the
 * operation is cancelled, it triggers a closure callback to manage the UI state.
 *
 * @param isGalleryOpen A callback function that is invoked to toggle the visibility state of the
 *   gallery UI, typically to close the gallery screen.
 * @param addImage A callback function that handles the Bitmap image selected by the user. This
 *   function is called with the Bitmap as its argument.
 * @param context The context from which resources, such as the content resolver, are accessed.
 *   Needed for operations like fetching the bitmap from the media store.
 */
@Composable
fun GalleryScreen(isGalleryOpen: () -> Unit, addImage: (Bitmap) -> Unit, context: Context) {
  val launcher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri?
        ->
        if (uri != null) {
          val bitmap = uriToBitmap(uri, context)
          if (bitmap != null) {
            addImage(bitmap)
            isGalleryOpen() // Close the gallery view
          }
        } else {
          isGalleryOpen() // Close the gallery view if no image is selected
        }
      }

  // Relaunch the image picker whenever GalleryScreen is recomposed
  // which should typically be controlled by the state in the parent view
  DisposableEffect(Unit) {
    launcher.launch("image/*")
    onDispose {}
  }
}
/**
 * Displays a carousel dialog allowing users to select a default image from a predefined set.
 *
 * This composable function opens a dialog with a horizontally scrollable list (carousel) of images.
 * Each image can be selected, resulting in the image being converted to a Bitmap and returned
 * through the `onImageSelected` callback. The dialog is dismissed after an image is selected.
 *
 * @param onImageSelected A callback function that is invoked with the selected image as a Bitmap.
 *   This function is called when a user taps on an image in the carousel.
 * @param context The context used to access resources, necessary for decoding drawable resources
 *   into Bitmaps.
 * @param onDismiss A callback function that is invoked when the dialog is dismissed, either through
 *   an image selection or an external dismiss action.
 */
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
        modifier =
            Modifier.width((2 * CARD_IAMGES_SIZE).dp)
                .padding(MEDIUM_PADDING.dp)
                .testTag("DefaultImageCarousel")) {
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
/**
 * A composable function that displays an image wrapped in a stylized card.
 *
 * This function creates a clickable image card using a specified drawable resource. It is designed
 * to be a reusable component where the image acts as a button or a selectable item in the UI. The
 * card is circular and fills its content, making it suitable for profile images or other circular
 * icons.
 *
 * @param imageRes The resource identifier of the drawable image to display inside the card.
 * @param onClick A lambda function that is called when the image card is clicked. This allows the
 *   card to act interactively within the UI.
 */
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
/**
 * Displays a profile image with support for editing and automatic fetching from Firebase Storage.
 *
 * This composable function renders an image either from a provided bitmap or fetches it from
 * Firebase based on the user's ID. It supports an editing mode where a temporary bitmap can be used
 * instead of the fetched image. The image URL is also cached in SharedPreferences to optimize load
 * times and reduce network calls.
 *
 * @param userId The unique identifier for the user, used to fetch and cache the user's profile
 *   image URL.
 * @param modifier Modifier for styling and layout tweaks applied to the image component.
 * @param imageViewModel ViewModel that provides image fetching logic from Firebase Storage.
 * @param editing Boolean flag indicating if the image is in editing mode, which allows replacing
 *   the fetched image with a temporary bitmap.
 * @param bitmap Optional bitmap that can be displayed instead of the fetched image during editing.
 *
 * This function first attempts to retrieve a cached image URL from SharedPreferences. If available
 * and valid, it uses this URL to load the image. If not in editing mode or no temporary bitmap is
 * provided, it fetches the latest image URL from Firebase, updates the cache, and displays the
 * image. It includes a minimum delay before fetching the image to account for potential latency in
 * Firebase updates.
 */
@Composable
fun ProfileImage(
    userId: String,
    modifier: Modifier = Modifier,
    imageViewModel: ImageViewModel,
    editing: Boolean = false,
    bitmap: Bitmap? = null
) {
  val minDelay = 1500L // determined empirically, the smallest delay before the image can be fetched
  val sharedPreferences = LocalContext.current.getSharedPreferences(userId, Context.MODE_PRIVATE)
  var imageUrl by remember { mutableStateOf(sharedPreferences.getString(userId, null)) }
  val context = LocalContext.current

  // Fetch the profile image URL from Firebase Storage
  LaunchedEffect(userId) {
    delay(
        minDelay) // Delay to prevent fetching the image URL before the storage is actually updated
    imageViewModel.fetchProfileImageUrl(
        userId = userId,
        onSuccess = { url ->
          imageUrl = url
          sharedPreferences.edit().putString(userId, url).apply() // Cache the image URL
        },
        onFailure = { error ->
          Log.e("ProfileImage", "Failed to fetch image URL: ${error.message}")
        })
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

  Image(
      painter = painter,
      contentDescription = "Profile Image",
      modifier = modifier,
      contentScale = ContentScale.Crop)
}

/**
 * Retrieves the drawable resource ID associated with a specific category.
 *
 * This function maps each category of an activity to a corresponding drawable resource that
 * visually represents the category. It is useful for dynamically setting image resources in the UI
 * based on the category of the content being displayed.
 *
 * @param category The category for which the image resource ID needs to be fetched.
 * @return Returns an integer representing the drawable resource ID associated with the given
 *   category.
 */
fun getImageResourceIdForCategory(category: Category): Int {
  return when (category) {
    Category.SPORT -> R.drawable.sports_image
    Category.CULTURE -> R.drawable.culture_image
    Category.ENTERTAINMENT -> R.drawable.entertainment_image
    Category.SKILLS -> R.drawable.skills_image // Fallback image
  }
}
