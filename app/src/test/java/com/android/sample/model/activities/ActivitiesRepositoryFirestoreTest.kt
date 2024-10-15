package com.github.se.bootcamp.model.todo

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
            placesLeft = 0,
            maxPlaces = 0,
            participants = listOf(),
            images = listOf(),
            price = 0.0
        )


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

    /**
     * This test verifies that when fetching a ToDos list, the Firestore `get()` is called on the
     * collection reference and not the document reference.
     */
    @Test
    fun getActivities_callsDocuments() {
        // Ensure that mockToDoQuerySnapshot is properly initialized and mocked
        `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockActivityQuerySnapshot))

        // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
        `when`(mockActivityQuerySnapshot.documents).thenReturn(listOf())

        // Call the method under test
        activitiesRepositoryFirestore.getActivities(
            onSuccess = {

                // Do nothing; we just want to verify that the 'documents' field was accessed
            },
            onFailure = { fail("Failure callback should not be called") })

        // Verify that the 'documents' field was accessed
        verify(timeout(100)) { (mockActivityQuerySnapshot).documents }
    }

    /**
     * This test verifies that when we add a new ToDo, the Firestore `set()` is called on the document
     * reference. This does NOT CHECK the actual data being added
     */
    @Test
    fun addActivity_shouldCallFirestoreCollection() {
        `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

        // This test verifies that when we add a new ToDo, the Firestore `collection()` method is
        // called.
        activitiesRepositoryFirestore.addActivity(activity, onSuccess = {}, onFailure = {})

        shadowOf(Looper.getMainLooper()).idle()

        // Ensure Firestore collection method was called to reference the "ToDos" collection
        verify(mockDocumentReference).set(any())
    }

    /**
     * This check that the correct Firestore method is called when deleting. Does NOT CHECK that the
     * correct data is deleted.
     */
    @Test
    fun deleteActivitiesId_shouldCallDocumentReferenceDelete() {
        `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

        activitiesRepositoryFirestore.deleteActivityById("1", onSuccess = {}, onFailure = {})

        shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

        verify(mockDocumentReference).delete()
    }
}
