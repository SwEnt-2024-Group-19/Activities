package com.android.sample.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SimpleUser(
    val name: String,
    val surname: String,
    val age: Int,
)

@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onAddUser: (SimpleUser) -> Unit,
) {
  Column(
      modifier = Modifier.fillMaxSize().padding(50.dp),
  ) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    TextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Name") },
    )

    TextField(
        value = surname,
        onValueChange = { surname = it },
        label = { Text("Surname") },
    )

    TextField(
        value = age,
        onValueChange = { age = it },
        label = { Text("Age") },
    )

    Button(
        onClick = {
          onAddUser(SimpleUser(name, surname, age.toInt()))
          onDismiss()
        }) {
          Text("Add user")
        }
  }
}
