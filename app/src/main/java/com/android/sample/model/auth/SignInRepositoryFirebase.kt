package com.android.sample.model.auth

import android.util.Log
import com.android.sample.model.profile.ProfilesRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

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
