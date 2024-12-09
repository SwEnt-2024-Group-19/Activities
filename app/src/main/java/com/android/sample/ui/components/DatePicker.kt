package com.android.sample.ui.components

import androidx.compose.runtime.Composable
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.ZoneOffset

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
      buttons = {
        positiveButton("Ok")
        negativeButton("Cancel")
      }) {
        datepicker(
            initialDate = initialDate ?: LocalDate.now(),
            title = "Select a date",
            allowedDateValidator = { date -> date.isAfter(LocalDate.now().minusDays(1)) }) {
              onDateSelected(Timestamp(it.atStartOfDay().toInstant(ZoneOffset.MIN)))
            }
      }
}
