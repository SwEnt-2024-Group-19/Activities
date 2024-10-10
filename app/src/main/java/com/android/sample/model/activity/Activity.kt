package com.android.sample.model.activity

import com.google.firebase.Timestamp

// Data class representing a to-do item
data class Activity(
    val uid: String,
    val name: String,
    val description: String,
    val date: Timestamp,
    val location: String,
    val organizerName: String,
    val image: Long,
    val placesLeft: Long,
    val maxPlaces: Long
) {}
