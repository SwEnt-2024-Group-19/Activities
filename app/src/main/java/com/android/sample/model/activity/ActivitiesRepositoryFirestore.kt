package com.android.sample.model.activity

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class ActivitiesRepositoryFirestore(private val db: FirebaseFirestore) : ActivitiesRepository {

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
                  Activity(
                      document.id,
                      data["title"] as String,
                      data["description"] as String,
                      data["date"] as Timestamp,
                      data["price"] as Double,
                      data["location"] as String,
                      data["creator"] as String,
                      listOf(),
                      data["placesLeft"] as Long,
                      data["maxPlaces"] as Long,
                      ActivityStatus.valueOf(data["status"] as String)
                  , listOf())
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

  private fun performFirestoreOperation(
      task: Task<Void>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        result.exception?.let { e ->
          Log.e("TodosRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }
}
