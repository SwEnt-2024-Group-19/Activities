package com.android.sample.model.profile

import com.android.sample.model.activity.categories
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

open class ProfilesRepositoryFirestore @Inject constructor(private val db: FirebaseFirestore) :
    ProfilesRepository {

  override fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("profiles").document(userId).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null) {
          val user = documentToUser(document) // Convert document to User
          onSuccess(user)
        } else {
          onSuccess(null) // No document found, return null
        }
      } else {
        task.exception?.let { e -> onFailure(e) }
      }
    }
  }

  fun documentToUser(document: DocumentSnapshot): User? {
    val interestsData = document.get("interests") as? List<*>
    val interests: List<Interest> =
        interestsData?.mapNotNull { interestData ->
          val interest = interestData as? Map<*, *> ?: return@mapNotNull null
          val name = interest["name"] as? String ?: return@mapNotNull null
          val categoryData = interest["category"] as? String ?: return@mapNotNull null
          val category = categories.find { it.name == categoryData } ?: return@mapNotNull null
          if (category != categoryOf[name]) return@mapNotNull null
          Interest(name = name, category = category)
        } ?: return null

    return try {
      User(
          id = document.id,
          name = document.getString("name") ?: return null,
          surname = document.getString("surname") ?: return null,
          interests = interests,
          activities =
              (document.get("activities") as? List<*>)?.filterIsInstance<String>() ?: return null,
          photo = document.getString("photo") ?: return null,
          likedActivities =
              (document.get("likedActivities") as? List<*>)?.filterIsInstance<String>()
                  ?: return null)
    } catch (e: Exception) {
      null
    }
  }

  override fun addActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection("profiles")
        .document(userId)
        .update("activities", FieldValue.arrayUnion(activityId))
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onSuccess()
          } else {
            task.exception?.let { e -> onFailure(e) }
          }
        }
  }

  override fun addLikedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection("profiles")
        .document(userId)
        .update("likedActivities", FieldValue.arrayUnion(activityId))
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onSuccess()
          } else {
            task.exception?.let { e -> onFailure(e) }
          }
        }
  }

  override fun removeLikedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection("profiles")
        .document(userId)
        .update("likedActivities", FieldValue.arrayRemove(activityId))
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onSuccess()
          } else {
            task.exception?.let { e -> onFailure(e) }
          }
        }
  }

  override fun removeJoinedActivity(
      userId: String,
      activityId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection("profiles")
        .document(userId)
        .update("activities", FieldValue.arrayRemove(activityId))
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onSuccess()
          } else {
            task.exception?.let { e -> onFailure(e) }
          }
        }
  }

  override fun addProfileToDatabase(
      userProfile: User,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection("profiles")
        .document(userProfile.id)
        .set(userProfile)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  override fun updateProfile(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("profiles")
        .document(user.id)
        .set(user)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }
}
