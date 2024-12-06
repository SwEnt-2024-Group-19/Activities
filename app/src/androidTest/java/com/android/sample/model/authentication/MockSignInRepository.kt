package com.android.sample.model.authentication

import com.android.sample.model.auth.SignInRepository
import com.android.sample.resources.dummydata.testUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class MockSignInRepository : SignInRepository {
  private var signedInUserId: String? = null
  private val authStateListeners = mutableListOf<(String?) -> Unit>()

  override fun observeAuthState(onSignedIn: (String) -> Unit, onSignedOut: () -> Unit) {
    authStateListeners.add { userId -> if (userId != null) onSignedIn(userId) else onSignedOut() }
    notifyAuthStateChanged()
  }

  override suspend fun signInWithEmail(email: String, password: String): AuthResult {
    val authResult = Mockito.mock(AuthResult::class.java)
    val firebaseUser = Mockito.mock(FirebaseUser::class.java)
    `when`(firebaseUser.uid).thenReturn(testUser.id)
    `when`(authResult.user).thenReturn(firebaseUser)

    // Return a mocked AuthResult object with a test user
    return authResult
  }

  override suspend fun signInWithGoogle(idToken: String): AuthResult {
    val authResult = Mockito.mock(AuthResult::class.java)
    val firebaseUser = Mockito.mock(FirebaseUser::class.java)
    `when`(firebaseUser.uid).thenReturn(testUser.id)
    `when`(authResult.user).thenReturn(firebaseUser)

    // Return a mocked AuthResult object with a test user
    return authResult
  }

  override fun signOut() {
    setSignedInUserId(null)
  }

  private fun setSignedInUserId(userId: String?) {
    signedInUserId = userId
    notifyAuthStateChanged()
  }

  private fun notifyAuthStateChanged() {
    authStateListeners.forEach { it(signedInUserId) }
  }
}
