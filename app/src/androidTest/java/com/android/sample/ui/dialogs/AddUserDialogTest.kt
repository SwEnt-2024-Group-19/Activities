package com.android.sample.ui.dialogs

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.emptyUser
import com.android.sample.resources.dummydata.simpleUser
import org.junit.Rule
import org.junit.Test

class AddUserDialogTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun addUserDialogDisplaysCorrectly() {
    composeTestRule.setContent { AddUserDialog(onDismiss = {}, onAddUser = {}) }

    composeTestRule.onNodeWithTag("nameTextFieldUser").assertExists()
    composeTestRule.onNodeWithTag("surnameTextFieldUser").assertExists()
    composeTestRule.onNodeWithTag("addUserButton").assertExists()
  }

  @Test
  fun addUserDialogAddsUserOnButtonClick() {
    var userAdded: User? = null
    composeTestRule.setContent { AddUserDialog(onDismiss = {}, onAddUser = { userAdded = it }) }

    composeTestRule.onNodeWithTag("nameTextFieldUser").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextFieldUser").performTextInput("Doe")
    composeTestRule.onNodeWithTag("addUserButton").performClick()

    assert(userAdded == simpleUser)
  }

  @Test
  fun addUserDialogDismissesOnButtonClick() {
    var dismissed = false
    composeTestRule.setContent { AddUserDialog(onDismiss = { dismissed = true }, onAddUser = {}) }

    composeTestRule.onNodeWithTag("addUserButton").performClick()

    assert(dismissed)
  }

  @Test
  fun addUserDialogHandlesEmptyFields() {
    var userAdded: User? = null
    composeTestRule.setContent { AddUserDialog(onDismiss = {}, onAddUser = { userAdded = it }) }

    composeTestRule.onNodeWithTag("addUserButton").performClick()

    assert(userAdded == emptyUser)
  }

  @Test
  fun addUserDialogHandlesInvalidAge() {
    var userAdded: User? = null
    composeTestRule.setContent { AddUserDialog(onDismiss = {}, onAddUser = { userAdded = it }) }

    composeTestRule.onNodeWithTag("nameTextFieldUser").performTextInput("John")
    composeTestRule.onNodeWithTag("surnameTextFieldUser").performTextInput("Doe")
    composeTestRule.onNodeWithTag("addUserButton").performClick()

    assert(userAdded == simpleUser)
  }
}
