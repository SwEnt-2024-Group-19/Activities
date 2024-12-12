package com.android.sample.model.profile

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.sample.model.activity.Category
import com.android.sample.model.activity.categories

@Entity(tableName = "User")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val surname: String,
    val interests: List<Interest>?,
    val activities: List<String>?,
    val photo: String?, // Optional, could be null if not provided
    val likedActivities: List<String>? = emptyList()
)

data class Interest(val name: String, val category: Category)

val interestStringValues =
    mapOf(
        categories[0] to
            listOf( // Sport
                "Basketball",
                "Cycling",
                "Football",
                "Running",
                "Swimming",
                "Tennis",
                "Volleyball",
                "Yoga",
                "Other sports"),
        categories[1] to
            listOf( // Culture
                "Art", "History", "Science", "Travel", "Other culture"),
        categories[2] to
            listOf( // Skills
                "Cooking",
                "Dancing",
                "Programming",
                "Writing",
                "Drawing",
                "Photography",
                "Other skills"),
        categories[3] to
            listOf( // Entertainment
                "Movies", "Music", "Reading", "TV Shows", "Video Games", "Other entertainment"))

val interestsCategories = interestStringValues.keys.toList()
val categoryOf =
    interestStringValues
        .flatMap { (category, interests) -> interests.map { it to category } }
        .toMap()
