package com.android.sample.model.profile

import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.map.Location
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
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

  private val activity =
      Activity(
          title = "FOOTBALL",
          uid = "1",
          status = ActivityStatus.ACTIVE,
          location = Location(46.519962, 6.633597, "EPFL"),
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

  private val user =
      User(
          id = "1",
          name = "John",
          surname = "Doe",
          photo = "urlToPhoto",
          interests = listOf("Reading", "Hiking"),
          activities = listOf("Activity1", "Activity2"))

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
    `when`(mockDocumentSnapshot.toObject(User::class.java))
        .thenReturn(
            User(
                id = "1",
                name = "John",
                surname = "Doe",
                interests = listOf("Reading", "Hiking"),
                activities = listOf("Activity1", "Activity2"),
                photo = "urlToPhoto"))

    profileRepositoryFirestore.getUser(
        "1", onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(mockDocumentReference).get()
  }

  @Test
  fun updateProfile_shouldCallFirestoreCollection() {
    val user =
        User(
            id = "1",
            name = "John",
            surname = "Doe",
            interests = listOf("Reading", "Hiking"),
            activities = listOf("Activity1", "Activity2"),
            photo = "urlToPhoto")

    `when`(mockDocumentReference.set(user)).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.updateProfile(user, onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).set(user)
  }

  @Test
  fun updateProfile_shouldCallFirestoreCollectionn() {
    val mockTaskVoid: Task<Void> = Tasks.forResult(null)
    `when`(mockDocumentReference.set(user)).thenReturn(mockTaskVoid)

    profileRepositoryFirestore.updateProfile(user, onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).set(user)
  }
}
