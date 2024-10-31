package com.android.sample.model.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationRepository
import com.android.sample.model.map.LocationViewModel
import io.mockk.coEvery
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

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // for LiveData

    private lateinit var viewModel: LocationViewModel
    private val mockRepository: LocationRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = LocationViewModel(mockRepository)
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
    fun `setQuery should invoke repository search when query is non-empty`() = runTest {
        // Given
        val testQuery = "test location"
        val testLocations = listOf(
            Location(latitude = 12.34, longitude = 56.78, name = "Test Location")
        )

        coEvery { mockRepository.search(testQuery, any(), any()) } answers {
            thirdArg<(List<Location>) -> Unit>().invoke(testLocations) // success callback
        }

        // When
        viewModel.setQuery(testQuery)

        // Then
        coVerify { mockRepository.search(testQuery, any(), any()) }
        assertEquals(testLocations, viewModel.locationSuggestions.first())
        assertEquals("Test Location", viewModel.locationSuggestions.first()[0].name)
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
    fun `setQuery should handle empty results`() = runTest {
        // Given
        val testQuery = "nonexistent location"
        val emptyResults = emptyList<Location>()

        // Mocking `search` to return an empty list via the success callback
        coEvery { mockRepository.search(testQuery, any(), any()) } answers {
            thirdArg<(List<Location>) -> Unit>().invoke(emptyResults) // success callback with empty list
        }

        // When
        viewModel.setQuery(testQuery)

        // Then
        // Collect the first emitted value from the locationSuggestions flow and assert it matches emptyResults
        val actualResults = viewModel.locationSuggestions.first()
        assertEquals(emptyResults, actualResults)
    }

}
