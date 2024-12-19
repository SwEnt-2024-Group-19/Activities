package com.android.sample.model.activities

import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.resources.dummydata.activity1
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock

class ActivityDetailsViewModelTest {

  private val viewModel = ListActivitiesViewModel(mock(), mock())

  @Test
  fun `prepareCalendarEvent returns valid CalendarEvent`() {
    val calendarEvent = viewModel.prepareCalendarEvent(activity1)

    assertNotNull(calendarEvent)
    assertEquals(activity1.title, calendarEvent?.title)
    assertEquals(activity1.location?.name, calendarEvent?.location)
    assertEquals(activity1.description, calendarEvent?.description)
  }

  @Test
  fun `prepareCalendarEvent returns null for invalid duration`() {
    val invalidActivity = activity1.apply { duration = "invalid_duration" }
    val calendarEvent = viewModel.prepareCalendarEvent(invalidActivity)

    assertNull(calendarEvent)
  }
}
