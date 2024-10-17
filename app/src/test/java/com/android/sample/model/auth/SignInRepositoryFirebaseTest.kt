package com.android.sample.model.auth

import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.profile.ProfilesRepositoryFirestore
import com.android.sample.model.profile.User
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SignInRepositoryFirebaseTest {

  private lateinit var firebaseAuth: FirebaseAuth
  private lateinit var signInRepository: SignInRepositoryFirebase
  private lateinit var profilesRepository: ProfilesRepositoryFirestore
  private lateinit var navigationActions: NavigationActions
  private lateinit var firebaseFirestore: FirebaseFirestore

  @Before
  fun setUp() {
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    // Mock FirebaseAuth
    firebaseAuth = mock(FirebaseAuth::class.java)
    firebaseFirestore = mock(FirebaseFirestore::class.java)
    profilesRepository = mock(ProfilesRepositoryFirestore::class.java)
    navigationActions = mock(NavigationActions::class.java)
    signInRepository = SignInRepositoryFirebase(firebaseAuth, profilesRepository)
  }

  @Test
  fun `signInWithEmail should return AuthResult on success`() = runBlocking {
    // Given
    val email = "test@example.com"
    val password = "password123"
    val mockAuthResult = mock(AuthResult::class.java)

    // Mock the Task<AuthResult> returned by signInWithEmailAndPassword
    val mockTask = Tasks.forResult(mockAuthResult)
    `when`(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)

    // When
    val result = signInRepository.signInWithEmail(email, password)

    // Then
    verify(firebaseAuth).signInWithEmailAndPassword(email, password)
    assert(result == mockAuthResult)
  }

  @Test(expected = Exception::class)
  fun `signInWithEmail should throw exception on failure`(): Unit = runBlocking {
    // Given
    val email = "test@example.com"
    val password = "password123"

    // Mock the Task<AuthResult> to return an exception
    val mockTask = Tasks.forException<AuthResult>(Exception("Firebase sign-in failed"))
    `when`(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)

    // When
    signInRepository.signInWithEmail(email, password) // Should throw the exception

    // Then exception is expected
  }

  @Test
  fun `signInWithGoogle should return AuthResult on success`() = runBlocking {
    // Given
    val idToken = "testGoogleIdToken"
    val mockAuthResult = mock(AuthResult::class.java)

    // Create the mock credential
    val mockCredential = mock(AuthCredential::class.java)

    // Mock the static method GoogleAuthProvider.getCredential
    mockStatic(GoogleAuthProvider::class.java).use { googleAuthProviderMock ->
      googleAuthProviderMock
          .`when`<AuthCredential> { GoogleAuthProvider.getCredential(idToken, null) }
          .thenReturn(mockCredential)

      // Mock the Task<AuthResult> returned by signInWithCredential
      val mockTask = Tasks.forResult(mockAuthResult)
      `when`(firebaseAuth.signInWithCredential(mockCredential)).thenReturn(mockTask)

      // When
      val result = signInRepository.signInWithGoogle(idToken)

      // Then
      verify(firebaseAuth).signInWithCredential(mockCredential)
      assert(result == mockAuthResult)
    }
  }

  @Test(expected = Exception::class)
  fun `signInWithGoogle should throw exception on failure`(): Unit = runBlocking {
    // Given
    val idToken = "testGoogleIdToken"

    // Create the mock credential
    val mockCredential = GoogleAuthProvider.getCredential(idToken, null)

    // Mock the Task<AuthResult> to return an exception
    val mockTask = Tasks.forException<AuthResult>(Exception("Google sign-in failed"))
    `when`(firebaseAuth.signInWithCredential(mockCredential)).thenReturn(mockTask)

    // When
    signInRepository.signInWithGoogle(idToken) // Should throw the exception
  }

  @Test
  fun `checkUserProfile should call onAuthError if user ID is null`() {
    // Given
    val onAuthError = mock<(String) -> Unit>()

    // When
    signInRepository.checkUserProfile(null, navigationActions, {}, onAuthError)

    // Then
    verify(onAuthError).invoke("User ID is null, cannot proceed!")
  }

  @Test
  fun `checkUserProfile should navigate to create profile if user profile is not found`() =
      runTest {
        val uid = "testUid"
        // Mock getUser to return null (no profile found)
        `when`(profilesRepository.getUser(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
          println("Simulating getUser callback with null profile")
          val onSuccess = it.arguments[1] as (Any?) -> Unit
          onSuccess(null) // Simulate no profile found
        }

        `when`(navigationActions.currentRoute()).thenReturn(Screen.AUTH)
        // When
        signInRepository.checkUserProfile(uid, navigationActions, {}, {})

        advanceUntilIdle()
        // Then
        verify(navigationActions).navigateTo(Screen.CREATE_PROFILE)
      }

  @Test
  fun `checkUserProfile should navigate to overview if user profile is found`() = runTest {
    val uid = "testUid"
    val mockUserProfile = mock(User::class.java)

    // Mock getUser to return a user profile
    `when`(profilesRepository.getUser(eq(uid), anyOrNull(), anyOrNull())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (Any?) -> Unit
      onSuccess(mockUserProfile) // Simulate profile found
    }
    `when`(navigationActions.currentRoute()).thenReturn(Screen.AUTH)

    // When
    signInRepository.checkUserProfile(uid, navigationActions, {}, {})

    // Then
    verify(navigationActions).navigateTo(Screen.OVERVIEW)
  }

  @Test
  fun `checkUserProfile should call onAuthError if getUser fails`() = runTest {
    val uid = "testUid"
    // Mock getUser to throw an exception
    `when`(profilesRepository.getUser(eq(uid), anyOrNull(), anyOrNull())).thenAnswer { invocation ->
      val onFailure = invocation.arguments[2] as (Exception) -> Unit
      onFailure(Exception("getUser failed")) // Simulate failure
    }

    // When
    signInRepository.checkUserProfile(uid, navigationActions, {}, {})

    // Then
    verify(navigationActions, never()).navigateTo(Screen.CREATE_PROFILE)
  }
}
