package com.android.sample.model.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sample.model.activity.database.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
open class ProfileViewModel
@Inject
constructor(private val repository: ProfilesRepository, private val localDatabase: AppDatabase) :
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
        clearUserData()
      }
    }
  }

  fun fetchUserData(userId: String) {
    viewModelScope.launch {
      try {
        // Attempt to fetch from Firestore
        repository.getUser(
            userId = userId,
            onSuccess = { user ->
              user?.let {
                userState_.value = it
                viewModelScope.launch {
                  localDatabase.userDao().insert(it) // Cache locally
                }
              }
            },
            onFailure = {
              // If Firestore fails, load from Room
              viewModelScope.launch {
                val cachedUser = localDatabase.userDao().getUser(userId)
                userState_.value = cachedUser
              }
            })
      } catch (e: Exception) {
        // If unexpected exception occurs, fallback to Room
        val cachedUser = localDatabase.userDao().getUser(userId)
        userState_.value = cachedUser
      }
    }
  }

  fun getUserData(userId: String, onSuccess: (User?) -> Unit) {
    repository.getUser(
        userId, onSuccess = { onSuccess(it) }, onFailure = { Log.e("error", " not fetching") })
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

  fun removeJoinedActivity(userId: String, activityId: String) {
    repository.removeJoinedActivity(
        userId = userId,
        activityId = activityId,
        onSuccess = { fetchUserData(userId) },
        onFailure = {})
  }

  fun updateProfile(user: User) {
    repository.updateProfile(user = user, onSuccess = { fetchUserData(user.id) }, onFailure = {})
  }

  fun loadCachedProfile(): User? {
    return runBlocking { localDatabase.userDao().getUser(Firebase.auth.currentUser?.uid ?: "") }
  }
}
