package com.android.sample.model.hour_date

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

open class HourDateViewModel : ViewModel() {

  @SuppressLint("DefaultLocale")
  fun calculateDuration(start: String, end: String): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = LocalTime.parse(start, formatter)
    val endTime = LocalTime.parse(end, formatter)

    val duration =
        if (endTime.isAfter(startTime)) {
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

  fun addDurationToTime(start: String, duration: String): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = LocalTime.parse(start, formatter)
    val durationParts = duration.split(":")
    val hoursToAdd = durationParts[0].toLong()
    val minutesToAdd = durationParts[1].toLong()

    val resultTime = startTime.plusHours(hoursToAdd).plusMinutes(minutesToAdd)
    return resultTime.format(formatter)
  }

  fun combineDateAndTime(date: Timestamp, time: String): Timestamp {
    // Convert Firebase Timestamp to LocalDate
    try {
      // Convert Firebase Timestamp to LocalDate
      val localDate = date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

      val formatter = DateTimeFormatter.ofPattern("HH:mm")
      val localTime = LocalTime.parse(time, formatter) // Parse the time using the provided format

      // Combine LocalDate and LocalTime to LocalDateTime
      val combinedDateTime = LocalDateTime.of(localDate, localTime)

      // Convert back to Firebase Timestamp
      return Timestamp(
          combinedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000, 0)
    } catch (e: DateTimeParseException) {
      // Log the error or handle it as needed
      println("Failed to parse time, error: ${e.message}")
      val defaultDateTime = LocalDateTime.now()
      return Timestamp(
          defaultDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000,
          0) // Or return a default Timestamp if that fits your use case
    }
  }
}
