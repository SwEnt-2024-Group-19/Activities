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
    private val permissionChecker: LocationPermissionChecker
) : ViewModel() {

  private val _query = MutableStateFlow("")
  open val query: StateFlow<String> = _query

  private var _locationSuggestions = MutableStateFlow(emptyList<Location>())
  val locationSuggestions: StateFlow<List<Location>> = _locationSuggestions

  private val _currentLocation = MutableStateFlow<Location?>(null)
  val currentLocation: StateFlow<Location?> = _currentLocation

  fun setQuery(query: String) {
    _query.value = query

    if (query.isNotEmpty()) {
      repository.search(
          query,
          onSuccess = { locations ->
            _locationSuggestions.value = locations.distinct() // Filter out repetitive updates
          },
          onFailure = { throwable ->
            _locationSuggestions.value = emptyList()
            Log.e(
                "LocationSuggestions",
                "Failed to fetch suggestions: ${throwable.message}",
                throwable)
          })
    } else {
      _locationSuggestions.value = emptyList()
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
