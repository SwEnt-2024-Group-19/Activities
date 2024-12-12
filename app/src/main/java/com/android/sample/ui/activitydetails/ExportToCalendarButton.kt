package com.android.sample.ui.activitydetails

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.sample.model.activity.Activity
import java.util.*

@Composable
fun ExportActivityToCalendarButton(activity: Activity, context: Context = LocalContext.current) {
  Button(
      onClick = {
        val calendar = Calendar.getInstance()
        // Assuming the activity's start time is a proper timestamp
        calendar.time = activity.date.toDate()

        val startMillis = calendar.timeInMillis

        // For the end time, we assume the `duration` field is in the format HH:mm (e.g., "02:00").
        val durationParts = activity.duration.split(":")
        val hours = durationParts[0].toInt()
        val minutes = durationParts[1].toInt()

        calendar.add(Calendar.HOUR, hours)
        calendar.add(Calendar.MINUTE, minutes)
        val endMillis = calendar.timeInMillis

        exportToCalendar(
            context = context,
            activityTitle = activity.title,
            description = activity.description,
            location = activity.location?.name ?: "Unknown Location",
            startMillis = startMillis,
            endMillis = endMillis)
      },
      modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Export to Calendar")
      }
}

fun exportToCalendar(
    context: Context,
    activityTitle: String,
    description: String,
    location: String,
    startMillis: Long,
    endMillis: Long
) {
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
          putExtra(
              CalendarContract.Events.EVENT_TIMEZONE,
              "Europe/Zurich") // timezone is fixed, should be dynamic
        }

    // Check if an app exists to handle the intent
    if (intent.resolveActivity(context.packageManager) != null) {
      context.startActivity(intent)
    } else {
      Toast.makeText(context, "No apps available to handle calendar events", Toast.LENGTH_SHORT)
          .show()
    }
  } else {
    Toast.makeText(
            context,
            "This activity is missing data. Unable to export to calendar.",
            Toast.LENGTH_SHORT)
        .show()
  }
}
