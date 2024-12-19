package com.android.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND_BUTTON
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Composable function to display a date picker dialog.
 *
 * @param onDateSelected The callback to handle the selected date.
 * @param isOpen The state of the dialog.
 * @param initialDate The initial date to be displayed.
 * @param onCloseRequest The callback to handle the dialog close request.
 */
@Composable
fun MyDatePicker(
    onDateSelected: (Timestamp) -> Unit,
    isOpen: Boolean,
    initialDate: LocalDate?,
    onCloseRequest: (MaterialDialogState) -> Unit
) {
  MaterialDialog(
      onCloseRequest = onCloseRequest,
      dialogState = rememberMaterialDialogState(isOpen),
      properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true),
      buttons = {
        positiveButton(
            text = "Ok",
            textStyle = TextStyle(color = Color(MAIN_COLOR_DARK), fontWeight = FontWeight.Bold))
        positiveButton(
            text = "Cancel",
            textStyle = TextStyle(color = Color(MAIN_COLOR_DARK), fontWeight = FontWeight.Bold))
      },
      backgroundColor = Color(MAIN_BACKGROUND_BUTTON)) {
        datepicker(
            initialDate = initialDate ?: LocalDate.now(),
            title = "Select a date",
            allowedDateValidator = { date -> date.isAfter(LocalDate.now().minusDays(1)) },
            colors =
                DatePickerDefaults.colors(
                    headerBackgroundColor = Color(MAIN_COLOR_DARK),
                    headerTextColor = Color.White,
                    calendarHeaderTextColor = Color(MAIN_COLOR_DARK),
                    dateActiveBackgroundColor = Color(MAIN_COLOR_DARK),
                    dateInactiveBackgroundColor = Color.LightGray,
                )) {
              onDateSelected(Timestamp(it.atStartOfDay().toInstant(ZoneOffset.MIN)))
            }
      }
}
