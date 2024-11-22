package com.android.sample.model.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
open class ListActivitiesViewModel
@Inject
constructor(
    private val repository: ActivitiesRepository,
) : ViewModel() {

  private val selectedActivity_ = MutableStateFlow<Activity?>(null)
  open val selectedActivity: StateFlow<Activity?> = selectedActivity_.asStateFlow()

  private val _uiState = MutableStateFlow<ActivitiesUiState>(ActivitiesUiState.Success(emptyList()))
  open val uiState: StateFlow<ActivitiesUiState> = _uiState

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
  sealed class ActivitiesUiState {
    data class Success(val activities: List<Activity>) : ActivitiesUiState()

    data class Error(val exception: Exception) : ActivitiesUiState()
  }
}
