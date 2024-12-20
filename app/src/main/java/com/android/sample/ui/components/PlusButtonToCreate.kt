package com.android.sample.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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

/**
 * Composable function to display the plus button to create a new activity or to overview.
 *
 * @param navigationActions The NavigationActions to handle the navigation.
 * @param activityType The type of activity to be created.
 */
@Composable
fun PlusButtonToCreate(navigationActions: NavigationActions, activityType: Int) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(STANDARD_PADDING.dp)
              .clickable(
                  onClick = {
                    if (activityType == 0) {
                      navigationActions.navigateTo(TopLevelDestinations.ADD_ACTIVITY)
                    } else if (activityType == 1)
                        navigationActions.navigateTo(TopLevelDestinations.OVERVIEW)
                  })
              .testTag("plusRowToCreate")
              .border(
                  width = LINE_STROKE.dp,
                  color = Color.Black,
                  shape = RoundedCornerShape(STANDARD_PADDING.dp)),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
        imageVector = TopLevelDestinations.ADD_ACTIVITY.iconOutlined,
        contentDescription = TopLevelDestinations.ADD_ACTIVITY.textId,
    )
  }
}
