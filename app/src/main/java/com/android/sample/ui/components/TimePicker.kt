package com.android.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND_BUTTON
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.android.sample.resources.C.Tag.MAIN_COLOR_LIGHT
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
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
        positiveButton(
            text = "Ok",
            textStyle = TextStyle(color = Color(MAIN_COLOR_DARK), fontWeight = FontWeight.Bold))
          negativeButton(
            text = "Cancel",
            textStyle = TextStyle(color = Color(MAIN_COLOR_DARK), fontWeight = FontWeight.Bold)){
              onCloseRequest(dialogState)
          }
      },
  ) {
    timepicker(
        initialTime = LocalTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES),
        title = "Pick a time",
        is24HourClock = !isAmPm,
        colors =
            TimePickerDefaults.colors(
                headerTextColor = Color(MAIN_COLOR_DARK),
                inactiveBackgroundColor = Color(MAIN_BACKGROUND_BUTTON),
                activeBackgroundColor = Color(MAIN_COLOR_DARK),
                activeTextColor = Color.White,
                inactiveTextColor = Color(MAIN_COLOR_DARK),
                selectorColor = Color(MAIN_COLOR_LIGHT),
            )) {
          onTimeSelected(
              Timestamp(
                  LocalDate.now().atTime(it).atZone(java.time.ZoneId.systemDefault()).toInstant()))
        }
  }
}
