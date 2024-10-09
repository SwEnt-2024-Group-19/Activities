package com.android.sample.model.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class ListActivitiesViewModel : ViewModel() {
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
