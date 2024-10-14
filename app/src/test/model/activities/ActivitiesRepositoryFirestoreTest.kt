package com.android.sample.model.activities

import androidx.test.core.app.ApplicationProvider
import com.android.sample.R
import com.android.sample.model.activity.ActivitiesRepositoryFirestore
import com.android.sample.model.activity.Activity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify

class activitiesRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockActivityQuerySnapshot: QuerySnapshot

  private lateinit var ActivitiesRepositoryFirestore: ActivitiesRepositoryFirestore

  private val activity =
      Activity(
          uid = "3",
          name = "Fun Farm",
          description = "Come discover the new farm and enjoy with your family!",
          date = Timestamp.now(),
          location = "Lausanne",
          organizerName = "Rola",
          image = R.drawable.farm.toLong(),
          20,
          22)

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    ActivitiesRepositoryFirestore = ActivitiesRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun getNewUid() {
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = ActivitiesRepositoryFirestore.getNewUid()
    assert(uid == "1")
  }

  /**
   * This test verifies that when fetching a activities list, the Firestore `get()` is called on the
   * collection reference and not the document reference.
   */
  @Test
  fun getactivities_callsDocuments() {
    // Ensure that mockactivityQuerySnapshot is properly initialized and mocked
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockActivityQuerySnapshot))

    // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
    `when`(mockActivityQuerySnapshot.documents).thenReturn(listOf())

    // Call the method under test
    ActivitiesRepositoryFirestore.getActivities(
        onSuccess = {

          // Do nothing; we just want to verify that the 'documents' field was accessed
        },
        onFailure = { fail("Failure callback should not be called") })

    // Verify that the 'documents' field was accessed
    verify(timeout(100)) { (mockActivityQuerySnapshot).documents }
  }

  @Test
  fun getActivities_handlesNullData() {
    // Mock addSnapshotListener to return a null data map in the DocumentSnapshot
    `when`(mockCollectionReference.addSnapshotListener(any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as EventListener<QuerySnapshot>
      listener.onEvent(mockActivityQuerySnapshot, null) // Simulate snapshot listener triggering
      null
    }

    // Mock the QuerySnapshot to return a list with a document that has null data
    `when`(mockActivityQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    // Simulate the DocumentSnapshot having null data
    `when`(mockDocumentSnapshot.data).thenReturn(null)

    ActivitiesRepositoryFirestore.getActivities(
        onSuccess = { activities ->
          assert(activities.isEmpty()) // Ensure no activities are returned
        },
        onFailure = { fail("Failure callback should not be called") })

    verify(mockActivityQuerySnapshot).documents
  }

  @Test
  fun getActivities_handlesFirestoreError() {
    // Simulate an error from Firestore
    val firestoreException =
        FirebaseFirestoreException("Firestore error", FirebaseFirestoreException.Code.ABORTED)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forException(firestoreException))

    ActivitiesRepositoryFirestore.getActivities(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { exception ->
          assert(
              exception
                  is FirebaseFirestoreException) // Ensure the failure callback is invoked with the
          // error
        })
  }
}
