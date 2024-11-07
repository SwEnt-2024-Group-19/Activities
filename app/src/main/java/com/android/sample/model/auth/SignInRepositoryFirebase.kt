package com.android.sample.model.auth

import android.util.Log
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class SignInRepositoryFirebase
@Inject
constructor(private val auth: FirebaseAuth, private val profilesRepository: ProfilesRepository) :
    SignInRepository {
  override suspend fun signInWithEmail(email: String, password: String): AuthResult {
    return auth.signInWithEmailAndPassword(email, password).await()
  }

  override suspend fun signInWithGoogle(idToken: String): AuthResult {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    return auth.signInWithCredential(credential).await()
  }

  override fun checkUserProfile(
      uid: String?,
      navigationActions: NavigationActions,
      onAuthSuccess: () -> Unit,
      onAuthError: (String) -> Unit
  ) {
    if (uid == null) {
      onAuthError("User ID is null, cannot proceed!")
      return
    }

    profilesRepository.getUser(
        uid,
        onSuccess = { userProfile ->
          if (userProfile == null) {
            Log.d("SignInRepository", "No user profile found for user ID: $uid")
            navigationActions.navigateTo(Screen.CREATE_PROFILE)
          } else {
            Log.d("SignInRepository", "User profile found for user ID: $uid")
            navigationActions.navigateTo(Screen.OVERVIEW)
          }
          onAuthSuccess()
        },
        onFailure = {
          onAuthError("Error checking user profile!")
          Log.e("SignInRepository", "Error checking user profile: ${it.message}")
        })
  }

  override fun signOut() {
    auth.signOut()
  }
}
