package com.android.sample.ui.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class AddUserDialogTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun addUserDialogDisplaysCorrectly() {
    composeTestRule.setContent {
      AddUserDialog(onDismiss = {}, onAddUser = {}, Modifier.padding(0.dp))
    }

    composeTestRule.onNodeWithTag("nameTextFieldUser").assertExists()
    composeTestRule.onNodeWithTag("surnameTextFieldUser").assertExists()
    composeTestRule.onNodeWithTag("ageTextFieldUser").assertExists()
    composeTestRule.onNodeWithTag("addUserButton").assertExists()
  }

  @Test
  fun addUserDialogAddsUserOnButtonClick() {
    var userAdded: SimpleUser? = null
    composeTestRule.setContent {
      AddUserDialog(onDismiss = {}, onAddUser = { userAdded = it }, Modifier.padding(0.dp))
    }

    composeTestRule.onNodeWithTag("nameTextFieldUser").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextFieldUser").performTextInput("Doe")
    composeTestRule.onNodeWithTag("ageTextFieldUser").performTextInput("30")
    composeTestRule.onNodeWithTag("addUserButton").performClick()

    assert(userAdded == SimpleUser("John", "Doe", 30))
  }

  @Test
  fun addUserDialogDismissesOnButtonClick() {
    var dismissed = false
    composeTestRule.setContent {
      AddUserDialog(onDismiss = { dismissed = true }, onAddUser = {}, Modifier.padding(0.dp))
    }

    composeTestRule.onNodeWithTag("addUserButton").performClick()

    assert(dismissed)
  }

  @Test
  fun addUserDialogHandlesEmptyFields() {
    var userAdded: SimpleUser? = null
    composeTestRule.setContent {
      AddUserDialog(onDismiss = {}, onAddUser = { userAdded = it }, Modifier.padding(0.dp))
    }

    composeTestRule.onNodeWithTag("addUserButton").performClick()

    assert(userAdded == SimpleUser("", "", 0))
  }

  @Test
  fun addUserDialogHandlesInvalidAge() {
    var userAdded: SimpleUser? = null
    composeTestRule.setContent {
      AddUserDialog(onDismiss = {}, onAddUser = { userAdded = it }, Modifier.padding(0.dp))
    }

    composeTestRule.onNodeWithTag("nameTextFieldUser").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextFieldUser").performTextInput("Doe")
    composeTestRule.onNodeWithTag("ageTextFieldUser").performTextInput("invalid")
    composeTestRule.onNodeWithTag("addUserButton").performClick()

    assert(userAdded == SimpleUser("John", "Doe", 0))
  }
}
