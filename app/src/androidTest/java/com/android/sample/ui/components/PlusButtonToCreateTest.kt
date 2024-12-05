package com.android.sample.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.TopLevelDestinations
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify

class PlusButtonToCreateTest {

  private lateinit var mockNavigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockNavigationActions = Mockito.mock(NavigationActions::class.java)
    composeTestRule.setContent { PlusButtonToCreate(navigationActions = mockNavigationActions) }
  }

  @Test
  fun plusButtonToCreate_isDisplayed() {
    composeTestRule.onNodeWithTag("plusRowToCreate").assertIsDisplayed()
  }

  @Test
  fun plusButtonToCreate_performsClick() {
    composeTestRule.onNodeWithTag("plusRowToCreate").performClick()
    verify(mockNavigationActions).navigateTo(TopLevelDestinations.ADD_ACTIVITY)
  }

  @Test
  fun plusButtonToCreate_iconIsDisplayed() {
    composeTestRule
        .onNodeWithContentDescription(TopLevelDestinations.ADD_ACTIVITY.textId)
        .assertIsDisplayed()
  }
}
