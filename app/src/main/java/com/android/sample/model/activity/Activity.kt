package com.android.sample.model.activity

import com.android.sample.ui.dialogs.SimpleUser
import com.google.firebase.Timestamp

data class Activity(
    val uid: String,
    var title: String,
    var description: String,
    var date: Timestamp,
    var startTime: String,
    var duration: String,
    // var category: Category,
    var price: Double,
    var location: String,
    var creator: String,
    var images: List<String>,
    var placesLeft: Long,
    var maxPlaces: Long,
    var status: ActivityStatus,
    val type: ActivityType,
    var participants: List<SimpleUser>
)

enum class ActivityType {
  PRO,
  INDIVIDUAL,
  SOLO,
}

val types =
    listOf(
        ActivityType.PRO,
        ActivityType.INDIVIDUAL,
        ActivityType.SOLO,
    )

enum class ActivityStatus {
  ACTIVE,
  FINISHED,
}

// enum class Category {
//  WORKSHOP,
//  TALK,
//  KEYNOTE,
//  BREAK,
//  LUNCH,
//  NETWORKING,
//  SOCIAL,
//  OTHER,
// }
//
// val categories =
//    listOf(
//        Category.WORKSHOP,
//        Category.TALK,
//        Category.KEYNOTE,
//        Category.BREAK,
//        Category.LUNCH,
//        Category.NETWORKING,
//        Category.SOCIAL,
//        Category.OTHER,
//    )

// Setup later
// @Composable
// fun CategoryDropdown() {
//  var selectedCategory = remember { mutableStateOf<Category?>(null) }
//  var open = remember { mutableStateOf(false) }
//  Column {
//    OutlinedTextField(
//        value = CategoryToString(selectedCategory),
//        onValueChange = { open.value = true },
//        label = { Text("Enter Location") },
//        modifier = Modifier.fillMaxWidth().testTag("inputTodoLocation").padding(16.dp),
//    )
//
//    DropdownMenu(
//        expanded = open.value,
//        properties = PopupProperties(focusable = false),
//        onDismissRequest = { open.value = false },
//        modifier = Modifier.fillMaxWidth().padding(16.dp)) {
//          categories.forEach { cat ->
//            DropdownMenuItem(
//                onClick = {
//                  selectedCategory = mutableStateOf<Category?>(cat)
//                  open.value = false
//                },
//                text = { Text("") },
//            )
//          }
//        }
//  }
// }
//
// fun CategoryToString(category: MutableState<Category?>): String {
//
//  return when (category.value) {
//    Category.WORKSHOP -> "Workshop"
//    Category.TALK -> "Talk"
//    Category.KEYNOTE -> "Keynote"
//    Category.BREAK -> "Break"
//    Category.LUNCH -> "Lunch"
//    Category.NETWORKING -> "Networking"
//    Category.SOCIAL -> "Social"
//    Category.OTHER -> "Other"
//    null -> ""
//  }
// }
