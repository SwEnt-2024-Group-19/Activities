import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.auth.SignInRepository
import com.android.sample.model.auth.SignInViewModel
import com.android.sample.resources.dummydata.email
import com.android.sample.resources.dummydata.idToken
import com.android.sample.resources.dummydata.password
import com.android.sample.resources.dummydata.uid
import com.android.sample.ui.navigation.NavigationActions
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
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SignInViewModelTest {
  private lateinit var signInRepository: SignInRepository
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

    // Mock the repository
    signInRepository = mock(SignInRepository::class.java)

    // Initialize the ViewModel with the mocked repository
    signInViewModel = SignInViewModel(signInRepository)
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

    // When
    signInViewModel.signInWithEmailAndPassword(
        email, password, {}, {}, mock(NavigationActions::class.java))

    advanceUntilIdle()

    // Then
    verify(signInRepository).signInWithEmail(eq(email), eq(password))
  }

  @Test
  fun `signInWithGoogle should call repository`() = runTest {
    // Given

    // When
    signInViewModel.handleGoogleSignInResult(idToken, {}, {}, mock(NavigationActions::class.java))

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
    val navigationActions = mock(NavigationActions::class.java)

    // When
    signInViewModel.handleGoogleSignInResult(null, {}, onAuthError, navigationActions)

    // Advance coroutine
    advanceUntilIdle()

    // Then
    verify(onAuthError).invoke("Google Sign-in failed! Token is null.")
    verifyNoInteractions(signInRepository) // Ensure the repository is not called
  }

  @Test
  fun `handleGoogleSignInResult should call checkUserProfile on successful sign-in`() = runTest {
    // Given
    val onAuthSuccess = mock<() -> Unit>()
    val onAuthError = mock<(String) -> Unit>()
    val navigationActions = mock(NavigationActions::class.java)

    // Mock the FirebaseUser and AuthResult
    val mockUser = mock(FirebaseUser::class.java)
    `when`(mockUser.uid).thenReturn(uid)

    val mockAuthResult = mock(AuthResult::class.java)
    `when`(mockAuthResult.user).thenReturn(mockUser)

    // Mock the repository to return the AuthResult with non-null user
    `when`(signInRepository.signInWithGoogle(eq(idToken))).thenReturn(mockAuthResult)

    // When
    signInViewModel.handleGoogleSignInResult(idToken, onAuthSuccess, onAuthError, navigationActions)

    // Advance coroutine
    advanceUntilIdle()

    // Then
    verify(signInRepository)
        .checkUserProfile(eq("testUid"), eq(navigationActions), eq(onAuthSuccess), eq(onAuthError))
    verify(signInRepository).signInWithGoogle(eq(idToken)) // Ensure repository was called
  }

  @Test
  fun `handleGoogleSignInResult should call onAuthError when sign-in fails`() = runTest {
    // Given
    val onAuthSuccess = mock<() -> Unit>()
    val onAuthError = mock<(String) -> Unit>()
    val navigationActions = mock(NavigationActions::class.java)

    // Mock the repository to throw an exception
    `when`(signInRepository.signInWithGoogle(eq(idToken)))
        .thenThrow(RuntimeException("Sign-in error"))

    // When
    signInViewModel.handleGoogleSignInResult(idToken, onAuthSuccess, onAuthError, navigationActions)

    // Advance coroutine
    advanceUntilIdle()

    // Then
    verify(onAuthError).invoke("Google Sign-in failed: Sign-in error")
    verify(signInRepository).signInWithGoogle(eq(idToken)) // Ensure repository was called
  }

  @Test
  fun `signInWithEmailAndPassword should call checkUserProfile with null UID and invoke onAuthError`() =
      runTest {
        // Given
        val onAuthError = mock<(String) -> Unit>()
        val navigationActions = mock(NavigationActions::class.java)

        // Mock the AuthResult with a null user
        val mockAuthResult = mock(AuthResult::class.java)
        `when`(mockAuthResult.user).thenReturn(null)

        // Mock the repository to return the AuthResult with null user
        `when`(signInRepository.signInWithEmail(eq(email), eq(password))).thenReturn(mockAuthResult)

        // Mock checkUserProfile to ensure that it will be called and invoke onAuthError
        `when`(
                signInRepository.checkUserProfile(
                    eq(null), eq(navigationActions), anyOrNull(), eq(onAuthError)))
            .thenAnswer {
              it.getArgument<(String) -> Unit>(3).invoke("User ID is null, cannot proceed!")
            }

        // When
        signInViewModel.signInWithEmailAndPassword(
            email, password, {}, onAuthError, navigationActions)

        // Advance coroutine
        advanceUntilIdle()

        // Then, verify that onAuthError is invoked with the correct message
        verify(onAuthError).invoke("User ID is null, cannot proceed!")
        verify(signInRepository)
            .signInWithEmail(eq(email), eq(password)) // Ensure repository was called
  }

  @Test
  fun `signInWithEmailAndPassword should call checkUserProfile when authResult user is not null`() =
      runTest {
        // Given

        val onAuthSuccess = mock<() -> Unit>()
        val onAuthError = mock<(String) -> Unit>()
        val navigationActions = mock(NavigationActions::class.java)

        // Mock the AuthResult with a non-null user and UID
        val mockUser = mock(FirebaseUser::class.java)
        `when`(mockUser.uid).thenReturn(uid)
        val mockAuthResult = mock(AuthResult::class.java)
        `when`(mockAuthResult.user).thenReturn(mockUser)

        // Mock the repository to return the AuthResult with a non-null user
        `when`(signInRepository.signInWithEmail(eq(email), eq(password))).thenReturn(mockAuthResult)

        // When
        signInViewModel.signInWithEmailAndPassword(
            email, password, onAuthSuccess, onAuthError, navigationActions)

        // Advance coroutine
        advanceUntilIdle()

        // Then
        verify(signInRepository)
            .checkUserProfile(eq(uid), eq(navigationActions), eq(onAuthSuccess), eq(onAuthError))
        verify(signInRepository)
            .signInWithEmail(eq(email), eq(password)) // Ensure repository was called
  }
}
