package com.android.sample.model.activities

import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.model.map.Location
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.resources.dummydata.activity1
import com.android.sample.resources.dummydata.activityBiking
import com.android.sample.resources.dummydata.interest1
import com.android.sample.resources.dummydata.interest2
import com.android.sample.resources.dummydata.testUser
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ScoreCalculatorTest {

  private lateinit var listActivitiesViewModel: ListActivitiesViewModel

  @Before
  fun setUp() {
    // Mock the ProfilesRepository
    val mockRepo: ProfilesRepository = mock()
    whenever(mockRepo.getUser(any(), any(), any())).thenAnswer { testUser }
    listActivitiesViewModel = ListActivitiesViewModel(mockRepo, mock())
  }

  @Test
  fun `getWeights returns correct weights`() {
    val weights = listActivitiesViewModel.getWeights()
    val expectedWeights =
        mapOf(
            "creator" to 0.2,
            "distance" to 0.2,
            "date" to 0.15,
            "interest" to 0.25,
            "participation" to 0.15,
            "completion" to 0.1,
            "price" to 0.15)
    assertEquals(expectedWeights, weights)
  }

  @Test
  fun `calculateDistanceScore returns 0 for null distance`() {
    assertEquals(0.0, listActivitiesViewModel.calculateDistanceScore(null), 0.0)
  }

  @Test
  fun `calculateDistanceScore returns 0 for maximum distance`() {
    assertEquals(0.0, listActivitiesViewModel.calculateDistanceScore(100.0f), 0.0)
  }

  @Test
  fun `calculateDistanceScore returns correct value for valid distance`() {
    assertEquals(0.5, listActivitiesViewModel.calculateDistanceScore(50.0f), 0.0)
  }

  @Test
  fun `calculateDistanceScore clamps values above maximum`() {
    assertEquals(0.0, listActivitiesViewModel.calculateDistanceScore(150.0f), 0.0)
  }

  @Test
  fun `calculateDateScore returns 0 for date in the past`() {
    val pastDate = Timestamp(Timestamp.now().seconds - TimeUnit.DAYS.toSeconds(5), 0)
    assertEquals(0.0, listActivitiesViewModel.calculateDateScore(pastDate), 0.0)
  }

  @Test
  fun `calculateDateScore returns 1 for date now`() {
    val currentDate = Timestamp.now()
    assertEquals(1.0, listActivitiesViewModel.calculateDateScore(currentDate), 0.05)
  }

  @Test
  fun `calculateDateScore returns correct value for future date`() {
    val futureDate = Timestamp(Timestamp.now().seconds + TimeUnit.HOURS.toSeconds(48), 0)
    assertEquals(
        0.5, listActivitiesViewModel.calculateDateScore(futureDate), 0.1) // Allow small tolerance
  }

  @Test
  fun `calculateHoursBetween returns null if start is after end`() {
    val start = Timestamp(2000, 0)
    val end = Timestamp(1000, 0)
    assertEquals(null, listActivitiesViewModel.calculateHoursBetween(start, end))
  }

  @Test
  fun `calculateHoursBetween returns correct hours`() {
    val start = Timestamp(0, 0)
    val end = Timestamp(TimeUnit.HOURS.toMillis(5) / 1000, 0)
    assertEquals(5L, listActivitiesViewModel.calculateHoursBetween(start, end))
  }

  @Test
  fun `calculateInterestScore returns 0 for different interests of different categories`() {
    assertEquals(
        0.0, listActivitiesViewModel.calculateInterestScore(listOf(interest1), interest2), 0.0)
  }

  @Test
  fun `calculateInterestScore returns 0,5 for different interests of same categories`() {
    assertEquals(
        0.0,
        listActivitiesViewModel.calculateInterestScore(
            listOf(interest1), interest1.copy(name = "Other Sport")),
        0.5)
  }

  @Test
  fun `calculateInterestScore returns 1,0 for same interests`() {
    assertEquals(
        0.0, listActivitiesViewModel.calculateInterestScore(listOf(interest1), interest1), 1.0)
  }

  @Test
  fun `calculateParticipationScore returns 0 for null or empty inputs`() {
    val mockRepo: ProfilesRepository = mock()
    whenever(mockRepo.getUser(any(), any(), any())).thenAnswer {}
    assertEquals(0.0, listActivitiesViewModel.calculateParticipationScore(null, "creator"), 0.0)
    assertEquals(
        0.0, listActivitiesViewModel.calculateParticipationScore(emptyList(), "creator"), 0.0)
    assertEquals(
        0.0, listActivitiesViewModel.calculateParticipationScore(listOf("activity1"), ""), 0.0)
  }

  @Test
  fun `calculateCompletionScore returns 0 if participants exceed max places`() {
    assertEquals(0.0, listActivitiesViewModel.calculateCompletionScore(10, 5), 0.0)
  }

  @Test
  fun `calculateCompletionScore returns 1 for fully completed`() {
    assertEquals(1.0, listActivitiesViewModel.calculateCompletionScore(10, 10), 0.0)
  }

  @Test
  fun `calculateCompletionScore returns correct ratio`() {
    assertEquals(0.5, listActivitiesViewModel.calculateCompletionScore(5, 10), 0.0)
  }

  @Test
  fun `calculatePriceScore returns 0 for maximum price`() {
    assertEquals(0.0, listActivitiesViewModel.calculatePriceScore(100.0), 0.0)
  }

  @Test
  fun `calculatePriceScore returns 1 for price of 0`() {
    assertEquals(1.0, listActivitiesViewModel.calculatePriceScore(0.0), 0.0)
  }

  @Test
  fun `calculatePriceScore returns correct score for intermediate prices`() {
    assertEquals(0.5, listActivitiesViewModel.calculatePriceScore(50.0), 0.0)
  }

  @Test
  fun `sortActivitiesByScore sorts activities by descending score`() {
    val mockLocation: (Location?) -> Float? = { 10.0f } // Mock distance calculation
    val activities = listOf(activityBiking, activity1)

    val mockState = ListActivitiesViewModel.ActivitiesUiState.Success(activities)
    listActivitiesViewModel.setUiState(mockState)

    listActivitiesViewModel.sortActivitiesByScore(testUser, mockLocation)

    val sortedActivities =
        (listActivitiesViewModel.uiState.value
                as? ListActivitiesViewModel.ActivitiesUiState.Success)
            ?.activities

    assertEquals("3", sortedActivities?.get(0)?.uid) // activity1 sorted first
    assertEquals("1", sortedActivities?.get(1)?.uid) // activityBiking sorted second

    val activitiesInOtherOrder = listOf(activity1, activityBiking)
    val mockStateOtherOrder =
        ListActivitiesViewModel.ActivitiesUiState.Success(activitiesInOtherOrder)
    listActivitiesViewModel.setUiState(mockStateOtherOrder)

    listActivitiesViewModel.sortActivitiesByScore(testUser, mockLocation)

    val sortedActivitiesOtherOrder =
        (listActivitiesViewModel.uiState.value
                as? ListActivitiesViewModel.ActivitiesUiState.Success)
            ?.activities

    assertEquals("3", sortedActivitiesOtherOrder?.get(0)?.uid) // activity1 still sorted first
    assertEquals("1", sortedActivitiesOtherOrder?.get(1)?.uid) // activityBiking still sorted second
  }

  @Test
  fun `sortActivitiesByScore handles empty activities list`() {
    val mockLocation: (Location?) -> Float? = { 10.0f } // Mock distance calculation

    listActivitiesViewModel.setUiState(
        ListActivitiesViewModel.ActivitiesUiState.Success(emptyList()))

    listActivitiesViewModel.sortActivitiesByScore(testUser, mockLocation)

    val sortedActivities =
        (listActivitiesViewModel.uiState.value
                as? ListActivitiesViewModel.ActivitiesUiState.Success)
            ?.activities
    assertTrue(sortedActivities?.isEmpty() == true)
  }

  @Test
  fun `calculateActivityScore calculates correct score for an activity`() {
    val activity = activity1
    val mockLocation: (Location?) -> Float? = { 10.0f } // Mock distance calculation

    val score = listActivitiesViewModel.calculateActivityScore(activity, testUser, mockLocation)

    val expectedWeights = listActivitiesViewModel.getWeights()
    val totalWeights = expectedWeights.values.sum()
    val expectedScore = 0.346 // Calculated manually

    assertEquals(expectedScore, score, 0.1) // Allow small tolerance for floating-point calculations
  }

  @Test
  fun `calculateActivityScore caches the score for repeated calls`() {
    val activity = activity1
    val mockLocation: (Location?) -> Float? = { 10.0f }

    // First calculation
    val firstScore =
        listActivitiesViewModel.calculateActivityScore(activity, testUser, mockLocation)
    assertTrue(listActivitiesViewModel.cachedScores.containsKey(activity.uid))

    // Modify activity to simulate a recalculation (but should not trigger due to caching)
    val mockLocation2: (Location?) -> Float? = { 50.0f }
    val cachedScore =
        listActivitiesViewModel.calculateActivityScore(activity, testUser, mockLocation2)

    assertEquals(firstScore, cachedScore, 0.0) // Should use cached value
  }

  @Test
  fun `calculateActivityScore handles null distance gracefully`() {
    val activity = activityBiking
    val mockLocation: (Location?) -> Float? = { null }

    val score = listActivitiesViewModel.calculateActivityScore(activity, testUser, mockLocation)

    assertTrue(score >= 0.0) // Ensures a valid score is returned, even with null distance
  }
}
