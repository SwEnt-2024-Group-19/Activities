package com.android.sample.model.map

import android.util.Log
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

class NominatimLocationRepository @Inject constructor(private val client: OkHttpClient) :
    LocationRepository {

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
              .header(
                  "User-Agent", "Aptivities/1.0 (elvan@epfl.ch)") // Set a proper User-Agent
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
}
