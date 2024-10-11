package com.android.sample.model

data class Activity(
    val uid: String,
    val name: String,
    val description: String,
    val organizerName: String,
    val date: String,
    val location: String,
    val price: Double,
    val status: String // e.g., Open, Closed, Cancelled
)
