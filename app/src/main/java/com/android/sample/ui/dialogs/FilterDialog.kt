package com.android.sample.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.sample.R
import com.android.sample.model.hour_date.HourDateViewModel
import com.android.sample.resources.C.Tag.DEFAULT_MAX_PRICE
import com.android.sample.resources.C.Tag.DIALOG_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.TEXT_PADDING
import com.android.sample.ui.components.MyDatePicker
import com.android.sample.ui.components.MyTimePicker
import com.google.firebase.Timestamp
import java.time.ZoneId

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onFilter:
        (
            price: Double?,
            membersAvailable: Int?,
            minDate: Timestamp?,
            maxDate: Timestamp?,
            startTime: String?,
            endTime: String?,
            distance: Double?,
            onlyPRO: Boolean?) -> Unit,
) {
  val context = LocalContext.current
  val hourDateViewModel = HourDateViewModel()
  var maxPrice by remember { mutableStateOf(DEFAULT_MAX_PRICE) }
  var availablePlaces by remember { mutableStateOf<Int?>(null) }
  var startDateTimestamp by remember { mutableStateOf<Timestamp>(Timestamp.now()) }
  var endDateTimestamp by remember { mutableStateOf<Timestamp?>(null) }
  var startTime by remember { mutableStateOf<String?>(null) }
  var endTime by remember { mutableStateOf<String?>(null) }
  var onlyPRO by remember { mutableStateOf<Boolean?>(false) }
  var distance by remember { mutableStateOf<String?>(null) }
  var startDateIsOpen by remember { mutableStateOf(false) }
  var endDateIsOpen by remember { mutableStateOf(false) }
  var startDateIsSet by remember { mutableStateOf(false) }
  var endDateIsSet by remember { mutableStateOf(false) }
  var startTimeIsOpen by remember { mutableStateOf(false) }
  var startTimeIsSet by remember { mutableStateOf(false) }
  var durationIsOpen by remember { mutableStateOf(false) }
  var durationIsSet by remember { mutableStateOf(false) }

  Dialog(
      onDismissRequest = { onDismiss() },
      properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .height(DIALOG_PADDING.dp)
                    .background(
                        color = Color.White, shape = RoundedCornerShape(size = MEDIUM_PADDING.dp))
                    .verticalScroll(rememberScrollState())
                    .testTag("FilterDialog"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Column(
              modifier = Modifier.padding(MEDIUM_PADDING.dp),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Price Range",
                    modifier =
                        Modifier.padding(bottom = STANDARD_PADDING.dp).testTag("priceRangeLabel"))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      // Minimum Price Display
                      Text(text = "CHF 0,0", modifier = Modifier.testTag("minPriceDisplay"))

                      // Maximum Price Display
                      Text(
                          text = "CHF ${"%.2f".format(maxPrice)}",
                          modifier = Modifier.testTag("maxPriceDisplay"))
                    }
                Spacer(modifier = Modifier.height(8.dp))

                // Slider for price range
                Slider(
                    value = maxPrice,
                    onValueChange = { newMaxPrice -> maxPrice = newMaxPrice },
                    valueRange = 0f..DEFAULT_MAX_PRICE, // Total range of the slider
                    modifier = Modifier.testTag("priceRangeSlider"))

                OutlinedTextField(
                    value = availablePlaces?.toString() ?: "",
                    onValueChange = { availablePlaces = it.toIntOrNull() },
                    label = { Text("Members Available") },
                    singleLine = true,
                    modifier = Modifier.testTag("membersAvailableTextField"),
                    shape = RoundedCornerShape(TEXT_PADDING.dp))

                Text(
                    text = "Date Range",
                    modifier =
                        Modifier.padding(top = STANDARD_PADDING.dp).testTag("dateRangeLabel"))
                OutlinedButton(
                    onClick = { startDateIsOpen = true },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(STANDARD_PADDING.dp)
                            .testTag("minDateTextField"),
                ) {
                  Icon(
                      Icons.Filled.CalendarMonth,
                      contentDescription = "select a date from which activities are shown",
                      modifier =
                          Modifier.padding(end = STANDARD_PADDING.dp).testTag("iconDateCreate"))
                  if (startDateIsSet)
                      Text(
                          "you selected activities after: ${
                                    startDateTimestamp.toDate().toString().take(11)
                                }," +
                              "${(startDateTimestamp.toDate().year) + 1900}  (click to change)")
                  else Text("select a date from which activities are shown")
                }
                if (startDateIsOpen) {
                  MyDatePicker(
                      onCloseRequest = { startDateIsOpen = false },
                      onDateSelected = { date ->
                        startDateTimestamp = date
                        startDateIsOpen = false
                        startDateIsSet = true
                      },
                      isOpen = startDateIsOpen,
                      initialDate =
                          startDateTimestamp
                              .toDate()
                              .toInstant()
                              .atZone(ZoneId.systemDefault())
                              .toLocalDate())
                }
                Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
                OutlinedButton(
                    onClick = { endDateIsOpen = true },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(STANDARD_PADDING.dp)
                            .testTag("maxDateTextField"),
                ) {
                  Icon(
                      Icons.Filled.CalendarMonth,
                      contentDescription = "select end date",
                      modifier =
                          Modifier.padding(end = STANDARD_PADDING.dp).testTag("iconDateCreate"))
                  if (endDateIsSet)
                      Text(
                          "you selected activities before: ${
                                endDateTimestamp?.toDate().toString().take(11)
                            }," +
                              "${(endDateTimestamp?.toDate()?.year)?.plus(1900)}  (click to change)")
                  else Text("Select a Date from which activities are hidden")
                }
                if (endDateIsOpen) {
                  MyDatePicker(
                      onCloseRequest = { endDateIsOpen = false },
                      onDateSelected = { date ->
                        endDateTimestamp = date
                        endDateIsOpen = false
                        endDateIsSet = true
                      },
                      isOpen = endDateIsOpen,
                      initialDate =
                          startDateTimestamp
                              .toDate()
                              .toInstant()
                              .atZone(ZoneId.systemDefault())
                              .toLocalDate())
                }
                Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
                Text(
                    text = "Date Range",
                    modifier =
                        Modifier.padding(top = STANDARD_PADDING.dp).testTag("dateRangeLabel"))
                OutlinedButton(
                    onClick = { startTimeIsOpen = true },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(STANDARD_PADDING.dp)
                            .testTag("startTimeTextField"),
                ) {
                  Icon(
                      Icons.Filled.AccessTime,
                      contentDescription = "select start time",
                      modifier =
                          Modifier.padding(end = STANDARD_PADDING.dp)
                              .testTag("iconStartTimeCreate"))
                  if (startTimeIsSet) Text("Start time: $startTime (click to change)")
                  else Text("Select start time")
                }
                if (startTimeIsOpen) {
                  MyTimePicker(
                      onTimeSelected = { time ->
                        startTime =
                            time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().toString()
                        startTimeIsOpen = false
                        startTimeIsSet = true
                      },
                      isOpen = startTimeIsOpen,
                      onCloseRequest = { startTimeIsOpen = false })
                }

                Spacer(modifier = Modifier.height(STANDARD_PADDING.dp))
                OutlinedButton(
                    onClick = { durationIsOpen = true },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(STANDARD_PADDING.dp)
                            .testTag("endTimeTextField"),
                ) {
                  Icon(
                      Icons.Filled.HourglassTop,
                      contentDescription = "select duration",
                      modifier =
                          Modifier.padding(end = STANDARD_PADDING.dp)
                              .align(Alignment.CenterVertically))
                  if (durationIsSet) Text("Finishing Time: $endTime (click to change)")
                  else Text("Select End Time")
                }
                if (durationIsOpen) {
                  MyTimePicker(
                      onTimeSelected = { time ->
                        endTime =
                            time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().toString()
                        durationIsOpen = false
                        durationIsSet = true
                      },
                      isOpen = durationIsOpen,
                      onCloseRequest = { durationIsOpen = false })
                }
                OutlinedTextField(
                    value = distance ?: "",
                    onValueChange = { distance = it },
                    label = { Text("distance") },
                    modifier = Modifier.testTag("distanceTextField"),
                    placeholder = { Text(text = "exp: 10.5 km") },
                    singleLine = true,
                    shape = RoundedCornerShape(TEXT_PADDING.dp))
                Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().testTag("onlyPROCheckboxRow"),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                  Checkbox(
                      modifier = Modifier.testTag("onlyPROCheckbox"),
                      checked = onlyPRO ?: false,
                      onCheckedChange = { onlyPRO = it },
                      colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colors.primary),
                  )
                  Text(
                      "Only see PRO activities",
                  )
                }

                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                PROinfo()
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                  TextButton(
                      onClick = { onDismiss() }, modifier = Modifier.testTag("cancelButton")) {
                        Text("Cancel")
                      }

                  Spacer(modifier = Modifier.width(SMALL_PADDING.dp))

                  Button(
                      onClick = {
                        if (endDateTimestamp != null && endDateTimestamp!! < startDateTimestamp) {
                          Toast.makeText(
                                  context,
                                  "Filter failed, end date is before start date",
                                  Toast.LENGTH_SHORT)
                              .show()
                        } else {
                          if (startTime != null &&
                              endTime != null &&
                              hourDateViewModel.combineDateAndTime(
                                  startDateTimestamp, startTime!!) >
                                  hourDateViewModel.combineDateAndTime(
                                      endDateTimestamp!!, endTime!!)) {
                            Toast.makeText(
                                    context,
                                    "Filter failed, start time is after end time",
                                    Toast.LENGTH_SHORT)
                                .show()
                          } else {
                            onFilter(
                                maxPrice.toDouble(),
                                availablePlaces,
                                startDateTimestamp,
                                endDateTimestamp,
                                startTime,
                                endTime,
                                distance?.toDouble(),
                                onlyPRO)
                            onDismiss()
                          }
                        }
                      },
                      modifier = Modifier.testTag("filterButton")) {
                        Text("Filter")
                      }
                }
              }
        }
      }
}

@Composable
fun PROinfo() {
  var showDialog by remember { mutableStateOf(false) }
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().testTag("PROSection")) {
        androidx.compose.material3.IconButton(
            modifier = Modifier.testTag("infoIconButton"), onClick = { showDialog = true }) {
              Icon(
                  painter = painterResource(id = android.R.drawable.ic_dialog_info),
                  contentDescription = "Info",
                  tint = Color.Gray)
            }
        Text(
            text = "PRO info",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = SMALL_PADDING.dp).testTag("PROInfo"))
      }

  if (showDialog) {
    androidx.compose.material3.AlertDialog(
        modifier = Modifier.testTag("PROInfoDialog"),
        onDismissRequest = { showDialog = false },
        confirmButton = {
          androidx.compose.material3.TextButton(
              modifier = Modifier.testTag("okButton"), onClick = { showDialog = false }) {
                Text(text = stringResource(id = R.string.ok))
              }
        },
        title = {
          Text(
              modifier = Modifier.testTag("PROInfoTitle"),
              text = stringResource(id = R.string.PRO_info))
        },
        text = {
          Text(
              modifier = Modifier.testTag("PROInfoText"),
              text = stringResource(id = R.string.PRO_explanation))
        })
  }
}
