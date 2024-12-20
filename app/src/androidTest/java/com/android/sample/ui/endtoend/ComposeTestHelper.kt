package com.android.sample.ui.endtoend

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement

/**
 * Helper class to interact with the UI.
 *
 * @param composeTestRule The [ComposeTestRule] to interact with the UI.
 */
class ComposeTestHelper(private val composeTestRule: ComposeTestRule) {

  /**
   * Clicks on a node with the given tag.
   *
   * @param tag The tag of the node to click.
   */
  fun click(
      tag: String,
      bottomNavItem: Boolean = false,
      text: Boolean = false,
      any: Boolean = false,
      index: Int = 0
  ) {
    if (bottomNavItem) {
      if (text || any) throw IllegalArgumentException("Bottom navigation items should use tags.")
      clickBottomNavigationItem(tag)
      return
    }
    val node = getNode(tag, text, any, index)
    see(tag, text = text, any = any, index = index)
    node.assertHasClickAction()
    node.performClick()
    composeTestRule.waitForIdle()
  }

  /** Click on bottom navigation item and check that the screen changes. */
  private fun clickBottomNavigationItem(tag: String) {
    see(BottomNavigation.MENU)
    see(tag)
    click(tag, text = false)
    // see(tag, selected = true) TODO: Uncomment this line when the bug is fixed
  }

  /**
   * Asserts that a node with the given tag is displayed and optionally selected.
   *
   * @param tag The tag of the node to assert. If [] is true, this is the text to search for.
   * @param selected Assert whether the node should be selected. default is null, meaning no check
   *   on selection.
   * @param text Whether to search for text instead of tag. default is false.
   * @param any Whether to accept multiple nodes to match the tag, and to select any (i.e. the
   *   first) of these nodes. default is false.
   */
  fun see(
      tag: String,
      selected: Boolean? = null,
      text: Boolean = false,
      any: Boolean = false,
      index: Int = 0
  ) {
    val node = getNode(tag, text, any, index)
    node.assertIsDisplayed()
    when (selected) {
      true -> node.assertIsSelected()
      false -> node.assertIsNotSelected()
      null -> {}
    }
  }

  private fun getNode(
      tag: String,
      text: Boolean = false,
      any: Boolean = false,
      index: Int = 0
  ): SemanticsNodeInteraction {
    return if (any) {
      if (text) composeTestRule.onAllNodesWithText(tag)[index]
      else composeTestRule.onAllNodesWithTag(tag)[index]
    } else {
      if (text) composeTestRule.onNodeWithText(tag) else composeTestRule.onNodeWithTag(tag)
    }
  }

  /** Scrolls to a node with the given tag. */
  fun scroll(parentTag: String, nodeTag: String, text: Boolean = false) =
      getNode(nodeTag, text).performScrollToNode(hasTestTag(nodeTag))

  /** Asserts that a node with the given tag is not displayed. */
  fun notSee(tag: String, text: Boolean = false) = getNode(tag, text).assertIsNotDisplayed()

  /**
   * Writes text to a node with the given tag.
   *
   * @param tag The tag of the node to write to.
   * @param input The text to write.
   * @param replace Whether to replace the text in the node. default is false.
   */
  fun write(tag: String, input: String, replace: Boolean = false) {
    when (replace) {
      true -> composeTestRule.onNodeWithTag(tag).performTextReplacement(input)
      false -> composeTestRule.onNodeWithTag(tag).performTextInput(input)
    }
    composeTestRule.waitForIdle()
  }
}
