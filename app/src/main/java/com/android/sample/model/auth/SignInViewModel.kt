// SignInViewModel.kt
package com.android.sample.model.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.sample.model.profile.ProfilesRepositoryFirestore
import com.android.sample.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import kotlinx.coroutines.launch

open class SignInViewModel
@Inject
constructor(
    private val signInRepository: SignInRepository,
) : ViewModel() {

  // Handles Email/Password Sign-in
  fun signInWithEmailAndPassword(
      email: String,
      password: String,
      onAuthSuccess: () -> Unit,
      onAuthError: (String) -> Unit,
      navigationActions: NavigationActions
  ) {
      Log.d("SignInViewModel", "signInWithEmailAndPassword")
    viewModelScope.launch {
      try {
        val authResult = signInRepository.signInWithEmail(email, password)
        signInRepository.checkUserProfile(
            authResult.user?.uid, navigationActions, onAuthSuccess, onAuthError)
      } catch (e: Exception) {
        onAuthError("Email login failed: ${e.message}")
      }
    }
  }

  // Handles Google Sign-in (called from the Composable)
  fun handleGoogleSignInResult(
      idToken: String?,
      onAuthSuccess: () -> Unit,
      onAuthError: (String) -> Unit,
      navigationActions: NavigationActions
  ) {
    if (idToken == null) {
      onAuthError("Google Sign-in failed! Token is null.")
      return
    }
    viewModelScope.launch {
      try {
        val authResult = signInRepository.signInWithGoogle(idToken)

        signInRepository.checkUserProfile(
            authResult.user?.uid, navigationActions, onAuthSuccess, onAuthError)
      } catch (e: Exception) {
        onAuthError("Google Sign-in failed: ${e.message}")
      }
    }
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
            return SignInViewModel(signInRepository) as T
          }
        }
  }
}
