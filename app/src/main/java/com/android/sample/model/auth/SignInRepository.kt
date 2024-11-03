package com.android.sample.model.auth

import com.android.sample.ui.navigation.NavigationActions
import com.google.firebase.auth.AuthResult

interface SignInRepository {
  suspend fun signInWithEmail(email: String, password: String): AuthResult

  suspend fun signInWithGoogle(idToken: String): AuthResult

  fun checkUserProfile(
      uid: String?,
      navigationActions: NavigationActions,
      onAuthSuccess: () -> Unit,
      onAuthError: (String) -> Unit
  )

  fun signOut()
}
