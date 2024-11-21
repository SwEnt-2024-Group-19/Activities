// SignInViewModel.kt
package com.android.sample.model.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.ProfilesRepositoryFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
open class SignInViewModel
@Inject
constructor(
    private val signInRepository: SignInRepository,
    private val profilesRepository: ProfilesRepository
) : ViewModel() {

  // Handles Email/Password Sign-in
  fun signInWithEmailAndPassword(
      email: String,
      password: String,
      onProfileExists: () -> Unit,
      onProfileMissing: () -> Unit,
      onFailure: (String) -> Unit
  ) {
    viewModelScope.launch {
      try {
        val authResult = signInRepository.signInWithEmail(email, password)
        checkUserProfile(authResult.user?.uid, onProfileExists, onProfileMissing, onFailure)
      } catch (e: Exception) {
        Log.e("SignInViewModel", "Error signing in with email", e)
        onFailure(e.message ?: "Unknown error")
      }
    }
  }

  // Handles Google Sign-in (called from the Composable)
  fun handleGoogleSignInResult(
      idToken: String?,
      onProfileExists: () -> Unit,
      onProfileMissing: () -> Unit,
      onFailure: (String) -> Unit
  ) {
    if (idToken == null) {
      onFailure("Google Sign-in failed! Token is null.")
      return
    }
    viewModelScope.launch {
      try {
        val authResult = signInRepository.signInWithGoogle(idToken)
        checkUserProfile(authResult.user?.uid, onProfileExists, onProfileMissing, onFailure)
      } catch (e: Exception) {
        Log.e("SignInViewModel", "Error signing in with Google", e)
        onFailure(e.message ?: "Unknown error")
      }
    }
  }

  fun signOut() {
    signInRepository.signOut()
  }

  private fun checkUserProfile(
      uid: String?,
      onProfileExists: () -> Unit,
      onProfileMissing: () -> Unit,
      onFailure: (String) -> Unit
  ) {
    uid?.let {
      profilesRepository.getUser(
          it,
          { user ->
            if (user != null) {
              onProfileExists()
            } else {
              onProfileMissing()
            }
          },
          {
            Log.e("SignInViewModel", "Error checking user profile", it)
            onFailure("Error checking user profile")
          })
    } ?: onFailure("UID is null")
  }

  companion object {
    fun Factory(): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Create FirebaseAuth instance
            val firebaseAuth = FirebaseAuth.getInstance()
            // Create Firestore instance
            val firestore = Firebase.firestore
            // Create ProfilesRepositoryFirestore
            val profilesRepository = ProfilesRepositoryFirestore(firestore)
            // Pass both to SignInRepositoryFirebase
            val signInRepository = SignInRepositoryFirebase(firebaseAuth, profilesRepository)
            return SignInViewModel(signInRepository, profilesRepository) as T
          }
        }
  }
}
