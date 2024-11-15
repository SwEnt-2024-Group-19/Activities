package com.android.sample.model.profile

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
        task.exception?.let { e ->
          Log.e("UserProfileRepository", "Error getting user document", e)
          onFailure(e)
        }
      }
    }
  }

  override fun observeAuthState(onSignedIn: (String) -> Unit, onSignedOut: () -> Unit) {
    Firebase.auth.addAuthStateListener { auth ->
      val currentUser = auth.currentUser
      Log.d("ProfileViewModel", "User is authenticated, fetching data.")
      if (currentUser != null) {
        Log.d("ProfileViewModel", "User is authenticated, fetching data.")
        onSignedIn(currentUser.uid)
      } else {
        Log.d("ProfileViewModel", "No user is authenticated, skipping data fetch.")
        onSignedOut()
      }
    }
  }

  private fun documentToUser(document: DocumentSnapshot): User? {
    return try {
      User(
          id = document.id,
          name = document.getString("name") ?: return null,
          surname = document.getString("surname") ?: return null,
          interests =
              (document.get("interests") as? List<*>)?.filterIsInstance<String>() ?: return null,
          activities =
              (document.get("activities") as? List<*>)?.filterIsInstance<String>() ?: return null,
          photo = document.getString("photo") ?: return null,
          likedActivities =
              (document.get("likedActivities") as? List<*>)?.filterIsInstance<String>()
                  ?: return null)
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
            task.exception?.let { e ->
              Log.e("ProfilesRepository", "Error adding activity to profile", e)
              onFailure(e)
            }
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
        .addOnFailureListener { exception ->
          Log.e("ProfilesRepository", "Error adding user profile")
          onFailure(exception)
        }
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
}
