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

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  BottomNavigation(
      modifier = Modifier.fillMaxWidth().height(60.dp).testTag("bottomNavigationMenu"),
      backgroundColor = Color(0xfff1fdfd)) {
        tabList.forEachIndexed { index, tab ->
          val isSelected = tab.route == selectedItem

          // Animate scale and offset when selected
          val scale by animateFloatAsState(if (isSelected) 0.9f else 1f)
          val offsetY by animateFloatAsState(if (isSelected) -5f else 0f)

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
              selectedContentColor = Color(0xFF5CE1E6),
              unselectedContentColor = Color.Gray)
        }
      }
}

@Composable
fun MiddleButton(isSelected: Boolean) {
  val middleButtonSize = if (isSelected) 50.dp else 40.dp
  Image(
      painter = painterResource(id = R.drawable.aptivity_logo),
      contentDescription = null,
      modifier = Modifier.size(middleButtonSize))
  if (isSelected) {
    Box(
        modifier =
            Modifier.height(5.dp)
                .width(30.dp)
                .clip(RoundedCornerShape(90))
                .background(Color(0xFF00499E)))
  }
}

@Composable
fun RegularButton(tab: TopLevelDestination, isSelected: Boolean) {
  Icon(
      imageVector = if (isSelected) tab.iconFilled else tab.iconOutlined,
      contentDescription = null,
      tint = if (isSelected) Color(0xFF00499E) else Color.Gray)
  if (isSelected) {
    Spacer(modifier = Modifier.height(4.dp))
    Box(
        modifier =
            Modifier.height(5.dp)
                .width(30.dp)
                .clip(RoundedCornerShape(90))
                .background(Color(0xFF00499E)))
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
