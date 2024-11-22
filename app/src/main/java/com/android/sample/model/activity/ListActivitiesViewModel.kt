package com.android.sample.model.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sample.model.map.Location
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
open class ListActivitiesViewModel
@Inject
constructor(
    private val profilesRepository: ProfilesRepository,
    private val repository: ActivitiesRepository,
) : ViewModel() {

  private val selectedActivity_ = MutableStateFlow<Activity?>(null)
  open val selectedActivity: StateFlow<Activity?> = selectedActivity_.asStateFlow()

  private val _uiState = MutableStateFlow<ActivitiesUiState>(ActivitiesUiState.Success(emptyList()))
  open val uiState: StateFlow<ActivitiesUiState> = _uiState

  /** Set the UI state to a new value For testing purposes only */
  open fun setUiState(state: ActivitiesUiState) {
    _uiState.value = state
  }

  private val cachedScores_ = mutableMapOf<String, Double>()
  open val cachedScores = cachedScores_

  init {
    repository.init { viewModelScope.launch { getActivities() } }
  }

  fun getNewUid(): String {
    return repository.getNewUid()
  }

  fun addActivity(activity: Activity) {
    repository.addActivity(activity, { getActivities() }, {})
  }

  fun updateActivity(activity: Activity) {
    repository.updateActivity(activity, { getActivities() }, {})
  }

  fun deleteActivityById(id: String) {
    repository.deleteActivityById(id, { getActivities() }, {})
  }

  fun selectActivity(activity: Activity) {
    selectedActivity_.value = activity
  }

  fun getActivities(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {

    val onS = { activities: List<Activity> ->
      _uiState.value = ActivitiesUiState.Success(activities)
      onSuccess()
    }
    val onF = { exception: Exception ->
      _uiState.value = ActivitiesUiState.Error(exception)
      onFailure(exception)
    }
    repository.getActivities(onS, onF)
  }

  fun sortActivitiesByScore(user: User, distanceTo: (Location?) -> Float?) {
    val activities =
        (_uiState.value as? ActivitiesUiState.Success)?.activities?.sortedByDescending {
          calculateActivityScore(it, user, distanceTo)
        }

    if (activities != null) _uiState.value = ActivitiesUiState.Success(activities)
  }

  open fun getWeights(): Map<String, Double> {
    return mapOf(
        "distance" to 0.2,
        "date" to 0.15,
        "interest" to 0.25,
        "participation" to 0.15,
        "completion" to 0.1,
        "price" to 0.15)
  }

  open fun calculateDistanceScore(distance: Float?): Double {
    val MAX_DISTANCE = 100.0 // 100 km
    if (distance == null) return 0.0
    return 1 - (distance / MAX_DISTANCE).coerceAtMost(1.0)
  }

  open fun calculateDateScore(date: Timestamp): Double {
    val MAX_HOURS = 96.0 // 4 days
    val hoursBetween = calculateHoursBetween(Timestamp.now(), date)?.toDouble() ?: return 0.0

    return 1 - (hoursBetween / MAX_HOURS).coerceAtMost(1.0)
  }

  open fun calculateHoursBetween(start: Timestamp, end: Timestamp): Long? {
    val startMillis = start.toDate().time
    val endMillis = end.toDate().time

    if (startMillis > endMillis) return null

    val differenceMillis = endMillis - startMillis
    return TimeUnit.MILLISECONDS.toHours(differenceMillis)
  }

  open fun calculateInterestScore(): Double {
    // return user.interests.count { it == activity.category }.toDouble() /
    // user.interests.size.coerceAtLeast(1) TODO in Sprint 7
    return 0.0
  }

  open fun calculateParticipationScore(userActivities: List<String>?, creator: String): Double {
    var participationScore = 0.0
    if (userActivities.isNullOrEmpty() || creator.isEmpty()) return participationScore
    profilesRepository.getUser(
        creator,
        { creatorLambda ->
          val matchingActivities =
              creatorLambda?.activities?.count { userActivities.contains(it) } ?: 0
          participationScore = (matchingActivities.toDouble() / 10).coerceAtMost(1.0)
        },
        { participationScore = 0.0 })
    return participationScore
  }

  open fun calculateCompletionScore(numberParticipants: Int, maxPlaces: Long): Double {
    if (maxPlaces == 0L || numberParticipants == 0 || numberParticipants > maxPlaces.toInt())
        return 0.0
    return (numberParticipants.toDouble() / maxPlaces).coerceAtMost(1.0)
  }

  open fun calculatePriceScore(price: Double): Double {
    val MAX_PRICE = 100.0 // Maximum reasonable price
    return 1 - (price / MAX_PRICE).coerceAtMost(1.0)
  }

  open fun calculateActivityScore(
      activity: Activity,
      user: User,
      distanceTo: (Location?) -> Float?
  ): Double {
    val weights = getWeights()
    val totalWeights = weights.values.sum()

    if (cachedScores_.containsKey(activity.uid)) return cachedScores_[activity.uid]!!

    val distanceScore = calculateDistanceScore(distanceTo(activity.location))
    val dateScore = calculateDateScore(activity.date)
    val interestScore = calculateInterestScore()
    val participationScore = calculateParticipationScore(user.activities, activity.creator)
    val completionScore = calculateCompletionScore(activity.participants.size, activity.maxPlaces)
    val priceScore = calculatePriceScore(activity.price)

    val score =
        (distanceScore * weights["distance"]!! +
            dateScore * weights["date"]!! +
            interestScore * weights["interest"]!! +
            participationScore * weights["participation"]!! +
            completionScore * weights["completion"]!! +
            priceScore * weights["price"]!!) / totalWeights

    cachedScores_[activity.uid] = score
    return score
  }

  sealed class ActivitiesUiState {
    data class Success(val activities: List<Activity>) : ActivitiesUiState()

    data class Error(val exception: Exception) : ActivitiesUiState()
  }
}
