package com.android.sample.model.profile

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

open class ProfilesRepositoryFirestore(private val db: FirebaseFirestore) : ProfilesRepository {

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
        task.exception?.let { e ->
          Log.e("UserProfileRepository", "Error getting user document", e)
          onFailure(e)
        }
      }
    }
  }

  private fun documentToUser(document: DocumentSnapshot): User? {
    return try {
      val id = document.id
      val name = document.getString("name") ?: return null
      val surname = document.getString("surname") ?: return null
      val photo = document.getString("photo") ?: return null
      val interests =
          (document.get("interests") as? List<*>)?.filterIsInstance<String>() ?: return null
      Log.e("not an error", "interests are $interests")
      val activities =
          (document.get("activities") as? List<*>)?.filterIsInstance<String>() ?: return null

      User(
          id = id,
          name = name,
          surname = surname,
          interests = interests,
          activities = activities,
          photo = photo)
    } catch (e: Exception) {
      Log.e("ProfilesRepositoryFirestore", "Error converting document to User", e)
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
            task.exception?.let { e ->
              Log.e("ProfilesRepository", "Error adding activity to profile", e)
              onFailure(e)
            }
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
        .addOnFailureListener { exception ->
          Log.e("ProfilesRepository", "Error updating user profile", exception)
          onFailure(exception)
        }
  }

  override fun deleteActivityFromProfile(
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
            task.exception?.let { e ->
              Log.e("ProfilesRepository", "Error deleting activity from profile", e)
              onFailure(e)
            }
          }
        }
  }
}
