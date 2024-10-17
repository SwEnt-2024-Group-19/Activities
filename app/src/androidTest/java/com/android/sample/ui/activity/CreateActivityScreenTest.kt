package com.android.sample.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.ui.dialogs.SimpleUser
import com.android.sample.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class CreateActivityScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockViewModel = mockk<ListActivitiesViewModel>()
  private val mockProfileViewModel = mockk<ProfileViewModel>()
  private val mockNavigationActions = mock<NavigationActions>()

  @Test
  fun createActivityScreen_displaysTitleField() {

    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputTitleCreate").assertExists()
    composeTestRule.onNodeWithTag("inputTitleCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysDescriptionField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertExists()
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysDateField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputDateCreate").assertExists()
    composeTestRule.onNodeWithTag("inputDateCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysPriceField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputPriceCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPriceCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysPlacesLeftField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysLocationField() {
    composeTestRule.setContent {
      CreateActivityScreen(mockViewModel, mockNavigationActions, mockProfileViewModel)
    }
    composeTestRule.onNodeWithTag("inputLocationCreate").assertExists()
    composeTestRule.onNodeWithTag("inputLocationCreate").assertIsDisplayed()
  }

  @Test
  fun attendeeRowDisplaysCorrectInformation() {
    val attendee = SimpleUser("John", "Doe", 30)
    val attendees = listOf(attendee)

    composeTestRule.setContent {
      Row(
          modifier = Modifier.padding(8.dp).background(Color(0xFFFFFFFF)),
      ) {
        Text(
            text = attendees[0].name,
            modifier = Modifier.padding(8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = attendees[0].surname,
            modifier = Modifier.padding(8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = attendees[0].age.toString(),
            modifier = Modifier.padding(8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
        )
        Button(
            onClick = { /* no-op */},
            modifier = Modifier.width(40.dp).height(40.dp).testTag("removeAttendeeButton"),
        ) {
          Icon(
              Icons.Filled.PersonRemove,
              contentDescription = "remove attendee",
          )
        }
      }
    }

    composeTestRule.onNodeWithText("John").assertExists()
    composeTestRule.onNodeWithText("Doe").assertExists()
    composeTestRule.onNodeWithText("30").assertExists()
  }

  @Test
  fun removeButtonRemovesAttendee() {
    var attendees = listOf(SimpleUser("John", "Doe", 30))

    composeTestRule.setContent {
      Row(
          modifier = Modifier.padding(8.dp).background(Color(0xFFFFFFFF)),
      ) {
        Text(
            text = attendees[0].name,
            modifier = Modifier.padding(8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = attendees[0].surname,
            modifier = Modifier.padding(8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = attendees[0].age.toString(),
            modifier = Modifier.padding(8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
        )
        Button(
            onClick = { attendees = attendees.filter { it != attendees[0] } },
            modifier = Modifier.width(40.dp).height(40.dp).testTag("removeAttendeeButton"),
        ) {
          Icon(
              Icons.Filled.PersonRemove,
              contentDescription = "remove attendee",
          )
        }
      }
    }

    composeTestRule.onNodeWithTag("removeAttendeeButton").performClick()
    assert(attendees.isEmpty())
  }

  @Test
  fun parseFraction_returnsCorrectValueForValidFraction() {
    val result = parseFraction("5/10", 0)
    assertEquals(5, result)
  }

  @Test
  fun parseFraction_returnsCorrectValueForValidFractionSecondPart() {
    val result = parseFraction("5/10", 1)
    assertEquals(10, result)
  }

  @Test
  fun parseFraction_returnsNullForInvalidFraction() {
    val result = parseFraction("invalid", 0)
    assertEquals(null, result)
  }

  @Test
  fun parseFraction_returnsNullForNonIntegerValues() {
    val result = parseFraction("5/ten", 1)
    assertEquals(null, result)
  }

  @Test
  fun parseFraction_returnsNullForEmptyString() {
    val result = parseFraction("", 0)
    assertEquals(null, result)

  }
}
