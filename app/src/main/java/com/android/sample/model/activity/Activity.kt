package com.android.sample.model.activity


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import coil3.Bitmap
import com.android.sample.ui.dialogs.SimpleUser
import com.google.firebase.Timestamp

data class Activity(
    val uid: String,
    var title: String,
    var description: String,
    var date: Timestamp,
    // var category: Category,
    var price: Double,
    var location: String,
    var creator: String,
    var images: List<String>,
    var placesLeft: Long,
    var maxPlaces: Long,
    var status: ActivityStatus,
    var participants:List<SimpleUser>
)

enum class ActivityStatus {
  ACTIVE,
  FINISHED,
}

enum class Category {
  WORKSHOP,
  TALK,
  KEYNOTE,
  BREAK,
  LUNCH,
  NETWORKING,
  SOCIAL,
  OTHER,
}

val categories =
    listOf(
        Category.WORKSHOP,
        Category.TALK,
        Category.KEYNOTE,
        Category.BREAK,
        Category.LUNCH,
        Category.NETWORKING,
        Category.SOCIAL,
        Category.OTHER,
    )

@Composable
fun CategoryDropdown() {
  var selectedCategory = remember { mutableStateOf<Category?>(null) }
  var open = remember { mutableStateOf(false) }
  Column {
    OutlinedTextField(
        value = CategoryToString(selectedCategory),
        onValueChange = { open.value = true },
        label = { Text("Enter Location") },
        modifier = Modifier.fillMaxWidth().testTag("inputTodoLocation").padding(16.dp),
    )

    DropdownMenu(
        expanded = open.value,
        properties = PopupProperties(focusable = false),
        onDismissRequest = { open.value = false },
        modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          categories.forEach { cat ->
            DropdownMenuItem(
                onClick = {
                  selectedCategory = mutableStateOf<Category?>(cat)
                  open.value = false
                },
                text = { Text("") },
            )
          }
        }
  }
}

fun CategoryToString(category: MutableState<Category?>): String {

  return when (category.value) {
    Category.WORKSHOP -> "Workshop"
    Category.TALK -> "Talk"
    Category.KEYNOTE -> "Keynote"
    Category.BREAK -> "Break"
    Category.LUNCH -> "Lunch"
    Category.NETWORKING -> "Networking"
    Category.SOCIAL -> "Social"
    Category.OTHER -> "Other"
    null -> ""
  }
}
