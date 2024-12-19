package com.android.sample.model.auth

import android.util.Log
import com.android.sample.model.profile.ProfilesRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing authentication with Firebase.
 *
 * Handles authentication-related operations such as observing auth state,
 * signing in with email or Google, and signing out.
 *
 * @param auth FirebaseAuth instance for handling authentication.
 * @param profilesRepository Repository for managing user profiles.
 */
class SignInRepositoryFirebase
@Inject
constructor(private val auth: FirebaseAuth, private val profilesRepository: ProfilesRepository) :
    SignInRepository {

  override fun observeAuthState(onSignedIn: (String) -> Unit, onSignedOut: () -> Unit) {
    auth.addAuthStateListener { auth ->
      val currentUser = auth.currentUser
      if (currentUser != null) {
        Log.d("ProfileViewModel", "User is authenticated, fetching data.")
        onSignedIn(currentUser.uid)
      } else {
        Log.d("ProfileViewModel", "No user is authenticated, skipping data fetch.")
        onSignedOut()
      }
    }
  }
  /**
   * Signs in a user using their email and password.
   *
   * This function uses Firebase Authentication's `signInWithEmailAndPassword` and suspends
   * execution until the sign-in operation is complete.
   *
   * @param email The user's email address.
   * @param password The user's password.
   * @return An `AuthResult` containing information about the authenticated user.
   */
  override suspend fun signInWithEmail(email: String, password: String): AuthResult {
    return auth.signInWithEmailAndPassword(email, password).await()
  }

  /**
   * Signs in a user using a Google ID token.
   *
   * This function uses Firebase Authentication's `signInWithCredential` with a Google credential
   * and suspends execution until the sign-in operation is complete.
   *
   * @param idToken The Google ID token obtained from the Google Sign-In flow.
   * @return An `AuthResult` containing information about the authenticated user.
   */
  override suspend fun signInWithGoogle(idToken: String): AuthResult {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    return auth.signInWithCredential(credential).await()
  }

  override fun signOut() {
    auth.signOut()
  }
}
