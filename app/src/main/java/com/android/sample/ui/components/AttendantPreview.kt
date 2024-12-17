package com.android.sample.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.LINE_STROKE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_IMAGE_SIZE
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.ui.camera.ProfileImage

/**
 * Composable function to display the preview of the attendant. Once clicked, it navigates to the
 * profile of the user.
 *
 * @param user The user to be displayed.
 * @param onProfileClick The callback to handle the click on the profile.
 * @param imageViewModel The ImageViewModel to handle the image operations.
 * @param deleteAttendant The callback to delete the attendant.
 * @param index The index of the attendant.
 */
@Composable
fun AttendantPreview(
    user: User,
    onProfileClick: (User) -> Unit,
    imageViewModel: ImageViewModel,
    deleteAttendant: (User) -> Unit,
    index: Int
) {
  Row(
      modifier =
          Modifier.clickable { onProfileClick(user) }
              .padding(MEDIUM_PADDING.dp)
              .border(LINE_STROKE.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
              .testTag("attendeeRow${index}")) {
        if (user.id == "") {
          // Guest users have no id
          Icon(
              Icons.Default.Person,
              contentDescription = "Person",
              modifier = Modifier.align(Alignment.CenterVertically))
        } else {
          ProfileImage(
              userId = user.id,
              modifier = Modifier.padding(end = MEDIUM_PADDING.dp).width(SMALL_IMAGE_SIZE.dp),
              imageViewModel = imageViewModel)
        }
        Spacer(modifier = Modifier.width(MEDIUM_PADDING.dp))

        Text(
            text = "${user.name} ${user.surname}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("attendeeName${index}").align(Alignment.CenterVertically))

        Spacer(modifier = Modifier.width(STANDARD_PADDING.dp))
        IconButton(
            onClick = { deleteAttendant(user) },
            modifier = Modifier.padding(STANDARD_PADDING.dp).testTag("removeAttendeeButton")) {
              Icon(Icons.Default.PersonRemove, contentDescription = "Delete")
            }
      }
}
