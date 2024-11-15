package com.android.sample.model.authentication

import android.util.Log
import com.android.sample.model.auth.SignInRepository
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.delay
import org.mockito.Mockito

class MockSignInRepository : SignInRepository {

  override suspend fun signInWithEmail(email: String, password: String): AuthResult {
    // Simulate a delay and return a mocked AuthResult object
    delay(500)
    return Mockito.mock(AuthResult::class.java).apply {
    }
  }

  override suspend fun signInWithGoogle(idToken: String): AuthResult {
    // Simulate a delay and return a mocked AuthResult object
    delay(500)
    return Mockito.mock(AuthResult::class.java).apply {
      // Configure any specific behavior if needed
    }
  }

  override fun checkUserProfile(
    uid: String?,
    navigationActions: NavigationActions,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
  ) {
    // Simulate the profile check logic
    if (true) { // Will be correctly implemented when an e2e will test onAuthError
      Log.d("MockSignInRepository", "User profile found")
      onAuthSuccess()
      navigationActions.navigateTo(Screen.OVERVIEW)
    } else {
      onAuthError("User profile not found")
    }
  }

  override fun signOut() {
    // Simulate the sign-out logic
    println("User signed out")
  }
}
