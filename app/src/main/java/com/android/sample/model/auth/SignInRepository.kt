package com.android.sample.model.auth

import com.google.firebase.auth.AuthResult

interface SignInRepository {
  suspend fun signInWithEmail(email: String, password: String): AuthResult

  suspend fun signInWithGoogle(idToken: String): AuthResult

  fun signOut()
}
