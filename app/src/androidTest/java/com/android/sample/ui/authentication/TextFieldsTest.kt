package com.android.sample.ui.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.ui.components.PasswordTextField
import org.junit.Rule
import org.junit.Test

class TextFieldsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testPasswordIsMaskedByDefault() {
    val password = "testPassword"
    val isPasswordVisible = mutableStateOf(false)

    composeTestRule.setContent {
      PasswordTextField(
          password = password,
          onPasswordChange = {},
          isPasswordVisible = isPasswordVisible.value,
          onPasswordVisibilityChange = { isPasswordVisible.value = !isPasswordVisible.value })
    }

    // Check that the trailing icon shows "Show password", indicating the password is masked
    composeTestRule.onNodeWithContentDescription("Show password").assertExists()
  }

  @Test
  fun testPasswordVisibilityToggle() {
    val password = "testPassword"
    val isPasswordVisible = mutableStateOf(false)

    composeTestRule.setContent {
      PasswordTextField(
          password = password,
          onPasswordChange = {},
          isPasswordVisible = isPasswordVisible.value,
          onPasswordVisibilityChange = { isPasswordVisible.value = !isPasswordVisible.value })
    }

    composeTestRule.onNodeWithText("Password").assertExists()

    // Initially, the icon should indicate that the password is hidden ("Show password")
    composeTestRule.onNodeWithContentDescription("Show password").assertExists()

    // Click the visibility icon to show the password
    composeTestRule.onNodeWithContentDescription("Show password").performClick()

    // Now the icon should indicate that the password is visible ("Hide password")
    composeTestRule.onNodeWithContentDescription("Hide password").assertExists()

    // Click the visibility icon to hide the password again
    composeTestRule.onNodeWithContentDescription("Hide password").performClick()

    // The icon should go back to indicating that the password is hidden ("Show password")
    composeTestRule.onNodeWithContentDescription("Show password").assertExists()
  }
}
