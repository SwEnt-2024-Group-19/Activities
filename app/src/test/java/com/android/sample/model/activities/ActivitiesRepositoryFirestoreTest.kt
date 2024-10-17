package com.android.sample.model.activities

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.activity.ActivitiesRepositoryFirestore
import com.android.sample.model.activity.Activity
import com.android.sample.model.activity.ActivityStatus
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ActivitiesRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockActivityQuerySnapshot: QuerySnapshot

  private lateinit var activitiesRepositoryFirestore: ActivitiesRepositoryFirestore

  private val activity =
      Activity(
          title = "FOOTBALL",
          uid = "1",
          status = ActivityStatus.ACTIVE,
          location = "",
          date = Timestamp.now(),
          creator = "me",
          description = "Do something",
          placesTaken = 0,
          maxPlaces = 0,
          participants = listOf(),
          images = listOf(),
          price = 0.0)

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    activitiesRepositoryFirestore = ActivitiesRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun getNewUid() {
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = activitiesRepositoryFirestore.getNewUid()
    assert(uid == "1")
  }

  @Test
  fun getActivities_callsDocuments() {
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockActivityQuerySnapshot))
    `when`(mockActivityQuerySnapshot.documents).thenReturn(listOf())

    activitiesRepositoryFirestore.getActivities(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(timeout(100)) { (mockActivityQuerySnapshot).documents }
  }

  @Test
  fun addActivity_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    activitiesRepositoryFirestore.addActivity(activity, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun deleteActivitiesId_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    activitiesRepositoryFirestore.deleteActivityById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
  }

  @Test
  fun updateActivity_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    activitiesRepositoryFirestore.updateActivity(activity, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun init_shouldCallOnSuccess() {
    var onSuccessCalled = false

    activitiesRepositoryFirestore.init { onSuccessCalled = true }

    assert(onSuccessCalled)
  }
}
