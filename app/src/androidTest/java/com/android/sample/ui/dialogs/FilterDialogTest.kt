package com.android.sample.ui.dialogs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FilterDialogTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun filterDialog_shouldDisplayAllComponents() {
    composeTestRule.setContent {
      FilterDialog(onDismiss = {}, onFilter = { _, _, _, _, _, _, _, _ -> })
    }

    composeTestRule.onNodeWithTag("FilterDialog").assertIsDisplayed()
    composeTestRule.onNodeWithText("Price Range").assertIsDisplayed()
    composeTestRule.onNodeWithTag("membersAvailableTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("minDateTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("maxDateTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("startTimeTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("endTimeTextField").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("FilterDialog")
        .performScrollToNode(hasTestTag("distanceTextField"))
    composeTestRule.onNodeWithTag("distanceTextField").assertIsDisplayed()

    composeTestRule.onNodeWithTag("FilterDialog").performScrollToNode(hasTestTag("cancelButton"))
    composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    composeTestRule.onNodeWithText("Filter").assertIsDisplayed()
  }
  /*
    @Test
    fun filterDialog_shouldUpdateMaxPrice() {
      composeTestRule.setContent { FilterDialog(onDismiss = {}, onFilter = { _, _, _, _ -> }) }

      // Check initial value
      composeTestRule.onNodeWithText("€ 300,00").assertIsDisplayed()

      // Simulate slider value update
      composeTestRule.onNodeWithTag("priceRangeSlider").performSemanticsAction(
          SemanticsActions.SetProgress) {
            it(150f)
          }

      // Verify updated value
      composeTestRule.onNodeWithText("€ 150,00").assertIsDisplayed()
    }
  */
  @Test
  fun filterDialog_shouldUpdateMembersAvailable() {
    composeTestRule.setContent {
      FilterDialog(onDismiss = {}, onFilter = { _, _, _, _, _, _, _, _ -> })
    }

    val inputText = "10"
    composeTestRule.onNodeWithTag("membersAvailableTextField").performTextInput(inputText)

    // Verify the input value
    composeTestRule.onNodeWithTag("membersAvailableTextField").assertTextContains(inputText)
  }

  @Test
  fun filterDialog_shouldSetDistance() {
    composeTestRule.setContent {
      FilterDialog(onDismiss = {}, onFilter = { _, _, _, _, _, _, _, _ -> })
    }

    val distance = "10"
    composeTestRule.onNodeWithTag("distanceTextField").performTextInput(distance)

    // Verify the input value
    composeTestRule.onNodeWithTag("distanceTextField").assertTextContains(distance)
  }

  @Test
  fun filterDialog_shouldDismissOnCancel() {
    var dismissed = false

    composeTestRule.setContent {
      FilterDialog(onDismiss = { dismissed = true }, onFilter = { _, _, _, _, _, _, _, _ -> })
    }

    // Perform click on Cancel
    composeTestRule.onNodeWithTag("FilterDialog").performScrollToNode(hasTestTag("cancelButton"))
    composeTestRule.onNodeWithTag("cancelButton").performClick()

    // Verify dismiss was called
    assert(dismissed)
  }

  @Test
  fun filterDialog_shouldCallOnFilter() {
    var filterCalled = false
    var maxPrice: Double? = null
    var membersAvailable: Int? = null
    var startTime: String? = null
    var endTime: String? = null
    var distance: Double? = null

    composeTestRule.setContent {
      FilterDialog(
          onDismiss = {},
          onFilter = { price, members, _, _, startT, endT, dist, _ ->
            filterCalled = true
            maxPrice = price
            membersAvailable = members
            startTime = startT
            endTime = endT
            distance = dist
          })
    }

    // Set values
    composeTestRule.onNodeWithTag("membersAvailableTextField").performTextInput("5")
    composeTestRule.onNodeWithTag("distanceTextField").performTextInput("10")
    composeTestRule.onNodeWithTag("FilterDialog").performScrollToNode(hasTestTag("filterButton"))
    composeTestRule.onNodeWithTag("filterButton").performClick()

    // Verify filter callback
    assert(filterCalled)
    assertEquals(300.0, maxPrice) // Default max price
    assertEquals(5, membersAvailable)
    assertEquals(10.0, distance)
  }

  @Test
  fun test_PROInfo_initialState() {

    composeTestRule.setContent { PROinfo() }

    composeTestRule.onNodeWithTag("PROSection").assertIsDisplayed()

    composeTestRule.onNodeWithTag("infoIconButton").assertIsDisplayed()

    composeTestRule.onNodeWithTag("PROInfo").assertTextContains("PRO info")

    composeTestRule.onNodeWithTag("PROInfoDialog").assertDoesNotExist()
  }

  @Test
  fun test_PROInfo_dialogOpensOnClick() {
    composeTestRule.setContent { PROinfo() }

    composeTestRule.onNodeWithTag("infoIconButton").performClick()

    composeTestRule.onNodeWithTag("PROInfoDialog").assertIsDisplayed()

    composeTestRule.onNodeWithTag("PROInfoTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PROInfoText").assertIsDisplayed()
  }

  @Test
  fun test_PROInfo_dialogClosesOnOkClick() {
    composeTestRule.setContent { PROinfo() }

    composeTestRule.onNodeWithTag("infoIconButton").performClick()

    composeTestRule.onNodeWithTag("PROInfoDialog").assertIsDisplayed()

    composeTestRule.onNodeWithTag("okButton").performClick()

    composeTestRule.onNodeWithTag("PRODialog").assertDoesNotExist()
  }
}
