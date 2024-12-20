package com.android.sample.ui.activitydetails

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.resources.C.Tag.LIGHT_BLUE
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import java.util.*

@Composable
fun ExportActivityToCalendarButton(
    viewModel: ListActivitiesViewModel,
    activity: Activity,
    context: Context = LocalContext.current
) {
  IconButton(
      onClick = {
        val calendarEvent = viewModel.prepareCalendarEvent(activity)

        if (calendarEvent != null) {
          exportToCalendar(context = context, calendarEvent = calendarEvent)
        }
      },
      modifier = Modifier.padding(MEDIUM_PADDING.dp)) {
        Icon(Icons.Default.DateRange, contentDescription = "Schedule", tint = Color(LIGHT_BLUE))
      }
}

fun exportToCalendar(context: Context, calendarEvent: ListActivitiesViewModel.CalendarEvent) {
  val activityTitle = calendarEvent.title
  val location = calendarEvent.location
  val description = calendarEvent.description
  val startMillis = calendarEvent.startMillis
  val endMillis = calendarEvent.endMillis

  if (activityTitle.isNotEmpty() && location.isNotEmpty() && description.isNotEmpty()) {
    val intent =
        Intent(Intent.ACTION_INSERT).apply {
          data = CalendarContract.Events.CONTENT_URI
          putExtra(CalendarContract.Events.TITLE, activityTitle)
          putExtra(CalendarContract.Events.EVENT_LOCATION, location)
          putExtra(CalendarContract.Events.DESCRIPTION, description)
          putExtra(CalendarContract.Events.ALL_DAY, false)
          putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
          putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
        }

    // Check if an app exists to handle the intent
    if (intent.resolveActivity(context.packageManager) != null) {
      context.startActivity(intent)
    } else {
      Toast.makeText(context, context.getString(R.string.no_calendar_app), Toast.LENGTH_SHORT)
          .show()
    }
  } else {
    Toast.makeText(
            context, context.getString(R.string.missing_data_export_calendar), Toast.LENGTH_SHORT)
        .show()
  }
}
