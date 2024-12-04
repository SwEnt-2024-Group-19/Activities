package com.android.sample.model.image

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.android.gms.tasks.OnSuccessListener
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
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

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
}
