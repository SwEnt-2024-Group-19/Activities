package com.android.sample.model.hour_date

import java.time.format.DateTimeParseException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HourDateViewModelTest {
  private val viewModel = HourDateViewModel()

  @Test
  fun calculateDuration_returnsCorrectDuration_whenEndTimeIsAfterStartTime() {
    val result = viewModel.calculateDuration("08:00", "10:30")
    assertEquals("02:30", result)
  }

  @Test
  fun calculateDuration_returnsZeroDuration_whenStartTimeEqualsEndTime() {
    val result = viewModel.calculateDuration("12:00", "12:00")
    assertEquals("00:00", result)
  }

  @Test(expected = DateTimeParseException::class)
  fun calculateDuration_throwsException_whenInvalidTimeFormat() {
    viewModel.calculateDuration("invalid", "10:00")
  }

  @Test
  fun isBeginGreaterThanEnd_returnsTrue_whenStartTimeIsAfterEndTime() {
    val result = viewModel.isBeginGreaterThanEnd("18:00", "08:00")
    assertFalse(result)
  }

  @Test
  fun isBeginGreaterThanEnd_returnsFalse_whenStartTimeIsBeforeEndTime() {
    val result = viewModel.isBeginGreaterThanEnd("08:00", "18:00")
    assertTrue(result)
  }

  @Test
  fun isBeginGreaterThanEnd_returnsFalse_whenStartTimeEqualsEndTime() {
    val result = viewModel.isBeginGreaterThanEnd("12:00", "12:00")
    assertFalse(result)
  }

  @Test
  fun addDurationToTime_returnsCorrectTime_whenDurationIsAdded() {
    val result = viewModel.addDurationToTime("08:00", "02:30")
    assertEquals("10:30", result)
  }

  @Test
  fun addDurationToTime_returnsCorrectTime_whenDurationCrossesMidnight() {
    val result = viewModel.addDurationToTime("23:00", "02:30")
    assertEquals("01:30", result)
  }

  @Test
  fun addDurationToTime_returnsCorrectTime_whenDurationIsZero() {
    val result = viewModel.addDurationToTime("12:00", "00:00")
    assertEquals("12:00", result)
  }
}
