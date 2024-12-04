package com.android.sample.ui.dialogs

import android.icu.util.GregorianCalendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.sample.R
import com.android.sample.resources.C.Tag.DEFAULT_MAX_PRICE
import com.android.sample.resources.C.Tag.DIALOG_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.resources.C.Tag.TEXT_PADDING
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onFilter:
        (
            price: Double?,
            membersAvailable: Int?,
            schedule: Timestamp?,
            duration: String?,
            onlyPRO: Boolean?) -> Unit,
) {
  var maxPrice by remember { mutableStateOf(DEFAULT_MAX_PRICE) }
  var availablePlaces by remember { mutableStateOf<Int?>(null) }
  var minDate by remember { mutableStateOf<String?>(null) }
  var minDateTimestamp by remember { mutableStateOf<Timestamp?>(null) }
  var duration by remember { mutableStateOf<String?>(null) }
  var onlyPRO by remember { mutableStateOf<Boolean?>(false) }

  Dialog(
      onDismissRequest = { onDismiss() },
      properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .height(DIALOG_PADDING.dp)
                    .background(
                        color = Color.White, shape = RoundedCornerShape(size = MEDIUM_PADDING.dp))
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
                      Text(text = "€ 0,0", modifier = Modifier.testTag("minPriceDisplay"))

                      // Maximum Price Display
                      Text(
                          text = "€ ${"%.2f".format(maxPrice)}",
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
                    modifier = Modifier.testTag("membersAvailableTextField"),
                    shape = RoundedCornerShape(TEXT_PADDING.dp))

                OutlinedTextField(
                    value = minDate ?: "",
                    onValueChange = { minDate = it },
                    label = { Text("startDate") },
                    modifier = Modifier.testTag("minDateTextField"),
                    placeholder = {
                      androidx.compose.material3.Text(
                          text = stringResource(id = R.string.request_date_activity_withFormat))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(TEXT_PADDING.dp))
                OutlinedTextField(
                    value = duration ?: "",
                    onValueChange = { duration = it },
                    label = { Text("Duration") },
                    modifier = Modifier.testTag("durationTextField"),
                    shape = RoundedCornerShape(TEXT_PADDING.dp))
                Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
                Row(modifier = Modifier.fillMaxWidth().testTag("onlyPROCheckboxRow"),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                        Checkbox(
                            modifier = Modifier.testTag("onlyPROCheckbox"),
                            checked = onlyPRO ?: false,
                            onCheckedChange = { onlyPRO = it },
                            colors =
                                CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colors.primary),
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
                        if (minDate != null) {
                          val calendar = GregorianCalendar()
                          val parts = minDate!!.split("/")
                          calendar.set(
                              parts[2].toInt(),
                              parts[1].toInt() - 1, // Months are 0-based
                              parts[0].toInt(),
                              0,
                              0,
                              0)
                          minDateTimestamp = Timestamp(calendar.time)
                        }
                        onFilter(
                            maxPrice.toDouble(),
                            availablePlaces,
                            minDateTimestamp,
                            duration,
                            onlyPRO)
                        onDismiss()
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
              androidx.compose.material3.Icon(
                  painter = painterResource(id = android.R.drawable.ic_dialog_info),
                  contentDescription = "Info",
                  tint = Color.Gray)
            }
      androidx.compose.material3.Text(
          text = "PRO info",
          style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(end = SMALL_PADDING.dp).testTag("paymentInfo"))
      }

  if (showDialog) {
    androidx.compose.material3.AlertDialog(
        modifier = Modifier.testTag("paymentInfoDialog"),
        onDismissRequest = { showDialog = false },
        confirmButton = {
          androidx.compose.material3.TextButton(
              modifier = Modifier.testTag("okButton"), onClick = { showDialog = false }) {
                androidx.compose.material3.Text(text = stringResource(id = R.string.ok))
              }
        },
        title = {
          androidx.compose.material3.Text(
              modifier = Modifier.testTag("PROInfoTitle"),
              text = stringResource(id = R.string.PRO_info))
        },
        text = {
          androidx.compose.material3.Text(
              modifier = Modifier.testTag("PROInfoText"),
              text = stringResource(id = R.string.PRO_explanation))
        })
  }
}
