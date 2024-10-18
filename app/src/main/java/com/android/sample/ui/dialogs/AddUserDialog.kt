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

data class SimpleUser(
    val name: String,
    val surname: String,
    val age: Int,
)

@Composable
fun AddUserDialog(onDismiss: () -> Unit, onAddUser: (SimpleUser) -> Unit) {
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
                    .background(color = Color.White, shape = RoundedCornerShape(size = 16.dp))
                    .testTag("addUserDialog"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          var name by remember { mutableStateOf("") }
          var surname by remember { mutableStateOf("") }
          var age by remember { mutableStateOf("") }

          OutlinedTextField(
              modifier = Modifier.testTag("nameTextFieldUser"),
              value = name,
              onValueChange = { name = it },
              label = { Text("Name") },
          )

          OutlinedTextField(
              modifier = Modifier.testTag("surnameTextFieldUser"),
              value = surname,
              onValueChange = { surname = it },
              label = { Text("Surname") },
          )
          OutlinedTextField(
              modifier = Modifier.testTag("ageTextFieldUser"),
              value = age,
              onValueChange = { age = it },
              label = { Text("Age") },
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
              modifier = Modifier.testTag("addUserButton"),
              onClick = {
                val userAge =
                    try {
                      age.toInt()
                    } catch (e: NumberFormatException) {
                      0
                    }
                onAddUser(SimpleUser(name, surname, userAge))
                onDismiss()
              }) {
                Text("Add user")
              }
        }
      }
}
