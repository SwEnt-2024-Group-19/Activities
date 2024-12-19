package com.android.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
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
  val dialogState = rememberMaterialDialogState(isOpen)
  MaterialDialog(
      onCloseRequest = onCloseRequest,
      dialogState = dialogState,
      buttons = {
        positiveButton("Ok")
        negativeButton("Cancel") { onCloseRequest(dialogState) }
      }) {
        datepicker(
            initialDate = initialDate ?: LocalDate.now(),
            title = "Select a date",
            allowedDateValidator = { date -> date.isAfter(LocalDate.now().minusDays(1)) }) {
              onDateSelected(Timestamp(it.atStartOfDay().toInstant(ZoneOffset.MIN)))
            }
      }
}
