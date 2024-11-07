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
  private val query_ = MutableStateFlow("")
  val query: StateFlow<String> = query_

  private var locationSuggestions_ = MutableStateFlow(emptyList<Location>())
  val locationSuggestions: StateFlow<List<Location>> = locationSuggestions_

  private val _currentLocation = MutableStateFlow<Location?>(null)
  val currentLocation: StateFlow<Location?> = _currentLocation

  fun setQuery(query: String) {
    query_.value = query

    if (query.isNotEmpty()) {
      repository.search(query, { locationSuggestions_.value = it }, {})
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
