package com.android.sample.model.profile

data class User(
    val id: String,
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
