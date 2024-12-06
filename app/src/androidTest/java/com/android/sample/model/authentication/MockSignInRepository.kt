package com.android.sample.model.authentication

import com.android.sample.model.auth.SignInRepository
import com.android.sample.resources.dummydata.e2e_Credentials
import com.android.sample.resources.dummydata.e2e_EmailToUserId
import com.android.sample.resources.dummydata.e2e_IdTokenToUserId
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

    val isCorrectCredentials = e2e_Credentials[email] == password
    if (!isCorrectCredentials) {
      throw Exception(
          "Invalid credentials: if you are expecting this to pass, make sure you have the user in the e2e_Credentials map")
    }

    val userId =
        e2e_EmailToUserId[email]
            ?: throw Exception(
                "User not found: make sure you have the user in the e2e_EmailToUserId map")
    setSignedInUserId(userId)

    `when`(firebaseUser.uid).thenReturn(userId)
    `when`(authResult.user).thenReturn(firebaseUser)

    return authResult
  }

  override suspend fun signInWithGoogle(idToken: String): AuthResult {
    val authResult = Mockito.mock(AuthResult::class.java)
    val firebaseUser = Mockito.mock(FirebaseUser::class.java)

    val userId =
        e2e_IdTokenToUserId[idToken]
            ?: throw Exception(
                "Invalid token: if you are expecting this to pass, make sure you have the user in the e2e_IdTokenToUserId map")
    setSignedInUserId(userId)

    `when`(firebaseUser.uid).thenReturn(userId)
    `when`(authResult.user).thenReturn(firebaseUser)

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
