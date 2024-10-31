package com.android.sample.model.map

import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class NominatimLocationRepositoryTest {

  @Mock private lateinit var mockClient: OkHttpClient
  @Mock private lateinit var mockCall: Call

  private lateinit var locationRepository: NominatimLocationRepository

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    locationRepository = NominatimLocationRepository(mockClient)
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
    print("test123")

    `when`(mockClient.newCall(any<Request>())).thenReturn(mockCall)
    print("test125")
    doAnswer { invocation ->
          (invocation.arguments[0] as Callback).onResponse(mockCall, mockResponse)
          null
        }
        .`when`(mockCall)
        .enqueue(any())
    print("test124")

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

    `when`(mockClient.newCall(any())).thenReturn(mockCall)
    doAnswer { invocation ->
          (invocation.arguments[0] as Callback).onFailure(mockCall, exception)
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
    val mockResponse = createMockResponse("Not Found", 404) // Simulating a 404 response

    `when`(mockClient.newCall(any())).thenReturn(mockCall)
    doAnswer { invocation ->
          (invocation.arguments[0] as Callback).onResponse(mockCall, mockResponse)
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
