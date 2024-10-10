package com.android.sample.model.activity

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ActivityRepositoryFirestore(private val db: FirebaseFirestore) : ActivityRepository {
  private val activities = "activities"

  override fun getNewUid(): String {
    return db.collection(activities).document().id
  }

  override fun init(onSuccess: () -> Unit) {
      onSuccess()
  }

  override fun getActivities(onSuccess: (List<Activity>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(activities).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val activities =
            task.result?.mapNotNull { document -> documentToActivity(document) } ?: emptyList()
        onSuccess(activities)
      } else {
        task.exception?.let { e -> onFailure(e) }
      }
    }
  }

  override fun addActivity(
      activity: Activity,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(activities).document(activity.uid).set(activity), onSuccess, onFailure)
  }

  override fun updateActivity(
      activity: Activity,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(activities).document(activity.uid).set(activity), onSuccess, onFailure)
  }

  override fun deleteActivityById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(db.collection(activities).document(id).delete(), onSuccess, onFailure)
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

  private fun documentToActivity(document: DocumentSnapshot): Activity? {
    return try {
      val uid = document.id
      val name = document.getString("title") ?: return null
      val description = document.getString("description") ?: return null
      val assigneeName = document.getString("creator") ?: return null
      val dueDate = document.getTimestamp("date") ?: return null
      val locationData = document.get("location") as? Map<*, *>
      val location =
          locationData?.let {
            //                    Location(
            //                        latitude = it["latitude"] as? Double ?: 0.0,
            //                        longitude = it["longitude"] as? Double ?: 0.0,
            //                        name = it["name"] as? String ?: "")
          }
      val statusString = document.getString("status") ?: return null
      val status = ActivityStatus.valueOf(statusString)

      Activity(
          uid = uid,
          title = name,
          description = description,
          creator = assigneeName,
          date = dueDate.toString(),
          price = 0.0,
          location = "",
          images = emptyList(),
          placesLeft = 0,
          maxPlaces = 0,
          status = status)
    } catch (e: Exception) {
      Log.e("ActivityRepositoryFireStore", "Error converting document to Activity", e)
      null
    }
  }
}
