package com.android.sample.model.profile

import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProfileViewModelTest {

  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var firebaseAuth: FirebaseAuth
  private lateinit var profilesRepository: ProfilesRepositoryFirestore
  private lateinit var firebaseFirestore: FirebaseFirestore

  private val user =
      User(
          id = "1",
          name = "John",
          surname = "Doe",
          photo = "urlToPhoto",
          interests = listOf("Reading", "Hiking"),
          activities = listOf("Activity1", "Activity2"))

  private val activity =
      Activity(
          title = "FOOTBALL",
          uid = "1",
          status = ActivityStatus.ACTIVE,
          location = "",
          date = Timestamp.now(),
          creator = "me",
          description = "Do something",
          placesLeft = 0,
          maxPlaces = 0,
          participants = listOf(),
          images = listOf(),
          duration = "00:30",
          startTime = "09:00",
          type = ActivityType.PRO,
          price = 0.0)

  @Before
  fun setUp() {

    FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())

    // Mock FirebaseAuth
    firebaseAuth = mock(FirebaseAuth::class.java)
    firebaseFirestore = mock(FirebaseFirestore::class.java)
    profilesRepository = mock(ProfilesRepositoryFirestore::class.java)
    profileViewModel = ProfileViewModel( repository = profilesRepository)
  }

  @Test
  fun getActivitiesCallsRepository() {
    profileViewModel.fetchUserData(user.id)
    verify(profilesRepository).getUser(any(), any(), any())
  }

  @Test
  fun addActivitiesCallsRepository() {
    profileViewModel.addActivity(activity.uid, user.id)
    verify(profilesRepository).addActivity(eq(activity.uid), any(), any(), any())
  }

  @Test
  fun getActivitiesSuccessCallback() {
    val onSuccess = mock<(User?) -> Unit>()
    val onFailure = mock<(Exception) -> Unit>()

    // Assuming `getUser` is called with three parameters: userId, onSuccess, onFailure
    // Using `eq` for specific values and `any` for generics where the actual instance doesn't
    // matter for the test
    profileViewModel.fetchUserData(user.id)

    // Correct the usage by ensuring all parameters use matchers:
    verify(profilesRepository).getUser(eq(user.id), any(), any())
  }

  @Test
  fun updateActivitiesCallsRepository() {
    // This should be an operation that triggers updateProfile
    profileViewModel.updateProfile(user)
    verify(profilesRepository).updateProfile(eq(user), any(), any())
  }

  @Test
  fun createUserProfileCallsRepositoryWithCorrectArguments() {
    val onSuccess = mock<() -> Unit>()
    val onError = mock<(Exception) -> Unit>()

    profileViewModel.createUserProfile(user, onSuccess, onError)

    // Verify that the repository's addProfileToDatabase method is called with the correct user
    verify(profilesRepository).addProfileToDatabase(eq(user), any(), any())
  }

  @Test
  fun createUserProfileSuccessTriggersOnSuccessCallback() {
    val onSuccess = mock<() -> Unit>()
    val onError = mock<(Exception) -> Unit>()

    // Configure the repository mock to trigger the onSuccess callback
    `when`(profilesRepository.addProfileToDatabase(eq(user), any(), any())).thenAnswer {
      val successCallback = it.getArgument<() -> Unit>(1)
      successCallback()
    }

    profileViewModel.createUserProfile(user, onSuccess, onError)

    // Verify that onSuccess callback is triggered
    verify(onSuccess).invoke()
  }

  @Test
  fun createUserProfileFailureTriggersOnErrorCallback() {
    val onSuccess = mock<() -> Unit>()
    val onError = mock<(Exception) -> Unit>()
    val exception = Exception("Database error")

    // Configure the repository mock to trigger the onError callback
    `when`(profilesRepository.addProfileToDatabase(eq(user), any(), any())).thenAnswer {
      val errorCallback = it.getArgument<(Exception) -> Unit>(2)
      errorCallback(exception)
    }

    profileViewModel.createUserProfile(user, onSuccess, onError)

    // Verify that onError callback is triggered with the correct exception
    verify(onError).invoke(eq(exception))
  }
}
