package com.android.sample.model.activity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.sample.model.map.Location
import com.android.sample.model.profile.User
import com.android.sample.resources.C.Tag.CULTURE_COLOR
import com.android.sample.resources.C.Tag.ENTERTAINMENT_COLOR
import com.android.sample.resources.C.Tag.SKILLS_COLOR
import com.android.sample.resources.C.Tag.SPORT_COLOR
import com.google.firebase.Timestamp

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey val uid: String,
    var title: String,
    var description: String,
    var date: Timestamp,
    var startTime: String,
    var duration: String,
    var category: Category = Category.SPORT,
    var subcategory: String = "None",
    var price: Double,
    var location: Location?,
    var creator: String,
    var images: List<String>,
    var placesLeft: Long,
    var maxPlaces: Long,
    var status: ActivityStatus,
    val type: ActivityType,
    var participants: List<User>,
    var comments: List<Comment> = emptyList(),
    var likes: Map<String, Boolean> = emptyMap()
) {
  // Function to calculate the normalized rating for the activity
  fun getActivityRating(): Double {
    val totalVotes = likes.size
    val likeCount = likes.values.count { it }

    return if (totalVotes > 0) likeCount.toDouble() / totalVotes else -1.0
  }

  /**
   * Normalized weighted rating of the activity based on the number of participants, percentage of
   * completion, and percentage of participants who liked/disliked the activity.
   */
  fun getActivityWeightedRating(): Double {
    val rating = getActivityRating()
    val completionRatio = 1 - placesLeft.toDouble() / maxPlaces
    val likesCompletionRatio =
        if (participants.isNotEmpty()) likes.size.toDouble() / participants.size else -1.0

    val ratingWeight =
        if (rating >= 0) 0.5
        else 0.0 // does not penalize if no likes because it's assumed to not be the creator's fault
    val completionRatioWeight = 0.25
    val likesCompletionRatioWeight =
        if (likesCompletionRatio >= 0) 0.25
        else 0.0 // does not penalize if no likes because it's assumed to not be the creator's fault
    val totalWeight = ratingWeight + completionRatioWeight + likesCompletionRatioWeight

    return (rating * ratingWeight +
        completionRatio * completionRatioWeight +
        likesCompletionRatio * likesCompletionRatioWeight) / totalWeight
  }
}

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
}

val types = ActivityType.values().toList()

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

val CategoryColorMap =
    mapOf(
        Category.SPORT to SPORT_COLOR,
        Category.CULTURE to CULTURE_COLOR,
        Category.SKILLS to SKILLS_COLOR,
        Category.ENTERTAINMENT to ENTERTAINMENT_COLOR,
    )
