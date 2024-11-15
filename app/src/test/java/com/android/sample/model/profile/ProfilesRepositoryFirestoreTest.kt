package com.android.sample.model.profile

import com.android.sample.resources.dummydata.testUser
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class ProfilesRepositoryFirestoreTest {
  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var profileRepositoryFirestore: ProfilesRepositoryFirestore

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    profileRepositoryFirestore = ProfilesRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun getUser_callsDocumentss() {
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.toObject(User::class.java)).thenReturn(testUser)

    profileRepositoryFirestore.getUser(
        "1", onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(mockDocumentReference).get()
  }

  @Test
  fun updateProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(testUser)).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.updateProfile(testUser, onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).set(testUser)
  }

  @Test
  fun addProfileToDatabase_shouldCallFirestoreToAddUserProfile() {
    val mockTaskVoid = Tasks.forResult<Void>(null)
    `when`(mockDocumentReference.set(testUser)).thenReturn(mockTaskVoid)

    profileRepositoryFirestore.addProfileToDatabase(testUser, onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).set(testUser)
  }

  @Test
  fun addActivity_shouldCallFirestoreToAddActivity() {
    `when`(mockDocumentReference.update(eq("activities"), any())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.addActivity("1", "activityId", onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).update(eq("activities"), any())
  }

  @Test
  fun addLikedActivity_shouldCallFirestoreToAddLikedActivity() {
    `when`(mockDocumentReference.update(eq("likedActivities"), any()))
        .thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.addLikedActivity("1", "activityId", onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).update(eq("likedActivities"), any())
  }

  @Test
  fun removeLikedActivity_shouldCallFirestoreToRemoveLikedActivity() {
    `when`(mockDocumentReference.update(eq("likedActivities"), any()))
        .thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.removeLikedActivity(
        "1", "activityId", onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).update(eq("likedActivities"), any())
  }

  @Test
  fun getUser_shouldCallFailureCallbackOnError() {
    val exception = Exception("Test exception")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    profileRepositoryFirestore.getUser(
        "1",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { assert(it == exception) })
  }

  @Test
  fun addActivity_shouldCallFailureCallbackOnError() {
    val exception = Exception("Test exception")
    `when`(mockDocumentReference.update(eq("activities"), any()))
        .thenReturn(Tasks.forException(exception))

    profileRepositoryFirestore.addActivity(
        "1",
        "activityId",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { assert(it == exception) })
  }

  @Test
  fun addLikedActivity_shouldCallFailureCallbackOnError() {
    val exception = Exception("Test exception")
    `when`(mockDocumentReference.update(eq("likedActivities"), any()))
        .thenReturn(Tasks.forException(exception))

    profileRepositoryFirestore.addLikedActivity(
        "1",
        "activityId",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { assert(it == exception) })
  }

  @Test
  fun removeLikedActivity_shouldCallFailureCallbackOnError() {
    val exception = Exception("Test exception")
    `when`(mockDocumentReference.update(eq("likedActivities"), any()))
        .thenReturn(Tasks.forException(exception))

    profileRepositoryFirestore.removeLikedActivity(
        "1",
        "activityId",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { assert(it == exception) })
  }

  @Test
  fun updateProfile_shouldCallFailureCallbackOnError() {
    val exception = Exception("Test exception")
    `when`(mockDocumentReference.set(testUser)).thenReturn(Tasks.forException(exception))

    profileRepositoryFirestore.updateProfile(
        testUser,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { assert(it == exception) })
  }

  @Test
  fun addProfileToDatabase_shouldCallFailureCallbackOnError() {
    val exception = Exception("Test exception")
    `when`(mockDocumentReference.set(testUser)).thenReturn(Tasks.forException(exception))

    profileRepositoryFirestore.addProfileToDatabase(
        testUser,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { assert(it == exception) })
  }
}
