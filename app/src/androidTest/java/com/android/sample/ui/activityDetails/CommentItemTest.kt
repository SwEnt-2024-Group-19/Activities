package com.android.sample.ui.activityDetails

import android.content.SharedPreferences
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.resources.dummydata.testComment
import com.android.sample.ui.activitydetails.CommentItem
import com.android.sample.ui.activitydetails.CommentSection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class CommentItemTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val testProfileId = "123"
  private var sharedPreferences = mock(SharedPreferences::class.java)
  private var mockImageRepository = mock(ImageRepositoryFirestore::class.java)
  private var mockImageViewModel: ImageViewModel =
      ImageViewModel(mockImageRepository, sharedPreferences)

  @Test
  fun commentItem_displaysCommentCorrectly() {
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }

    composeTestRule
        .onNodeWithTag("commentContent_${testComment.uid}")
        .assertTextContains("This is a comment")
    composeTestRule.onNodeWithText("Amine").assertIsDisplayed()
  }

  @Test
  fun deleteButton_displaysForCreator() {
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }

    composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
  }

  @Test
  fun deleteButton_doesNotDisplayForNonCreator() {
    val nonCreatorProfileId = "999"
    composeTestRule.setContent {
      CommentItem(
          profileId = nonCreatorProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }

    composeTestRule.onNodeWithText("Delete").assertDoesNotExist()
  }

  @Test
  fun replyButton_displaysForLoggedInUser() {
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("ReplyButton_${testComment.uid}").assertIsDisplayed()
  }

  @Test
  fun replyButton_displaysLoginPromptForAnonymousUser() {
    val anonymousProfileId = "anonymous"
    composeTestRule.setContent {
      CommentSection(
          profileId = anonymousProfileId,
          comments = listOf(testComment),
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          onAddComment = {},
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("notLoggedInMessage").assertIsDisplayed()
  }

  @Test
  fun replyInputField_displaysWhenReplyButtonClicked() {
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }

    composeTestRule.onNodeWithTag("ReplyButton_${testComment.uid}").performClick()
    composeTestRule.onNodeWithTag("replyInputField_${testComment.uid}").assertIsDisplayed()
  }

  @Test
  fun replyInputField_doesNotDisplayForAnonymousUser() {
    val anonymousProfileId = "anonymous"
    composeTestRule.setContent {
      CommentItem(
          profileId = anonymousProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }

    composeTestRule.onNodeWithText("Reply").assertDoesNotExist()
  }

  @Test
  fun repliesAreDisplayedCorrectly() {
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }
    composeTestRule.onNodeWithText("John")
    composeTestRule.onNodeWithText("This is a reply").assertIsDisplayed()
  }

  @Test
  fun replyButton_isNotDisplayedForReplies() {
    val replyComment = testComment.replies.first() // The reply to the original comment
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = replyComment, // Pass in the reply comment
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          allowReplies = false // Disable replies for this test
          ,
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }

    // Ensure that the reply button is not displayed for replies
    composeTestRule.onNodeWithTag("ReplyButton_${replyComment.uid}").assertDoesNotExist()
  }

  @Test
  fun replies_areCorrectlyNestedUnderComments() {
    // Set the content with the comment that has a reply
    composeTestRule.setContent {
      CommentSection(
          profileId = testProfileId,
          comments = listOf(testComment), // Pass in the comment with its reply
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          onAddComment = {},
          creatorId = "1",
          imageViewModel = mockImageViewModel)
    }

    // Ensure the original comment is displayed
    composeTestRule.onNodeWithText("This is a comment").assertIsDisplayed()
    composeTestRule.onNodeWithText("Amine")

    // Ensure the reply is displayed
    composeTestRule.onNodeWithText("This is a reply").assertIsDisplayed()
    composeTestRule.onNodeWithText("John").assertIsDisplayed()

    // Check that the reply is well nested, ensuring its indented and positioned under its origin
    // comment
    composeTestRule
        .onNodeWithText("This is a reply")
        .assertIsDisplayed()
        .assertLeftPositionInRootIsEqualTo(98.5.dp)
  }

  @Test
  fun CommentOfCreatorHasABadge() {
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          creatorId = "123",
          imageViewModel = mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("creatorBadge$testProfileId").isDisplayed()
  }

  @Test
  fun CommentOfNonCreatorHasNoBadge() {
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {},
          creatorId = "124",
          imageViewModel = mockImageViewModel)
    }
    composeTestRule.onNodeWithTag("creatorBadge$testProfileId").assertDoesNotExist()
  }
}
