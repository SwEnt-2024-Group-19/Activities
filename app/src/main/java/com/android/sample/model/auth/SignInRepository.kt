package com.android.sample.model.auth

import com.google.firebase.auth.AuthResult

interface SignInRepository {
  fun observeAuthState(onSignedIn: (String) -> Unit, onSignedOut: () -> Unit)

  suspend fun signInWithEmail(email: String, password: String): AuthResult

  suspend fun signInWithGoogle(idToken: String): AuthResult

  fun signOut()
}
