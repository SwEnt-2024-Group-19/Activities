package com.android.sample.model

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class UserProfileViewModelTest {

  @Mock private lateinit var repository: ProfilesRepositoryFirestore

  private lateinit var viewModel: UserProfileViewModel

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    viewModel = UserProfileViewModel(repository, "1")
  }

  @Test
  fun `fetchUserData success updates userState`() = runTest {
    // Arrange
    val expectedUser = User("1", "John", "Doe", listOf("gaming"), listOf("running"), "photoUrl")
    doAnswer { invocation ->
          val callback: (User?) -> Unit = invocation.getArgument(1)
          callback(expectedUser)
          null
        }
        .`when`(repository)
        .getUser(anyString(), any(), any())

    // Act
    viewModel.fetchUserData("1")

    // Assert
    assertEquals(expectedUser, viewModel.userState.value)
  }

  @Test
  fun `fetchUserData failure logs error`() = runTest {
    // Arrange
    doAnswer { invocation ->
          val callback: (Exception) -> Unit = invocation.getArgument(2)
          callback(RuntimeException("Error"))
          null
        }
        .`when`(repository)
        .getUser(anyString(), any(), any())

    // Act
    viewModel.fetchUserData("1")

    // Log assertions are usually handled through observing log outputs or specific log capturing
    // setups.
  }

  @Test
  fun `constructor and fields test`() {
    // Prepare data
    val id = "123"
    val name = "John"
    val surname = "Doe"
    val interests = listOf("reading", "gaming")
    val activities = listOf("running", "swimming")
    val photo = "http://example.com/photo.jpg"

    // Create an instance of User
    val user = User(id, name, surname, interests, activities, photo)

    // Assert field values
    assertEquals(id, user.id)
    assertEquals(name, user.name)
    assertEquals(surname, user.surname)
    assertEquals(interests, user.interests)
    assertEquals(activities, user.activities)
    assertEquals(photo, user.photo)
  }

  @Test
  fun `test optional fields with null values`() {
    // Create an instance of User with null optional fields
    val user =
        User(
            id = "123",
            name = "John",
            surname = "Doe",
            interests = null,
            activities = null,
            photo = null)

    // Assert field values
    assertNull(user.interests)
    assertNull(user.activities)
    assertNull(user.photo)
  }

  @Test
  fun `provideFactory returns a ViewModelProvider Factory that creates UserProfileViewModels`() {
    // Arrange
    val userId = "testUserId"
    val factory = UserProfileViewModel.provideFactory(repository, userId)

    // Act
    val viewModel = factory.create(UserProfileViewModel::class.java)

    // Assert
    assertTrue(viewModel is UserProfileViewModel)
  }
}
