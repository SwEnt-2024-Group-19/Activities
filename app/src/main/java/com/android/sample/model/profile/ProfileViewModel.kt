package com.android.sample.model.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
open class ProfileViewModel @Inject constructor(private val repository: ProfilesRepository) :
    ViewModel() {
  private var userState_ = MutableStateFlow<User?>(null)
  open val userState: StateFlow<User?> = userState_.asStateFlow()

  init {
    observeAuthState()
  }

  private fun observeAuthState() {
    repository.observeAuthState(
        onSignedIn = { fetchUserData(it) }, onSignedOut = { clearUserData() })
  }

  fun fetchUserData(userId: String) {
    repository.getUser(
        userId,
        onSuccess = { userState_.value = it },
        onFailure = { Log.e("error", " not fetching") })
  }

  fun clearUserData() {
    userState_.value = null
  }

  fun createUserProfile(userProfile: User, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    repository.addProfileToDatabase(
        userProfile = userProfile,
        onSuccess = { onSuccess() },
        onFailure = { error -> onError(error) })
  }

  fun addActivity(userId: String, activityId: String) {
    repository.addActivity(
        userId = userId,
        activityId = activityId,
        onSuccess = { fetchUserData(userId) },
        onFailure = {})
  }

  fun addLikedActivity(userId: String, activityId: String) {
    repository.addLikedActivity(
        userId = userId,
        activityId = activityId,
        onSuccess = { fetchUserData(userId) },
        onFailure = {})
  }

  fun removeLikedActivity(userId: String, activityId: String) {
    repository.removeLikedActivity(
        userId = userId,
        activityId = activityId,
        onSuccess = { fetchUserData(userId) },
        onFailure = {})
  }

  fun updateProfile(user: User) {
    repository.updateProfile(user = user, onSuccess = { fetchUserData(user.id) }, onFailure = {})
  }
}
