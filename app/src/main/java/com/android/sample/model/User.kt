package com.android.sample.model

data class User(
    val id: String,
    val name: String,
    val surname: String,
    val interests: List<String>?,
    val activities: List<String>?,
    val photo: String? // Optional, could be null if not provided
)
