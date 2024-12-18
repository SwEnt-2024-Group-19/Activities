package com.android.sample.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Tag.MEDIUM_PADDING
import com.android.sample.resources.C.Tag.PRIMARY_COLOR
import com.android.sample.resources.C.Tag.SMALL_PADDING
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

    SearchBar(

        modifier=
          Modifier.testTag("searchBar")
              .clip(shape = RoundedCornerShape(MEDIUM_PADDING.dp))
              .fillMaxWidth()
              .padding(STANDARD_PADDING.dp).testTag("searchBar"),
        query = value,
        onQueryChange = onValueChange,
        placeholder = {
            Text("Discover new activities")
        },
        onSearch = {},
        active = false,
        onActiveChange = {},
        trailingIcon = { IconButton(onClick =  onClickFilter, modifier = Modifier.testTag("filterDialog") ) { Icon(Icons.Sharp.FilterList, contentDescription = "Filter Activities", tint = Color(
            PRIMARY_COLOR))
            }},

        leadingIcon = {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "" // Add a valid content description
                )
            }
        }
    ) { }

}
