package com.android.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun MyDateRangePicker(
    onDateRangeSelected: (Timestamp, Timestamp) -> Unit,
    isOpen: Boolean,
    initialStartDate: LocalDate? = null,
    initialEndDate: LocalDate? = null,
    onCloseRequest: (Boolean) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(true) }
    var startDate by remember { mutableStateOf(initialStartDate ?: LocalDate.now()) }
    var endDate by remember { mutableStateOf(initialEndDate ?: LocalDate.now().plusDays(1)) }

    val dialogState = rememberMaterialDialogState(isOpen)

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Next") {
                showStartDatePicker = false // Move to end date picker
            }
            negativeButton("Cancel") {
                onCloseRequest(false) // Close without selecting
            }
        }) {
        if (showStartDatePicker) {
            datepicker(
                initialDate = startDate,
                title = "Select Start Date",
                allowedDateValidator = { it.isAfter(LocalDate.now().minusDays(1)) }) {
                startDate = it
            }
        } else {
            datepicker(
                initialDate = endDate,
                title = "Select End Date",
                allowedDateValidator = { it.isAfter(startDate) }) {
                endDate = it
                dialogState.hide() // Close dialog
                onDateRangeSelected(
                    Timestamp(startDate.atStartOfDay().toInstant(ZoneOffset.UTC)),
                    Timestamp(endDate.atStartOfDay().toInstant(ZoneOffset.UTC)))
                onCloseRequest(false) // Complete the process
            }
        }
    }
}
