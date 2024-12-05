package com.android.sample.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Tag.LINE_STROKE
import com.android.sample.resources.C.Tag.STANDARD_PADDING
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.TopLevelDestinations

@Composable
fun PlusButtonToCreate(navigationActions: NavigationActions, category: String) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(STANDARD_PADDING.dp)
              .testTag("plusRowToCreate")
              .border(
                  width = LINE_STROKE.dp,
                  color = Color.Black,
                  shape = RoundedCornerShape(STANDARD_PADDING.dp)),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    IconButton(
        onClick = {
          if (category == "created") {
            navigationActions.navigateTo(TopLevelDestinations.ADD_ACTIVITY)
          } else if (category == "enrolled")
              navigationActions.navigateTo(TopLevelDestinations.OVERVIEW)
        }) {
          Icon(
              imageVector = TopLevelDestinations.ADD_ACTIVITY.icon,
              contentDescription = TopLevelDestinations.ADD_ACTIVITY.textId,
          )
        }
  }
}
