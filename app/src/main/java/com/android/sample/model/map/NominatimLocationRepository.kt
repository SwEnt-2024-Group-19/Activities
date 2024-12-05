package com.android.sample.model.map

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.IOException
import javax.inject.Inject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException

class NominatimLocationRepository
@Inject
constructor(
    private val client: OkHttpClient,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {
  private val locationRequest =
      LocationRequest.Builder(10000) // 10 seconds
          .setMinUpdateIntervalMillis(5000) // 5 seconds
          .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
          .build()

  private val locationCallback =
      object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
          val location = locationResult.lastLocation
          if (location != null) {
            // Update the ViewModel or any listener with the new location
            Log.d(
                "NominatimLocationRepository",
                "Updated location: ${location.latitude}, ${location.longitude}")
            // You could notify the ViewModel here, for example:
            onLocationUpdate?.invoke(
                Location(location.latitude, location.longitude, "Current Location"))
          }
        }
      }

  private var onLocationUpdate: ((Location) -> Unit)? = null

  @SuppressLint("MissingPermission")
  fun startLocationUpdates(onLocationUpdate: (Location) -> Unit) {
    this.onLocationUpdate = onLocationUpdate
    fusedLocationClient.requestLocationUpdates(
        locationRequest, locationCallback, null // Pass Looper.getMainLooper() if needed
        )
  }

  fun stopLocationUpdates() {
    fusedLocationClient.removeLocationUpdates(locationCallback)
  }

  private fun parseBody(body: String): List<Location> {
    return try {
      val jsonArray = JSONArray(body)
      List(jsonArray.length()) { i ->
        val jsonObject = jsonArray.getJSONObject(i)
        val lat = jsonObject.getDouble("lat")
        val lon = jsonObject.getDouble("lon")
        val name = jsonObject.getString("display_name")
        Location(lat, lon, name)
      }
    } catch (e: JSONException) {
      // Log the error and return an empty list if the JSON format is incorrect
      Log.e("NominatimLocationRepository", "Failed to parse JSON body", e)
      emptyList()
    }
  }

  override fun search(
      query: String,
      onSuccess: (List<Location>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Using HttpUrl.Builder to properly construct the URL with query parameters.
    val url =
        HttpUrl.Builder()
            .scheme("https")
            .host("nominatim.openstreetmap.org")
            .addPathSegment("search")
            .addQueryParameter("q", query)
            .addQueryParameter("format", "json")
            .build()

    val request =
        Request.Builder()
            .url(url)
            .header("User-Agent", "Aptivities/1.0 (elvan@epfl.ch)") // Set a proper User-Agent
            .header("Referer", "https://aptivities-epfl.com") // Optionally add a Referer
            .build()
    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                Log.e("NominatimLocationRepository", "Failed to execute request", e)
                onFailure(e)
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) {
                    onFailure(Exception("Unexpected code $response"))
                    Log.d("NominatimLocationRepository", "Unexpected code $response")
                    return
                  }

                  val body = response.body?.string()
                  if (body != null) {
                    onSuccess(parseBody(body))
                    Log.d("NominatimLocationRepository", "Body: $body")
                  } else {
                    Log.d("NominatimLocationRepository", "Empty body")
                    onSuccess(emptyList())
                  }
                }
              }
            })
  }

  @SuppressLint("MissingPermission")
  override fun getCurrentLocation(onSuccess: (Location) -> Unit, onFailure: (Exception) -> Unit) {
      // Attempt to get the last known location from the fused location provider
      fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            // If a valid location is found, invoke the success callback
            location?.let {
            Log.d("NominatimLocationRepository", "Location: $location")
            onSuccess(Location(it.latitude, it.longitude, "Current Location"))
          }
            // If no location is available, request a fresh location update
                ?: requestLocationUpdate(onSuccess, onFailure)
        }
        .addOnFailureListener { onFailure(it) }
  }

  @SuppressLint("MissingPermission")
  private fun requestLocationUpdate(onSuccess: (Location) -> Unit, onFailure: (Exception) -> Unit) {
    val cancellationTokenSource = CancellationTokenSource()
    fusedLocationClient
        .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
        .addOnSuccessListener { location ->
          Log.d("NominatimLocationRepository", "Location: $location")
          location?.let { onSuccess(Location(it.latitude, it.longitude, "Updated Location")) }
              ?: onFailure(Exception("Location not available"))
        }
        .addOnFailureListener { onFailure(it) }
  }
}
