package com.android.sample.model.authentication

import com.android.sample.model.auth.SignInRepository
import com.android.sample.resources.dummydata.testUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class MockSignInRepository : SignInRepository {

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
    // Simulate the sign-out logic
    println("User signed out")
  }
}
