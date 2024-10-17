package com.android.sample.model.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class ListActivitiesViewModel(private val repository: ActivitiesRepository) : ViewModel() {

  private val selectedActivity_ = MutableStateFlow<Activity?>(null)
  open val selectedActivity: StateFlow<Activity?> = selectedActivity_.asStateFlow()

  private val _uiState = MutableStateFlow<ActivitiesUiState>(ActivitiesUiState.Success(emptyList()))
  val uiState: StateFlow<ActivitiesUiState> = _uiState

  init {
    repository.init {
      // if (FirebaseAuth.getInstance().currentUser != null) {
      viewModelScope.launch { getActivities() }
      // }
    }
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

  sealed class ActivitiesUiState {
    data class Success(val activities: List<Activity>) : ActivitiesUiState()

    data class Error(val exception: Exception) : ActivitiesUiState()
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ListActivitiesViewModel(ActivitiesRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }
}
