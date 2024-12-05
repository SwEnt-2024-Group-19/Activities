package com.android.sample.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.simpleUser
import com.android.sample.resources.dummydata.testUser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class AttendantPreviewTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var mockImageRepository: ImageRepositoryFirestore
  private lateinit var mockImageViewModel: ImageViewModel

  @Before
  fun setUp() {
    mockImageRepository = mock(ImageRepositoryFirestore::class.java)
    mockImageViewModel = ImageViewModel(mockImageRepository)
  }

  @Test
  fun attendantPreview_displaysUserNameAndSurname() {
    composeTestRule.setContent { AttendantPreview(testUser, {}, mockImageViewModel, {}, 0) }

    composeTestRule.onNodeWithTag("attendeeName0", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("${testUser.name} ${testUser.surname}").assertIsDisplayed()
  }

  @Test
  fun attendantPreview_displaysDefaultIconWhenUserIdIsEmpty() {
    composeTestRule.setContent { AttendantPreview(simpleUser, {}, mockImageViewModel, {}, 0) }

    composeTestRule.onNodeWithContentDescription("Person").assertIsDisplayed()
  }

  @Test
  fun attendantPreview_callsOnProfileClickWhenRowIsClicked() {
    var clickedUser: User? = null
    composeTestRule.setContent {
      AttendantPreview(testUser, { clickedUser = it }, mockImageViewModel, {}, 0)
    }

    composeTestRule.onNodeWithTag("attendeeRow0").performClick()
    assertEquals(testUser, clickedUser)
  }

  @Test
  fun attendantPreview_callsDeleteAttendantWhenRemoveButtonIsClicked() {
    var deletedUser: User? = null
    composeTestRule.setContent {
      AttendantPreview(testUser, {}, mockImageViewModel, { deletedUser = it }, 0)
    }

    composeTestRule.onNodeWithTag("removeAttendeeButton").performClick()
    assertEquals(testUser, deletedUser)
  }
}
