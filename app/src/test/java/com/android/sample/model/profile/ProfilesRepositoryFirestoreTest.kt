package com.android.sample.model.profile

import com.android.sample.resources.dummydata.testUser
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class ProfilesRepositoryFirestoreTest {
  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockActivityQuerySnapshot: QuerySnapshot

  private lateinit var profileRepositoryFirestore: ProfilesRepositoryFirestore

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary

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
  fun updateProfile_shouldCallFirestoreCollectionn() {
    val mockTaskVoid: Task<Void> = Tasks.forResult(null)
    `when`(mockDocumentReference.set(testUser)).thenReturn(mockTaskVoid)

    profileRepositoryFirestore.updateProfile(testUser, onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).set(testUser)
  }

  @Test
  fun createProfile_shouldCallFirestoreCollectionn() {
    val mockTaskVoid: Task<Void> = Tasks.forResult(null)
    `when`(mockDocumentReference.set(testUser)).thenReturn(mockTaskVoid)

    profileRepositoryFirestore.addProfileToDatabase(testUser, onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).set(testUser)
  }
}
