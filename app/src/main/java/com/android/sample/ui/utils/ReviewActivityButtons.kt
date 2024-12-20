package com.android.sample.ui.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SUCCESS_COLOR

@Composable
fun ReviewActivityButtons(currentReview: Boolean?, review: (Boolean?) -> Unit) {
  var isLiked: Boolean? by remember { mutableStateOf(currentReview) }
  Row {
    IconButton(
        onClick = {
          isLiked = if (isLiked == true) null else true
          review(isLiked)
        },
        colors =
            IconButtonDefaults.iconButtonColors(
                containerColor = if (isLiked == true) Color(SUCCESS_COLOR) else Color.Transparent,
                contentColor =
                    if (isLiked == true) MaterialTheme.colorScheme.onError
                    else MaterialTheme.colorScheme.onSurface),
        modifier = Modifier.testTag("likeIconButton_${isLiked == true}")) {
          Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "Like")
        }
    Spacer(modifier = Modifier.width(MEDIUM_PADDING.dp))
    IconButton(
        onClick = {
          isLiked = if (isLiked == false) null else false
          review(isLiked)
        },
        colors =
            IconButtonDefaults.iconButtonColors(
                containerColor =
                    if (isLiked == false) MaterialTheme.colorScheme.error else Color.Transparent,
                contentColor =
                    if (isLiked == false) MaterialTheme.colorScheme.onError
                    else MaterialTheme.colorScheme.onSurface),
        modifier = Modifier.testTag("dislikeIconButton_${isLiked == false}")) {
          Icon(
              imageVector = Icons.Default.ThumbDown,
              contentDescription = "Dislike",
              tint =
                  if (isLiked == false) MaterialTheme.colorScheme.onError
                  else MaterialTheme.colorScheme.onSurface)
        }
  }
}
