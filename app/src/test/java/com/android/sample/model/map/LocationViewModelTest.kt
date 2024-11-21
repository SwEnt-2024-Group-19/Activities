package com.android.sample.model.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class LocationViewModelTest {

  @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var viewModel: LocationViewModel
  private val mockRepository: LocationRepository = mock()
  private val mockPermissionChecker: LocationPermissionChecker = mock()

  @Before
  fun setup() {
    viewModel = LocationViewModel(mockRepository, mockPermissionChecker)
  }

  @Test
  fun `setQuery should update query state`() = runTest {
    // Given
    val testQuery = "test location"

    // When
    viewModel.setQuery(testQuery)

    // Then
    assertEquals(testQuery, viewModel.query.first())
  }

  @Test
  fun `setQuery should not invoke repository search when query is empty`() = runTest {
    // Given
    val emptyQuery = ""

    // When
    viewModel.setQuery(emptyQuery)

    // Then
    verify(mockRepository, never()).search(any(), any(), any())
  }

  @Test
  fun `fetchCurrentLocation should call getCurrentLocation when permission is granted`() = runTest {
    // Given
    whenever(mockPermissionChecker.hasLocationPermission()).thenReturn(true)
    doAnswer { invocation ->
          val successCallback = invocation.arguments[0] as (Location) -> Unit
          successCallback(Location(1.0, 2.0, "Test Location"))
          null
        }
        .whenever(mockRepository)
        .getCurrentLocation(any(), any())

    // When
    viewModel.fetchCurrentLocation()

    // Then
    verify(mockRepository).getCurrentLocation(any(), any())
    assertEquals(Location(1.0, 2.0, "Test Location"), viewModel.currentLocation.first())
  }

  @Test
  fun `fetchCurrentLocation should not call getCurrentLocation when permission is denied`() =
      runTest {
        // Given
        whenever(mockPermissionChecker.hasLocationPermission()).thenReturn(false)

        // When
        viewModel.fetchCurrentLocation()

        // Then
        verify(mockRepository, never()).getCurrentLocation(any(), any())
      }

  @Test
  fun `fetchCurrentLocation should update currentLocation when getCurrentLocation succeeds`() =
      runTest {
        // Given
        whenever(mockPermissionChecker.hasLocationPermission()).thenReturn(true)
        val expectedLocation = Location(1.0, 2.0, "Test Location")
        doAnswer { invocation ->
              val successCallback = invocation.arguments[0] as (Location) -> Unit
              successCallback(expectedLocation)
              null
            }
            .whenever(mockRepository)
            .getCurrentLocation(any(), any())

        // When
        viewModel.fetchCurrentLocation()

        // Then
        assertEquals(expectedLocation, viewModel.currentLocation.first())
      }

  @Test
  fun `fetchCurrentLocation should handle error when getCurrentLocation fails`() = runTest {
    // Given
    whenever(mockPermissionChecker.hasLocationPermission()).thenReturn(true)
    doAnswer { invocation ->
          val errorCallback = invocation.arguments[1] as (Exception) -> Unit
          errorCallback(Exception("Location error"))
          null
        }
        .whenever(mockRepository)
        .getCurrentLocation(any(), any())

    // When
    viewModel.fetchCurrentLocation()

    // Then
    assertEquals(null, viewModel.currentLocation.first())
  }

  @Test
  fun `getDistanceFromCurrentLocation should return null when current location is null`() =
      runTest {
        // Given
        viewModel.setCurrentLocation(null)
        val activityLocation = Location(1.0, 2.0, "Test Location")

        // When
        val distance = viewModel.getDistanceFromCurrentLocation(activityLocation)

        // Then
        assertEquals(null, distance)
      }

  @Test
  fun `getDistanceFromCurrentLocation should return distance between current location and activity location`() =
      runTest {
        // Given
        val currentLocation = Location(46.518831258, 6.559331096, "EPFL")
        viewModel.setCurrentLocation(currentLocation)
        val activityLocation = Location(46.5375, 6.573611, "Epenex")
        // When
        val distance = viewModel.getDistanceFromCurrentLocation(activityLocation)
        // Then
        assertNotNull(distance)
        if (distance != null) {
          assertEquals(2.34f, distance, 0.05f)
        }
      }
}
