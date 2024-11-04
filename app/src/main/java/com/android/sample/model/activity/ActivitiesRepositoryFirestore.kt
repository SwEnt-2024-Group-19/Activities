package com.android.sample.model.activity

import android.util.Log
import com.android.sample.model.map.Location
import com.android.sample.ui.dialogs.SimpleUser
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
                  val participants =
                      (data["participants"] as? List<Map<String, Any>>)?.map { participantData ->
                        SimpleUser(
                            name = participantData["name"] as? String ?: "No Name",
                            surname = participantData["surname"] as? String ?: "No Surname",
                            age = participantData["age"] as? Int ?: 0)
                      } ?: listOf()
                  val activityType =
                      data["type"]?.let {
                        try {
                          ActivityType.valueOf(it as String)
                        } catch (e: IllegalArgumentException) {
                          ActivityType.SOLO // Replace with your default ActivityType
                        }
                      } ?: ActivityType.SOLO
                  val comments =
                      (data["comments"] as? List<Map<String, Any>>)?.map { commentData ->
                        Comment(
                            uid = commentData["uid"] as? String ?: "No UID",
                            userId = commentData["userId"] as? String ?: "No User ID",
                            userName = commentData["userName"] as? String ?: "No User Name",
                            content = commentData["content"] as? String ?: "No Content",
                            timestamp = commentData["timestamp"] as? Timestamp ?: Timestamp.now(),
                            replies =
                                (commentData["replies"] as? List<Map<String, Any>>)?.map { replyData
                                  ->
                                  Comment(
                                      uid = replyData["uid"] as? String ?: "No UID",
                                      userId = replyData["userId"] as? String ?: "No User ID",
                                      userName = replyData["userName"] as? String ?: "No User Name",
                                      content = replyData["content"] as? String ?: "No Content",
                                      timestamp =
                                          replyData["timestamp"] as? Timestamp ?: Timestamp.now(),
                                  )
                                } ?: emptyList())
                      } ?: emptyList()
                  Activity(
                      uid = document.id,
                      title = data["title"] as? String ?: "No Title",
                      description = data["description"] as? String ?: "No Description",
                      date = data["date"] as? Timestamp ?: Timestamp.now(),
                      startTime = data["startTime"] as? String ?: "HH:mm",
                      duration = data["duration"] as? String ?: "HH:mm",
                      price = data["price"] as? Double ?: 0.0,
                      location = data["location"] as? Location ?: Location(0.0, 0.0,"Unknown Location"),
                      creator = data["creator"] as? String ?: "Anonymous",
                      images = listOf(),
                      placesLeft = data["placesLeft"] as? Long ?: 0,
                      maxPlaces = data["maxPlaces"] as? Long ?: 0,
                      status = ActivityStatus.valueOf(data["status"] as? String ?: "ACTIVE"),
                      type = activityType,
                      participants = participants,
                      comments = comments)
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
