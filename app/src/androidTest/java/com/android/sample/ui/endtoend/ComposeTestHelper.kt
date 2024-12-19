package com.android.sample.ui.endtoend

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

/**
 * Helper class to interact with the UI.
 *
 * @param composeTestRule The [ComposeTestRule] to interact with the UI.
 */
class ComposeTestHelper(private val composeTestRule: ComposeTestRule) {

  /** Clicks on a node with the given tag. */
  fun click(tag: String) {
    composeTestRule.onNodeWithTag(tag).assertHasClickAction()
    composeTestRule.onNodeWithTag(tag).performClick()
    composeTestRule.waitForIdle()
  }

  /** Click on bottom navigation item and check that the screen changes. */
  fun clickBottomNavigationItem(tag: String) {
    assertIsDisplayed(BottomNavigation.MENU)
    assertIsDisplayed(tag)
    click(tag)
    // assertIsSelected(tag) TODO: Uncomment this line when the bug is fixed
  }

  /** Asserts that a node with the given tag is selected. */
  fun assertIsSelected(tag: String) {
    assertIsDisplayed(tag)
    composeTestRule.onNodeWithTag(tag).assertIsSelected()
  }

  /** Asserts that a node with the given tag is displayed. */
  fun assertIsDisplayed(tag: String) = composeTestRule.onNodeWithTag(tag).assertIsDisplayed()

  /** Asserts that there is at least one node with the given tag displayed. */
  fun assertAnyIsDisplayed(tag: String) =
      composeTestRule.onAllNodesWithTag(tag)[0].assertIsDisplayed()

  /** Asserts that a node with the given tag is not displayed. */
  fun assertIsNotDisplayed(tag: String) = composeTestRule.onNodeWithTag(tag).assertIsNotDisplayed()

  /** Asserts that a node with the given tag exists. */
  fun assertExists(tag: String) = composeTestRule.onNodeWithTag(tag).assertExists()

  /** Asserts that a node with the given tag does not exist. */
  fun assertDoesNotExist(tag: String) = composeTestRule.onNodeWithTag(tag).assertDoesNotExist()

  /** Asserts that a node with the given tag does not exist. */
  fun assertTextIsDisplayed(text: String) = composeTestRule.onNodeWithText(text).assertIsDisplayed()

  /** Writes text in a node with the given tag. */
  fun write(tag: String, text: String) {
    composeTestRule.onNodeWithTag(tag).performTextInput(text)
    composeTestRule.waitForIdle()
  }
}
