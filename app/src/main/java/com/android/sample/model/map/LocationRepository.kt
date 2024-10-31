package com.android.sample.model.map

interface LocationRepository {
  fun search(query: String, onSuccess: (List<Location>) -> Unit, onFailure: (Exception) -> Unit)
}
