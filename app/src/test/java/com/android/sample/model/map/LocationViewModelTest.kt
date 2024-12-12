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
  private val mockRepository: NominatimLocationRepository = mock()
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
  fun `startTrackingLocation should call repository startLocationUpdates when permission is granted`() =
      runTest {
        // Given
        whenever(mockPermissionChecker.hasLocationPermission()).thenReturn(true)
        doAnswer { invocation ->
              val onUpdate = invocation.arguments[0] as (Location) -> Unit
              onUpdate(Location(1.0, 2.0, "Updated Location", "Updated Location"))
              null
            }
            .whenever(mockRepository)
            .startLocationUpdates(any())

        // When
        viewModel.startTrackingLocation()

        // Then
        verify(mockRepository).startLocationUpdates(any())
        assertEquals(
            Location(1.0, 2.0, "Updated Location", "Updated Location"),
            viewModel.currentLocation.first())
      }

  @Test
  fun `startTrackingLocation should not call repository startLocationUpdates when permission is denied`() =
      runTest {
        // Given
        whenever(mockPermissionChecker.hasLocationPermission()).thenReturn(false)

        // When
        viewModel.startTrackingLocation()

        // Then
        verify(mockRepository, never()).startLocationUpdates(any())
      }

  @Test
  fun `stopTrackingLocation should call repository stopLocationUpdates`() = runTest {
    // When
    viewModel.stopTrackingLocation()

    // Then
    verify(mockRepository).stopLocationUpdates()
  }

  @Test
  fun `getDistanceFromCurrentLocation should return null when current location is null`() =
      runTest {
        // Given
        viewModel.setCurrentLocation(null)
        val activityLocation = Location(1.0, 2.0, "Test Location", "Test Location")

        // When
        val distance = viewModel.getDistanceFromCurrentLocation(activityLocation)

        // Then
        assertEquals(null, distance)
      }

  @Test
  fun `getDistanceFromCurrentLocation should return distance between current location and activity location`() =
      runTest {
        // Given
        val currentLocation =
            Location(46.518831258, 6.559331096, "EPFL", "Ecole Polytechnique Fédérale de Lausanne")
        viewModel.setCurrentLocation(currentLocation)
        val activityLocation = Location(46.5375, 6.573611, "Epenex", "Epenex")

        // When
        val distance = viewModel.getDistanceFromCurrentLocation(activityLocation)

        // Then
        assertNotNull(distance)
        if (distance != null) {
          assertEquals(2.34f, distance, 0.05f)
        }
      }
}
