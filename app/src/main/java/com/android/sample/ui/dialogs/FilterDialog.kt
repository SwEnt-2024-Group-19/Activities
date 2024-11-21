package com.android.sample.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.sample.resources.C.Tag.LARGE_PADDING
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onFilter: (price: Double?, membersAvailable: Int?, schedule: Timestamp?, duration: String?) -> Unit
) {
    var minPrice by remember { mutableStateOf(0f) }
    var maxPrice by remember { mutableStateOf(30000f) }
    var availablePlaces by remember { mutableStateOf<Int?>(null) }
    var schedule by remember { mutableStateOf<Timestamp?>(null) }
    var duration by remember { mutableStateOf<String?>(null) }
    Dialog(onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier =
            Modifier.fillMaxWidth()
                .height(LARGE_PADDING.dp)
                .background(
                    color = Color.White, shape = RoundedCornerShape(size = MEDIUM_PADDING.dp)
                )
                .testTag("FilterDialog"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.padding(MEDIUM_PADDING.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Price Range",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minimum Price Display
                    Text(
                        text = "€ ${"%.2f".format(minPrice)}",
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    // Maximum Price Display
                    Text(
                        text = "€ ${"%.2f".format(maxPrice)}",
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Slider for price range
                RangeSlider(
                    value = minPrice..maxPrice,
                    onValueChange = { values ->
                        minPrice = values.start
                        maxPrice = values.endInclusive
                    },
                    valueRange = minPrice..maxPrice,
                    steps = 100
                )

                OutlinedTextField(
                    value = availablePlaces?.toString() ?: "",
                    onValueChange = { availablePlaces = it.toIntOrNull() },
                    label = { Text("Members Available") },
                    modifier = Modifier.width(MEDIUM_PADDING.dp)
                )
                OutlinedTextField(
                    value = schedule?.toString() ?: "",
                    onValueChange = { /* Handle schedule input */ },
                    label = { Text("Schedule") },
                    modifier = Modifier.width(MEDIUM_PADDING.dp)
                )
                OutlinedTextField(
                    value = duration ?: "",
                    onValueChange = { duration = it },
                    label = { Text("Duration") },
                    modifier = Modifier.width(MEDIUM_PADDING.dp)
                )
                Spacer(modifier = Modifier.height(MEDIUM_PADDING.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                    Button(onClick = {
                        onFilter(maxPrice.toDouble(), availablePlaces, schedule, duration)
                        onDismiss()
                    }) {
                        Text("Filter")
                    }
                }
            }
        }
    }
}

