package com.android.sample.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class ReviewActivityButtonsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun clickingLikeButton_updatesStateAndCallsReview() {
    val reviewMock: (Boolean?) -> Unit = mock()

    composeTestRule.setContent { ReviewActivityButtons(currentReview = null, review = reviewMock) }

    composeTestRule.onNodeWithTag("likeIconButton_false").performClick()
    verify(reviewMock).invoke(true) // Clicking like should set the state to `true`
    reset(reviewMock)

    // Click again to toggle back to null
    composeTestRule.onNodeWithTag("likeIconButton_true").performClick()
    verify(reviewMock).invoke(null)
  }

  @Test
  fun clickingDislikeButton_updatesStateAndCallsReview() {
    val reviewMock: (Boolean?) -> Unit = mock()

    composeTestRule.setContent { ReviewActivityButtons(currentReview = null, review = reviewMock) }

    composeTestRule.onNodeWithTag("dislikeIconButton_false").performClick()
    verify(reviewMock).invoke(false)
    reset(reviewMock)

    composeTestRule.onNodeWithTag("dislikeIconButton_true").performClick()
    verify(reviewMock).invoke(null)
  }

  @Test
  fun likeAndDislikeButtons_doNotInterfere() {
    val reviewMock: (Boolean?) -> Unit = mock()

    composeTestRule.setContent { ReviewActivityButtons(currentReview = null, review = reviewMock) }

    // Like button click
    composeTestRule.onNodeWithTag("likeIconButton_false").performClick()
    verify(reviewMock).invoke(true)
    reset(reviewMock)

    // Dislike button click
    composeTestRule.onNodeWithTag("dislikeIconButton_false").performClick()
    verify(reviewMock).invoke(false)
    reset(reviewMock)

    // Like button click again
    composeTestRule.onNodeWithTag("likeIconButton_false").performClick()
    verify(reviewMock).invoke(true)
  }
}
