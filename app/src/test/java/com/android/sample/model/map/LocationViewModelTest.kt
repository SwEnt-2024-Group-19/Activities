package com.android.sample.model.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coVerify
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
}
