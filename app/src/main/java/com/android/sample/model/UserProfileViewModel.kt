package com.android.sample.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class UserProfileViewModel(private val repository: ProfilesRepository, userId: String) :
    ViewModel() {
  private var userState_ = MutableStateFlow<User?>(null)
  open val userState: StateFlow<User?> = userState_.asStateFlow()

  init {
    fetchUserData(userId)
  }

  fun fetchUserData(userId: String) {
    repository.getUser(
        userId,
        onSuccess = { userState_.value = it },
        onFailure = { Log.e("error", " not fetching") })
  }

  companion object {
    fun provideFactory(repository: ProfilesRepository, userId: String): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return UserProfileViewModel(repository, userId) as T
        }
      }
    }
  }
}
