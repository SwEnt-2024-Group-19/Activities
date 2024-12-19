package com.android.sample.ui.components

import androidx.compose.runtime.Composable
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime

/**
 * Composable function to display a time picker dialog.
 *
 * @param onTimeSelected The callback to handle the selected time.
 * @param isOpen The state of the dialog.
 * @param onCloseRequest The callback to handle the dialog close request.
 * @param isAmPm The flag to show the time picker in 12-hour format.
 */
@Composable
fun MyTimePicker(
    onTimeSelected: (Timestamp) -> Unit,
    isOpen: Boolean,
    onCloseRequest: (MaterialDialogState) -> Unit,
    isAmPm: Boolean = true
) {
  val dialogState = rememberMaterialDialogState(isOpen)
  MaterialDialog(
      onCloseRequest = onCloseRequest,
      dialogState = dialogState,
      buttons = {
        positiveButton(text = "Ok")
        negativeButton(text = "Cancel") { onCloseRequest(dialogState) }
      }) {
        timepicker(
            initialTime = LocalTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES),
            title = "Pick a time",
            is24HourClock = !isAmPm) {
              onTimeSelected(
                  Timestamp(
                      LocalDate.now()
                          .atTime(it)
                          .atZone(java.time.ZoneId.systemDefault())
                          .toInstant()))
            }
      }
}
