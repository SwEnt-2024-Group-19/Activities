package com.android.sample.model.activity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.sample.model.map.Location
import com.android.sample.model.profile.User
import com.google.firebase.Timestamp

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey val uid: String,
    var title: String,
    var description: String,
    var date: Timestamp,
    var startTime: String,
    var duration: String,
    // var category: Category,
    var price: Double,
    var location: Location?,
    var creator: String,
    var images: List<String>,
    var placesLeft: Long,
    var maxPlaces: Long,
    var status: ActivityStatus,
    val type: ActivityType,
    var participants: List<User>,
    var comments: List<Comment> = emptyList()
)

data class Comment(
    val uid: String,
    val userId: String,
    val userName: String,
    val content: String,
    val timestamp: Timestamp,
    var replies: List<Comment> = emptyList()
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
