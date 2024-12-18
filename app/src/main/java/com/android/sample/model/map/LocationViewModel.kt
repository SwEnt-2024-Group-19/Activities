package com.android.sample.model.map

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
open class LocationViewModel
@Inject
constructor(
    private val repository: LocationRepository,
    private val permissionChecker: PermissionChecker
) : ViewModel() {

  private val _query = MutableStateFlow("")
  open val query: StateFlow<String> = _query

  private var _locationSuggestions = MutableStateFlow(emptyList<Location>())
  val locationSuggestions: StateFlow<List<Location>> = _locationSuggestions

  private val _currentLocation = MutableStateFlow<Location?>(null)
  val currentLocation: StateFlow<Location?> = _currentLocation

  fun setCurrentLocation(location: Location?) {
    _currentLocation.value = location
  }

  fun setQuery(query: String) {
    _query.value = query

    if (query.isNotEmpty()) {
      repository.search(
          query,
          onSuccess = { locations ->
            _locationSuggestions.value = locations.distinct() // Filter out repetitive updates
          },
          onFailure = { throwable -> _locationSuggestions.value = emptyList() })
    } else {
      _locationSuggestions.value = emptyList()
    }
  }

  /**
   * Starts tracking the user's location if permission is granted. It calls `startLocationUpdates`
   * from the repository, which continuously fetches location updates.
   */
  fun startTrackingLocation() {
    if (permissionChecker.hasLocationPermission()) {
      (repository as? NominatimLocationRepository)?.startLocationUpdates { location ->
        _currentLocation.value = location
      }
    } else {}
  }

  fun stopTrackingLocation() {
    (repository as? NominatimLocationRepository)?.stopLocationUpdates()
  }

  /**
   * Calculate the distance between the current location and the given activity location.
   *
   * @param activityLocation The location of the activity.
   * @return The distance in kilometers.
   */
  fun getDistanceFromCurrentLocation(activityLocation: Location?): Float? {
    if (activityLocation == null) return null
    val currentLocation = _currentLocation.value ?: return null
    return calculateDistance(
            currentLocation.latitude,
            currentLocation.longitude,
            activityLocation.latitude,
            activityLocation.longitude)
        .toFloat()
  }

  /**
   * Calculate the distance between two locations using the Haversine formula.
   *
   * @param lat1 Latitude of the first location.
   * @param lon1 Longitude of the first location.
   * @param lat2 Latitude of the second location.
   * @param lon2 Longitude of the second location.
   * @return The distance in kilometers.
   */
  private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Radius of the Earth in kilometers

    // Convert latitude and longitude from degrees to radians
    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    // Haversine formula
    val dLat = lat2Rad - lat1Rad
    val dLon = lon2Rad - lon1Rad

    val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    // Distance in kilometers
    return earthRadius * c
  }
}
