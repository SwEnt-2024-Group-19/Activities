package com.android.sample.model.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ProfileViewModel(private val repository: ProfilesRepository, userId: String) :
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

  fun createUserProfile(userProfile: User, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    repository.addProfileToDatabase(
        userProfile = userProfile,
        onSuccess = { onSuccess() },
        onFailure = { error -> onError(error) })
  }

  companion object {
    fun Factory(uid: String): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(ProfilesRepositoryFirestore(Firebase.firestore), uid) as T
          }
        }
  }
}
