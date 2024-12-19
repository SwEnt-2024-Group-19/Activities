package com.android.sample.model.activities

import com.android.sample.resources.dummydata.activity1
import com.android.sample.resources.dummydata.activity2
import com.android.sample.resources.dummydata.emptyUser
import com.android.sample.resources.dummydata.simpleUser
import com.android.sample.resources.dummydata.testUser
import com.android.sample.resources.dummydata.userWithActivities
import org.junit.Assert.*
import org.junit.Test

class ActivityReviewFunctionsTest {

  /**
   * Like and dislike distribution: 2 likes, 1 dislike Completion ratio: 0.5 Likes completion ratio:
   * 0.5
   */
  private val myActivity1 =
      activity1.copy(
          participants =
              listOf(
                  testUser,
                  simpleUser,
                  userWithActivities,
                  emptyUser,
                  testUser.copy(id = "user5"),
                  testUser.copy(id = "user6")),
          likes = mapOf("user1" to true, "user2" to false, "user3" to true),
          placesLeft = 6,
          maxPlaces = 12)

  /**
   * Like and dislike distribution: 1 like, 1 dislike Completion ratio: 0.6666 Likes completion
   * ratio: 0.5
   */
  private val myActivity2 =
      activity2.copy(
          participants = listOf(testUser, simpleUser, userWithActivities, emptyUser),
          likes = mapOf("user1" to true, "user2" to false),
          placesLeft = 2,
          maxPlaces = 6)

  @Test
  fun `test getActivityRating for activity1`() {
    val expectedRating = 0.6666 // 2 likes / 3 total votes
    val rating = myActivity1.getActivityRating()

    assertEquals("Rating should be correctly calculated", expectedRating, rating, 0.0001)
  }

  @Test
  fun `test getActivityRating for activity2`() {
    val expectedRating = 0.5 // 1 likes / 2 total votes
    val rating = myActivity2.getActivityRating()

    assertEquals("Rating should be correctly calculated", expectedRating, rating, 0.0001)
  }

  @Test
  fun `test getActivityWeightedRating for activity1`() {
    // (0.6666 * 0.5) + (0.5 * 0.25) + (0.5 * 0.25) = 0.5833
    val expectedWeightedRating = 0.5833
    val weightedRating = myActivity1.getActivityWeightedRating()

    assertEquals(
        "Weighted rating should be calculated correctly",
        expectedWeightedRating,
        weightedRating,
        0.0001)
  }

  @Test
  fun `test getActivityWeightedRating for activity2`() {
    // (0.5 * 0.5) + (0.6666 * 0.25) + (0.5 * 0.25) = 0.5416
    val expectedWeightedRating = 0.5416
    val weightedRating = myActivity2.getActivityWeightedRating()

    assertEquals(
        "Weighted rating should be calculated correctly",
        expectedWeightedRating,
        weightedRating,
        0.0001)
  }

  @Test
  fun `test getActivityRating when no likes or dislikes`() {
    val activityWithoutVotes = myActivity1.copy(likes = emptyMap())
    val rating = activityWithoutVotes.getActivityRating()

    assertEquals("Rating should be -1 when there are no votes", -1.0, rating, 0.0)
  }
}
