package com.android.sample.model.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sample.model.activity.database.AppDatabase
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.network.NetworkManager
import com.android.sample.ui.components.performOfflineAwareAction
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
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
constructor(
    private val repository: ProfilesRepository,
    private val localDatabase: AppDatabase,
    signInRepository: SignInRepository
) : ViewModel() {
  private var userState_ = MutableStateFlow<User?>(null)
  open val userState: StateFlow<User?> = userState_.asStateFlow()

  init {
    signInRepository.observeAuthState(
        onSignedIn = { fetchUserData(it) }, onSignedOut = { clearUserData() })
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

  fun getUserData(userId: String, onResult: (User?) -> Unit) {
    repository.getUser(userId = userId, onSuccess = onResult, onFailure = { onResult(null) })
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

  fun addReview(userId: String, activityId: String, review: Boolean?) {
    getUserData(
        userId,
        onResult = {
          val updatedUser =
              it?.copy(
                  likes =
                      it.likes.toMutableMap().apply {
                        if (review != null) put(activityId, review) else remove(activityId)
                      }) ?: return@getUserData
          repository.updateProfile(user = updatedUser, onSuccess = {}, onFailure = {})
        })
  }

  /** Navigate to the appropriate screen based on the category */
  fun navigateToActivity(navigationActions: NavigationActions, context: Context) {
    val networkManager = NetworkManager(context)
    performOfflineAwareAction(
        context,
        networkManager,
        onPerform = { navigationActions.navigateTo(Screen.ACTIVITY_DETAILS) })
  }
}
