package com.android.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun MyDatePicker(onDateSelected: (Timestamp) -> Unit, isOpen: Boolean) {
  // Implementation
  MaterialDialog(
      dialogState = rememberMaterialDialogState(isOpen),
      buttons = {
        positiveButton("Ok")
        negativeButton("Cancel")
      }) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Select a date",
            allowedDateValidator = { date -> date.isAfter(LocalDate.now().minusDays(1)) }) {
              onDateSelected(Timestamp(it.atStartOfDay().toInstant(ZoneOffset.MIN)))
            }
      }
}

@Preview
@Composable
fun MyDatePickerPreview() {
  MyDatePicker(onDateSelected = {}, isOpen = true)
}
