package com.android.sample.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.WIDTH_FRACTION_SM
import com.android.sample.ui.components.TextFieldWithErrorState

/**
 * Composable function to display the dialog to add a user.
 *
 * @param onDismiss The callback to dismiss the dialog.
 * @param onAddUser The callback to add the user.
 */
@Composable
fun AddUserDialog(onDismiss: () -> Unit, onAddUser: (User) -> Unit) {
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
                    .background(
                        color = Color(MAIN_BACKGROUND),
                        shape = RoundedCornerShape(size = ROUNDED_CORNER_SHAPE_DEFAULT.dp))
                    .testTag("addUserDialog"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          var name by remember { mutableStateOf("") }
          var surname by remember { mutableStateOf("") }
          Text(
              text = "Add a user",
              style = MaterialTheme.typography.titleLarge,
              modifier = Modifier.testTag("addUserDialogTitle"),
          )
          TextFieldWithErrorState(
              modifier = Modifier.fillMaxWidth(WIDTH_FRACTION_SM),
              value = name,
              onValueChange = { name = it },
              label = "Name",
              validation = { nameAttendee ->
                if (nameAttendee.isEmpty()) {
                  "Name cannot be empty"
                } else {
                  null
                }
              },
              testTag = "nameTextFieldUser",
              errorTestTag = "nameErrorUser")

          Spacer(modifier = Modifier.padding(SMALL_PADDING.dp))
          TextFieldWithErrorState(
              modifier = Modifier.fillMaxWidth(WIDTH_FRACTION_SM),
              value = surname,
              onValueChange = { surname = it },
              label = "Surname",
              validation = { null },
              testTag = "surnameTextFieldUser",
              errorTestTag = "surnameErrorUser")
          TextButton(
              modifier =
                  Modifier.testTag("addUserButton").fillMaxWidth().padding(MEDIUM_PADDING.dp),
              onClick = {
                onAddUser(
                    User(
                        name = name,
                        surname = surname,
                        id = "",
                        interests = listOf(),
                        activities = listOf(),
                        photo = null,
                        likedActivities = listOf(),
                    ))
                onDismiss()
              }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add user",
                    tint = Color(MAIN_COLOR_DARK),
                )
                Text(
                    "Add user",
                    color = Color(MAIN_COLOR_DARK),
                )
              }
        }
      }
}
