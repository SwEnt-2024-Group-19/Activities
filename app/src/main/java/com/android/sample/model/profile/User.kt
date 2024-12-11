package com.android.sample.model.profile

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.sample.resources.C.Tag.ART_ACTIVITY_COLOR
import com.android.sample.resources.C.Tag.CULTURAL_ACTIVITY_COLOR
import com.android.sample.resources.C.Tag.INDOOR_COLOR
import com.android.sample.resources.C.Tag.MUSICAL_ACTIVITY_COLOR
import com.android.sample.resources.C.Tag.OUTDOOR_COLOR
import com.android.sample.resources.C.Tag.SPORT_COLOR

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

data class Interest(val category: String, val interest: String)

val InterestCategories =
    listOf(
        "Sport",
        "Outdoor Activity",
        "Indoor Activity",
        "Cultural Activity",
        "Art Activity",
        "Musical Activity") + "None"
val InterestCategoriesColors =
    mapOf(
        "Sport" to SPORT_COLOR,
        "Outdoor Activity" to OUTDOOR_COLOR,
        "Indoor Activity" to INDOOR_COLOR,
        "Cultural Activity" to CULTURAL_ACTIVITY_COLOR,
        "Art Activity" to ART_ACTIVITY_COLOR,
        "Musical Activity" to MUSICAL_ACTIVITY_COLOR,
        "None" to Color.Gray)
