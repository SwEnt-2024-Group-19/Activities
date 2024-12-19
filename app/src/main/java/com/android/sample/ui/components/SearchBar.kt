package com.android.sample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND_BUTTON
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.PRIMARY_COLOR
import com.android.sample.resources.C.Tag.ROUNDED_CORNER_SHAPE_DEFAULT
import com.android.sample.resources.C.Tag.STANDARD_PADDING

/**
 * Composable function to display the search bar.
 *
 * @param onValueChange The callback to handle the value change in the search bar.
 * @param value The value of the search bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(onValueChange: (String) -> Unit, value: String, onClickFilter: () -> Unit) {

  OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      label = { Text("Search") },
      leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
      trailingIcon = {
        IconButton(onClick = onClickFilter, modifier = Modifier.testTag("filterDialog")) {
          Icon(
              Icons.Sharp.FilterList,
              contentDescription = "Filter Activities",
              tint = Color(PRIMARY_COLOR))
        }
      },
      modifier =
          Modifier.testTag("searchBar")
              .clip(shape = RoundedCornerShape(MEDIUM_PADDING.dp))
              .fillMaxWidth()
              .padding(STANDARD_PADDING.dp)
              .background(Color(MAIN_BACKGROUND_BUTTON)),
      shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_DEFAULT.dp),
      colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(MAIN_COLOR_DARK)),
      singleLine = true)
}
