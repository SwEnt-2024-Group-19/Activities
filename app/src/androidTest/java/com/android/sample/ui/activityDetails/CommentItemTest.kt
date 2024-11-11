package com.android.sample.ui.activityDetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.activity.Comment
import com.android.sample.ui.activitydetails.CommentItem
import com.android.sample.ui.activitydetails.CommentSection
import com.google.firebase.Timestamp
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommentItemTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var testComment: Comment
  private val testProfileId = "123"
  val timestamp = Timestamp.now()

  @Before
  fun setUp() {
    testComment =
        Comment(
            uid = UUID.randomUUID().toString(),
            userId = "123",
            userName = "Amine",
            content = "This is a comment",
            timestamp = timestamp,
            replies =
                listOf(
                    Comment(
                        uid = UUID.randomUUID().toString(),
                        userId = "124",
                        userName = "John",
                        content = "This is a reply",
                        timestamp = timestamp)))
  }

  @Test
  fun commentItem_displaysCommentCorrectly() {
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {})
    }

    composeTestRule
        .onNodeWithTag("commentUserNameAndContent_${testComment.uid}")
        .assertTextContains("Amine: This is a comment")
    composeTestRule
        .onNodeWithTag("commentTimestamp_${testComment.uid}")
        .assertTextContains(timestamp.toDate().toString())
  }

  @Test
  fun deleteButton_displaysForCreator() {
    composeTestRule.setContent {
      CommentItem(
          profileId = testProfileId,
          comment = testComment,
          onReplyComment = { _, _ -> },
          onDeleteComment = {})
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
          onDeleteComment = {})
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
          onDeleteComment = {})
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
          onAddComment = {})
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
          onDeleteComment = {})
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
          onDeleteComment = {})
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
          onDeleteComment = {})
    }

    composeTestRule.onNodeWithText("John: This is a reply").assertIsDisplayed()
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
          )
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
          onAddComment = {})
    }

    // Ensure the original comment is displayed
    composeTestRule.onNodeWithText("Amine: This is a comment").assertIsDisplayed()

    // Ensure the reply is displayed
    composeTestRule.onNodeWithText("John: This is a reply").assertIsDisplayed()

    // Check that the reply is well nested, ensuring its indented and positioned under its origin
    // comment
    composeTestRule
        .onNodeWithText("John: This is a reply")
        .assertIsDisplayed()
        .assertLeftPositionInRootIsEqualTo(40.05.dp)
  }
}
