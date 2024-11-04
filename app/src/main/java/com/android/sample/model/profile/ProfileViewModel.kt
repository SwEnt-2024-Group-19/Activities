package com.android.sample.model.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
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
    Firebase.auth.addAuthStateListener { auth ->
      val currentUser = auth.currentUser
      if (currentUser != null) {
        fetchUserData(currentUser.uid)
      } else {
        Log.d("ProfileViewModel", "No user is authenticated, skipping data fetch.")
      }
    }
  }

  fun fetchUserData(userId: String) {
    repository.getUser(
        userId,
        onSuccess = { userState_.value = it },
        onFailure = { Log.e("error", " not fetching") })
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

  companion object {
    fun Factory(): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(ProfilesRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }
}
