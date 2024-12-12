package com.android.sample.model.map

import android.location.Location as AndroidLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.io.IOException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

class NominatimLocationRepositoryTest {

  @Mock private lateinit var mockClient: OkHttpClient
  @Mock private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
  @Mock private lateinit var mockCall: Call
  @Mock private lateinit var mockLocationTask: Task<AndroidLocation>

  private lateinit var locationRepository: NominatimLocationRepository

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    locationRepository = NominatimLocationRepository(mockClient, fusedLocationProviderClient)

    // Ensure that addOnSuccessListener and addOnFailureListener return the same mockLocationTask
    // instance
    whenever(mockLocationTask.addOnSuccessListener(any<OnSuccessListener<AndroidLocation>>()))
        .thenReturn(mockLocationTask)
    whenever(mockLocationTask.addOnFailureListener(any<OnFailureListener>()))
        .thenReturn(mockLocationTask)

    // Mock the lastLocation task to return the mockLocationTask
    whenever(fusedLocationProviderClient.lastLocation).thenReturn(mockLocationTask)
  }

  @Test
  fun `search should return locations on successful response`() {
    // Given
    val query = "Central Park"
    val mockResponseBody =
        """
            [
                {"lat": 40.785091, "lon": -73.968285, "display_name": "Central Park"}
            ]
        """
            .trimIndent()
    val mockResponse = createMockResponse(mockResponseBody, 200)

    whenever(mockClient.newCall(any<Request>())).thenReturn(mockCall)
    doAnswer { invocation ->
          val callback = invocation.arguments[0] as Callback
          callback.onResponse(mockCall, mockResponse)
          null
        }
        .`when`(mockCall)
        .enqueue(any())

    // When
    var result: List<Location>? = null
    locationRepository.search(
        query,
        onSuccess = { locations -> result = locations },
        onFailure = { throw AssertionError("Failure callback should not be called") })

    // Then
    assertEquals(1, result?.size)
    assertEquals("Central Park", result?.first()?.name)
    result?.first()?.latitude?.let { assertEquals(40.785091, it, 0.00001) }
    result?.first()?.longitude?.let { assertEquals(-73.968285, it, 0.00001) }
  }

  @Test
  fun `search should call onFailure on network error`() {
    // Given
    val query = "invalid query"
    val exception = IOException("Network error")

    whenever(mockClient.newCall(any())).thenReturn(mockCall)
    doAnswer { invocation ->
          val callback = invocation.arguments[0] as Callback
          callback.onFailure(mockCall, exception)
          null
        }
        .`when`(mockCall)
        .enqueue(any())

    // When
    var failureCalled = false
    locationRepository.search(
        query,
        onSuccess = { throw AssertionError("Success callback should not be called") },
        onFailure = { failureCalled = true })

    // Then
    assertTrue(failureCalled)
  }

  @Test
  fun `search should call onFailure for unsuccessful response`() {
    // Given
    val query = "invalid query"
    val mockResponse = createMockResponse("Not Found", 404)

    whenever(mockClient.newCall(any())).thenReturn(mockCall)
    doAnswer { invocation ->
          val callback = invocation.arguments[0] as Callback
          callback.onResponse(mockCall, mockResponse)
          null
        }
        .`when`(mockCall)
        .enqueue(any())

    // When
    var failureCalled = false
    locationRepository.search(
        query,
        onSuccess = { throw AssertionError("Success callback should not be called") },
        onFailure = { exception ->
          failureCalled = true
          assertEquals("Unexpected code $mockResponse", exception.message)
        })

    // Then
    assertTrue(failureCalled)
  }

  @Test
  fun `getCurrentLocation should call onFailure when location retrieval fails`() {
    // Given
    val exception = Exception("Location error")
    whenever(fusedLocationProviderClient.lastLocation).thenReturn(mockLocationTask)
    doAnswer { invocation ->
          val failureListener = invocation.getArgument<OnFailureListener>(0)
          failureListener.onFailure(exception) // Simulate an error
          null
        }
        .`when`(mockLocationTask)
        .addOnFailureListener(any())

    var failureCalled = false
    var capturedException: Exception? = null

    // When
    locationRepository.getCurrentLocation(
        onSuccess = { throw AssertionError("Success callback should not be called") },
        onFailure = { ex ->
          failureCalled = true
          capturedException = ex
        })

    // Then
    assertTrue(failureCalled)
    assertEquals(exception, capturedException)
  }

  private fun createMockResponse(body: String?, code: Int): Response {
    return Response.Builder()
        .code(code)
        .message("OK")
        .protocol(Protocol.HTTP_1_1)
        .request(Request.Builder().url("https://nominatim.openstreetmap.org").build())
        .body(body?.toResponseBody("application/json".toMediaTypeOrNull()))
        .build()
  }
}
