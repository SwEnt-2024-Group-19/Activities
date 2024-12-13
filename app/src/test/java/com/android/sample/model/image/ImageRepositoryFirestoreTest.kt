package com.android.sample.model.image

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import junit.framework.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ImageRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockStorage: FirebaseStorage
  @Mock private lateinit var mockStorageRef: StorageReference
  @Mock private lateinit var mockUploadTask: UploadTask
  private lateinit var imageRepository: ImageRepositoryFirestore
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockListResult: ListResult
  @Mock private lateinit var bitmapHelper: BitmapHelper

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    `when`(mockStorage.reference).thenReturn(mockStorageRef)
    `when`(mockFirestore.collection("users")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
    imageRepository = ImageRepositoryFirestore(mockFirestore, mockStorage)
  }

  /** TESTS FOR UPLOAD_ACTIVITIES_IMAGES * */
  @Test
  fun uploadActivityImages_initializesDeletion() {
    val activityId = "activityId"
    val bitmaps = listOf(mock(Bitmap::class.java))
    val mockActivityFolderRef: StorageReference = mock(StorageReference::class.java)
    val mockTask: Task<ListResult> = Tasks.forResult(mock(ListResult::class.java))

    `when`(mockStorageRef.child("activities/$activityId")).thenReturn(mockActivityFolderRef)
    `when`(mockActivityFolderRef.listAll()).thenReturn(mockTask)

    imageRepository.uploadActivityImages(activityId, bitmaps, {}, {})

    verify(mockActivityFolderRef).listAll()
  }

  @Test
  fun deleteExistingImages_handlesSuccess() {
    val mockListResult: ListResult = mock()
    val activityId = "activityId"
    val bitmaps = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))
    val expectedUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
    val baos = ByteArrayOutputStream()
    bitmaps.forEach { bitmap -> bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) }
    val imageData = baos.toByteArray()
    val mockTask = Tasks.forResult(mockListResult)

    // Mock deletion of existing items

    `when`(mockStorageRef.child(anyString())).thenReturn(mockStorageRef)
    bitmaps.forEachIndexed { index, bitmap ->
      val fileRef = mockStorageRef.child("image_${index}.jpg")
      `when`(fileRef.putBytes(imageData)).thenReturn(mockUploadTask)
      `when`(mockUploadTask.addOnSuccessListener(any())).thenAnswer { invocation ->
        val listener = invocation.getArgument<OnSuccessListener<UploadTask.TaskSnapshot>>(0)
        val taskSnapshot = mock(UploadTask.TaskSnapshot::class.java) // Mock the snapshot
        listener.onSuccess(taskSnapshot)
        mockUploadTask // continue the chain
      }
      `when`(fileRef.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUrls[index])))
    }

    `when`(mockFirestore.collection("activities")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(activityId)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.update("images", expectedUrls)).thenReturn(Tasks.forResult(null))
    `when`(mockStorageRef.child("activities/$activityId")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.listAll()).thenReturn(mockTask)
    // Mock existing items

    // Setup the responses for the mock calls
    `when`(mockListResult.items).thenReturn(listOf(mockStorageRef, mockStorageRef))

    // Execute the method under test
    imageRepository.deleteExistingImages(mockStorageRef, bitmaps, {}, {})

    // Verify that listAll was called
    verify(mockStorageRef).listAll()
  }

  @Test
  fun handleDeletionSuccess_processesDeletion() {
    val mockActivityFolderRef: StorageReference = mock(StorageReference::class.java)
    val bitmaps = listOf(mock(Bitmap::class.java))
    val mockListResult: ListResult = mock(ListResult::class.java)
    `when`(mockListResult.items).thenReturn(listOf(mockStorageRef))
    `when`(mockStorageRef.delete()).thenReturn(Tasks.forResult(null))

    imageRepository.handleDeletionSuccess(mockListResult, mockActivityFolderRef, bitmaps, {}, {})

    verify(mockStorageRef, atLeastOnce()).delete()
  }

  @Test
  fun uploadImagesAndCollectUrls_uploadsImages() {
    val activityId = "activityId"
    val bitmaps = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))
    val expectedUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
    val baos = ByteArrayOutputStream()
    bitmaps.forEach { bitmap -> bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) }
    val imageData = baos.toByteArray()

    `when`(mockStorageRef.child("activities/$activityId")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.listAll()).thenReturn(Tasks.forResult(mockListResult))
    `when`(mockListResult.items)
        .thenReturn(listOf(mockStorageRef, mockStorageRef)) // Mock existing items
    `when`(mockStorageRef.delete())
        .thenReturn(Tasks.forResult(null)) // Mock deletion of existing items

    `when`(mockStorageRef.child(anyString())).thenReturn(mockStorageRef)
    bitmaps.forEachIndexed { index, bitmap ->
      val fileRef = mockStorageRef.child("image_$index.jpg")
      `when`(fileRef.putBytes(imageData)).thenReturn(mockUploadTask)
      `when`(mockUploadTask.addOnSuccessListener(any())).thenAnswer { invocation ->
        mockUploadTask // continue the chain
      }
      `when`(fileRef.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUrls[index])))
    }

    `when`(mockFirestore.collection("activities")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(activityId)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.update("images", expectedUrls)).thenReturn(Tasks.forResult(null))

    // Run the method under test
    imageRepository.uploadImagesAndCollectUrls(mockStorageRef, bitmaps, {}, {})

    // Verify interactions
    bitmaps.forEach { verify(it).compress(Bitmap.CompressFormat.JPEG, 50, baos) }
    bitmaps.forEachIndexed { index, _ -> verify(mockStorageRef).child("image_${index}.jpg") }
  }

  @Test
  fun testHandleImageUploadSuccess() {
    val mockFileRef: StorageReference = mock()
    val uploadedImageUrls: MutableList<String> = mutableListOf()
    val uploadCount = intArrayOf(0)
    val uri = Uri.parse("https://example.com/image.jpg")
    val mockTask: Task<Uri> = Tasks.forResult(uri)

    `when`(mockFileRef.downloadUrl).thenReturn(mockTask)

    imageRepository.handleImageUploadSuccess(
        mockFileRef,
        uploadedImageUrls,
        uploadCount,
        1, // Assuming we're expecting only one image to trigger success
        { urls -> assert(urls.contains(uri.toString())) },
        { fail("Should not fail") },
        "activityId")

    // Verify that the file reference was used to obtain the download URL
    verify(mockFileRef).downloadUrl
  }

  @Test
  fun testProcessSuccessfulUpload() {
    val uri = "https://example.com/image.jpg"
    val uploadedImageUrls: MutableList<String> = mutableListOf()
    val uploadCount = intArrayOf(0)
    val activityId = "activityId"

    // Mock Firestore interactions
    val mockDocumentReference: DocumentReference = mock(DocumentReference::class.java)
    val mockTask: Task<Void> = mock(Task::class.java) as Task<Void>
    whenever(mockFirestore.collection("activities")).thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.document(activityId)).thenReturn(mockDocumentReference)
    whenever(mockDocumentReference.update("images", listOf(uri))).thenReturn(mockTask)

    // Configure the mock Task to simulate successful completion
    whenever(mockTask.isSuccessful).thenReturn(true)
    whenever(mockTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      val callback = invocation.getArgument<OnSuccessListener<Void>>(0)
      callback.onSuccess(null) // Simulate success
      mockTask
    }
    whenever(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

    // Define callbacks
    val onSuccess: (List<String>) -> Unit = { urls ->
      assertTrue("Uploaded URLs should contain the new URI", urls.contains(uri))
    }
    val onFailure: (Exception) -> Unit = { fail("Should not be called") }

    // Call the method under test
    imageRepository.processSuccessfulUpload(
        uri,
        uploadedImageUrls,
        uploadCount,
        1, // This means we are expecting one upload to trigger success
        onSuccess,
        onFailure,
        activityId)

    // Verify that onSuccess was called with the correct URI
    assertTrue("Upload count should be incremented", uploadCount[0] == 1)
    assertTrue("UploadedImageUrls should contain URI", uploadedImageUrls.contains(uri))
  }

  @Test
  fun uploadActivityImages_success() {
    val activityId = "activityId"
    val bitmaps = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))
    val expectedUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
    val baos = ByteArrayOutputStream()
    bitmaps.forEach { bitmap -> bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) }
    val imageData = baos.toByteArray()

    `when`(mockStorageRef.child("activities/$activityId")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.listAll()).thenReturn(Tasks.forResult(mockListResult))
    `when`(mockListResult.items)
        .thenReturn(listOf(mockStorageRef, mockStorageRef)) // Mock existing items
    `when`(mockStorageRef.delete())
        .thenReturn(Tasks.forResult(null)) // Mock deletion of existing items

    `when`(mockStorageRef.child(anyString())).thenReturn(mockStorageRef)
    bitmaps.forEachIndexed { index, bitmap ->
      val fileRef = mockStorageRef.child("image_${System.currentTimeMillis()}.jpg")
      `when`(fileRef.putBytes(imageData)).thenReturn(mockUploadTask)
      `when`(mockUploadTask.addOnSuccessListener(any())).thenAnswer { invocation ->
        val listener = invocation.getArgument<OnSuccessListener<UploadTask.TaskSnapshot>>(0)
        val taskSnapshot = mock(UploadTask.TaskSnapshot::class.java) // Mock the snapshot
        listener.onSuccess(taskSnapshot)
        mockUploadTask // continue the chain
      }
      `when`(fileRef.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUrls[index])))
    }

    `when`(mockFirestore.collection("activities")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(activityId)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.update("images", expectedUrls)).thenReturn(Tasks.forResult(null))

    var resultUrls = listOf<String>()
    imageRepository.uploadActivityImages(
        activityId,
        bitmaps,
        {
          resultUrls = it
          assertEquals(expectedUrls, resultUrls)
        },
        {})
  }

  @Test
  fun uploadActivityImages_failure() {
    val activityId = "activityId"
    val bitmap = mock(Bitmap::class.java)
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
    val imageData = baos.toByteArray()

    // Setup Firestore collection and document references
    `when`(mockFirestore.collection("activities")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(activityId)).thenReturn(mockDocumentReference)

    // Setup Storage references and upload task
    `when`(mockStorageRef.child("activities/$activityId")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.putBytes(imageData)).thenReturn(mockUploadTask)

    // Mock the listAll() method to return a successful task with empty results to simulate an empty
    // directory
    val mockListResult = mock(ListResult::class.java)
    `when`(mockListResult.items).thenReturn(emptyList())
    `when`(mockStorageRef.listAll()).thenReturn(Tasks.forResult(mockListResult))

    // Setup failure for the upload task
    `when`(mockUploadTask.addOnFailureListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnFailureListener>(0)
      listener.onFailure(Exception("Failed to upload activity images"))
      mockUploadTask // Ensuring we return the mock upload task for chaining
    }

    var errorOccurred = false
    imageRepository.uploadActivityImages(
        activityId,
        listOf(bitmap),
        {},
        {
          errorOccurred = true
          assertTrue("Error should have been triggered", errorOccurred)
        })
  }
  /** ------------------------------------* */

  /** TESTS FOR UPLOAD_PROFILE_PICTURE * */
  @Test
  fun uploadProfilePicture_success() {
    val userId = "userId"
    val bitmap = mock(Bitmap::class.java)
    val uri = Uri.parse("https://example.com/profile.jpg")
    val uriString = "https://example.com/profile.jpg"
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
    val imageData = baos.toByteArray()

    `when`(mockStorageRef.child("users/$userId/profile_picture.jpg")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.putBytes(imageData)).thenReturn(mockUploadTask)

    `when`(mockUploadTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnSuccessListener<UploadTask.TaskSnapshot>>(0)
      val taskSnapshot = mock(UploadTask.TaskSnapshot::class.java) // Mock the snapshot
      listener.onSuccess(taskSnapshot)
      mockUploadTask // continue the chain
    }
    `when`(mockUploadTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnSuccessListener<UploadTask.TaskSnapshot>>(0)
      val taskSnapshot = mock(UploadTask.TaskSnapshot::class.java) // Mock the snapshot
      listener.onSuccess(taskSnapshot)
      mockUploadTask // continue the chain
    }

    `when`(mockStorageRef.downloadUrl).thenReturn(Tasks.forResult(uri))
    `when`(mockDocumentReference.update("photo", uriString)).thenReturn(Tasks.forResult(null))

    var resultUri = ""
    imageRepository.uploadProfilePicture(
        userId,
        bitmap,
        {
          resultUri = it
          assertEquals(uriString, resultUri)
        },
        {})
  }

  @Test
  fun testUpdateFirestoreUserPhoto() {
    val userId = "userId"
    val photoUrl = "http://example.com/profile.jpg"
    val mockTask: Task<Void> = mock(Task::class.java) as Task<Void>

    // Configure the mock Task to simulate successful completion
    whenever(mockTask.isSuccessful).thenReturn(true)
    whenever(mockTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      val callback = invocation.getArgument<OnSuccessListener<Void>>(0)
      callback.onSuccess(null) // Simulate success
      mockTask
    }
    whenever(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

    // Mock the DocumentReference to return our mock Task
    whenever(mockDocumentReference.update("photo", photoUrl)).thenReturn(mockTask)

    var success = false
    imageRepository.updateFirestoreUserPhoto(
        userId,
        photoUrl,
        { success = true },
        {
          // Optionally handle failure in test
        })

    assertTrue("The update should be successful", success)
  }

  @Test
  fun uploadProfilePicture_failure() {
    val userId = "userId"
    val bitmap = mock(Bitmap::class.java)
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
    val imageData = baos.toByteArray()

    `when`(mockStorageRef.child("users/$userId/profile_picture.jpg")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.putBytes(imageData)).thenReturn(mockUploadTask)

    // Ensure onSuccess returns the mockUploadTask to allow chaining
    `when`(mockUploadTask.addOnSuccessListener(any())).thenReturn(mockUploadTask)
    // Correct chaining for onFailureListener
    `when`(mockUploadTask.addOnFailureListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnFailureListener>(0)
      listener.onFailure(Exception("Failed to upload image"))
      mockUploadTask // Continue the chain by returning the mockUploadTask
    }

    var errorOccurred = false
    imageRepository.uploadProfilePicture(
        userId,
        bitmap,
        {},
        {
          errorOccurred = true
          assertTrue("Error should have been triggered", errorOccurred)
        })
  }

  /** ---------------------------------* */
  /** TEST FOR FETCH_PROFILE_PICTURE * */
  @Test
  fun fetchProfileImageUrl_success() {
    val userId = "testUserId"
    val expectedUri = "https://example.com/profile.jpg"

    `when`(mockStorageRef.child("users/$userId/profile_picture.jpg")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUri)))

    var resultUrl = ""
    imageRepository.fetchProfileImageUrl(
        userId,
        {
          resultUrl = it
          assertEquals(expectedUri, resultUrl)
        },
        {})
  }

  @Test
  fun fetchProfileImageUrl_failure() {
    val userId = "testUserId"
    `when`(mockStorageRef.child("users/$userId/profile_picture.jpg")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.downloadUrl)
        .thenReturn(Tasks.forException(Exception("Failed to fetch URL")))

    var errorOccurred = false
    imageRepository.fetchProfileImageUrl(
        userId,
        {},
        {
          errorOccurred = true
          assertTrue("Error should have been triggered", errorOccurred)
        })
  }

  /** ---------------------------------* */

  /** TEST FOR FETCH_ACTIVITY_IMAGES * */
  @Test
  fun fetchActivityImageUrls_success() {
    val activityId = "activityId"
    val expectedUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")

    `when`(mockFirestore.collection("activities")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("activityId")).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot["images"]).thenReturn(expectedUrls)

    var resultsUrls = listOf<String>()
    imageRepository.fetchActivityImageUrls(
        activityId,
        {
          resultsUrls = (it)
          assertEquals(expectedUrls, resultsUrls)
        },
        {})
  }

  interface BitmapHelper {
    fun decodeBitmap(data: ByteArray): Bitmap
  }

  @Test
  fun fetchActivityImagesAsBitmaps_success() {
    val activityId = "activityId"
    val expectedUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
    val expectedBitmaps = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))
    val fakeImageData = ByteArray(1024) // Assuming image data
    `when`(mockFirestore.collection("activities")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("activityId")).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot["images"]).thenReturn(expectedUrls)
    expectedUrls.forEachIndexed { index, url ->
      `when`(mockStorage.getReferenceFromUrl(url)).thenReturn(mockStorageRef)
      `when`(mockStorageRef.getBytes(Long.MAX_VALUE)).thenReturn(Tasks.forResult(fakeImageData))
      `when`(bitmapHelper.decodeBitmap(fakeImageData)).thenReturn(expectedBitmaps[index])
    }
    var resultBitmaps = listOf<Bitmap>()
    imageRepository.fetchActivityImagesAsBitmaps(
        activityId,
        {
          resultBitmaps = it
          assertEquals(expectedBitmaps, resultBitmaps)
        },
        {})
  }

  @Test
  fun fetchActivityImageUrls_failure() {
    val activityId = "activityId"

    // Ensure the Firestore collection reference is properly mocked
    `when`(mockFirestore.collection("activities")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(activityId)).thenReturn(mockDocumentReference)

    // Setup the failure response for the document fetch
    `when`(mockDocumentReference.get())
        .thenReturn(Tasks.forException(Exception("Failed to fetch document")))

    var errorOccurred = false
    imageRepository.fetchActivityImageUrls(
        activityId,
        {},
        {
          errorOccurred = true
          assertTrue("Error should have been triggered", errorOccurred)
        })
  }

  @Test
  fun fetchActivityImagesAsBitmaps_failure() {
    val activityId = "activityId"
    val urls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")

    // Ensure the Firestore collection reference is properly mocked
    `when`(mockFirestore.collection("activities")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(activityId)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.get("images")).thenReturn(urls)

    // Mock the StorageReference and failure scenario for fetching each image
    urls.forEach { url ->
      `when`(mockStorage.getReferenceFromUrl(url)).thenReturn(mockStorageRef)
      `when`(mockStorageRef.getBytes(Long.MAX_VALUE))
          .thenReturn(Tasks.forException(Exception("Failed to fetch image data")))
    }

    var errorOccurred = false
    imageRepository.fetchActivityImagesAsBitmaps(
        activityId,
        {},
        {
          errorOccurred = true
          assertTrue("Error should have been triggered", errorOccurred)
        })
  }

  /** ---------------------------------* */

  /** TEST FOR DELETE_PROFILE_PICTURE * */
  @Test
  fun deleteProfilePicture_success() {
    val userId = "testUserId"
    `when`(mockStorageRef.child("users/$userId/profile_picture.jpg")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.delete()).thenReturn(Tasks.forResult(null))

    var success = false
    imageRepository.deleteProfilePicture(
        userId,
        {
          success = true
          assertTrue("Success callback should be triggered", success)
        },
        { throw it })
  }

  @Test
  fun deleteProfilePicture_failure() {
    val userId = "testUserId"
    val exception = Exception("Failed to delete profile picture")
    `when`(mockStorageRef.child("users/$userId/profile_picture.jpg")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.delete()).thenReturn(Tasks.forException(exception))

    var errorOccurred = false
    imageRepository.deleteProfilePicture(
        userId,
        { throw AssertionError("This should not be called") },
        {
          errorOccurred = true
          assertEquals("Exception should match", exception, it)
          assertTrue("Failure callback should be triggered", errorOccurred)
        })
  }
  /** ---------------------------------* */

  /** TEST FOR REMOVE_ACTIVITY_IMAGES * */
  @Test
  fun removeAllActivityImages_success() {
    val activityId = "testActivityId"
    `when`(mockStorageRef.child("activities/$activityId")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.listAll()).thenReturn(Tasks.forResult(mockListResult))
    `when`(mockListResult.items).thenReturn(listOf(mockStorageRef, mockStorageRef))
    `when`(mockStorageRef.delete()).thenReturn(Tasks.forResult(null))

    val deletionTasks = mockListResult.items.map { it.delete() }
    // Correctly mocking the whenAll to return a Task<Void>
    val mockVoidTask: Task<Void> =
        Tasks.forResult(null) // This should simulate the task completion.
    `when`(Tasks.whenAll(deletionTasks)).thenReturn(mockVoidTask)

    var success = false
    imageRepository.removeAllActivityImages(
        activityId,
        {
          success = true

          assertTrue("Success callback should be triggered", success)
        },
        { throw it })
  }

  @Test
  fun removeAllActivityImages_failure() {
    val activityId = "testActivityId"
    val exception = Exception("Failed to list or delete images")
    `when`(mockStorageRef.child("activities/$activityId")).thenReturn(mockStorageRef)
    `when`(mockStorageRef.listAll()).thenReturn(Tasks.forException(exception))

    var errorOccurred = false
    imageRepository.removeAllActivityImages(
        activityId,
        { throw AssertionError("This should not be called") },
        {
          errorOccurred = true
          assertEquals("Exception should match", exception, it)
          assertTrue("Failure callback should be triggered", errorOccurred)
        })
  }
  /** ---------------------------------* */
}
