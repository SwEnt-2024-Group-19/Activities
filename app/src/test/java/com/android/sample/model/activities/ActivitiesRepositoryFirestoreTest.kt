package com.android.sample.model.activities

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.activity.ActivitiesRepositoryFirestore
import com.android.sample.resources.dummydata.activityBiking
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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

  private val activity = activityBiking

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
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult<Void>(null))

    activitiesRepositoryFirestore.addActivity(activity, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun deleteActivitiesId_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult<Void>(null))

    activitiesRepositoryFirestore.deleteActivityById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
  }

  @Test
  fun addActivity_callsOnFailureOnError() {
    val exception = FirebaseFirestoreException("Error", FirebaseFirestoreException.Code.ABORTED)
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    activitiesRepositoryFirestore.addActivity(
        activity,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assert(failureCalled)
  }

  @Test
  fun deleteActivityById_callsOnFailureOnError() {
    val exception = FirebaseFirestoreException("Error", FirebaseFirestoreException.Code.ABORTED)
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    activitiesRepositoryFirestore.deleteActivityById(
        "1",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assert(failureCalled)
  }

  @Test
  fun updateActivity_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult<Void>(null))

    activitiesRepositoryFirestore.updateActivity(activity, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateActivity_callsOnFailureOnError() {
    val exception = FirebaseFirestoreException("Error", FirebaseFirestoreException.Code.ABORTED)
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    activitiesRepositoryFirestore.updateActivity(
        activity,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assert(failureCalled)
  }
}
