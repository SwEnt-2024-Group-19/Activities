package com.android.sample.model.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class SignInRepositoryFirebase
@Inject
constructor(private val auth: FirebaseAuth) :
    SignInRepository {
  override suspend fun signInWithEmail(email: String, password: String): AuthResult {
    return auth.signInWithEmailAndPassword(email, password).await()
  }

  override suspend fun signInWithGoogle(idToken: String): AuthResult {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    return auth.signInWithCredential(credential).await()
  }

  override fun signOut() {
    auth.signOut()
  }
}
