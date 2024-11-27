package com.android.sample.model.hour_date

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

open class HourDateViewModel : ViewModel() {

    @SuppressLint("DefaultLocale")
    fun calculateDuration(start: String, end: String): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val startTime = LocalTime.parse(start, formatter)
        val endTime = LocalTime.parse(end, formatter)

        val duration = if (endTime.isAfter(startTime)) {
            Duration.between(startTime, endTime)
        } else {
            Duration.between(startTime, endTime.plusHours(24))
        }

        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        return String.format("%02d:%02d", hours, minutes)
    }

    fun isBeginGreaterThanEnd(start: String, end: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val startTime = LocalTime.parse(start, formatter)
        val endTime = LocalTime.parse(end, formatter)
        return endTime.isAfter(startTime)
    }
}