package com.android.sample.model.activity

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class ActivitiesRepositoryFirestore(private val db: FirebaseFirestore) : ActivitiesRepository {

  private val collectionPath = "activities"
  private val TAG = "ActivitiesRepositoryFirestore"

  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getActivities(onSuccess: (List<Activity>) -> Unit, onFailure: (Exception) -> Unit) {

    db.collection(collectionPath).addSnapshotListener { snapshot, e ->
      if (e != null) {
        Log.e(TAG, "Error getting documents", e)
        onFailure(e)
        return@addSnapshotListener
      }

      if (snapshot != null) {
        val activities =
            snapshot.documents
                .map { document ->
                  Log.d(TAG, "${document.id} => ${document.data}")
                  val data = document.data ?: return@map null // Handle null data gracefully
                  Activity(
                      document.id,
                      data["name"] as String,
                      data["description"] as String,
                      data["date"] as Timestamp,
                      data["location"] as String,
                      data["organizerName"] as String,
                      data["image"] as Long,
                      data["placesLeft"] as Long,
                      data["maxPlaces"] as Long)
                }
                .filterNotNull() // Filter out any null results

        onSuccess(activities)
      }
    }
  }

  override fun addActivity(
      activity: Activity,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }
}
