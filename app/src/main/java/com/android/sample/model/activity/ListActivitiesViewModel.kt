package com.android.sample.model.activity

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sample.App
import com.android.sample.model.map.Location
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

  private val selectedUser_ = MutableStateFlow<User?>(null)
  open val selectedUser: StateFlow<User?> = selectedUser_.asStateFlow()

  private val _uiState = MutableStateFlow<ActivitiesUiState>(ActivitiesUiState.Success(emptyList()))
  open val uiState: StateFlow<ActivitiesUiState> = _uiState

  // Filter state variables
  var maxPrice by mutableStateOf(Double.MAX_VALUE)
  var availablePlaces by mutableStateOf<Int?>(null)
  var minDate by mutableStateOf<Timestamp?>(null)
  var maxDate by mutableStateOf<Timestamp?>(null)

  var startTime by mutableStateOf<String?>(null)
  var endTime by mutableStateOf<String?>(null)
  var distance by mutableStateOf<Double?>(null)
  var onlyPRO by mutableStateOf(false)

  /** Set the UI state to a new value For testing purposes only */
  open fun setUiState(state: ActivitiesUiState) {
    _uiState.value = state
  }

  private val cachedScores_ = mutableMapOf<String, Double>()
  open val cachedScores = cachedScores_

  init {
    repository.init { viewModelScope.launch { getActivities() } }
  }

  /**
   * Updates the filter state with the provided parameters.
   *
   * @param price The maximum price for filtering activities. If null, defaults to Double.MAX_VALUE.
   * @param placesAvailable The number of available places for filtering activities.
   * @param minDateTimestamp The minimum date for filtering activities.
   * @param maxDateTimestamp The maximum date for filtering activities.
   * @param startTime The start time for filtering activities.
   * @param endTime The end time for filtering activities.
   * @param distance The maximum distance for filtering activities.
   * @param seeOnlyPRO A boolean indicating whether to filter only PRO activities. If null, defaults
   *   to false.
   */
  fun updateFilterState(
      price: Double?,
      placesAvailable: Int?,
      minDateTimestamp: Timestamp?,
      maxDateTimestamp: Timestamp?,
      startTime: String?,
      endTime: String?,
      distance: Double?,
      seeOnlyPRO: Boolean?
  ) {
    maxPrice = price ?: Double.MAX_VALUE
    availablePlaces = placesAvailable
    minDate = minDateTimestamp
    maxDate = maxDateTimestamp
    this.startTime = startTime
    this.endTime = endTime
    this.distance = distance
    onlyPRO = seeOnlyPRO ?: false
  }

  /** Generates a new unique identifier. */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /**
   * Adds a new activity to the repository.
   *
   * @param activity The activity to be added.
   */
  fun addActivity(activity: Activity) {
    repository.addActivity(
        activity,
        {
          getActivities()
          viewModelScope.launch {
            try {
              Firebase.auth.currentUser?.uid?.let { currentUserId ->
                profilesRepository.getUser(
                    userId = currentUserId,
                    onSuccess = { currentUser ->
                      if (currentUser != null) {
                        App.getInstance()
                            .scheduleNotification(
                                activity = activity, isCreator = activity.creator == currentUser.id)
                      }
                    },
                    onFailure = { e ->
                      Log.e("ListActivitiesViewModel", "Failed to schedule notification", e)
                    })
              }
            } catch (e: Exception) {
              Log.e("ListActivitiesViewModel", "Error scheduling notification", e)
            }
          }
        },
        { error -> Log.e("ListActivitiesViewModel", "Failed to add activity", error) })
  }

  /**
   * Updates an existing activity in the repository.
   *
   * @param activity The activity to be updated.
   */
  fun updateActivity(activity: Activity) {
    repository.updateActivity(
        activity,
        {
          getActivities()
          // notification scheduling
          Firebase.auth.currentUser?.uid?.let { currentUserId ->
            profilesRepository.getUser(
                userId = currentUserId,
                onSuccess = { currentUser ->
                  if (currentUser != null) {
                    App.getInstance()
                        .scheduleNotification(
                            activity = activity, isCreator = activity.creator == currentUser.id)
                  }
                },
                onFailure = { e ->
                  Log.e("ListActivitiesViewModel", "Failed to schedule notification for update", e)
                })
          }
        },
        {})
  }

  /**
   * Deletes an activity from the repository by its identifier.
   *
   * @param id The identifier of the activity to be deleted.
   */
  fun deleteActivityById(id: String) {
    // Get activity before deletion to access its data
    val activityToDelete =
        (_uiState.value as? ActivitiesUiState.Success)?.activities?.find { it.uid == id }

    activityToDelete?.let { activity ->
      // Send deletion notification (which also cancels any scheduled notifications)
      App.getInstance().sendDeletionNotification(activity)
    }
    repository.deleteActivityById(id, { getActivities() }, {})
  }

  /**
   * Selects an activity.
   *
   * @param activity The activity to be selected.
   */
  fun selectActivity(activity: Activity) {
    selectedActivity_.value = activity
  }

  /**
   * Selects a user.
   *
   * @param user The user to be selected.
   */
  fun selectUser(user: User) {
    selectedUser_.value = user
  }

  /**
   * Updates the list of activities.
   *
   * @param onSuccess The callback to be invoked upon successful retrieval of activities.
   * @param onFailure The callback to be invoked upon failure to retrieve activities.
   */
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

  /**
   * Reviews an activity.
   *
   * @param activity The activity to be reviewed.
   * @param userId The identifier of the user reviewing the activity.
   * @param review The review to be added.
   */
  fun reviewActivity(activity: Activity, userId: String, review: Boolean?) {
    val newLikes = activity.likes.plus(userId to review)
    val newActivity = activity.copy(likes = newLikes)
    updateActivity(newActivity)
  }

  /**
   * Sorts the activities by score.
   *
   * @param user The user to sort the activities for.
   * @param distanceTo The function to calculate the distance to the activity.
   */
  fun sortActivitiesByScore(user: User, distanceTo: (Location?) -> Float?) {
    val activities =
        (_uiState.value as? ActivitiesUiState.Success)?.activities?.sortedByDescending {
          calculateActivityScore(it, user, distanceTo)
        }

    if (activities != null) _uiState.value = ActivitiesUiState.Success(activities)
  }

  /** Give the weights for the different factors that influence the score. */
  open fun getWeights(): Map<String, Double> {
    return mapOf(
        "distance" to 0.2,
        "date" to 0.15,
        "interest" to 0.25,
        "participation" to 0.15,
        "completion" to 0.1,
        "price" to 0.15)
  }

  /**
   * Calculates the distance score.
   *
   * @param distance The distance to the activity.
   */
  open fun calculateDistanceScore(distance: Float?): Double {
    val MAX_DISTANCE = 100.0 // 100 km
    if (distance == null) return 0.0
    return 1 - (distance / MAX_DISTANCE).coerceAtMost(1.0)
  }

  /**
   * Calculates the date score.
   *
   * @param date The date of the activity.
   */
  open fun calculateDateScore(date: Timestamp): Double {
    val MAX_HOURS = 96.0 // 4 days
    val hoursBetween = calculateHoursBetween(Timestamp.now(), date)?.toDouble() ?: return 0.0

    return 1 - (hoursBetween / MAX_HOURS).coerceAtMost(1.0)
  }

  /**
   * Calculates the hours between two timestamps.
   *
   * @param start The start timestamp.
   * @param end The end timestamp.
   */
  open fun calculateHoursBetween(start: Timestamp, end: Timestamp): Long? {
    val startMillis = start.toDate().time
    val endMillis = end.toDate().time

    if (startMillis > endMillis) return null

    val differenceMillis = endMillis - startMillis
    return TimeUnit.MILLISECONDS.toHours(differenceMillis)
  }

  /** Calculates the interest score. */
  open fun calculateInterestScore(): Double {
    // return user.interests.count { it == activity.category }.toDouble() /
    // user.interests.size.coerceAtLeast(1) TODO in Sprint 7
    return 0.0
  }

  /**
   * Calculates the participation score.
   *
   * @param userActivities The activities of the user.
   * @param creator The creator of the activity.
   */
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

  /**
   * Calculates the completion score.
   *
   * @param numberParticipants The number of participants in the activity.
   * @param maxPlaces The maximum number of places in the activity.
   */
  open fun calculateCompletionScore(numberParticipants: Int, maxPlaces: Long): Double {
    if (maxPlaces == 0L || numberParticipants == 0 || numberParticipants > maxPlaces.toInt())
        return 0.0
    return (numberParticipants.toDouble() / maxPlaces).coerceAtMost(1.0)
  }

  /**
   * Calculates the price score.
   *
   * @param price The price of the activity.
   */
  open fun calculatePriceScore(price: Double): Double {
    val MAX_PRICE = 100.0 // Maximum reasonable price
    return 1 - (price / MAX_PRICE).coerceAtMost(1.0)
  }

  /**
   * Calculates the score of an activity.
   *
   * @param activity The activity to calculate the score for.
   * @param user The user to calculate the score for.
   * @param distanceTo The function to calculate the distance to the activity.
   */
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
