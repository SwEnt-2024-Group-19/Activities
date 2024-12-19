package com.android.sample.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.resources.C.BOTTOM_ICON_OFFSET
import com.android.sample.resources.C.BOTTOM_ICON_SCALE
import com.android.sample.resources.C.BOTTOM_ICON_SELECTOR_HEIGHT
import com.android.sample.resources.C.BOTTOM_ICON_SELECTOR_WIDTH
import com.android.sample.resources.C.BOTTOM_NAVIGATION_MENU_HEIGHT
import com.android.sample.resources.C.NON_SELECTED_TAB_SIZE
import com.android.sample.resources.C.SELECTED_TAB_SIZE
import com.android.sample.resources.C.Tag.MAIN_BACKGROUND
import com.android.sample.resources.C.Tag.MAIN_COLOR_DARK
import com.android.sample.resources.C.Tag.MAIN_COLOR_LIGHT

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  BottomNavigation(
      modifier =
          Modifier.fillMaxWidth()
              .height(BOTTOM_NAVIGATION_MENU_HEIGHT.dp)
              .testTag("bottomNavigationMenu"),
      backgroundColor = Color(MAIN_BACKGROUND)) {
        tabList.forEachIndexed { index, tab ->
          val isSelected = tab.route == selectedItem

          // Animate scale and offset when selected
          val scale by animateFloatAsState(if (isSelected) BOTTOM_ICON_SCALE else 1f, label = "")
          val offsetY by
              animateFloatAsState(if (isSelected) -BOTTOM_ICON_OFFSET else 0f, label = "")

          BottomNavigationItem(
              icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier =
                        Modifier.scale(scale) // Scale the icon
                            .offset(y = offsetY.dp) // Move the icon up when selected
                    ) {
                      if (index == 2) { // Middle Button Special Case
                        MiddleButton(isSelected = isSelected)
                      } else {
                        RegularButton(tab = tab, isSelected = isSelected)
                      }
                    }
              },
              selected = isSelected,
              onClick = { onTabSelect(tab) },
              alwaysShowLabel = false,
              selectedContentColor = Color(MAIN_COLOR_LIGHT),
              unselectedContentColor = Color.Gray,
              modifier = Modifier.testTag(tab.textId))
        }
      }
}

@Composable
fun MiddleButton(isSelected: Boolean) {
  val middleButtonSize = if (isSelected) SELECTED_TAB_SIZE.dp else NON_SELECTED_TAB_SIZE.dp
  Image(
      painter = painterResource(id = R.drawable.aptivity_logo),
      contentDescription = null,
      modifier = Modifier.size(middleButtonSize))
  if (isSelected) {
    Box(
        modifier =
            Modifier.height(BOTTOM_ICON_SELECTOR_HEIGHT.dp)
                .width(BOTTOM_ICON_SELECTOR_WIDTH.dp)
                .clip(RoundedCornerShape(90))
                .background(Color(MAIN_COLOR_DARK)))
  }
}

@Composable
fun RegularButton(tab: TopLevelDestination, isSelected: Boolean) {
  Icon(
      imageVector = if (isSelected) tab.iconFilled else tab.iconOutlined,
      contentDescription = null,
      tint = if (isSelected) Color(MAIN_COLOR_DARK) else Color.Gray)
  if (isSelected) {
    Spacer(modifier = Modifier.height(4.dp))
    Box(
        modifier =
            Modifier.height(BOTTOM_ICON_SELECTOR_HEIGHT.dp)
                .width(BOTTOM_ICON_SELECTOR_WIDTH.dp)
                .clip(RoundedCornerShape(90))
                .background(Color(MAIN_COLOR_DARK)))
  }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationPreview() {
  BottomNavigationMenu(
      onTabSelect = {},
      tabList = LIST_TOP_LEVEL_DESTINATION,
      selectedItem = TopLevelDestinations.ADD_ACTIVITY.route)
}
