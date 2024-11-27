package com.android.sample.model.activity

import com.android.sample.model.map.Location
import com.android.sample.model.profile.User
import com.google.firebase.Timestamp

data class Activity(
    val uid: String,
    var title: String,
    var description: String,
    var date: Timestamp,
    var startTime: String,
    var duration: String,
    var category: Category=Category.SPORT,
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

 enum class Category {
     SPORT,
     CULTURE,
        SKILLS,
     ENTERTAINMENT,
 }

 val categories = Category.values().toList()

