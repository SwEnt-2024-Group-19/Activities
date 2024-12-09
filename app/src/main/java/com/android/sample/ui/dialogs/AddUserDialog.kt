package com.android.sample.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.android.sample.resources.C.Tag.MEDIUM_PADDING

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
                    .height(300.dp)
                    .background(
                        color = Color.White, shape = RoundedCornerShape(size = MEDIUM_PADDING.dp))
                    .testTag("addUserDialog"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          var name by remember { mutableStateOf("") }
          var surname by remember { mutableStateOf("") }

          OutlinedTextField(
              modifier = Modifier.testTag("nameTextFieldUser"),
              value = name,
              onValueChange = { name = it },
              label = { Text("Name") },
              singleLine = true,
          )

          OutlinedTextField(
              modifier = Modifier.testTag("surnameTextFieldUser"),
              value = surname,
              onValueChange = { surname = it },
              label = { Text("Surname") },
              singleLine = true,
          )
          Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
          Button(
              modifier = Modifier.testTag("addUserButton"),
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
                Text("Add user")
              }
        }
      }
}
