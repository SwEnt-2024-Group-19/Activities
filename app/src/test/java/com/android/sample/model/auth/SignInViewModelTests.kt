// import path for your androidTest
// directory
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.model.profile.MockProfilesRepository // Replace this with the correct
import com.android.sample.model.profile.ProfilesRepository
import com.android.sample.model.profile.User
import com.android.sample.resources.dummydata.email
import com.android.sample.resources.dummydata.idToken
import com.android.sample.resources.dummydata.password
import com.android.sample.resources.dummydata.uid
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SignInViewModelTest {
  private lateinit var signInRepository: SignInRepository
  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var signInViewModel: SignInViewModel
  private lateinit var viewModelFactory: ViewModelProvider.Factory

  private val testDispatcher = StandardTestDispatcher()

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Mock the sign-in repository
    signInRepository = mock(SignInRepository::class.java)

    // Use the provided MockProfilesRepository
    profilesRepository =
        MockProfilesRepository().apply {
          // Add a mock user profile to simulate the existence of the user
          addProfileToDatabase(
              userProfile =
                  User(
                      id = uid,
                      name = "Test User",
                      surname = "Test",
                      interests = listOf(),
                      activities = listOf(),
                      photo = null,
                      likedActivities = listOf()),
              onSuccess = {},
              onFailure = {})
        }

    // Initialize the ViewModel with the mocked repositories
    signInViewModel = SignInViewModel(signInRepository, profilesRepository)
    viewModelFactory = SignInViewModel.Factory()
  }

  @After
  fun tearDown() {
    // Reset the Main dispatcher to the original one
    Dispatchers.resetMain()
  }

  @Test
  fun `signInWithEmail should call repository`() = runTest {
    // Given
    val onProfileExists = mock<() -> Unit>()
    val onProfileMissing = mock<() -> Unit>()
    val onSignInFailure = mock<(String) -> Unit>()

    // When
    signInViewModel.signInWithEmailAndPassword(
        email, password, onProfileExists, onProfileMissing, onSignInFailure)

    advanceUntilIdle()

    // Then
    verify(signInRepository).signInWithEmail(eq(email), eq(password))
  }

  @Test
  fun `signInWithGoogle should call repository`() = runTest {
    // Given
    val onProfileExists = mock<() -> Unit>()
    val onProfileMissing = mock<() -> Unit>()
    val onSignInFailure = mock<(String) -> Unit>()

    // When
    signInViewModel.handleGoogleSignInResult(
        idToken, onProfileExists, onProfileMissing, onSignInFailure)

    advanceUntilIdle()

    // Then
    verify(signInRepository).signInWithGoogle(eq(idToken))
  }

  @Test
  fun `Factory creates SignInViewModel with correct dependencies`() {
    // Use ViewModelProvider to create the ViewModel using the factory
    val viewModel = viewModelFactory.create(SignInViewModel::class.java)

    // Check that the ViewModel is not null
    assertNotNull(viewModel)

    // Check that it's an instance of SignInViewModel
    assertTrue(viewModel is SignInViewModel)
  }

  @Test
  fun `handleGoogleSignInResult should call onAuthError when idToken is null`() = runTest {
    // Given
    val onAuthError = mock<(String) -> Unit>()

    // When
    signInViewModel.handleGoogleSignInResult(null, {}, {}, onAuthError)

    // Advance coroutine
    advanceUntilIdle()

    // Then
    verify(onAuthError).invoke("Google Sign-in failed! Token is null.")
    verifyNoInteractions(signInRepository) // Ensure the repository is not called
  }

  @Test
  fun `handleGoogleSignInResult should call onProfileExists on successful sign-in`() = runTest {
    // Given
    val onProfileExists = mock<() -> Unit>()
    val onProfileMissing = mock<() -> Unit>()
    val onAuthError = mock<(String) -> Unit>()

    // Mock the FirebaseUser and AuthResult
    val mockUser = mock(FirebaseUser::class.java)
    `when`(mockUser.uid).thenReturn(uid)

    val mockAuthResult = mock(AuthResult::class.java)
    `when`(mockAuthResult.user).thenReturn(mockUser)

    // Mock the repository to return the AuthResult with non-null user
    `when`(signInRepository.signInWithGoogle(eq(idToken))).thenReturn(mockAuthResult)

    // When
    signInViewModel.handleGoogleSignInResult(
        idToken, onProfileExists, onProfileMissing, onAuthError)

    // Advance coroutine
    advanceUntilIdle()

    // Then
    verify(onProfileExists).invoke() // Ensure onProfileExists callback is called
    verify(signInRepository).signInWithGoogle(eq(idToken)) // Ensure repository was called
  }

  @Test
  fun `handleGoogleSignInResult should call onAuthError when sign-in fails`() = runTest {
    // Given
    val onProfileExists = mock<() -> Unit>()
    val onProfileMissing = mock<() -> Unit>()
    val onAuthError = mock<(String) -> Unit>()

    // Mock the repository to throw an exception
    `when`(signInRepository.signInWithGoogle(eq(idToken)))
        .thenThrow(RuntimeException("Sign-in error"))

    // When
    signInViewModel.handleGoogleSignInResult(
        idToken, onProfileExists, onProfileMissing, onAuthError)

    // Advance coroutine
    advanceUntilIdle()

    // Then
    verify(onAuthError).invoke("Sign-in error")
    verify(signInRepository).signInWithGoogle(eq(idToken)) // Ensure repository was called
  }

  @Test
  fun `signInWithEmailAndPassword should call onAuthError when UID is null`() = runTest {
    // Given
    val onProfileExists = mock<() -> Unit>()
    val onProfileMissing = mock<() -> Unit>()
    val onAuthError = mock<(String) -> Unit>()

    // Mock the AuthResult with a null user
    val mockAuthResult = mock(AuthResult::class.java)
    `when`(mockAuthResult.user).thenReturn(null)

    // Mock the repository to return the AuthResult with null user
    `when`(signInRepository.signInWithEmail(eq(email), eq(password))).thenReturn(mockAuthResult)

    // When
    signInViewModel.signInWithEmailAndPassword(
        email, password, onProfileExists, onProfileMissing, onAuthError)

    advanceUntilIdle()

    // Then, verify that onAuthError is invoked with the correct message
    verify(onAuthError).invoke("UID is null")
    verify(signInRepository)
        .signInWithEmail(eq(email), eq(password)) // Ensure repository was called
  }

  @Test
  fun `signInWithEmailAndPassword should call onProfileExists when authResult user is not null`() =
      runTest {
        // Given
        val onProfileExists = mock<() -> Unit>()
        val onProfileMissing = mock<() -> Unit>()
        val onAuthError = mock<(String) -> Unit>()

        // Mock the AuthResult with a non-null user and UID
        val mockUser = mock(FirebaseUser::class.java)
        `when`(mockUser.uid).thenReturn(uid)
        val mockAuthResult = mock(AuthResult::class.java)
        `when`(mockAuthResult.user).thenReturn(mockUser)

        // Mock the repository to return the AuthResult with a non-null user
        `when`(signInRepository.signInWithEmail(eq(email), eq(password))).thenReturn(mockAuthResult)

        // When
        signInViewModel.signInWithEmailAndPassword(
            email, password, onProfileExists, onProfileMissing, onAuthError)

        advanceUntilIdle()

        // Then
        verify(onProfileExists).invoke() // Ensure onProfileExists callback is called
        verify(signInRepository)
            .signInWithEmail(eq(email), eq(password)) // Ensure repository was called
  }
}
