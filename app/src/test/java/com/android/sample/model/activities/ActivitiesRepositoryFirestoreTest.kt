package com.android.sample.model.activities

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.activity.ActivitiesRepositoryFirestore
import com.android.sample.model.activity.ActivityStatus
import com.android.sample.model.activity.ActivityType
import com.android.sample.model.activity.Category
import com.android.sample.resources.dummydata.activityBiking
import com.android.sample.resources.dummydata.documentId
import com.android.sample.resources.dummydata.validData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.fail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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

  @Test
  fun performFirestoreOperation_callsOnSuccess() {
    val mockTask = Tasks.forResult<Void>(null)

    var successCalled = false
    activitiesRepositoryFirestore.performFirestoreOperation(
        mockTask,
        onSuccess = { successCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(successCalled)
  }

  @Test
  fun performFirestoreOperation_callsOnFailure() {
    val exception = FirebaseFirestoreException("Error", FirebaseFirestoreException.Code.ABORTED)
    val mockTask = Tasks.forException<Void>(exception)

    var failureCalled = false
    activitiesRepositoryFirestore.performFirestoreOperation(
        mockTask,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assert(failureCalled)
  }

  @Test
  fun `documentToActivity should return a valid Activity when data is valid`() {
    val result = activitiesRepositoryFirestore.documentToActivity(validData, documentId)

    assertNotNull(result)
    assertEquals(documentId, result!!.uid)
    assertEquals("Sample Title", result.title)
    assertEquals("Sample Description", result.description)
    assertEquals("10:00", result.startTime)
    assertEquals("2 hours", result.duration)
    assertEquals(15.0, result.price, 0.0001)
    assertEquals(12.34, result.location!!.latitude, 0.0001)
    assertEquals(56.78, result.location!!.longitude, 0.0001)
    assertEquals("Sample Location", result.location!!.shortName)
    assertEquals("creatorUserId", result.creator)
    assertEquals(listOf("image1.jpg", "image2.jpg"), result.images)
    assertEquals(5L, result.placesLeft)
    assertEquals(20L, result.maxPlaces)
    assertEquals(ActivityStatus.ACTIVE, result.status)
    assertEquals(ActivityType.INDIVIDUAL, result.type)
    assertEquals(1, result.participants.size)
    assertEquals("John", result.participants.first().name)
    assertEquals(1, result.comments.size)
    assertEquals("Nice activity!", result.comments.first().content)
    assertEquals(1, result.comments.first().replies.size)
  }

  @Test
  fun `documentToActivity should handle missing optional fields gracefully`() {
    val minimalData =
        mapOf(
            "title" to "Minimal Title",
            "description" to "Minimal Description",
            "date" to Timestamp.now())

    val result = activitiesRepositoryFirestore.documentToActivity(minimalData, documentId)

    assertNotNull(result)
    assertEquals("Minimal Title", result!!.title)
    assertEquals("Minimal Description", result.description)
    assertTrue(result.images.isEmpty())
    assertTrue(result.participants.isEmpty())
    assertTrue(result.comments.isEmpty())
    assertEquals("No Location", result.location!!.shortName)
    assertEquals(ActivityStatus.ACTIVE, result.status)
    assertEquals(ActivityType.INDIVIDUAL, result.type)
  }

  @Test
  fun deleteActivityById_successfulDeletion_callsOnSuccess() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult<Void>(null))

    var successCallbackCalled = false

    activitiesRepositoryFirestore.deleteActivityById(
        id = "testId",
        onSuccess = { successCallbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Success callback should be called", successCallbackCalled)
  }

  @Test
  fun addActivity_successfulAddition_callsOnSuccess() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult<Void>(null))

    var successCallbackCalled = false

    activitiesRepositoryFirestore.addActivity(
        activity = activity,
        onSuccess = { successCallbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Success callback should be called", successCallbackCalled)
  }

  @Test
  fun documentToActivity_handlesNullDataGracefully() {
    val nullActivity =
        activitiesRepositoryFirestore.documentToActivity(data = emptyMap(), documentId = "testId")

    assertNotNull("Returned activity should not be null", nullActivity)
    assertEquals("Activity title should be default", "No Title", nullActivity?.title)
  }

  @Test
  fun `test documentToActivity with valid data`() {
    val data = mapOf(
      "title" to "Sample Activity",
      "description" to "This is a sample activity",
      "date" to Timestamp.now(),
      "startTime" to "10:00",
      "duration" to "02:00",
      "price" to 20.0,
      "location" to mapOf(
        "latitude" to 46.519962,
        "longitude" to 6.633597,
        "name" to "EPFL, Lausanne"
      ),
      "creator" to "user123",
      "images" to listOf("image1.jpg", "image2.jpg"),
      "placesLeft" to 5L,
      "maxPlaces" to 10L,
      "status" to "ACTIVE",
      "type" to "PRO",
      "participants" to listOf(
        mapOf(
          "name" to "John",
          "surname" to "Doe",
          "id" to "user123",
          "interests" to listOf(
            mapOf(
              "name" to "Cycling",
              "category" to "SPORT"
            )
          ),
          "activities" to listOf("activity1"),
          "photo" to "photo.jpg",
          "likedActivities" to listOf("activity1")
        )
      ),
      "comments" to listOf(
        mapOf(
          "uid" to "comment1",
          "userId" to "user123",
          "userName" to "John Doe",
          "content" to "Great activity!",
          "timestamp" to Timestamp.now(),
          "replies" to listOf(
            mapOf(
              "uid" to "reply1",
              "userId" to "user456",
              "userName" to "Jane Doe",
              "content" to "I agree!",
              "timestamp" to Timestamp.now()
            )
          )
        )
      ),
      "likes" to mapOf("user123" to true),
      "category" to "SPORT",
      "subcategory" to "Cycling"
    )

    val documentId = "activity123"
    val activity = activitiesRepositoryFirestore.documentToActivity(data, documentId)

    assertNotNull(activity)
    assertEquals("activity123", activity?.uid)
    assertEquals("Sample Activity", activity?.title)
    assertEquals("This is a sample activity", activity?.description)
    assertEquals("10:00", activity?.startTime)
    assertEquals("02:00", activity?.duration)
    assertEquals(20.0, activity?.price?:0.0, 0.0)
    assertEquals("user123", activity?.creator)
    assertEquals(5L, activity?.placesLeft)
    assertEquals(10L, activity?.maxPlaces)
    assertEquals(ActivityStatus.ACTIVE, activity?.status)
    assertEquals(ActivityType.PRO, activity?.type)
    assertEquals(1, activity?.participants?.size)
    assertEquals(1, activity?.comments?.size)
    assertEquals(1, activity?.likes?.size)
    assertEquals(Category.SPORT, activity?.category)
    assertEquals("Cycling", activity?.subcategory)
  }

  @Test
  fun `test documentToActivity with missing optional fields`() {
    val data = mapOf(
      "title" to "Sample Activity",
      "description" to "This is a sample activity",
      "date" to Timestamp.now(),
      "startTime" to "10:00",
      "duration" to "02:00",
      "price" to 20.0,
      "location" to mapOf(
        "latitude" to 46.519962,
        "longitude" to 6.633597,
        "name" to "EPFL, Lausanne"
      ),
      "creator" to "user123",
      "status" to "ACTIVE",
      "type" to "PRO",
      "category" to "SPORT"
    )

    val documentId = "activity123"
    val activity = activitiesRepositoryFirestore.documentToActivity(data, documentId)

    assertNotNull(activity)
    assertEquals("activity123", activity?.uid)
    assertEquals("Sample Activity", activity?.title)
    assertEquals("This is a sample activity", activity?.description)
    assertEquals("10:00", activity?.startTime)
    assertEquals("02:00", activity?.duration)
    assertEquals(20.0, activity?.price?:0.0, 0.0)
    assertEquals("user123", activity?.creator)
    assertEquals(0L, activity?.placesLeft)
    assertEquals(0L, activity?.maxPlaces)
    assertEquals(ActivityStatus.ACTIVE, activity?.status)
    assertEquals(ActivityType.PRO, activity?.type)
    assertEquals(0, activity?.participants?.size)
    assertEquals(0, activity?.comments?.size)
    assertEquals(0, activity?.likes?.size)
    assertEquals(Category.SPORT, activity?.category)
    assertEquals("None", activity?.subcategory)
  }
}
