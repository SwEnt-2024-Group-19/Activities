package com.android.sample.model.map

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
open class LocationViewModel
@Inject
constructor(
    private val repository: LocationRepository,
    private val permissionChecker: PermissionChecker
) : ViewModel() {

  private val query_ = MutableStateFlow("")
  open val query: StateFlow<String> = query_

  private var locationSuggestions_ = MutableStateFlow(emptyList<Location>())
  val locationSuggestions: StateFlow<List<Location>> = locationSuggestions_

  private val _currentLocation = MutableStateFlow<Location?>(null)
  val currentLocation: StateFlow<Location?> = _currentLocation

  fun setQuery(query: String) {
    query_.value = query

    if (query.isNotEmpty()) {
      repository.search(
          query,
          onSuccess = { locations ->
            locationSuggestions_.value = locations.distinct() // Filter out repetitive updates
            println("Updated location suggestions: ${locationSuggestions_.value}") // Debugging line
          },
          onFailure = { throwable ->
            locationSuggestions_.value = emptyList()
            println("Failed to fetch suggestions, cleared list. Error: ${throwable.message}")
            // or
            Log.e(
                "LocationSuggestions",
                "Failed to fetch suggestions: ${throwable.message}",
                throwable)
            // Include the error message in the log message for easier debugging
          })
    } else {
      locationSuggestions_.value = emptyList()
      println("Query is empty, cleared suggestions.") // Debugging line
    }
  }

  fun fetchCurrentLocation() {
    if (permissionChecker.hasLocationPermission()) {
      repository.getCurrentLocation(
          onSuccess = { _currentLocation.value = it },
          onFailure = { Log.e("LocationViewModel", "Failed to get location", it) })
    } else {
      Log.e("LocationViewModel", "Location permission not granted")
      // Handle the permission denial as appropriate
    }
  }
}
