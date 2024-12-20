package com.android.sample.ui.map

import com.android.sample.model.map.Location
import com.android.sample.model.map.LocationRepository
import com.android.sample.resources.dummydata.e2e_locations

class MockLocationRepository : LocationRepository {
  override fun search(
      query: String,
      onSuccess: (List<Location>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    onSuccess(e2e_locations.filter { it.key.contains(query, ignoreCase = true) }.values.toList())
  }

  override fun getCurrentLocation(onSuccess: (Location) -> Unit, onFailure: (Exception) -> Unit) {
    onSuccess(e2e_locations.values.first())
  }
}
