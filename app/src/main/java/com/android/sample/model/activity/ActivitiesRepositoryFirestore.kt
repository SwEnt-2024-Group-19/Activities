package com.android.sample.model.activity

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

open class ActivitiesRepositoryFirestore(private val db: FirebaseFirestore) : ActivitiesRepository {

  private val activitiesCollectionPath = "activities"
  private val TAG = "ActivitiesRepositoryFirestore"

  override fun getNewUid(): String {
    return db.collection(activitiesCollectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getActivities(onSuccess: (List<Activity>) -> Unit, onFailure: (Exception) -> Unit) {

    db.collection(activitiesCollectionPath).addSnapshotListener { snapshot, e ->
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
                  val activityType =
                      data["type"]?.let {
                        try {
                          ActivityType.valueOf(it as String)
                        } catch (e: IllegalArgumentException) {
                          ActivityType.SOLO // Replace with your default ActivityType
                        }
                      } ?: ActivityType.SOLO
                  Activity(
                      document.id,
                      data["title"] as? String ?: "No Title",
                      data["description"] as? String ?: "No Description",
                      data["date"] as? Timestamp ?: Timestamp.now(),
                      data["startTime"] as? String ?: "HH:mm",
                      data["duration"] as? String ?: "HH:mm",
                      data["price"] as? Double ?: 0.0,
                      data["location"] as? String ?: "Unknown Location",
                      data["creator"] as? String ?: "Anonymous",
                      listOf(),
                      data["placesLeft"] as? Long ?: 0,
                      data["maxPlaces"] as? Long ?: 0,
                      ActivityStatus.valueOf(data["status"] as? String ?: "ACTIVE"),
                      activityType,
                      participants = listOf())
                }
                .filterNotNull() // Filter out any null results

        onSuccess(activities)
      } else onFailure(e ?: Exception("Error getting documents"))
    }
  }

  override fun addActivity(
      activity: Activity,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(activitiesCollectionPath).document(activity.uid).set(activity),
        onSuccess,
        onFailure)
  }

  override fun updateActivity(
      activity: Activity,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(activitiesCollectionPath).document(activity.uid).set(activity),
        onSuccess,
        onFailure)
  }

  override fun deleteActivityById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(activitiesCollectionPath).document(id).delete(), onSuccess, onFailure)
  }

  fun performFirestoreOperation(
      task: Task<Void>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        result.exception?.let { e ->
          Log.e("ActivitiesRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }
}
