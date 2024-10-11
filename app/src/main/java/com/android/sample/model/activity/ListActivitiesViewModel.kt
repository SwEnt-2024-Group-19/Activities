package com.android.sample.model.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ListActivitiesViewModel : ViewModel() {
  private val activity_ = MutableStateFlow<List<Activity>>(emptyList())
  val activity: StateFlow<List<Activity>> = activity_.asStateFlow()

  // Selected todo, i.e the todo for the detail view
  private val selectedActivity_ = MutableStateFlow<Activity?>(null)
  open val selectedActivity: StateFlow<Activity?> = selectedActivity_.asStateFlow()

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ListActivitiesViewModel(
                /** TODO * */
                )
                as T
          }
        }
  }
}
