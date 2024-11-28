package com.android.sample.model.activity

import android.util.Log
import com.android.sample.model.map.Location
import com.android.sample.model.profile.Interest
import com.android.sample.model.profile.User
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

open class ActivitiesRepositoryFirestore @Inject constructor(private val db: FirebaseFirestore) :
    ActivitiesRepository {

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
                  documentToActivity(data, document.id)
                }
                .filterNotNull() // Filter out any null results

        onSuccess(activities)
      } else onFailure(e ?: Exception("Error getting documents"))
    }
  }

  fun documentToActivity(data: Map<String, Any>, documentId: String): Activity? {

    val images = data["images"] as? List<String> ?: listOf()
    val participants =
        (data["participants"] as? List<Map<String, Any>>)?.map { participantData ->
          User(
              name = participantData["name"] as? String ?: "No Name",
              surname = participantData["surname"] as? String ?: "No Surname",
              id = participantData["id"] as? String ?: "No ID",
              interests =
                  (participantData["interests"] as? List<*>)?.mapNotNull { interestData ->
                    // Ensure interestData is a map and extract category and interest
                    (interestData as? Map<*, *>)?.let {
                      val category = it["category"] as? String
                      val interest = it["interest"] as? String
                      if (category != null && interest != null) {
                        Interest(category, interest)
                      } else {
                        null
                      }
                    }
                  } ?: listOf(),
              activities = (participantData["activities"] as? List<String>) ?: listOf(),
              photo = participantData["photo"] as? String,
              likedActivities = (participantData["likedActivities"] as? List<String>) ?: listOf())
        } ?: listOf()
    val activityType =
        data["type"]?.let {
          try {
            ActivityType.valueOf(it as String)
          } catch (e: IllegalArgumentException) {
            ActivityType.SOLO
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
                  (commentData["replies"] as? List<Map<String, Any>>)?.map { replyData ->
                    Comment(
                        uid = replyData["uid"] as? String ?: "No UID",
                        userId = replyData["userId"] as? String ?: "No User ID",
                        userName = replyData["userName"] as? String ?: "No User Name",
                        content = replyData["content"] as? String ?: "No Content",
                        timestamp = replyData["timestamp"] as? Timestamp ?: Timestamp.now(),
                    )
                  } ?: emptyList())
        } ?: emptyList()

    val locationData = data["location"] as? Map<String, Any>
    val location =
        locationData?.let {
          Location(
              latitude = it["latitude"] as? Double ?: 0.0,
              longitude = it["longitude"] as? Double ?: 0.0,
              name = it["name"] as? String ?: "No Location")
        } ?: Location(0.0, 0.0, "No Location")

    return Activity(
        uid = documentId,
        title = data["title"] as? String ?: "No Title",
        description = data["description"] as? String ?: "No Description",
        date = data["date"] as? Timestamp ?: Timestamp.now(),
        startTime = data["startTime"] as? String ?: "HH:mm",
        duration = data["duration"] as? String ?: "HH:mm",
        price = data["price"] as? Double ?: 0.0,
        location = location, // Default value
        creator = data["creator"] as? String ?: "Anonymous",
        images = images,
        placesLeft = data["placesLeft"] as? Long ?: 0,
        maxPlaces = data["maxPlaces"] as? Long ?: 0,
        status = ActivityStatus.valueOf(data["status"] as? String ?: "ACTIVE"),
        type = activityType,
        participants = participants,
        comments = comments,
        category = Category.valueOf(data["category"] as? String ?: "SPORT"))
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
