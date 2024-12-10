package com.android.sample.ui.components

import androidx.compose.runtime.Composable
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MyTimePicker(onTimeSelected: (Timestamp) -> Unit, isOpen: Boolean, onCloseRequest: (MaterialDialogState) -> Unit) {
  MaterialDialog(
      onCloseRequest = onCloseRequest,
      dialogState = rememberMaterialDialogState(isOpen),
      buttons = {
        positiveButton(text = "Ok")
        negativeButton(text = "Cancel")
      }) {
        timepicker(
            initialTime = LocalTime.NOON,
            title = "Pick a time",
        ) {
          onTimeSelected(
              Timestamp(
                  LocalDate.now().atTime(it).atZone(java.time.ZoneId.systemDefault()).toInstant()))
        }
      }
}
