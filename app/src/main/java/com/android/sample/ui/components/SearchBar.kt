package com.android.sample.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.STANDARD_PADDING

@Composable
fun SearchBar(onValueChange: (String) -> Unit, value: String) {
  OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      label = { Text("Search") },
      leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
      modifier =
          Modifier.testTag("searchBar")
              .clip(shape = RoundedCornerShape(MEDIUM_PADDING.dp))
              .fillMaxWidth()
              .padding(STANDARD_PADDING.dp),
      singleLine = true)
}
