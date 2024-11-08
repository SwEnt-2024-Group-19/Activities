package com.android.sample.model.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationViewModelTest {

  @get:Rule val instantExecutorRule = InstantTaskExecutorRule() // for LiveData

  private lateinit var viewModel: LocationViewModel
  private val mockRepository: LocationRepository = mockk(relaxed = true)
  private val mockPermissionChecker: LocationPermissionChecker = mockk(relaxed = true)

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
    coVerify(exactly = 0) { mockRepository.search(any(), any(), any()) }
  }

  @Test
  fun `fetchCurrentLocation should call getCurrentLocation when permission is granted`() = runTest {
    // Given
    every { mockPermissionChecker.hasLocationPermission() } returns true
    coEvery { mockRepository.getCurrentLocation(any(), any()) } answers
        {
          firstArg<(Location) -> Unit>().invoke(Location(1.0, 2.0, "Test Location"))
        }

    // When
    viewModel.fetchCurrentLocation()

    // Then
    coVerify { mockRepository.getCurrentLocation(any(), any()) }
    assertEquals(Location(1.0, 2.0, "Test Location"), viewModel.currentLocation.first())
  }

  @Test
  fun `fetchCurrentLocation should not call getCurrentLocation when permission is denied`() =
      runTest {
        // Given
        every { mockPermissionChecker.hasLocationPermission() } returns false

        // When
        viewModel.fetchCurrentLocation()

        // Then
        coVerify(exactly = 0) { mockRepository.getCurrentLocation(any(), any()) }
      }

  @Test
  fun `fetchCurrentLocation should update currentLocation when getCurrentLocation succeeds`() =
      runTest {
        // Given
        every { mockPermissionChecker.hasLocationPermission() } returns true
        val expectedLocation = Location(1.0, 2.0, "Test Location")
        coEvery { mockRepository.getCurrentLocation(any(), any()) } answers
            {
              firstArg<(Location) -> Unit>().invoke(expectedLocation)
            }

        // When
        viewModel.fetchCurrentLocation()

        // Then
        assertEquals(expectedLocation, viewModel.currentLocation.first())
      }

  @Test
  fun `fetchCurrentLocation should handle error when getCurrentLocation fails`() = runTest {
    // Given
    every { mockPermissionChecker.hasLocationPermission() } returns true
    coEvery { mockRepository.getCurrentLocation(any(), any()) } answers
        {
          secondArg<(Exception) -> Unit>().invoke(Exception("Location error"))
        }

    // When
    viewModel.fetchCurrentLocation()

    // Then
    // Check that the state of `currentLocation` remains unchanged (null or the previous state)
    assertEquals(null, viewModel.currentLocation.first()) // Assuming initial state is null
  }
}
