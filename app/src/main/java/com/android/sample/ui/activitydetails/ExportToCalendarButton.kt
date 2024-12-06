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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.sample.model.activity.Activity
import com.android.sample.resources.dummydata.activity
import java.util.*

@Composable
fun ExportActivityToCalendarButton(activity: Activity) {
  val context = LocalContext.current

  Button(
      onClick = {
        val calendar = Calendar.getInstance()
        // Assuming the activity's start time is a proper timestamp
        calendar.time = activity.date.toDate()

        val startMillis = calendar.timeInMillis

        // For the end time, we assume the `duration` field is in minutes.
        calendar.add(Calendar.MINUTE, activity.duration.toInt())
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
  val intent =
      Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, activityTitle)
        putExtra(CalendarContract.Events.DESCRIPTION, description)
        putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
        putExtra(
            CalendarContract.Events.AVAILABILITY,
            CalendarContract.Events.AVAILABILITY_BUSY) // Optional: Set availability
      }

  // Check if there is an app that can handle this intent
  if (intent.resolveActivity(context.packageManager) != null) {
    context.startActivity(intent)
  } else {
    Toast.makeText(context, "No calendar app available", Toast.LENGTH_SHORT).show()
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewExportButton() {
  ExportActivityToCalendarButton(activity)
}
